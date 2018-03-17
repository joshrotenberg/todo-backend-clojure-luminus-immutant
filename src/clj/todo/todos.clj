(ns todo.todos
  (:refer-clojure :exclude [read update])
  (:require [immutant.caching :as c]
            [mount.core :refer [defstate]])
  (:import java.util.UUID))

(defstate cache
  :start (c/cache "todos")
  :stop (c/stop "todos"))

(defn generate-id
  []
  (str (UUID/randomUUID)))

(defn create
  [m]
  (let [id (generate-id)
        m (merge m (when (nil? (:completed m))
                     {:completed false}))]
    (assoc (c/swap-in! cache id (constantly m)) :id id)))

(defn read
  [id]
  (when-let [todo (get cache id)]
    (assoc todo :id id)))

(defn read-all
  []
  (map (fn [[k v]] (assoc v :id k)) cache))

(defn update
  [id m]
  (when-let [current (read id)]
    (c/swap-in! cache id (constantly (merge current m)))))

(defn delete
  [id]
  (.remove cache id))

(defn delete-all
  []
  (.clear cache))