(ns social.service-test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]
            [social.service :as service]))

(def service
  (::bootstrap/service-fn (bootstrap/create-servlet service/service)))

(deftest home-page-test
  (is (=
       "Message!"
       (:body (response-for service :get "/"))))
  (is (=
       {"Content-Type" "text/plain"
        "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
        "X-Frame-Options" "DENY"
        "X-Content-Type-Options" "nosniff"
        "X-XSS-Protection" "1; mode=block"}
       (:headers (response-for service :get "/")))))


(deftest create-message-test
  (is (=
       {"Content-Type" "text/plain"}
       (:headers (response-for service :post "/messages?user-id=daines&text=I got something to say!")))))
