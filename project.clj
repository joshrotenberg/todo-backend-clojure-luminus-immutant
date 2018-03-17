(defproject todo "0.1.0-SNAPSHOT"

  :description "A ToDo Backend implementation in Clojure using Luminus and Immutant"
  :url "https://github.com/joshrotenberg/todo-backend-clojure-luminus-immutant"

  :dependencies [[clj-time "0.14.2"]
                 [org.immutant/immutant "2.1.10"]
                 [compojure "1.6.0"]
                 [cprop "0.1.11"]
                 [funcool/struct "1.2.0"]
                 [luminus-immutant "0.2.4"]
                 [luminus-nrepl "0.1.4"]
                 [luminus/ring-ttl-session "0.3.2"]
                 [markdown-clj "1.0.2"]
                 [metosin/compojure-api "1.1.12"]
                 [metosin/muuntaja "0.5.0"]
                 [metosin/ring-http-response "0.9.0"]
                 [mount "0.1.12"]
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.clojure/tools.logging "0.4.0"]
                 [cheshire "5.8.0"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-defaults "0.3.1"]
                 [selmer "1.11.7"]]

  :min-lein-version "2.0.0"
  
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :resource-paths ["resources"]
  :target-path "target/%s/"
  :main ^:skip-aot todo.core

  :plugins [[lein-immutant "2.1.0"]]

  :profiles
  {:uberjar       {:omit-source    true
                   :aot            :all
                   :uberjar-name   "todo.jar"
                   :source-paths   ["env/prod/clj"]
                   :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev   {:jvm-opts       ["-server" "-Dconf=dev-config.edn"]
                   :dependencies   [[pjstadig/humane-test-output "0.8.3"]
                                    [prone "1.5.0"]
                                    [ring/ring-devel "1.6.3"]
                                    [ring/ring-mock "0.3.2"]]
                   :plugins        [[com.jakemccrary/lein-test-refresh "0.19.0"]]

                   :source-paths   ["env/dev/clj"]
                   :resource-paths ["env/dev/resources"]
                   :repl-options   {:init-ns user}}
   ;:injections [(require 'pjstadig.humane-test-output)
   ;             (pjstadig.humane-test-output/activate!)]


   :project/test  {:jvm-opts       ["-server" "-Dconf=test-config.edn"]
                   :resource-paths ["env/test/resources"]}
   :profiles/dev  {}
   :profiles/test {}})
