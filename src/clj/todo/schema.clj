(ns todo.schema
  (:require [schema.core :as s]))

(s/def Id s/Str)
(s/def Title s/Str)
(s/def Order s/Num)
(s/def Completed s/Bool)
(s/def Url s/Str)

(s/defschema ToDoCreate
  {:title Title
   (s/optional-key :order) Order
   (s/optional-key :completed) Completed})

(s/defschema ToDoUpdate
  {(s/optional-key :title) Title
   (s/optional-key :order) Order
   (s/optional-key :completed) Completed})

(s/defschema ToDoResponse
  {:id Id
   :title Title
   :url  Url
   :completed Completed
   (s/optional-key :order) Order})
