(ns todo.test.handler
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [cheshire.core :as json]
            [todo.handler :refer :all]
            [mount.core :as mount]))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'todo.config/env
                 #'todo.handler/app
                 #'todo.todos/cache)
    (f)))

(deftest test-app
  (testing "/"
    (let [response (app (request :get "/todos"))]
      (is (= 200 (:status response))))
    (let [response (app (request :options "/todos"))]
      (is (= 200 (:status response))))))

(deftest test-add
  (testing "POST"
    (let [response (app (-> (request :post "/todos")
                            (json-body {:doof "cha"})))
          id (-> (:body response) slurp json/parse-string (get "id"))]
      (is (= 200 (:status response)))
      (let [response (app (request :get (str "/todos/" id)))]
        (println (request :get (str "/todos/" id)) response)
        (is (= 200 (:status response))))))


  (testing "main route"
    (let [response (app (request :get "/todos/plus?x=2&y=4"))]
      (is (= 200 (:status response)))))

  (testing "not-found route"
    (let [response (app (request :get "/invalid"))]
      (is (= 404 (:status response))))))
