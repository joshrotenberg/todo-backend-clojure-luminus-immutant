(ns todo.test.handler
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [cheshire.core :as json]
            [todo.handler :refer :all]
            [todo.routes.services :refer :all]
            [mount.core :as mount]))

(use-fixtures
  :each
  (fn [f]
    (mount/start #'todo.config/env
                 #'todo.handler/app
                 #'todo.todos/cache)
    (f)
    (mount/stop #'todo.todos/cache
                #'todo.handler/app
                #'todo.config/env)))

(deftest test-app
  (testing "/"
    (let [response (app (request :get "/todos"))]
      (is (= 200 (:status response))))
    (let [response (app (request :options "/todos"))]
      (is (= 200 (:status response)))))

  (testing "not-found route"
    (let [response (app (request :get "/invalid"))]
      (is (= 404 (:status response))))))

(defn- parse-response-body
  [response]
  (-> (:body response)
      slurp
      (json/parse-string true)))

(defn- post-todo
  [t]
  (app (-> (request :post "/todos")
           (json-body t))))

(deftest test-add
  (testing "add and get a todo"
    (let [response (post-todo {:doof "cha"})
          id (-> (:body response) slurp json/parse-string (get "id"))]
      (is (= 200 (:status response)))
      (let [response (app (request :get (str "/todos/" id)))
            todo (parse-response-body response)]
        (is (= todo {:id        id
                     :doof      "cha"
                     :completed false
                     :url       (str "http://localhost/todos/" id)}))
        (is (= 200 (:status response)))))))

(deftest test-get-all
  (testing "get all todos"
    (let [response1 (post-todo {:one "one"})
          todo1 (parse-response-body response1)
          response2 (post-todo {:two "two"})
          todo2 (parse-response-body response2)
          response3 (app (request :get "/todos"))
          both (parse-response-body response3)]
      (is (= 200 (:status response1)))
      (is (= 200 (:status response2)))
      (is (= 200 (:status response3)))
      (is (= todo1 (some #{todo1} both)))
      (is (= todo2 (some #{todo2} both))))))

(deftest test-delete
  (testing "delete a todo"
    (let [response1 (post-todo {:one "uno"})
          id (:id (parse-response-body response1))
          response2 (app (request :get (str "/todos/" id)))
          response3 (app (request :delete (str "/todos/" id)))
          response4 (app (request :get (str "/todos/" id)))]
      (is (= 200 (:status response1)))
      (is (= 200 (:status response2)))
      (is (= 200 (:status response3)))
      (is (= 404 (:status response4))))))

(deftest test-delete-all
  (testing "delete all todos"
    (let [response1 (post-todo {:one "uno"})
          id1 (:id (parse-response-body response1))
          response2 (post-todo {:two "dos"})
          id2 (:id (parse-response-body response2))
          response3 (app (request :get "/todos"))
          both (parse-response-body response3)
          response4 (app (request :get (str "/todos/" id1)))
          todo1 (parse-response-body response4)
          response5 (app (request :get (str "/todos/" id2)))
          todo2 (parse-response-body response5)
          response6 (app (request :delete "/todos"))
          response7 (app (request :get "/todos"))
          gone (parse-response-body response7)
          ]
      (is (= 200 (:status response1)))
      (is (= 200 (:status response2)))
      (is (= 200 (:status response3)))
      (is (= 200 (:status response4)))
      (is (= 200 (:status response5)))
      (is (= todo1 (some #{todo1} both)))
      (is (= todo2 (some #{todo2} both)))
      (is (= 200 (:status response6)))
      (is (= true (empty? gone))))))