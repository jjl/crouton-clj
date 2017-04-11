(set-env!
  :project 'irresponsible/crouton
  :version "0.1.0"
  :resource-paths #{"src"}
  :source-paths #{"src" "java"}
  :dependencies '[[org.clojure/clojure         "1.9.0-alpha15"  :scope "provided"]
                  [org.clojure/clojurescript   "1.9.456"        :scope "test"]
                  [adzerk/boot-cljs            "1.7.228-1"      :scope "test"]
                  [criterium                   "0.4.4"          :scope "test"]
                  [irresponsible/gadget        "0.2.0"          :scope "test"]
                  [adzerk/boot-test            "1.1.0"          :scope "test"]
                  [crisptrutski/boot-cljs-test "0.2.2-SNAPSHOT" :scope "test"]])

(require '[adzerk.boot-cljs :refer [cljs]]
         '[adzerk.boot-test :as t]
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
  (set-env! :source-paths   #(conj % "test")
            :resource-paths #(conj % "test"))
  identity)

(deftask clj-tests []
  (comp (testing) (javac) (speak) (t/test)))

(deftask cljs-tests []
  (comp (testing) (speak) (test-cljs)))

(deftask test []
  (comp (testing) (speak) (clj-tests) (cljs-tests)))

(deftask autotest []
  (comp (testing) (watch) (test)))

(deftask make-jar []
  (comp (pom) (jar)))

(deftask travis []
  (testing)
  (comp (t/test)))
