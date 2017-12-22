(ns my-exercise.search
  (:require [hiccup.page :refer [html5]]
            [ajax.core :refer [GET json-response-format]]
            [clojure.core.async :refer [chan <!! >!!]]
            [my-exercise.home :refer [header]]))

; CONSTANTS

(def base-ocd-id "ocd-division/country:us/state:")

(def api-endpoint "https://api.turbovote.org/elections/upcoming")

(def mock-data 
  (list
    {:district-divisions 
     [{:ocd-id "ocd-division/country:us/state:va/place:chilhowie"
       :voter-registration-authority-level "county"}]}))

; UTILITIES

(defn get-ocd-ids [{:keys [city state]}]
  "generate state and place (i.e. city) OCD IDs"
  (let [state-id (str base-ocd-id (clojure.string/lower-case state))
        city-id (str state-id "/place:" (clojure.string/lower-case city))]
     {:state-id state-id :city-id city-id}))

; Calls to TurboVote API

(defn make-handler [channel]
  "Write API response or error to channel"
  (fn [[ok response]]
    ;don't have API call working yet, use mock data for now
    (>!! channel mock-data)))
;  (fn [[ok response]]
;    (if ok
;      (>!! channel response)
;      (>!! channel {:error true}))))

(defn make-api-call [ocd-ids channel]
  "Make request to upcoming elections endpoint,
  and write response to provided channel"
  (let [query-params (clojure.string/join "," (vals ocd-ids))]
    (GET api-endpoint
         {:params {:district-divisions query-params}
          :timeout 10000
          :response-format (json-response-format {:keywords? true})
          :handler (make-handler channel)})))


(defn fetch-elections [ocd-ids]
  "Make request to upcoming elections API endpoint,
  and return result"
  (let [channel (chan)]
    (make-api-call ocd-ids channel)
    (<!! channel)))

(defn search-results [{:keys [params]}]
  "Fetch list of upcoming elections for provided address"
  ;NOTE: probably better to load static page right away,
  ;then make AJAX call to fetch election results,
  ;for quicker initial load time
  (let [ocd-ids (get-ocd-ids params)
        elections (fetch-elections ocd-ids)]
    [:p "TODO: election results here"]))

(defn results-page [request]
  (html5
    (header request)
    (search-results request)))

(defn error-page [request]
  (html5
    (header request)
    ;TODO provide more detailed feedback
    [:p "Your search was invalid, try again."]))

(defn valid? [request]
  "TODO: validate address."
  true)

(defn page [request]
  (if (valid? request)
    (results-page request)
    (error-page request)))
