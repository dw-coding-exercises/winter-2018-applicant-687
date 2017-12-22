(ns my-exercise.search-test
  (:require [clojure.test :refer :all]
            [my-exercise.search :refer :all]))

(deftest get-ocd-ids-test
  (testing "valid state and city ids are formed"
    (let [expected-ocd-ids {:state-id "ocd-division/country:us/state:fo"
                            :city-id "ocd-division/country:us/state:fo/place:foo"}]
      (is (= expected-ocd-ids
             (get-ocd-ids {:city "foo" :state "FO"}))))))