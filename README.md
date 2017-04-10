The irresponsible clojure guild presents...

# crouton - url routing to the overkill

A high performance URL router for the 90% case

## Oh lord, why another url router?

1. Decomplected

* HTTP specifies that a URL defines an endpoint, not a URL and a method.
* The method should be handled at the endpoint with something like [yada](https://github.com/juxt/yada/).

2. Routes as data

* Data is easy to inspect and generate
* Data can be generated from a GUI by a non-technical user

3. Bidirectional routing

* No more broken internal links. Come on libraries who can't, it's 2017...

4. Simplicity

* Even if a library gets this far, it's inevitably too complicated
* We have adopted a simple model that should be flexible enough for 99% of cases

5. Performance

* Metrics-driven optimisation: this thing is fast!

6. Reliability

* Liberal in what we accept, conservative in what we produce
* Handles even pathological inputs gracefully and quickly

7. Clojurescript support

* Including self-hosted cljs support (e.g. lumo, planck)

## Introduction

```
(ns crouton.test
  (:require [irresponsible.crouton :as c]))

;; take this typical url path:
(def path "/foo/bar/123")
;; it matches this route string (and potentially others)
(def route-str "/foo/:section/:id")
;; and we want to make sure id is an integer
(def route-preds {:id :crouton/int})
;; and here is a name for the route
(def route-name :generic-entry)

;; firstly, let's parse it out into a clojure data structure
;; (you may optionally create the data structures directly instead)
(def route-1 (c/parse-route route-name route-str route-preds))
;; => {"foo" {(? :section) {(? :int :crouton/int)}}}
;; `?` is a function in the irresponsible.crouton namespace which returns a placeholder

;; compile it to make it fast
(def fn-1 (c/compile-route route-1)) ; => function
(fn-1 path) ; => {:crouton/route :generic-entry :section "bar" :id 123}
```

## Usage




## Java API

## Copyright and License

MIT LICENSE

Copyright (c) 2017 James Laver

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

