(ns user
  (:require 
            [mount.core :as mount]
            [todo.core :refer [start-app]]))

(defn start []
  (mount/start-without #'todo.core/repl-server))

(defn stop []
  (mount/stop-except #'todo.core/repl-server))

(defn restart []
  (stop)
  (start))


