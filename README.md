The irresponsible clojure guild presents...

# crouton - url routing to the overkill

A high performance URL router for the 90% case

## Why?

Other libraries check some of these points, but not all:

1. Simplicity

* Does one thing, well (path routing)
* Works well in conjunction with something like [yada](https://github.com/juxt/yada/).

2. Routes as data

* Data is easy to inspect and generate
* Data can be generated from a GUI by a non-technical user

3. Bidirectional routing

* No more broken internal links. Come on libraries who can't, it's 2017...

4. Performance

* Suitable for use under strict response SLAs. This thing is lightning fast!
* Routes are compiled to an optimised form (hand-tuned java if you're on the JVM!)

5. Reliability

* Liberal in what we accept, conservative in what we produce
* Handles even pathological inputs gracefully and quickly

6. Clojurescript support

* Including self-hosted cljs support (e.g. lumo, planck)

## Example 1 Routing from clojure data

```clojure
(ns crouton.test
  (:require [irresponsible.crouton :as c]))

;; Our route is ordinary clojure data. Each has a name, which we recommend be a keyword.
;; Internally, we first split the url path into segments (the bits between the slashes)
;; We then match segment by segment, backtracking if necessary until a route matches


;; * Strings match themselves against a path segment ("bit between slashes")
;; * A placeholder (`c/?`) is used to match a url segment and assign it a name
;;   * It may have an optional validator (regex, function or predefined (keyword))
;; * A slurp (`c/*`) will always succeed and will store any remaining segments
;; * A map is used to indicate a choice between options. The order is this:
;;   * :crouton/end if present indicates this route should match if there are no more url segments
;;   * Strings, looked up in a map
;;   * Placeholders, first ones with validators, then ones without validators
;;   * Finally, Slurps, which always succeed
;; * Anything else will be interpreted
(def routes
  {:crouton/end :home ;; "/"
   "users" {(c/? :name) {(c/? :id :crouton/int) :user-profile}}
   "login" :login
   "logout" :logout"
   "admin" (c/* :admin)}) ;; Our hypothetical admin panel does its own thing

;; compile it to make it fast
(def route-fn (c/compile-route routes)) ; => function
;; Test it out!
(def path "/user/irresponsible/123")
(route-fn path) ; => {:crouton/route :user-profile :name "irresponsible" :id 123}
;; At this point, you probably want to look up :user-profile in a map of functions
;; or use a multimethod depending on performance requirements
```

## Example 2 : Loading strings

Some people prefer to see their urls as a list. We support that as well!

```clojure
(ns crouton.test
  (:require [irresponsible.crouton :as c]))

(def routes-list
  [[:home         "/"]
   [:user-profile "/user/:name/:id" {:id :crouton/int}]
   [:login        "/login"]
   [:logout       "/logout"]
   [:admin        "/admin/*"]]) ;; Our hypothetical admin panel does its own thing

;; Now we need to turn this into the clojure data we had in the last example
(def routes (c/parse-routes routes-list))
;; => {"users"  {(c/? :name) {(c/? :id :crouton/int) :user-profile}}
;;     "login"  :login
;;     "logout" :logout"
;;     "admin"  (c/* :admin)}

;; compile it to make it fast
(def route-fn (c/compile-route routes)) ; => function
;; Test it out!
(def path "/user/irresponsible/123")
(route-fn path) ; => {:crouton/route :user-profile :name "irresponsible" :id 123}
```

## Internals

The `Crouton` class is all you need to drive us from Java.

There are parallel clojurescript implementations

## Copyright and License

MIT LICENSE

Copyright (c) 2017 James Laver

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

