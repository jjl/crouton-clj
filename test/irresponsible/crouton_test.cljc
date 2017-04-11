(ns irresponsible.crouton-test
  (:require [irresponsible.crouton :as c]
   #?(:clj  [clojure.test :refer [deftest testing is]]
      :cljs [cljs.test :refer-macros [deftest testing is]])))

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

