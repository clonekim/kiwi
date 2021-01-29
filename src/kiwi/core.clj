(ns kiwi.core
  (:gen-class)
  (:require [clojure.tools.logging :as log]
            [clojure.data.json :as json]
            [nrepl.server :as nrepl]
            [mount.core :refer [defstate] :as mount]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [org.httpkit.server :as http]))



;; log uncaught exceptions in threads
(Thread/setDefaultUncaughtExceptionHandler
  (reify Thread$UncaughtExceptionHandler
    (uncaughtException [_ thread ex]
      (log/error {:what :uncaught-exception
                  :exception ex
                  :where (str "Uncaught exception on" (.getName thread))}))))

(def handler
  (routes
   (GET "/" [] "안녕 세상아 ")
   (route/not-found "<h1>404</h1>")))


(mount/defstate ^{:on-reload :noop} http-server
  :start
  (try
    (log/info "HTTP server stopped")
    (http/run-server handler {:port 8000})
    (catch Throwable t
      (log/error t)
      (throw t)
    ))

  :stop
  (when-not (nil? http-server)
    (http-server :timeout 100)
    (log/info "HTTP server stopped")))


(mount/defstate ^{:on-reload :noop}
  nrepl-server
  :start
  (try
    (log/info "nREPL server start")
    (nrepl/start-server :port 7000)
    (catch Throwable t
      (log/error t)
      (throw t)))

  :stop
  (when nrepl-server
    (nrepl/stop-server nrepl-server)
    (log/info "nREPL server stopped")))



(defn stop! []
  (doseq [component (:stopped (mount/start))]
    (log/info component "stopped"))
  (shutdown-agents))


(defn start! []
  (doseq [component (:started (mount/start))]
    (log/info component "started"))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop!)))


(defn -main []
  (start!))
