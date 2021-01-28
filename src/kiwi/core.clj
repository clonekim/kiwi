(ns kiwi.core
  (:gen-class)
  (:require [clojure.tools.logging :as log]
            [integrant.core :as ig]
            [compojure.core :refer :all]
            [compojure.route :as rotue]))



(defonce component (atom nil))

(defn start! []
  (reset! component (ig/init config)))


(defn -main []
  (start!))
