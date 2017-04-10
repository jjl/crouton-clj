(ns irresponsible.crouton.placeholder-test
  (:require [clojure.test :refer [testing is deftest]])
  (:import [irresponsible.crouton Crouton Endpoint Placeholder Route]
           [clojure.lang ITransientMap IPersistentVector]))

(deftest placeholder-test
  (let [p (Placeholder. :foo (Endpoint. ::test))
        ^ITransientMap t1 (transient {})
        ^ITransientMap t2 (transient {:a :b})
        ^IPersistentVector e []]
    (prn :route? (instance? Route p)
         :persistent? (instance? IPersistentVector e)
         :transient? (instance? ITransientMap t1))
    (is (= {:crouton/route ::test} (.match p e t1)))
    (is (= {:crouton/route ::test :a :b} (.match p e t2)))))
