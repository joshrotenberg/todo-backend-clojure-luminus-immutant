(ns todo.test.todo
  (:require [clojure.test :refer :all]
            [todo.todos :as t]
            [mount.core :as mount]))

(use-fixtures
  :each
  (fn [f]
    (mount/start #'todo.todos/cache)
    (f)
    (mount/stop #'todo.todos/cache)))

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

(deftest test-read-all
  (testing "reading all todos"
    (is (= true) (empty? (t/read-all)))
    (t/create {:foo "bar"})
    (is (= 1 (count (t/read-all))))
    (t/create {:foo "bar"})
    (is (= 2 (count (t/read-all))))))

(deftest test-update
  (testing "updating a todo"
    (let [todo (t/create {:one "two"})
          todo1 (t/update (:id todo) {:one "three"})]
      (is (= (t/read (:id todo)) todo1)))))
;
(deftest test-delete
  (testing "deleting a todo"
    (let [todo (t/create {:one "two"})
          id (:id todo)
          x (t/delete id)]
      (is (= nil (t/read id))))))
