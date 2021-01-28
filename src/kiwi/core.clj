(ns kiwi.core
  (:gen-class)
  (:require [clojure.tools.logging :as log]
            [clojure.data.json :as json]
            [integrant.core :as ig]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [org.httpkit.server :as http]))


(def config
  {:db/init {}
   :http/server {:port 7000 :handler (ig/ref :http/handler)}
   :http/handler {}})


(defmethod ig/init-key :db/init [_ _]
  (do
    (log/info "H2 db initalize..")))


(defmethod ig/init-key :http/server [_ {:keys [handler port]}]
  (let [server (http/run-server handler {:port port})]
    (log/info "Starting HTTP server on port" port)
    server))


(defmethod ig/halt-key! :http/server [_ server]
  (do
    (server :timeout 200)
    (log/info "HTTP server stopped")))


(defmethod ig/init-key :http/handler [_  _]
  (let [app (routes
             (GET "/" [] "안녕 세상아 ")
             (route/not-found "<h1>404</h1>"))]
    app))


(defonce component (atom nil))


(defn start! []
  (reset! component (ig/init config)))


(defn stop! []
  (when-not (nil? @component)
    (ig/halt! @component)
    (reset! component nil)))


(defn restart! []
  (do
    (stop!)
    (start!)))


(defn -main []
  (start!))
