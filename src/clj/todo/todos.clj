(ns todo.todos
  (:refer-clojure :exclude [read update])
  (:require [immutant.caching :as c]
            [mount.core :refer [defstate]]))

(defstate cache
  :start (c/cache "todos")
  :stop (c/stop "todos"))

(def id (atom 0))

(defn next-id
  []
  (swap! id inc))

(defn create
  [m]
  (let [id (next-id)]
    (assoc (c/swap-in! cache id (constantly m)) :id id)))

(defn read
  [id]
  (if-let [todo (get cache id)]
    (assoc todo :id id)))

(defn read-all
  []
  (map (fn [[k v]] (assoc v :id k)) cache))

(defn update
  [id m]
  (if-let [current (read id)]
    (do (println current)
        (c/swap-in! cache id (constantly (merge current m))))))

(defn delete
  [id]
  (.remove cache id))
