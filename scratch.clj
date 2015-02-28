(ns test (:require [clj-http.client :as http] [clj-time.local :as local]))

(defn url
  ([route]
   (str "http://localhost:8080" route))
  ([route params]
   (str "http://localhost:8080" route "?" (ring.util.codec/form-encode params))))

(def post-id (:body (http/post (url "/messages" {:user-id "daines" :text "My message goes here!"}))))
(http/put (url (str "/messages/" post-id) {:post-id post-id :user-id "michael"}))
(http/put (url (str "/messages/" post-id) {:post-id post-id :user-id "joe"}))
(http/post (url "/messages" {:user-id "daines" :text "My second message."}))
(def second-id (:body (http/post (url "/messages" {:user-id "daines" :text "Third time's a charm."}))))
(http/put (url (str "/messages/" second-id) {:post-id second-id :user-id "joe"}))
(http/post (url "/messages" {:user-id "michael" :text "What you talkin' 'bout?"}))
;; With location
(http/post (url "/messages" {:user-id "rick" :text "We're no strangers to love."
                           :latitude 50.0 :longitude 179.0}))
(http/post (url "/messages" {:user-id "rick" :text "You know the rules and so do I."
                           :latitude 90.0 :longitude 20.0}))
(http/post (url "/messages" {:user-id "rick" :text "A full commitment's what I'm thinking of."
                           :latitude 49.0 :longitude -179.0}))
(http/post (url "/messages" {:user-id "rick" :text "You wouldn't get this from any other guy."
                           :latitude 48.0 :longitude 20.0}))
(http/post (url "/messages" {:user-id "rick" :text "I just wanna tell you how I'm feeling."
                           :latitude -50.0 :longitude -179.0}))
(http/post (url "/messages" {:user-id "rick" :text "Gotta make you understand."
                           :latitude -44.0 :longitude 179.0}))

(http/get (url "/messages"))

(http/get (url (str "/messages/" post-id)))

(def bogus-id "14-dead-beef")
(http/put (url (str "/messages/" bogus-id) {:post-id bogus-id :user-id "michael"}))

(:body (http/get (url "/messages")))

(:body (http/get (url "/messages/latest" {:limit 10 :scroll-id 0})))

(:body (http/get (url "/messages/latest" {:limit 2 :scroll-id 2})))

(:body (http/get (url "/messages/latest")))
(:body (http/get (url "/messages/latest" {:scroll-id 0})))
(:body (http/get (url "/messages/latest" {:limit 2})))
(:body (http/get (url "/messages/latest" {:limit 2 :scroll-id 2})))

(:body (http/get (url "/messages/popular")))
(:body (http/get (url "/messages/popular" {:limit 2})))
(:body (http/get (url "/messages/popular" {:scroll-id 2})))
(:body (http/get (url "/messages/popular" {:limit 2 :scroll-id 2})))

(http/get (url "/messages/nearby" {:latitude 0.0 :longitude 0.0}))

(filter #(and (contains? % :a) (contains? % :b))
        {{:a "a" :b "b"} {:a "b"} {:b "a"} {:c "c"}})
