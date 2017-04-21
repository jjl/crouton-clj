[![Clojars Project](http://clojars.org/irresponsible/crouton/latest-version.svg)](http://clojars.org/irresponsible/crouton)

The irresponsible clojure guild presents...

# crouton - path routing to the overkill

A simple, high performance, data-driven URL path router for the 90% case.

## Requirements

A Java 8 VM. 7 was EOLed ages ago.

Clojure 1.8 or newer (actually only 1.7 if you don't use clojurescript)

## Why?

Some of my medium-term projects rule out other libraries for various reasons amongst these:

* Too complicated
* Complecting routing with dispatch based on the http method
* Not being described by edn data (and thus suitable for loading from a config file)
* Not supporting bidirectional routing
* Not supporting clojurescript
* Performing poorly

This isn't to say that other libraries aren't good (I'm quite fond of compojure in fact).

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
;;  * /admin ;; also scoops up anything after, e.g. /admin/foo/bar
(def routes
  {:/       :home
   "users"  {(c/? :name) {(c/? :id :crouton/pos-int) :user-profile}}
   "login"  :login
   "logout" :logout
   "admin"  {:& :admin}) ;; Our hypothetical admin panel does its own thing, we scoop the segments

;; The main structure here is the map. A map requests to match one or more alternative routes
;; The values of a map are either more maps (and thus matches) for the rest of the segments
;; or a name for the route. You may pick anything other than a map for a name, though we
;; recommend a keyword for simplicity.
;; Different keys in maps mean different things and are processed in this order:
;;   * `:/`, when present indicates this route should match if there are no more url segments
;;   * Strings match themselves against a path segment
;;   * A placeholder (`c/?`) is used to match a url segment and assign it a name
;;     * It may have an optional validator (regex, function or predefined (keyword))
;;       * Regexes must match the entire path segment, even if you don't wrap them with ^ and $
;;     * For performance, we test placeholders with conditions first
;;   * A slurp (`:&`) will always succeed and will store any remaining segments.

;; compile it to make it fast
(def router (c/compile routes)) ; => function
;; Test it out!
(def path "/user/irresponsible/123")
(c/route router path) ; => {:crouton/route :user-profile :name "irresponsible" :id 123}
;; At this point, you probably want to look up :user-profile in a map of functions
;; or use a multimethod depending on performance requirements

;; We can also go backwards
(c/unroute router {:crouton/route :user-profile :name "irresponsible" :id 123})
```

## Example 2 : Loading strings

Some people prefer to see their urls as a list of strings. We support that as well!

```clojure
(ns crouton.test
  (:require [irresponsible.crouton :as c]))

(def routes-list
  [[:home         "/"]
   [:user-profile "/user/:name/:id" {:id :crouton/pos-nt}]
   [:login        "/login"]
   [:logout       "/logout"]
   [:admin        "/admin/*"]]) ;; Our hypothetical admin panel does its own thing

;; Now we need to turn this into the clojure data we had in the last example
(def routes (c/parse-routes routes-list))
;; => {:/       :home
;;     "users"  {(c/? :name) {(c/? :id :crouton/pos-int) :user-profile}}
;;     "login"  :login
;;     "logout" :logout"
;;     "admin"  {:& :admin}}

;; compile it to make it fast
(def route-fn (c/compile routes)) ; => function
;; Test it out!
(def path "/user/irresponsible/123")
(route-fn path) ; => {:crouton/route :user-profile :name "irresponsible" :id 123}

```

## Validators

Validators may be:
* Clojure functions, which should return a (possibly coerced) value or nil if invalid
* Regexes, which must match the entire url segment
* Keywords naming predefined validators.

These predefined validators are fast and tested. Use them.

* `:crouton/pos-int` - a positive integer (in decimal notation). coerces to an int

We would like to have more. Please request them via issues or contribute via PR.

## Plans

* More predefined validators
* More data-driven optimisation
* Optimised CLJS versions (benched against multiple engines!)
* Investigate ProGuard for optimising generated jars

## Hacking

You need boot installed.

Run clojure and clojurescript tests:

```boot test```

Run clojure tests:

```boot clj-tests```

Run clojurescript tests:

```boot cljs-tests```

## Caveats

The clojurescript versions have not yet undergone proper optimisation.
They should be 'fast enough' for browser usage but don't be backing a
high-performance nodejs rest api with it yes.

Reverse routing is currently only available for the clj/cljs API. If
you wish to use it from java, you must use the clojure api through
clojure's java api.

It does say in the first example, but regexes must match the whole
segment text. This is a deliberate choice and involves a hack on cljs.

## Performance

* Regexes are by a fair margin the slowest route segments in isolation
* Lambda and clojure predicate placeholders depend on the efficiency of the lambda/predicate
* Try to avoid many placeholders in one map - they must be tried in order and may induce backtracking

You are advised to run your own benchmarks of the components with bench.clj in a cider repl

## Java API

The `Crouton` class is all you need to drive us from Java.

## Contributions

Issues and pull requests very welcome, including for documentation.

## Copyright and License

MIT LICENSE

Copyright (c) 2017 James Laver

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

