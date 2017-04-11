(ns irresponsible.crouton.java-test
  (:require [clojure.test :refer [testing is deftest]])
  (:import [irresponsible.crouton Crouton Predicate]
           [java.util HashMap]
           [clojure.lang ITransientMap IPersistentVector]))

(deftest endpoint-test
  (testing "validation"
    (is (= ::throw
           (try (Crouton/endpoint nil)
                (catch Exception e ::throw)))))
  (testing "success"
    (is (= {:crouton/route ::test} (.match (Crouton/endpoint ::test) [] (transient {}))))
    (is (= {:crouton/route ::test :bar :baz} (.match (Crouton/endpoint ::test) [] (transient {:bar :baz})))))
  (testing "failure"
    (is (nil? (.match (Crouton/endpoint ::test) ["foo"] (transient {}))))))

(deftest slurp-test
  (testing "validation"
    (is (= ::throw
           (try (Crouton/slurp nil)
                (catch Exception e ::throw)))))
  (testing "success"
    (is (= {:crouton/route ::test :crouton/slurp []}
           (.match (Crouton/slurp ::test) [] (transient {}))))
    (is (= {:crouton/route ::test :bar :baz :crouton/slurp ["test"]}
           (.match (Crouton/slurp ::test) ["test"] (transient {:bar :baz}))))))

(deftest placeholder-test
  (testing "validation"
    (is (= ::throw
           (try (Crouton/placeholder nil (Crouton/endpoint ::test))
                (catch Exception e ::throw))))
    (is (= ::throw
           (try (Crouton/placeholder :foo nil)
                (catch Exception e ::throw)))))
  (let [p (Crouton/placeholder :foo (Crouton/endpoint ::test))]
    (testing :success
      (is (= {:crouton/route ::test :foo "test"} (.match p ["test"] (transient {}))))
      (is (= {:crouton/route ::test :a :b :foo "test"} (.match p ["test"] (transient {:a :b})))))
    (testing :failure
      (is (nil? (.match p ["test" "tist"] (transient {}))))
      (is (nil? (.match p [] (transient {})))))))

(deftest regex-test
  (let [test (Crouton/endpoint ::test)]
    (testing "validation"
      (is (= ::throw
             (try (Crouton/regex nil #"" test)
                  (catch Exception e ::throw))))
      (is (= ::throw
             (try (Crouton/clojure :foo nil test)
                  (catch Exception e ::throw))))
      (is (= ::throw
             (try (Crouton/clojure :foo #"" nil)
                  (catch Exception e ::throw)))))
    (let [p (Crouton/regex :foo #"foo" test)]
      (testing :success
        (is (= {:crouton/route ::test :foo "foo"} (.match p ["foo"] (transient {}))))
        (is (= {:crouton/route ::test :a :b :foo "foo"} (.match p ["foo"] (transient {:a :b})))))
      (testing :failure
        (is (nil? (.match p ["bar"] (transient {}))))
        (is (nil? (.match p [] (transient {}))))))))

(deftest lambda-test
  (let [test (Crouton/endpoint ::test)]
    (testing "validation"
      (is (= ::throw
             (try (Crouton/clojure nil :foo test)
                  (catch Exception e ::throw))))
      (is (= ::throw
             (try (Crouton/clojure :foo nil test)
                  (catch Exception e ::throw))))
      (is (= ::throw
             (try (Crouton/clojure :foo :foo nil)
                  (catch Exception e ::throw)))))
    (let [p (Crouton/lambda :foo
                            (reify Predicate
                              (test [_ s]
                                (if (= "foo" s)
                                  nil
                                  s)))
                            test)]
      (testing :success
        (is (= {:crouton/route ::test :foo "test"} (.match p ["test"] (transient {}))))
        (is (= {:crouton/route ::test :a :b :foo "test"} (.match p ["test"] (transient {:a :b})))))
      (testing :failure
        (is (nil? (.match p ["foo"] (transient {}))))
        (is (nil? (.match p [] (transient {}))))))))

(deftest clojure-test
  (let [test (Crouton/endpoint ::test)]
    (testing "validation"
      (is (= ::throw
             (try (Crouton/clojure nil :foo test)
                  (catch Exception e ::throw))))
      (is (= ::throw
             (try (Crouton/clojure :foo nil test)
                  (catch Exception e ::throw))))
      (is (= ::throw
             (try (Crouton/clojure :foo :foo nil)
                  (catch Exception e ::throw)))))
    (let [p (Crouton/clojure :foo #(if (= "foo" %) nil %) test)]
      (testing :success
        (is (= {:crouton/route ::test :foo "test"} (.match p ["test"] (transient {}))))
        (is (= {:crouton/route ::test :a :b :foo "test"} (.match p ["test"] (transient {:a :b})))))
      (testing :failure
        (is (nil? (.match p ["foo"] (transient {}))))
        (is (nil? (.match p [] (transient {}))))))))

(deftest fallback-test
  (let [test (Crouton/endpoint ::test)
        f (Crouton/fallback test (Crouton/slurp ::slurp))]
    (testing "validation"
      (is (= ::throw
             (try (Crouton/fallback nil test)
                  (catch Exception e ::throw))))
      (is (= ::throw
             (try (Crouton/fallback test nil)
                  (catch Exception e ::throw)))))
    (testing "success"
      (is (= {:crouton/route ::test}
             (.match f [] (transient {}))))
      (is (= {:crouton/route ::test :bar :baz}
             (.match f [] (transient {:bar :baz}))))
      (is (= {:crouton/route ::slurp :bar :baz :crouton/slurp ["test"]}
             (.match f ["test"] (transient {:bar :baz}))))
      (is (= {:crouton/route ::slurp :bar :baz :crouton/slurp ["test" "again"]}
             (.match f ["test" "again"] (transient {:bar :baz})))))))

(deftest choice-test
  (let [test (Crouton/endpoint ::test)
        c (Crouton/choice [test (Crouton/slurp ::slurp)])]
    (testing "validation"
      (is (= ::throw
             (try (Crouton/choice nil)
                  (catch Exception e ::throw))))
      (is (= ::throw
             (try (Crouton/choice [])
                  (catch Exception e ::throw)))))
    (testing "success"
      (is (= {:crouton/route ::test :a :b}
             (.match c [] (transient {:a :b}))))
      (is (= {:crouton/route ::slurp :crouton/slurp ["test"] :a :b}
             (.match c ["test"] (transient {:a :b})))))))

(deftest routemap-test
  (testing "validation"
    (is (= ::throw
           (try (Crouton/routemap nil)
                (catch Exception e ::throw)))))
  (let [m (doto (HashMap.)
            (.put "test" (Crouton/endpoint ::test)))
        rm (Crouton/routemap m)]
    (testing "success"
      (is (= {:crouton/route ::test}
             (.match rm ["test"] (transient {}))))
      (is (= {:crouton/route ::test :bar :baz}
             (.match rm ["test"] (transient {:bar :baz})))))
    (testing "failure"
      (is (nil? (.match rm [""] (transient {}))))
      (is (nil? (.match rm ["bar"] (transient {})))))))

;; (deftest integration-test
;;   (let [
;;   (let [e1 (Crouton/endpoint ::e1)
;;         e2 (Crouton/endpoint ::e2)
;;         s  (Crouton/slurp ::slurp)
;;         h  (doto (HashMap.)
;;              (.put "foo" (Crouton/either e1 s))
;;              (.put "bar" e2))
;;         rm (Crouton/routemap h)]
;;     (is (= {:crouton/route ::e1}
;;            (.route rm ["foo"])))
;;     (is (= {:crouton/route ::e1}
;;            (.route rm ["foo"])))
;;     (is (= {:crouton/route ::e1}
;;            (.route rm ["foo"])))    
