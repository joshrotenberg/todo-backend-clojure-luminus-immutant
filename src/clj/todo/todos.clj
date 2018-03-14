(ns todo.todos
  (:require [immutant.caching :as c]
            [mount.core :refer [defstate]]))

(defstate todos-cache
  :start (c/cache "todos")
  :stop (c/stop "todos"))
