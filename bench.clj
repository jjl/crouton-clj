(require '[irresponsible.crouton :as c] :reload)
(import '[irresponsible.crouton Crouton Endpoint Placeholder RegexPH ClojurePH RouteMap Predicate Slurp]
        '[java.util HashMap])
(use '[criterium.core :exclude [warn]])
(set! *warn-on-reflection* true)

(let [path "//////foo///////bar///////baz-123///////"
      ^Endpoint e (Crouton/endpoint ::test)
      ^RegexPH reg (Crouton/regex ::regex #"foo" e)
      ^Placeholder pl (Crouton/placeholder ::place e)
      ^RouteMap rm (Crouton/routemap (doto (HashMap.) (.put "test" e)))
      ^Slurp s (Crouton/slurp ::test)]
  (quick-bench
   ;; uncomment one of these at a time
   ;; (c/parse-path path)
   ;; (.match e [] (transient {}))
   ;; (.match rm ["test"] (transient {}))
   ;; (.match s ["test" "and" "more"] (transient {}))
    (.match reg ["foo"] (transient {}))
   ;; (.match pl ["foo"] (transient {}))
  ))
  
