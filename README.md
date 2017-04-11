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

;; valid urls:
;;  * /
;;  * /users/irresponsible/123 ;; or various others of this form
;;  * /login
;;  * /logout
;;  * /admin ;; also scoops up its argument
(def routes
  {:/       :home
   "users"  {(c/? :name) {(c/? :id :crouton/int) :user-profile}}
   "login"  :login
   "logout" :logout
   "admin"  {:& :admin)}) ;; Our hypothetical admin panel does its own thing, we scoop the segments

;; The main structure here is the map. A map requests to match one or more alternative routes
;; The values of a map are either more maps (and thus matches) for the rest of the segments
;; or a name for the route. You may pick anything other than a map for a name, though we
;; recommend a keyword for simplicity.
;; Different keys in maps mean different things and are processed in this order:
;;   * `:/`, when present indicates this route should match if there are no more url segments
;;   * Strings match themselves against a path segment
;;   * A placeholder (`c/?`) is used to match a url segment and assign it a name
;;     * It may have an optional validator (regex, function or predefined (keyword))
;;     * For performance, we test placeholders with conditions first
;;   * A slurp (`:&`) will always succeed and will store any remaining segments.

;; compile it to make it fast
(def route-fn (c/compile routes)) ; => function
;; Test it out!
(def path "/user/irresponsible/123")
(route-fn path) ; => {:crouton/route :user-profile :name "irresponsible" :id 123}
;; At this point, you probably want to look up :user-profile in a map of functions
;; or use a multimethod depending on performance requirements
```

## Example 2 : Loading strings

Some people prefer to see their urls as a list of strings. We support that as well!

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
;; => {:/       :home
;;     "users"  {(c/? :name) {(c/? :id :crouton/int) :user-profile}}
;;     "login"  :login
;;     "logout" :logout"
;;     "admin"  {:& :admin}}

;; compile it to make it fast
(def route-fn (c/compile routes)) ; => function
;; Test it out!
(def path "/user/irresponsible/123")
(route-fn path) ; => {:crouton/route :user-profile :name "irresponsible" :id 123}
```

## Bidirectional Routing


## Internals

The `Crouton` class is all you need to drive us from Java.

There are parallel clojurescript implementations. They should be fast, but not *as* fast.

## Copyright and License

MIT LICENSE

Copyright (c) 2017 James Laver

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

