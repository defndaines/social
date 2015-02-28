(ns social.service
  (:require [io.pedestal.http :as bootstrap]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.route.definition :refer [defroutes]]
            [ring.util.response :as ring-resp]
            [clj-time.local :as local]))

;; NEVER do this for realz!
(def db (atom {}))

(defn find-message
  [message-id]
  (get @db message-id))

(defn param->message
  [query-params]
  (let [message (assoc query-params :message-id (str (java.util.UUID/randomUUID))
                                  :timestamp (local/local-now))]
    (swap! db assoc (:message-id message) message)
    message))

(defn create-message
  [request]
  ;; text (required), user_id (required),  parent_message_id (optional), latitude (optional), longitude (optional)
  (let [message (-> request :query-params param->message)]
    (ring-resp/response (:message-id message))))

(defn home-page
  [request]
  (ring-resp/response "Message!"))

(defn all-messages
  [request]
  (ring-resp/response (vals @db)))

(defn show-message
  [request]
  (let [message-id (-> request :path-params :id)
        message (find-message message-id)]
    (ring-resp/response message)))

(defn like-message
  ;; message_id (required), user_id (required)
  [request]
  (let [message-id (-> request :query-params :message-id)
        user-id (-> request :query-params :user-id)]
    (if-let [message (find-message message-id)]
      (do
        (if-let [likes (:likes message)]
          (swap! db assoc-in [message-id :likes] (conj likes user-id))
          (swap! db assoc-in [message-id] (assoc message :likes #{user-id})))
        (ring-resp/response ""))
      (ring-resp/status (ring-resp/response "Unrecognized message") 400))))

(defn add-reply
  [message request]
  )

(defn reply-to-message
  [request]
  (let [message-id (-> request :path-params :id)]
    (if-let [message (find-message message-id)]
      (add-reply message request)
      (ring-resp/status (ring-resp/response "Unrecognized message") 400))))

(defn latest
  [limit offset]
  (take limit
        (drop offset
              (sort-by :timestamp #(compare %2 %1) (vals @db)))))

(defn popular
  [limit offset]
  (take limit
        (drop offset
              (sort-by #(count (:likes %)) #(compare %2 %1) (vals @db)))))

(defn ->feed
  [message]
  {:message-id (:message-id message)
   :text (:text message)
   :parent-message-id (:parent-message-id message)
   :likes (count (:likes message))})

(defn feed
  [offset s]
   {:scroll-id offset
    :messages (map ->feed s)})

(defn show-latest
  [request]
  (let [limit (Integer/valueOf (get (:query-params request) :limit 10))
        offset (Integer/valueOf (get (:query-params request) :scroll-id 0))]
    ;; FIXME For some reason, this is failing if both aren't defined in request.
    (ring-resp/response (feed offset (latest limit offset)))))

(defn show-popular
  [request]
  (let [limit (Integer/valueOf (get (:query-params request) :limit 10))
        offset (Integer/valueOf (get (:query-params request) :scroll-id 0))]
    (ring-resp/response (feed offset (popular limit offset)))))

(defn show-nearby
  [request]
  (ring-resp/response "I should show you the nearby messages."))

(defn show-replies
  [request]
  (ring-resp/response "I should show you the replies to the message you asked for."))

(defroutes routes
  [[["/" {:get home-page}
     ["/messages" {:get all-messages
                 ;; FIXME Not validating that id and text are present.
                 :message create-message}
      ["/latest" 
       ^:constraints {:limit #"^\d+$"
                      :scroll-id #"^\d+$"}
       {:get show-latest}]
      ["/popular" {:get show-popular}]
      ["/nearby"
       ; ^:constraints {:latitude ;; restrict to -/+90.0 ??
       ;                :longitude ;; restrict to -/+180.0 ?? }
       {:get show-nearby}]
      ["/:id"
       {:get show-message
        ; :post reply-to-message
        :put like-message}
       ["/replies"
        {:get show-replies}]]]]]])

(def service {:env :prod
              ::bootstrap/routes routes
              ::bootstrap/resource-path "/public"
              ::bootstrap/type :jetty
              ::bootstrap/port 8080})
