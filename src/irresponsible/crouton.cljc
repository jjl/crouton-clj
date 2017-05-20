(ns irresponsible.crouton
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [irresponsible.spectra :as ss]
            [#?(:clj clojure.spec.alpha :cljs cljs.spec.alpha) :as s]
   #?(:clj  [clojure.core.match :refer [match]]
      :cljs [cljs.core.match :refer-macros [match]]))
#?(:clj (:import [java.util.regex Pattern]
                 [clojure.lang IPersistentMap IPersistentVector Keyword]
                 [irresponsible.crouton Crouton IRoute Preds$PosInt]))
  (:refer-clojure :exclude [* #?(:clj compile)]))

(defn- assoc-once
  "Assocs a key/value pair into a map, but throws if that key already existed
   args: [map key value]
   returns: map
   throws: if key is already contained in map"
  [m k v]
  (if (contains? m k)
    (throw (ex-info "Route name already used. Identifiers must be unique" {:got k}))
    (assoc m k v)))

(defn regex?
  "true if the given value is a regular expression object
   args: [value]
   returns: bool"
  [r]
  (instance? #?(:clj Pattern :cljs js/RegExp) r))

(defrecord Place [name validator])

(defn place?
  "true if the given value is a Place record
   args: [value]
   returns: bool"
  [p]
  (instance? Place p))

(s/def ::place place?)

(defn ?
  "Creates a Place from a name and optional validator.
   A Place is the clojure placeholder for one of a number of placeholder types
   args: [name] [name validator]
     name: keyword or string naming the placeholder
     validator: a regex, clojure function or keyword naming a predefined validator"
  [name & [validator]]
  (->Place
   (ss/assert! ::name name)
   (ss/assert! (s/nilable ::validator) validator)))

#?
(:clj
 (defn match-route
   "Matches an IRoute against the given segments, appending any placeholders into places
    args: [route segments places]
      route: something implementing/satisfying IRoute
      segments: vector of path segment strings to match against
      places: map of placeholder values seen so far
    returns: match map or nil"
   [^IRoute thing ^IPersistentVector pieces ^IPersistentMap places]
   (.match thing pieces places))
 :cljs
 (defprotocol IRoute ; we don't see the java interface, so make a protocol instead
   (match-route [self pieces places]
    "Matches an IRoute against the given segments, appending any placeholders into places
     args: [route segments places]
       route: something implementing/satisfying IRoute
       segments: vector of path segment strings to match against
       places: map of placeholder values seen so far
     returns: match map or nil")))

(defn iroute?
  "true if the given value satisfies or implements IRoute
   args: [value]
   returns: bool"
  [v]
  #?(:clj  (instance?  IRoute v)
     :cljs (satisfies? IRoute v)))

(defprotocol PrintSegment
  (print-segment [self places]
    "Print the given url segment
     args: [segment places]
       places: map of keyword placeholder values
     returns: string"))

(extend-protocol PrintSegment
  #?(:clj String :cljs js/String)
  (print-segment [self _] self)
  Keyword
  (print-segment [self places] (places self)))

(defn- print-route
  "Prints segments seperated by / with an additional / at the start
   args: [segments] ; seq/vec of strings
   returns: string"
  [pieces]
  (if (empty? pieces)
    "/"
    (str/join "/" (cons "" pieces))))


(s/def ::iroute iroute?)
(s/def ::iroutes  (s/coll-of iroute? :into []))
(s/def ::iroutes+ (s/coll-of iroute? :min-count 1 :into []))

(s/def ::name (some-fn keyword? string?))
(s/def ::precanned #{:crouton/pos-int
                     ::pos-int})
(s/def ::regex regex?)
(s/def ::fn (and ifn? (complement keyword?)))
(s/def ::validator (ss/some-spec ::precanned ::regex ::fn))

(defn parse-path
  "Splits a url path into segments on /
   args: [path] ; string
   returns: vector of strings"
  [path]
  #?(:clj  (Crouton/parse_path ^String path)
     :cljs (into [] (filter #(not= "" %) (str/split path #"/+")))))

#?
(:cljs
 (defrecord Placeholder [name next]
   IRoute
   (match-route [self pieces places]
     (when (seq pieces)
       (match-route next (subvec pieces 1) (assoc places name (nth pieces 0)))))))

(defn make-placeholder
  "Creates a Placeholder which matches any single url path segment
   args: [name next]
     name: string or keyword naming the placeholder
     next: IRoute
   returns: Placeholder"
  [name next]
  #?(:clj  (Crouton/placeholder name next)
     :cljs (->Placeholder
            (ss/assert! ::name name)
            (ss/assert! ::iroute next))))

#?
(:cljs
 (defrecord RegexPH [name regex next]
   IRoute
   (match-route [self pieces places]
     (when (seq pieces)
       (when-let [f (nth pieces 0 nil)]
         (when (.test regex f)
           (match-route next (subvec pieces 1) (assoc places name f))))))))

#?
(:cljs
 (defn- anchor-regex
   "Horrible hack that produces the same behaviour as the jvm version as javascript lacks
    a facility for matching against a whole string with a non-anchored regex
    args: [regex] ; a RegExp
    returns: RegExp
    note: According to mozilla, JS doesn't support \\a and \\z, so this *should* cover all the cases
    source: https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/RegExp#boundaries"
   [re]
   (let [source (.-source re)
         len    (.-length source)
         prefix (if (and (pos? len) (not= "^" (.charAt source 0))) "^" "")
         suffix (if (and (pos? len) (not= "$" (.charAt source (dec len)))) "$" "")]
     (js/RegExp (str prefix source suffix)))))

(defn make-regex
  "Creates a Placeholder which matches a segment against a regular expression
   args: [name regex next]
     name: string or keyword naming the placeholder
     regex: regular expression object, must match the entire segment
     next: IRoute
   returns: Placeholder"
 [name regex next]
  #?(:clj  (Crouton/regex name regex next)
     :cljs (->RegexPH
            (ss/assert! ::name name)
            (->> regex (ss/assert! ::regex) anchor-regex)
            (ss/assert! ::iroute next))))

#?
(:cljs
 (defrecord ClojurePH [name ifn next]
   IRoute
   (match-route [self pieces places]
     (when (seq pieces)
       (let [f (nth pieces 0)
             r (ifn f)]
         (when-not (nil? r)
           (match-route next (subvec pieces 1) (assoc places name r))))))))

(defn make-clojure
  "Creates a Placeholder which matches a segment against a clojure function
   args: [name pred next]
     name: string or keyword naming the placeholder
     pred: clojure IFn
     next: IRoute
   returns: Placeholder"
  [name ifn next]
  #?(:clj  (Crouton/clojure name ifn next)
     :cljs (->ClojurePH
            (ss/assert! ::name name)
            (ss/assert! ::fn ifn)
            (ss/assert! ::iroute next))))

#?
(:cljs
 (defrecord Endpoint [name]
   IRoute
   (match-route [self pieces places]
     (when (empty? pieces)
       (-> places
           (assoc :crouton/route name))))))

(defn make-endpoint
  "Creates an Endpoint, which matches when there are no more segments remaining
   args: [name]
     name: string or keyword naming the route
   returns: Endpoint"
  [name]
  #?(:clj  (Crouton/endpoint name)
     :cljs (->Endpoint (ss/assert! ::name name))))

#?
(:cljs
 (defrecord Fallback [f s]
   IRoute
   (match-route [self pieces places]
     (let [r1 (match-route f pieces places)]
       (if (nil? r1)
         (match-route s pieces places)
         r1)))))

(defn make-fallback [fst snd]
  "Creates a Fallback, which attempts to match first one route, then another
   args: [first second] ; both IRoute
     name: string or keyword naming the route
   returns: Fallback"
  #?(:clj  (Crouton/fallback fst snd)
     :cljs (->Fallback
            (ss/assert! ::iroute fst)
            (ss/assert! ::iroute snd))))

#?
(:cljs
 (defrecord Choice [items]
   IRoute
   (match-route [self pieces places]
     (loop [items items]
       (when (seq items)
         (let [i (nth items 0)
               r (match-route i pieces places)]
               (if (nil? r)
                 (recur (subvec items 1))
                 r)))))))

(defn make-choice
  "Creates a Choice, which attempts to match routes in order
   args: [choices] ; seq of IRoute
   returns: Choice"
  [items]
#?(:clj  (Crouton/choice items)
   :cljs (->Choice (ss/assert! ::iroutes+ items))))

#?
(:cljs
 (defrecord Slurp [name]
   IRoute
   (match-route [self pieces places]
     (-> places
         (assoc :crouton/route name)
         (assoc :crouton/slurp pieces)))))

(defn make-slurp
  "Creates a Slurp, which matches unconditionally and collects any remaining segments
   args: [name] ; string or keyword
   returns: Slurp"
  [name]
#?(:clj  (Crouton/slurp name)
   :cljs (->Slurp (ss/assert! ::name name))))

#?
(:cljs
 (defrecord RouteMap [routes]
   IRoute
   (match-route [self pieces places]
     (when (seq pieces)
       (let [f (nth pieces 0)]
         (when-let [r (routes f)]
           (match-route r (subvec pieces 1) places)))))))

(s/def ::routes-map (s/map-of string? iroute? :min-count 1))

(defn make-routemap
  "Creates a RouteMap, which matches a segment in a map
   args: [items] ; map of string -> IRoute
   returns: RouteMap"
  [items]
  #?(:clj  (Crouton/routemap items)
     :cljs (->RouteMap (ss/assert! ::routes-map items))))

(declare compile-route rev-compile-route)

(defn- compile-key-type
  "Turns a key into a category of route. Used during compilation of maps
   args: [k]
   returns: keyword"
  [k]
  (cond
    (#{:/ :&} k) k
    (place? k)  :ph
    (string? k) :str
    :else
    (throw (ex-info "Don't know what to do with this" {:got k :type (type k)}))))

(defn- compile-group-keys
  "Groups a map by the category of route indicated by the keys
   args: [map]
   returns: map where values are vectors of [k v]. keys may be:
     :/   endpoint
     :&   slurp
     :ph  placeholer
     :str string literal"
  [m]
  (group-by (comp compile-key-type first) m))

(defn- rev-group
  "Groups a path into strings and keywords
   args: [path] ; vec of strings and keywords
   returns: map where values are IRoute. keys may be:
     :strings  vector of string segments
     :keywords vector of keyword segments"
  [p]
  (-> #(cond (string? %)  :strings
             (keyword? %) :keywords
             :else (-> "Expected string or keyword"
                       (ex-info {:got %}) throw))
      (group-by p)))

(defn compile-strings
  "Compiles a RouteMap from string and actions
   args: [routes-map] ; map of [string route] splayed into a seq
   returns: RouteMap"
  [ss]
  (when (seq ss)
    (->> ss
         (into {} (map (fn [[k v]] [k (compile-route v)])))
         make-routemap)))

(defn- rev-compile-strings
  "Compiles routers from string segments and assocs them into printers
   args: [strings path printers]
     strings: list of [name route]
     path: vector of segments encountered so far
     printers: map of name to printer function
   returns: map of printers"
  [strings path printers]
  (reduce (fn [acc [k v]]
            (rev-compile-route v (conj path k) acc))
          printers strings))

#?
(:cljs
 (defn- parse-pos-int
   "Parses a string that looks like a non-negative integer
    args: [string]
    returns: int or nil"
   [^String s]
   (try
     (when-not (= "-" (.charAt s 0))
       (js/parseInt s 10))
     (catch :default e
       nil))))

(def ^:dynamic *validators*
  "The pool of precanned validators"
  {:crouton/pos-int #?(:clj Preds$PosInt/INSTANCE :cljs parse-pos-int)})

(defn make-precanned
  "Creates a placeholder with a predefined validator.
   args: [name validator next]
     name: keyword
     validator: keyword naming a validator
   returns: IRoute"
  [name validator next]
  (if-let [v (*validators* validator)]
    (#?(:clj Crouton/lambda :cljs make-clojure) name v next)
    (->> {:got validator :valid (set (keys *validators*))}
         (ex-info "Unknown predefined validator")
         throw)))

(defn check-places
  "Checks that every keyword is present in places (i.e. that every placeholder has a value)
   args: [places keywords]
   returns: nil
   throws: if one or more keywords is missing"
  [places keywords]
  (when-not (every? places keywords)
    (->> {:missing (set/difference keywords (keys places))}
         (ex-info "Missing placeholders")
         throw)))

(s/def ::validator? (s/nilable ::validator))

(defn compile-place
  "Compiles a Place record to a concrete Placeholder
   args: [place next] ; next = IRoute
   returns: IRoute placeholder"
  [{:keys [name validator]} next]
  (match (ss/conform! ::validator? validator)
    nil             (make-placeholder name next)
    [::regex     r] (make-regex name validator next)
    [::precanned k] (make-precanned name validator next)
    [::fn        f] (make-clojure name validator next)
    :else
    (throw (ex-info "don't know what to do with this validator"
                    {:got validator :name name}))))

(defn compile-places
  "Compiles a seq of Place records to a seq of IRoute
   args: [places]
   returns: seq of IRoutes"
  [ps]
  (when (seq ps)
    (->> ps
         (sort-by (comp nil? :validator first))
         (map (fn [[k v]] (compile-place k (compile-route v)))))))

(defn rev-compile-places
  "Updates the routers map with the routers made from compiling a list of Place records
   args: [segments path routers]
     places: vector of Place records
     path: vector of string segments
     routers: map of named reverse routing functions
   returns: new routers map "
  [ps path routers]
  (reduce (fn [acc [k v]]
            (rev-compile-route v (conj path (:name k)) acc))
          routers ps))

(defn compile-map
  "Compiles a map into an IRoute"
  [m]
  (when (empty? m)
    (throw (ex-info "Cannot compile an empty map!" {})))
  (let [{slash :/ slurp :&} m
        {:keys [ph str]} (compile-group-keys m)
        a (when slash [(make-endpoint slash)])
        b (when str [(compile-strings str)])
        c (compile-places ph)
        d (when slurp [(make-slurp slurp)])
        routes (concat a b c d)]
    (match routes
      [i]   i
      [i j] (make-fallback i j)
      :else (make-choice (into [] routes)))))

(defn- rev-compile-end
  "Compiles a reverse router for an endpoint
   args: [key path routers]
     key: string or keyword naming the router
     path: vector of segments travelled so far
     routers: map of key to router fn
   returns: new routers map"
  [k path routers]
  (let [{:keys [strings keywords]} (rev-group path)
        ks-set (set keywords)
        f (fn [places]
            (check-places places ks-set)
            (print-route (map #(print-segment % places) path)))]
    (assoc-once routers k f)))

((:a (rev-compile-end :a [] {})) {})

(defn- rev-compile-slurp
  "Compiles a reverse router for a slurp
   args: [key path routers
     key: string or keyword naming the router
     path: vector of segments travelled so far
     routers: map of key to router fn
   returns: new routers map"
  [k path routers]
  (let [{:keys [strings keywords]} (rev-group path)
        ks-set (set keywords)
        f (fn [{:keys [:crouton/slurp] :as places}]
            (check-places places ks-set)
            (as-> path $
              (map print-segment $)
              (concat $ slurp)
              (print-route $)))]
    (assoc-once routers k f)))

(defn- rev-compile-map
  "Compiles a reverse router for a given map
   args: [map path routers]
     map: of clojure routes
     path: vector of segments travelled so far
     routers: map of key to router fn
   returns: new routers map"
  [m path printers]
  (ss/assert! seq m)
  (let [{slash :/ slurp :&} m
        {:keys [ph str]} (compile-group-keys m)
        c (compile-places ph)]
    (cond->> printers
      slash     (rev-compile-end slash path)
      slurp     (rev-compile-slurp slurp path)
      (seq str) (rev-compile-strings str path)
      (seq ph)  (rev-compile-places ph path))))
  
(defn- compile-route
  "Compiles a router for a given route
   args: [route]
   returns: IRoute"
  [v]
  (if (map? v)
    (compile-map v)
    (make-endpoint v)))

(defn- rev-compile-route
  "Compiles a reverse router for a given or endpoint
   args: [map path routers]
     map: of clojure routes
     path: vector of segments travelled so far
     routers: map of key to router fn
   returns: new routers map"
  [v p m]
  (if (map? v)
    (rev-compile-map v p m)
    (rev-compile-end v p m)))

(defprotocol Routes
  (route [self path]
    "Matches the path against the routes we were compiled against
     args: [router path] ; path is string
     returns: match map or nil. match map keys:
       `:crouton/route` keyword naming a route. (always)
       `:crouton/slurp` any slurped segments as a vector of strings (maybe)
       also contains the values any placeholders matched")
  (unroute [self map]
    "Returns a url for the given match map
     args: [router match]
       match: map. keys:
         `:crouton/route` keyword naming a route. (always)
         `:crouton/slurp` any slurped segments as a vector of strings (maybe)
         also must contains values for any placeholders
     returns: string or nil
     throws: if there are any missing placholders"))

(defrecord Router [forward backward]
  Routes
  (route [self path]
    (match-route forward (parse-path path) {}))
  (unroute [self match]
    (backward match)))

(defn compile
  "Turns a routes map into a Router for routing and reverse routing
   args: [routes] ; a map
   returns: Router"
  [r]
  (ss/assert! map? r)
  (->Router (compile-route r) (rev-compile-route r [] {})))
