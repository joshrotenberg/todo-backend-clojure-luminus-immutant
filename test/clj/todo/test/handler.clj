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

;(defrecord ToDo [id title url completed order])

(defn- parse-response-body
  [response]
  (-> (:body response)
      slurp
      (json/parse-string true)))

(defn- post-todo
  [t]
  (app (-> (request :post "/todos")
           (json-body t))))

(defn- patch-todo
  [i t]
  (app (-> (request :patch (str "/todos/" i))
           (json-body t))))

(deftest test-app
  (testing "/"
    (let [response (app (request :get "/todos"))]
      (is (= 200 (:status response))))
    (let [response (app (request :options "/todos"))]
      (is (= 200 (:status response)))))
  (testing "not-found route"
    (let [response (app (request :get "/invalid"))]
      (is (= 404 (:status response))))))

(deftest test-add
  (testing "add and get a todo"
    (let [response (post-todo {:title "what" :order 0})
          id (-> (:body response) slurp json/parse-string (get "id"))]
      (is (= 200 (:status response)))
      (let [response (app (request :get (str "/todos/" id)))
            todo (parse-response-body response)]
        (is (= {:id        id
                :completed false
                :title "what"
                :url       (str "http://localhost/todos/" id)
                :order 0} todo))
        (is (= 200 (:status response)))))))

(deftest test-get-all
  (testing "get all todos"
    (let [response1 (post-todo {:title "one" :order 0})
          todo1 (parse-response-body response1)
          response2 (post-todo {:title "two" :order 0})
          todo2 (parse-response-body response2)
          response3 (app (request :get "/todos"))
          both (parse-response-body response3)]
      (is (= 200 (:status response1)))
      (is (= 200 (:status response2)))
      (is (= 200 (:status response3)))
      (is (= todo1 (some #{todo1} both)))
      (is (= todo2 (some #{todo2} both))))))

(deftest test-patch
  (testing "patch a todo"
    (let [response1 (post-todo {:title "one" :order 0})
          todo1 (parse-response-body response1)
          response2 (patch-todo (:id todo1) {:title "uno"})
          todo2 (parse-response-body response2)]
      (is (= 200 (:status response1)))
      (is (= 200 (:status response2)))
      (is (= todo2 (merge todo1 {:title "uno"}))))))

(deftest test-delete
  (testing "delete a todo"
    (let [response1 (post-todo {:title "uno" :order 2})
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
    (let [response1 (post-todo {:title "uno" :order 2})
          id1 (:id (parse-response-body response1))
          response2 (post-todo {:title "dos" :order 1})
          id2 (:id (parse-response-body response2))
          response3 (app (request :get "/todos"))
          both (parse-response-body response3)
          response4 (app (request :get (str "/todos/" id1)))
          todo1 (parse-response-body response4)
          response5 (app (request :get (str "/todos/" id2)))
          todo2 (parse-response-body response5)
          response6 (app (request :delete "/todos"))
          response7 (app (request :get "/todos"))
          gone (parse-response-body response7)]
      (is (= 200 (:status response1)))
      (is (= 200 (:status response2)))
      (is (= 200 (:status response3)))
      (is (= 200 (:status response4)))
      (is (= 200 (:status response5)))
      (is (= todo1 (some #{todo1} both)))
      (is (= todo2 (some #{todo2} both)))
      (is (= 200 (:status response6)))
      (is (= true (empty? gone))))))
