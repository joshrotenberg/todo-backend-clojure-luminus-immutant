(ns todo.test.todo
  (:require [clojure.test :refer :all]
            [todo.todos :as t]
            [mount.core :as mount]))

(use-fixtures
  :each
  (fn [f]
    (mount/start #'todo.todos/cache)))

(deftest test-create
  (testing "creating a todo"
    (let [todo (t/create {:one "two"})]
      (is (= (:one todo) "two")))))

(deftest test-read
  (testing "reading a todo"
    (let [todo (t/create {:one "two"})
          todo1 (t/read (:id todo))
          no-todo (t/read 22)]
      (is (=  todo todo1))
      (is (= nil no-todo)))))

(deftest test-update
  (testing "updating a todo"
    (let [todo (t/create {:one "two"})
          todo1 (t/update (:id todo) {:one "three"})]
      (is (= todo todo1)))))
