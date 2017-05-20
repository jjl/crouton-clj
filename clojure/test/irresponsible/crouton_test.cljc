(ns irresponsible.crouton-test
  (:require [irresponsible.crouton :as c]
   #?(:clj  [clojure.test :refer [deftest testing is]]
      :cljs [cljs.test :refer-macros [deftest testing is]]))
#?(:clj (:import [irresponsible.crouton Crouton Predicate]
                 [java.util HashMap]
                 [clojure.lang IPersistentMap IPersistentVector])))


(deftest assoc-once-test
  (let [assoc-once @#'c/assoc-once]
    (is (= {:a :b} (assoc-once {} :a :b)))
    (is (= {:a :b :c :d} (assoc-once {:c :d} :a :b)))
    (is (= ::throw
           (try
             (assoc-once {:a :b} :a :b)
             (catch #?(:clj Throwable :cljs :default) e ::throw))))))

(deftest regex?-test
  (is (c/regex? #""))
  (doseq [t ["" 123 1.23 [] {} ()]]
    (testing t
      (is (not (c/regex? t))))))

(deftest place?-test
  (is (c/place? (c/? :foo)))
  (is (c/place? (c/? :foo #"")))
  (doseq [t ["" #"" 123 1.23 [] {} ()]]
    (is (not (c/place? t)))))

(deftest iroute?-test
  (is (c/iroute? (c/make-endpoint :foo)))
  (doseq [t ["" #"" 123 1.23 [] {} ()]]
    (is (not (c/iroute? t)))))

(deftest print-segment-test)
(deftest print-route-test)

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

(deftest route-factories-test
  (let [routes [(c/make-endpoint :foo)
                (c/make-slurp :foo)
                (c/make-placeholder :foo (c/make-endpoint ::test))
                (c/make-regex :foo #"foo" (c/make-endpoint ::test))
                (c/make-clojure :foo cons (c/make-endpoint ::test))
                (c/make-fallback (c/make-endpoint ::test) (c/make-endpoint ::test))
                (c/make-choice [(c/make-endpoint ::test) (c/make-endpoint ::test)])
                (c/make-routemap {"test" (c/make-endpoint ::test)})]]
    (is (every? c/iroute? routes))))

(deftest compile-key-type-test
  (let [ckt @#'c/compile-key-type]
    (doseq [i [:/ :&]]
      (is (= i (ckt i))))
    (is (= :str (ckt "")))
    (is (= :ph (ckt (c/? :foo))))
    (is (= ::throw
           (try
             (ckt :foo)
             (catch #?(:clj Throwable :cljs :default) e ::throw))))))

(deftest compile-group-keys-test
  (let [cgk @#'c/compile-group-keys]
    ))
(deftest rev-group-test
  (let [rg @#'c/rev-group]
    ))
(deftest compile-strings-test)
(deftest rev-compile-strings-test
  (let [rcs @#'c/rev-compile-strings]
    ))
#?
(:cljs
 (deftest parse-pos-int-test))
(deftest precanned-test)
(deftest check-places-test)
(deftest compile-place-test)
(deftest compile-places-test)
(deftest rev-compile-places-test)
(deftest compile-map-test)
(deftest rev-compile-end-test
  (let [rce @#'c/rev-compile-end]
    ))
(deftest rev-compile-slurp-test
  (let [rcs @#'c/rev-compile-slurp]
    ))
(deftest rev-compile-map-test
  (let [rcm @#'c/rev-compile-map]
    ))
(deftest compile-route-test
  (let [cr @#'c/compile-route]
    ))
(deftest rev-compile-route-test
  (let [rcr @#'c/rev-compile-route]
    ))
(deftest compile-test)

(deftest endpoint-test
  (testing :validation
    (is (= ::throw
           (try (c/make-endpoint nil)
                (catch #?(:clj Throwable :cljs :default) e ::throw)))))
  (testing :success
    (is (= {:crouton/route ::test} (c/match-route (c/make-endpoint ::test) [] {})))
    (is (= {:crouton/route ::test :bar :baz} (c/match-route (c/make-endpoint ::test) [] {:bar :baz}))))
  (testing :failure
    (is (nil? (c/match-route (c/make-endpoint ::test) ["foo"] {})))))

(deftest slurp-test
  (testing :validation
    (is (= ::throw
           (try (c/make-slurp nil)
                (catch #?(:clj Throwable :cljs :default) e ::throw)))))
  (testing :success
    (is (= {:crouton/route ::test :crouton/slurp []}
           (c/match-route (c/make-slurp ::test) [] {})))
    (is (= {:crouton/route ::test :bar :baz :crouton/slurp ["test"]}
           (c/match-route (c/make-slurp ::test) ["test"] {:bar :baz})))))

(deftest placeholder-test
  (testing :validation
    (is (= ::throw
           (try (c/make-placeholder nil (c/make-endpoint ::test))
                (catch #?(:clj Throwable :cljs :default) e ::throw))))
    (is (= ::throw
           (try (c/make-placeholder :foo nil)
                (catch #?(:clj Throwable :cljs :default) e ::throw)))))
  (let [p (c/make-placeholder :foo (c/make-endpoint ::test))]
    (testing :success
      (is (= {:crouton/route ::test :foo "test"} (c/match-route p ["test"] {})))
      (is (= {:crouton/route ::test :a :b :foo "test"} (c/match-route p ["test"] {:a :b}))))
    (testing :failure
      (is (nil? (c/match-route p ["test" "tist"] {})))
      (is (nil? (c/match-route p [] {}))))))

(deftest regex-test
  (let [test (c/make-endpoint ::test)]
    (testing :validation
      (is (= ::throw
             (try (c/make-regex nil #"" test)
                  (catch #?(:clj Throwable :cljs :default) e ::throw))))
      (is (= ::throw
             (try (c/make-regex :foo nil test)
                  (catch #?(:clj Throwable :cljs :default) e ::throw))))
      (is (= ::throw
             (try (c/make-regex :foo #"" nil)
                  (catch #?(:clj Throwable :cljs :default) e ::throw)))))
    (let [p (c/make-regex :foo #"foo" test)]
      (testing :success
        (is (= {:crouton/route ::test :foo "foo"} (c/match-route p ["foo"] {})))
        (is (= {:crouton/route ::test :a :b :foo "foo"} (c/match-route p ["foo"] {:a :b}))))
      (testing :failure
        (is (nil? (c/match-route p ["bar"] {})))
        (is (nil? (c/match-route p [] {})))))))

#?
(:clj
 (deftest lambda-test
   (let [test (c/make-endpoint ::test)]
     (testing :validation
       (is (= ::throw
              (try (.lambda Crouton/INSTANCE nil :foo test)
                   (catch Throwable e ::throw))))
       (is (= ::throw
              (try (.lambda Crouton/INSTANCE :foo nil test)
                   (catch Throwable e ::throw))))
       (is (= ::throw
              (try (.lambda Crouton/INSTANCE :foo :foo nil)
                   (catch Throwable e ::throw)))))
     (let [p (.lambda Crouton/INSTANCE :foo
                             (reify Predicate
                               (test [_ s]
                                 (if (= "foo" s)
                                   nil
                                   s)))
                             test)]
       (testing :success
         (is (= {:crouton/route ::test :foo "test"} (c/match-route p ["test"] {})))
         (is (= {:crouton/route ::test :a :b :foo "test"} (c/match-route p ["test"] {:a :b}))))
       (testing :failure
         (is (nil? (c/match-route p ["foo"] {})))
         (is (nil? (c/match-route p [] {}))))))))
 
(deftest clojure-test
  (let [test (c/make-endpoint ::test)]
    (testing :validation
      (is (= ::throw
             (try (c/make-clojure nil :foo test)
                  (catch #?(:clj Throwable :cljs :default) e ::throw))))
      (is (= ::throw
             (try (c/make-clojure :foo nil test)
                  (catch #?(:clj Throwable :cljs :default) e ::throw))))
      (is (= ::throw
             (try (c/make-clojure :foo :foo nil)
                  (catch #?(:clj Throwable :cljs :default) e ::throw)))))
    (let [p (c/make-clojure :foo #(if (= "foo" %) nil %) test)]
      (testing :success
        (is (= {:crouton/route ::test :foo "test"} (c/match-route p ["test"] {})))
        (is (= {:crouton/route ::test :a :b :foo "test"} (c/match-route p ["test"] {:a :b}))))
      (testing :failure
        (is (nil? (c/match-route p ["foo"] {})))
        (is (nil? (c/match-route p [] {})))))))

(deftest fallback-test
  (let [test (c/make-endpoint ::test)
        f (c/make-fallback test (c/make-slurp ::slurp))]
    (testing :validation
      (is (= ::throw
             (try (c/make-fallback nil test)
                  (catch #?(:clj Throwable :cljs :default) e ::throw))))
      (is (= ::throw
             (try (c/make-fallback test nil)
                  (catch #?(:clj Throwable :cljs :default) e ::throw)))))
    (testing :success
      (is (= {:crouton/route ::test}
             (c/match-route f [] {})))
      (is (= {:crouton/route ::test :bar :baz}
             (c/match-route f [] {:bar :baz})))
      (is (= {:crouton/route ::slurp :bar :baz :crouton/slurp ["test"]}
             (c/match-route f ["test"] {:bar :baz})))
      (is (= {:crouton/route ::slurp :bar :baz :crouton/slurp ["test" "again"]}
             (c/match-route f ["test" "again"] {:bar :baz}))))))

(deftest choice-test
  (let [test (c/make-endpoint ::test)
        c (c/make-choice [test (c/make-slurp ::slurp)])]
    (testing :validation
      (is (= ::throw
             (try (c/make-choice nil)
                  (catch #?(:clj Throwable :cljs :default) e ::throw))))
      (is (= ::throw
             (try (c/make-choice [])
                  (catch #?(:clj Throwable :cljs :default) e ::throw)))))
    (testing :success
      (is (= {:crouton/route ::test :a :b}
             (c/match-route c [] {:a :b})))
      (is (= {:crouton/route ::slurp :crouton/slurp ["test"] :a :b}
             (c/match-route c ["test"] {:a :b}))))))

(deftest routemap-test
  (testing :validation
    (is (= ::throw
           (try (c/make-routemap nil)
                (catch #?(:clj Throwable :cljs :default) e ::throw)))))
  (let [rm (c/make-routemap {"test" (c/make-endpoint ::test)})]
    (testing :success
      (is (= {:crouton/route ::test}
             (c/match-route rm ["test"] {})))
      (is (= {:crouton/route ::test :bar :baz}
             (c/match-route rm ["test"] {:bar :baz}))))
    (testing :failure
      (is (nil? (c/match-route rm [""] {})))
      (is (nil? (c/match-route rm ["bar"] {}))))))

(deftest integration-test
  (let [e1 (c/make-endpoint ::e1)
        e2 (c/make-endpoint ::e2)
        s  (c/make-slurp ::slurp)
        rm (c/make-routemap {"foo" (c/make-fallback e1 s)
                             "bar" e2})]
    (is (= {:crouton/route ::e1}
           (c/match-route rm ["foo"] {})))
    (is (= {:crouton/route ::slurp :crouton/slurp ["bar"]}
           (c/match-route rm ["foo" "bar"] {})))
    (is (= {:crouton/route ::e2}
           (c/match-route rm ["bar"] {})))
    (let [ex-routes {:/       :home
                     "users"  {(c/? :name #"[a-z]+") {(c/? :id :crouton/pos-int) :user-profile}}
                     "login"  :login
                     "admin"  {:& :admin}}
          router (try (c/compile ex-routes)
                    (catch #?(:clj Throwable :cljs :default) e
                        (prn :fail e)))
          o1 {:crouton/route :home}
          o2 {:crouton/route :user-profile :name "irresponsible" :id 123}
          o3 {:crouton/route :login}
          o4 {:crouton/route :admin :crouton/slurp ["foo"]}]
      (is router)
      (is (= o1 (c/route router "/")))
      (is (= o2 (c/route router "/users/irresponsible/123")))
      (is (= o3 (c/route router "/login")))
      (is (= o4 (c/route router "/admin/foo"))))))
      ;; (is (= "/" (c/unroute router o1)))
      ;; (is (= "/users/irresponsible/123" (c/unroute router o2)))
      ;; (is (= "/login" (c/unroute router o3)))
      ;; (is (= "/admin" (c/unroute router o4))))))

