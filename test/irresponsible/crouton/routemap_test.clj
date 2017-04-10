(ns irresponsible.crouton.routemap-test
  (:require [clojure.test :refer [testing is deftest]])
  (:import [irresponsible.crouton Endpoint RouteMap]
           [java.util HashMap]))

(deftest routemap-test
  (testing "validation"
    (is (= ::throw
           (try (RouteMap. nil)
                (catch Exception e ::throw)))))
  (let [m (doto (HashMap.)
            (.put "test" (Endpoint. ::test)))
        rm (RouteMap. m)]
    (testing "success"
      (is (= {:crouton/route ::test}
             (.match rm ["test"] (transient {}))))
      (is (= {:crouton/route ::test :bar :baz}
             (.match rm ["test"] (transient {:bar :baz})))))
    (testing "failure"
      (is (nil? (.match rm [""] (transient {}))))
      (is (nil? (.match rm ["bar"] (transient {})))))))
      
