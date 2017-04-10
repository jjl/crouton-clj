(set-env!
  :project 'irresponsible/crouton
  :version "0.1.0"
  :resource-paths #{"src"}
  :source-paths #{"src" "java"}
  :dependencies '[[org.clojure/clojure "1.9.0-alpha15" :scope "provided"]
                  [criterium                  "0.4.4"  :scope "test"]
                  [irresponsible/gadget       "0.2.0"  :scope "test"]
                  [adzerk/boot-test           "1.1.0"  :scope "test"]])

(require '[adzerk.boot-test :as t])

(task-options!
  pom {:project (get-env :project)
       :version (get-env :version)
       :description "url routing to the overkill"
       :url "https://github.com/irresponsible/crouton"
       :scm {:url "https://github.com/irresponsible/crouton.git"}
       :license {"MIT" "https://en.wikipedia.org/MIT_License"}}
  target  {:dir #{"target"}})

(deftask testing []
  (set-env! :source-paths   #(conj % "test")
            :resource-paths #(conj % "test"))
  identity)

(deftask test []
  (comp (testing) (speak) (t/test)))

(deftask autotest []
  (comp (testing) (watch) (test)))

(deftask make-jar []
  (comp (pom) (jar)))

(deftask travis []
  (testing)
  (comp (t/test)))
