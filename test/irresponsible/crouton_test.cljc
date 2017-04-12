(ns irresponsible.crouton-test
  (:require [irresponsible.crouton :as c]
   #?(:clj  [clojure.test :refer [deftest testing is]]
      :cljs [cljs.test :refer-macros [deftest testing is]]))
#?(:clj (:import [irresponsible.crouton Crouton Predicate]
                 [java.util HashMap]
                 [clojure.lang ITransientMap IPersistentVector])))


(deftest parse-path-test
  (doseq [[in out] [["/" []]
                    [""  []]
                    ["foo"   ["foo"]]
                    ["foo/"  ["foo"]]
                    ["/foo"  ["foo"]]
                    ["/foo/" ["foo"]]
                    ["foo/bar"   ["foo" "bar"]]
                    ["foo/bar/"  ["foo" "bar"]]
                    ["/foo/bar"  ["foo" "bar"]]
                    ["/foo/bar/" ["foo" "bar"]]
                    ["foo/bar/baz-123"   ["foo" "bar" "baz-123"]]
                    ["foo/bar/baz-123/"  ["foo" "bar" "baz-123"]]
                    ["/foo/bar/baz-123"  ["foo" "bar" "baz-123"]]
                    ["/foo/bar/baz-123/" ["foo" "bar" "baz-123"]]
                    ["//////foo///////bar///////baz-123///////" ["foo" "bar" "baz-123"]]
                    ]]
    (testing [in out]
      (is (= out (c/parse-path in))))))

(deftest endpoint-test
  (testing "validation"
    (is (= ::throw
           (try (c/make-endpoint nil)
                (catch #?(:clj Exception :cljs :default) e ::throw)))))
  (testing "success"
    (is (= {:crouton/route ::test} (c/match (c/make-endpoint ::test) [] (transient {}))))
    (is (= {:crouton/route ::test :bar :baz} (c/match (c/make-endpoint ::test) [] (transient {:bar :baz})))))
  (testing "failure"
    (is (nil? (c/match (c/make-endpoint ::test) ["foo"] (transient {}))))))

(deftest slurp-test
  (testing "validation"
    (is (= ::throw
           (try (c/make-slurp nil)
                (catch #?(:clj Exception :cljs :default) e ::throw)))))
  (testing "success"
    (is (= {:crouton/route ::test :crouton/slurp []}
           (c/match (c/make-slurp ::test) [] (transient {}))))
    (is (= {:crouton/route ::test :bar :baz :crouton/slurp ["test"]}
           (c/match (c/make-slurp ::test) ["test"] (transient {:bar :baz}))))))

(deftest placeholder-test
  (testing "validation"
    (is (= ::throw
           (try (c/make-placeholder nil (c/make-endpoint ::test))
                (catch #?(:clj Exception :cljs :default) e ::throw))))
    (is (= ::throw
           (try (c/make-placeholder :foo nil)
                (catch #?(:clj Exception :cljs :default) e ::throw)))))
  (let [p (c/make-placeholder :foo (c/make-endpoint ::test))]
    (testing :success
      (is (= {:crouton/route ::test :foo "test"} (c/match p ["test"] (transient {}))))
      (is (= {:crouton/route ::test :a :b :foo "test"} (c/match p ["test"] (transient {:a :b})))))
    (testing :failure
      (is (nil? (c/match p ["test" "tist"] (transient {}))))
      (is (nil? (c/match p [] (transient {})))))))

(deftest regex-test
  (let [test (c/make-endpoint ::test)]
    (testing "validation"
      (is (= ::throw
             (try (c/make-regex nil #"" test)
                  (catch #?(:clj Exception :cljs :default) e ::throw))))
      (is (= ::throw
             (try (c/make-clojure :foo nil test)
                  (catch #?(:clj Exception :cljs :default) e ::throw))))
      (is (= ::throw
             (try (c/make-clojure :foo #"" nil)
                  (catch #?(:clj Exception :cljs :default) e ::throw)))))
    (let [p (c/make-regex :foo #"foo" test)]
      (testing :success
        (is (= {:crouton/route ::test :foo "foo"} (c/match p ["foo"] (transient {}))))
        (is (= {:crouton/route ::test :a :b :foo "foo"} (c/match p ["foo"] (transient {:a :b})))))
      (testing :failure
        (is (nil? (c/match p ["bar"] (transient {}))))
        (is (nil? (c/match p [] (transient {}))))))))

#?
(:clj
 (deftest lambda-test
   (let [test (c/make-endpoint ::test)]
     (testing "validation"
       (is (= ::throw
              (try (Crouton/lambda nil :foo test)
                   (catch Exception e ::throw))))
       (is (= ::throw
              (try (Crouton/lambda :foo nil test)
                   (catch Exception e ::throw))))
       (is (= ::throw
              (try (Crouton/lambda :foo :foo nil)
                   (catch Exception e ::throw)))))
     (let [p (Crouton/lambda :foo
                             (reify Predicate
                               (test [_ s]
                                 (if (= "foo" s)
                                   nil
                                   s)))
                             test)]
       (testing :success
         (is (= {:crouton/route ::test :foo "test"} (c/match p ["test"] (transient {}))))
         (is (= {:crouton/route ::test :a :b :foo "test"} (c/match p ["test"] (transient {:a :b})))))
       (testing :failure
         (is (nil? (c/match p ["foo"] (transient {}))))
         (is (nil? (c/match p [] (transient {})))))))))
 
(deftest clojure-test
  (let [test (c/make-endpoint ::test)]
    (testing "validation"
      (is (= ::throw
             (try (c/make-clojure nil :foo test)
                  (catch #?(:clj Exception :cljs :default) e ::throw))))
      (is (= ::throw
             (try (c/make-clojure :foo nil test)
                  (catch #?(:clj Exception :cljs :default) e ::throw))))
      (is (= ::throw
             (try (c/make-clojure :foo :foo nil)
                  (catch #?(:clj Exception :cljs :default) e ::throw)))))
    (let [p (c/make-clojure :foo #(if (= "foo" %) nil %) test)]
      (testing :success
        (is (= {:crouton/route ::test :foo "test"} (c/match p ["test"] (transient {}))))
        (is (= {:crouton/route ::test :a :b :foo "test"} (c/match p ["test"] (transient {:a :b})))))
      (testing :failure
        (is (nil? (c/match p ["foo"] (transient {}))))
        (is (nil? (c/match p [] (transient {}))))))))

(deftest fallback-test
  (let [test (c/make-endpoint ::test)
        f (c/make-fallback test (c/make-slurp ::slurp))]
    (testing "validation"
      (is (= ::throw
             (try (c/make-fallback nil test)
                  (catch #?(:clj Exception :cljs :default) e ::throw))))
      (is (= ::throw
             (try (c/make-fallback test nil)
                  (catch #?(:clj Exception :cljs :default) e ::throw)))))
    (testing "success"
      (is (= {:crouton/route ::test}
             (c/match f [] (transient {}))))
      (is (= {:crouton/route ::test :bar :baz}
             (c/match f [] (transient {:bar :baz}))))
      (is (= {:crouton/route ::slurp :bar :baz :crouton/slurp ["test"]}
             (c/match f ["test"] (transient {:bar :baz}))))
      (is (= {:crouton/route ::slurp :bar :baz :crouton/slurp ["test" "again"]}
             (c/match f ["test" "again"] (transient {:bar :baz})))))))

(deftest choice-test
  (let [test (c/make-endpoint ::test)
        c (c/make-choice [test (c/make-slurp ::slurp)])]
    (testing "validation"
      (is (= ::throw
             (try (c/make-choice nil)
                  (catch #?(:clj Exception :cljs :default) e ::throw))))
      (is (= ::throw
             (try (c/make-choice [])
                  (catch #?(:clj Exception :cljs :default) e ::throw)))))
    (testing "success"
      (is (= {:crouton/route ::test :a :b}
             (c/match c [] (transient {:a :b}))))
      (is (= {:crouton/route ::slurp :crouton/slurp ["test"] :a :b}
             (c/match c ["test"] (transient {:a :b})))))))

(deftest routemap-test
  (testing "validation"
    (is (= ::throw
           (try (c/make-routemap nil)
                (catch #?(:clj Exception :cljs :default) e ::throw)))))
  (let [rm (c/make-routemap {"test" (c/make-endpoint ::test)})]
    (testing "success"
      (is (= {:crouton/route ::test}
             (c/match rm ["test"] (transient {}))))
      (is (= {:crouton/route ::test :bar :baz}
             (c/match rm ["test"] (transient {:bar :baz})))))
    (testing "failure"
      (is (nil? (c/match rm [""] (transient {}))))
      (is (nil? (c/match rm ["bar"] (transient {})))))))

(deftest integration-test
  (let [e1 (c/make-endpoint ::e1)
        e2 (c/make-endpoint ::e2)
        s  (c/make-slurp ::slurp)
        rm (c/make-routemap {"foo" (c/make-fallback e1 s)
                             "bar" e2})]
    (is (= {:crouton/route ::e1}
           (c/match rm ["foo"] (transient {}))))
    (is (= {:crouton/route ::slurp :crouton/slurp ["bar"]}
           (c/match rm ["foo" "bar"] (transient {}))))
    (is (= {:crouton/route ::e2}
           (c/match rm ["bar"] (transient {}))))
    (let [ex-routes {:/       :home
                     "users"  {(c/? :name #"[a-z]+") {(c/? :id :crouton/pos-int) :user-profile}}
                     "login"  :login
                     "admin"  {:& :admin}}
          r-fn (try (c/compile ex-routes)
                    (catch #?(:clj Exception :cljs :default) e
                      (prn :fail e)))]
      (is (= {:crouton/route :home} (r-fn "/")))
      (is (= {:crouton/route :user-profile :name "irresponsible" :id 123} (r-fn "/users/irresponsible/123")))
      (is (= {:crouton/route :login} (r-fn "/login")))
      (is (= {:crouton/route :admin :crouton/slurp ["foo"]} (r-fn "/admin/foo"))))))

