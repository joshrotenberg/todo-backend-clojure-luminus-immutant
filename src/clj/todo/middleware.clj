(ns todo.middleware
  (:require [todo.env :refer [defaults]]
            [todo.config :refer [env]]
            [ring.middleware.flash :refer [wrap-flash]]
            [immutant.web.middleware :refer [wrap-session]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]))

(defonce cors-headers
  {"access-control-allow-headers" "accept, content-type"
   "access-control-allow-methods" "GET,HEAD,POST,DELETE,PUT"
   "access-control-allow-origin" "*"})

(defn wrap-cors [handler]
  (fn [request]
    (let [response (handler request)
          current-headers (get response :headers {})]
      (assoc-in response [:headers] (merge current-headers cors-headers)))))

(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
      wrap-flash
      (wrap-session {:cookie-attrs {:http-only true}})
      (wrap-defaults
       (-> site-defaults
           (assoc-in [:security :anti-forgery] false)
           (dissoc :session)))
      wrap-cors))
