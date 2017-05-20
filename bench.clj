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
        ^Endpoint e (.endpoint Crouton/INSTANCE ::test)
        ^RegexPH reg (.regex Crouton/INSTANCE ::regex #"foo" e)
        ^Placeholder pl (.placeholder Crouton/INSTANCE ::place e)
        ^RouteMap rm (.routemap Crouton/INSTANCE (doto (HashMap.) (.put "test" e)))
        ^Slurp s (.slurp Crouton/INSTANCE ::test)]
    {:parse    (quick-benchmark (c/parse-path path) {})
     :endpoint (quick-benchmark (.match e [] {}) {})
     :routemap (quick-benchmark (.match rm ["test"] {}) {})
     :slurp    (quick-benchmark (.match s ["test" "and" "more"] {}) {})
     :regex    (quick-benchmark (.match reg ["foo"] {}) {})
     :place    (quick-benchmark (.match pl ["foo"] {}) {})}))

(defn slow []
  (let [path "//////foo///////bar///////baz-123///////"
        ^Endpoint e (.endpoint Crouton/INSTANCE ::test)
        ^RegexPH reg (.regex Crouton/INSTANCE ::regex #"foo" e)
        ^Placeholder pl (.placeholder Crouton/INSTANCE ::place e)
        ^RouteMap rm (.routemap Crouton/INSTANCE (doto (HashMap.) (.put "test" e)))
        ^Slurp s (.slurp Crouton/INSTANCE ::test)]
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

;; (def rs (slow))
(def java (load "java-bench.json"))
(def kotlin rs)

(keys (:place java))
(doseq [k (keys kotlin)
        [name items] [[:java java]
                      [:kotlin kotlin]]]
  (prn name k)
  (report-result (items k)))
        
;(save rs "java-bench.json")

