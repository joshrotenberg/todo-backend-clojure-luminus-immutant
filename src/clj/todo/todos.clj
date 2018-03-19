(ns todo.todos
  (:refer-clojure :exclude [read update])
  (:require [immutant.caching :as c]
            [clojure.tools.logging :as log]
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
        m (merge m {:url (str "/todos/" id)}
                 (when (nil? (:completed m))
                   {:completed false}))]
    (c/swap-in! cache id (constantly (assoc m :id id)))))

(defn read
  [id]
  (get cache id nil))

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
