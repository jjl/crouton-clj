(set-env!
  :project 'irresponsible/crouton
  :version "0.1.0"
  :resource-paths #{"clojure/src"}
  :source-paths #{"clojure/src" "kotlin/src"}
  :dependencies '[[org.clojure/clojure "1.9.0-alpha16"  :scope "provided"]
                  [org.clojure/spec.alpha "0.1.108"]
                  [org.clojure/core.match "0.3.0-alpha4"]
                  [irresponsible/spectra  "0.1.0"]
                  [io.djy/boot-kotlin          "0.2.1"   :scope "test"]
                  [com.cognitect/transit-clj   "0.8.300" :scope "test"] ; only for benchmark comparison
                  [org.clojure/clojurescript   "1.9.542" :scope "test"]
                  [adzerk/boot-cljs            "2.0.0"   :scope "test"]
                  [criterium                   "0.4.4"   :scope "test"]
                  [irresponsible/gadget        "0.2.0"   :scope "test"]
                  [adzerk/boot-test            "1.1.0"   :scope "test"]
                  [crisptrutski/boot-cljs-test "0.3.0"   :scope "test"]])

(require '[adzerk.boot-cljs :refer [cljs]]
         '[adzerk.boot-test :as t]
         '[io.djy.boot-kotlin :refer [kotlinc]]
         '[crisptrutski.boot-cljs-test :refer [test-cljs]])

(task-options!
  pom {:project (get-env :project)
       :version (get-env :version)
       :description "url routing to the overkill"
       :url "https://github.com/irresponsible/crouton/"
       :scm {:url "https://github.com/irresponsible/crouton"}
       :license {"MIT" "https://en.wikipedia.org/MIT_License"}}
  target  {:dir #{"target"}})

(deftask testing []
  (set-env! :source-paths   #(conj % "clojure/test" "kotlin/test")
            :resource-paths #(conj % "clojure/test"))
  identity)

(deftask clj-tests []
  (comp (testing) (kotlinc) (speak) (t/test)))

(deftask cljs-tests []
  (comp (testing) (speak) (test-cljs)))

(deftask test []
  (comp (testing) (speak) (kotlinc) (t/test) (test-cljs)))

(deftask autotest []
  (comp (testing) (watch) (test)))

(deftask make-jar []
  (comp (kotlinc) (pom) (jar)))

(deftask travis []
  (testing)
  (comp (t/test)))
