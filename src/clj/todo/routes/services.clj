(ns todo.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [todo.todos :as t]
            [schema.core :as s]))

(s/def Id s/Num)
(s/defschema ToDo
  {(s/optional-key :id) Id})


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

    (POST "/" []
      :return s/Any
      :summary "Add a ToDo item"
      :body [request-body s/Any]
      (ok (t/create request-body)))

    (GET "/:id" []
      :return s/Any
      :path-params [id :- s/Any]
      :summary "Find a ToDo by id"
      (ok (t/read id)))

    (GET "/" []
      :return []
      (ok))

    (GET "/plus" []
      :return Long
      :query-params [x :- Long, {y :- Long 1}]
      :summary "x+y with query-parameters. y defaults to 1."
      (ok (+ x y)))

    (POST "/minus" []
      :return Long
      :body-params [x :- Long, y :- Long]
      :summary "x-y with body-parameters."
      (ok (- x y)))

    (GET "/times/:x/:y" []
      :return Long
      :path-params [x :- Long, y :- Long]
      :summary "x*y with path-parameters"
      (ok (* x y)))

    (POST "/divide" []
      :return Double
      :form-params [x :- Long, y :- Long]
      :summary "x/y with form-parameters"
      (ok (/ x y)))

    (GET "/power" []
      :return Long
      :header-params [x :- Long, y :- Long]
      :summary "x^y with header-parameters"
      (ok (long (Math/pow x y))))))
