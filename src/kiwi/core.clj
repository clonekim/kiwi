(ns kiwi.core
  (:gen-class)
  (:require [clojure.tools.logging :as log]
            [clojure.data.json :as json]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [integrant.core :as ig]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [nrepl.server :as nrepl]
            [org.httpkit.server :as http]
            [hbs.core :as hbs]
            [kiwi.mark :as mark]))


(def config
  {:db/server {}
   :renderer/init {:loader-path "/templates" :suffix ".html" :auto-reload true}
   :http/handler {}
   :http/server {:port 8000 :handler (ig/ref :http/handler)}
   :nrepl/server {:port 7000}})

(defonce hbs-registry (atom nil))


(defn render
  ([file]
   (render file {}))

  ([file map]
   (binding [hbs/*hbs* @hbs-registry]
     (hbs/render-file file map))))



(defmethod ig/init-key :db/server [_ _]
  (let [tcp (-> (org.h2.tools.Server/createTcpServer
                 (into-array String ["-tcpAllowOthers" "-ifNotExists"]))
                .start)

        cons (-> (org.h2.tools.Server/createWebServer
                     (into-array String ["-webPort" "8082" "-webAllowOthers" "-webDaemon" "-trace"]))
                    .start)]
    (log/info (. tcp getStatus))
    (log/info (. cons getStatus))

    {:tcp-server tcp
     :cons-server cons}))




(defmethod ig/init-key :renderer/init [_ {:keys [loader-path suffix auto-reload]}]
  (reset! hbs-registry
          (hbs/registry
           (hbs/classpath-loader loader-path suffix) :auto-reload? auto-reload)))



(defmethod ig/halt-key! :db/server [_ {:keys [tcp-server cons-server]}]
  (do
    (when-not (nil? tcp-server)
      (.stop tcp-server))

    (when-not (nil? cons-server)
      (.stop cons-server))))



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
             (GET "/" [] (render  "hello" {:name "3434"}))
             (GET "/markdown" [] (render "markdown"))
             (POST "/markdown" req
               (let [body (-> req :form-params :body)]
                 (log/info req)
                 (render "markdown" {:body body :preview (mark/parse body)})))

             (route/not-found "<h1>404</h1>"))]
    (-> app
        wrap-params
        wrap-keyword-params)))





(defmethod ig/init-key :nrepl/server [_ {:keys [port]}]
  (let [server (nrepl/start-server :port port)]
    (log/info "nREPL server started")
    server))


;; (defmethod ig/halt-key! :nrepl/server [_ server]
;;   (do
;;     (nrepl/stop-server server)
;;     (log/info "nREPL server stopped")))



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
