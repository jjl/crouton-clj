(refer-clojure :exclude '[load])
(require '[irresponsible.crouton :as c] :reload)
(require '[cognitect.transit :as tr]
         '[clojure.java.io :refer [input-stream output-stream]])
(import [java.io File])
(import '[irresponsible.crouton Crouton Endpoint Placeholder RegexPH ClojurePH RouteMap Predicate Slurp]
        '[java.util HashMap])
(use '[criterium.core :exclude [warn]])

(set! *warn-on-reflection* true)

(defn quick []
  (let [path "//////foo///////bar///////baz-123///////"
        ^Endpoint e (Crouton/endpoint ::test)
        ^RegexPH reg (Crouton/regex ::regex #"foo" e)
        ^Placeholder pl (Crouton/placeholder ::place e)
        ^RouteMap rm (Crouton/routemap (doto (HashMap.) (.put "test" e)))
        ^Slurp s (Crouton/slurp ::test)]
    {:parse    (quick-benchmark (c/parse-path path) {})
     :endpoint (quick-benchmark (.match e [] {}) {})
     :routemap (quick-benchmark (.match rm ["test"] {}) {})
     :slurp    (quick-benchmark (.match s ["test" "and" "more"] {}) {})
     :regex    (quick-benchmark (.match reg ["foo"] {}) {})
     :place    (quick-benchmark (.match pl ["foo"] {}) {})}))

(defn slow []
  (let [path "//////foo///////bar///////baz-123///////"
        ^Endpoint e (Crouton/endpoint ::test)
        ^RegexPH reg (Crouton/regex ::regex #"foo" e)
        ^Placeholder pl (Crouton/placeholder ::place e)
        ^RouteMap rm (Crouton/routemap (doto (HashMap.) (.put "test" e)))
        ^Slurp s (Crouton/slurp ::test)]
    {:parse    (benchmark (c/parse-path path) {})
     :endpoint (benchmark (.match e [] {}) {})
     :routemap (benchmark (.match rm ["test"] {}) {})
     :slurp    (benchmark (.match s ["test" "and" "more"] {}) {})
     :regex    (benchmark (.match reg ["foo"] {}) {})
     :place    (benchmark (.match pl ["foo"] {}) {})}))

(defn save [data ^String filename]
  (let [out (output-stream (File. filename))
        wr (tr/writer out :json)]
    (tr/write wr data)))

(defn load [^String filename]
  (let [in (input-stream (File. filename))
        r (tr/reader in :json)]
    (tr/read r)))

(def rs (slow))

(save rs "java-bench.json")

