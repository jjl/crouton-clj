(ns irresponsible.crouton.endpoint-test
  (:require [clojure.test :refer [testing is deftest]])
  (:import [irresponsible.crouton Endpoint]))

(deftest endpoint-test
  (testing "validation"
    (is (= ::throw
           (try (Endpoint. nil)
                (catch Exception e ::throw)))))
  (testing "success"
    (is (= {:crouton/route ::test} (.match (Endpoint. ::test) [] (transient {}))))
    (is (= {:crouton/route ::test :bar :baz} (.match (Endpoint. ::test) [] (transient {:bar :baz})))))
  (testing "failure"
    (is (nil? (.match (Endpoint. ::test) ["foo"] (transient {}))))))
