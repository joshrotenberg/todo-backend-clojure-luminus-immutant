(ns todo.todos
  (:require [immutant.caching :as c]
            [mount.core :refer [defstate]]))

(defstate cache
  :start (c/cache "todos")
  :stop (c/stop "todos"))
