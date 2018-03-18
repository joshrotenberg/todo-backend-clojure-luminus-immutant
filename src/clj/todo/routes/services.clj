(ns todo.routes.services
  (:require [ring.util.http-response :refer :all]
            [clojure.tools.logging :as log]
            [compojure.api.sweet :refer :all]
            [todo.todos :as t]
            [schema.core :as s]))

(s/def Id s/Num)
(s/defschema ToDo
  {(s/optional-key :id) Id})

(defn with-url
  [todo request]
  (let [host (-> request :headers (get "host" "localhost"))
        scheme (-> request :scheme name)
        id (:id todo)]
    (log/info "building url for" scheme host id)
    (merge todo {:url (str scheme "://" host "/todos/" id)})))

(defapi service-routes
  {:swagger {:ui   "/swagger-ui"
             :spec "/swagger.json"
             :data {:info {:version     "1.0.0"
                           :title       "ToDo API"
                           :description "ToDo Services"}}}}

  (context "/todos" []
    :tags ["todo"]

    (OPTIONS "/" []
      :summary "Support OPTIONS"
      (ok))
    (OPTIONS "/:id" []
      :summary "Support OPTIONS"
      (ok))

    (POST "/" request
      :return s/Any
      :summary "Add a todo item"
      :body [request-body s/Any]
      (let [todo (t/create request-body)]
        (ok (with-url todo request))))

    (GET "/:id" request
      :return s/Any
      :path-params [id :- s/Any]
      :summary "Find a todo by id"
      (if-let [todo (t/read id)]
        (ok (with-url todo request))
        (not-found)))

    (GET "/" request
      :return [s/Any]
      :summary "Get all todos"
      (ok (map #(with-url % request) (t/read-all))))

    (DELETE "/:id" []
      :return s/Any
      :path-params [id :- s/Any]
      :summary "Delete a todo"
      (ok (t/delete id)))

    (DELETE "/" []
      :return s/Any
      :summary "Delete all todos"
      (ok (t/delete-all)))))
