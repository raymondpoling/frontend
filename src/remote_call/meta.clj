(ns remote-call.meta
  (:require [diehard.core :as dh]
            [diehard.circuit-breaker :refer [state]]
            [cheshire.core :refer :all]
            [common-lib.core :as clc]
            [clj-http.client :as client]))

(dh/defcircuitbreaker ckt-brkr {:failure-threshold-ratio [8 10]
                              :delay-ms 1000})

(defn get-all-series [host]
  (clc/log-on-error {:status "failed"}
    (:results (:body (client/get (str "http://" host "/series") {:as :json})))))

(defn bulk-update-series [host series update]
  (clc/log-on-error {:status "failed"}
    (let [url (str "http://" host "/series/" series)]
      (println "url: " url)
      (:body (client/put url
              {:as :json
              :body (generate-string update) :headers {:content-type "application/json"}})))))

(defn get-meta-by-catalog-id [host id]
  (clc/log-on-error {:status "failed" :message "Could not find item in catalog"}
    (let [url (str "http://" host "/catalog-id/" id)]
      (:body (client/get url {:as :json})))))
