(import '[irresponsible.crouton Endpoint RouteMap]
        '[java.util HashMap])
(use '[criterium.core :exclude [warn]])

(let [^Endpoint e (Endpoint. ::test)]
  (quick-bench
   (.match e [] (transient {}))))

(let [^RouteMap r (RouteMap. (doto (HashMap.) (.put "test" (Endpoint. ::test))))]
  (quick-bench
   (.match r ["test"] (transient {}))))
