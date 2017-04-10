(ns irresponsible.crouton.pathparser-test
  (:require [clojure.test :refer [testing is deftest]])
  (:import [irresponsible.crouton PathParser]))

(deftest pathparser-test
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
      (is (= out (.parse PathParser/INSTANCE in))))))
