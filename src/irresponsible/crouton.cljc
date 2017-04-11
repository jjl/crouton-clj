(ns irresponsible.crouton
#?(:cljs (:require [clojure.string :as str]))
#?(:clj (:import [java.util.regex Pattern]
                 [irresponsible.crouton Crouton IRoute]))
  (:refer-clojure :exclude [*]))

(defrecord Place [name validator])

#?
(:clj
 (defn parse-path [^String path]
   (Crouton/parse_path path))
 :cljs
 (defn parse-path [path]
   (into [] (filter #(not= "" %) (str/split path #"/+")))))

(defn place? [p]
  (instance? Place p))

(defn ?
  ([name]
   (? name nil))
  ([name validator]
   (->Place name validator)))

#?
(:cljs
 (defprotocol IRoute
   (match [self pieces places])))

(defn iroute? [v]
  #?(:clj  (instance? IRoute v)
     :cljs (satisfies? IRoute v)))

#?
(:cljs
 (defrecord Placeholder [name next]
   IRoute
   (match [self pieces places]
     (when (seq pieces)
       (match next (subvec pieces 1) (assoc! places name (nth pieces 0)))))))

(defn make-placeholder [name next]
  #?(:clj
     (Crouton/placeholder name next)
     :cljs
     (do (when (nil? name)
           (throw (ex-info "placeholder expects a non-nil name (keyword recommended)" {:got name})))
         (when-not (iroute? next)
           (throw (ex-info "placeholder expects an IRoute next" {:got next})))
         (->Placeholder name next))))
 
(defn regex? [r]
  (instance? #?(:clj Pattern :cljs js/RegExp) r))

#?
(:cljs
 (defrecord RegexPH [name regex next]
   IRoute
   (match [self pieces places]
     (when (seq pieces)
       (let [f (nth pieces 0)]
         (when (.test regex f)
           (match next (subvec pieces 1) (assoc! places name f))))))))

(defn make-regex [name regex next]
  #?(:clj
     (Crouton/regex name regex next)
     :cljs
     (do (when (nil? name)
           (throw (ex-info "regex expects a non-nil name (keyword recommended)" {:got name})))
         (when-not (regex? regex)
           (throw (ex-info "regex expects a RegExp" {:got regex})))
         (when-not (iroute? next)
           (throw (ex-info "regex expects an IRoute next" {:got next})))
         (->RegexPH name regex next))))

#?
(:cljs
 (defrecord ClojurePH [name ifn next]
   IRoute
   (match [self pieces places]
     (when (seq pieces)
       (let [f (nth pieces 0)]
         (when (ifn f)
           (match next (subvec pieces 1) (assoc! places name f))))))))

(defn make-clojure [name ifn next]
  #?(:clj
     (Crouton/clojure name ifn next)
     :cljs
     (do (when (nil? name)
           (throw (ex-info "clojure expects a non-nil name (keyword recommended)" {:got name})))
         (when-not (ifn? ifn)
           (throw (ex-info "clojure expects an IFn" {:got ifn})))
         (when-not (iroute? next)
           (throw (ex-info "clojure expects an IRoute next" {:got next})))
         (->ClojurePH name ifn next))))

#?
(:cljs
 (defrecord Endpoint [name]
   IRoute
   (match [self pieces places]
     (when (empty? pieces)
       (-> places
           (assoc! :crouton/route name)
           persistent!)))))

(defn make-endpoint [name]
  #?(:clj
     (Crouton/endpoint name)
     :cljs
     (do (when (nil? name)
           (throw (ex-info "endpoint expects a non-nil name (keyword recommended)" {:got name})))
         (->Endpoint name))))

#?
(:cljs
 (defrecord Fallback [f s]
   IRoute
   (match [self pieces places]
     (let [r1 (match f pieces places)]
       (if (nil? r1)
         (match s pieces places)
         r1)))))

(defn make-fallback [fst snd]
  #?(:clj
     (Crouton/fallback fst snd)
     :cljs
     (do (when-not (and (iroute? fst) (iroute? snd))
           (throw (ex-info "fallback expects two IRoute arguments" {:got [fst snd]})))
         (->Fallback fst snd))))

#?
(:cljs
 (defrecord Choice [items]
   IRoute
   (match [self pieces places]
     (loop [items items]
       (when (seq items)
         (let [i (nth items 0)
               r (match i pieces places)]
               (if (nil? r)
                 (recur (subvec items 1))
                 r)))))))

(defn make-choice [items]
  #?(:clj
     (Crouton/choice items)
     :cljs
     (do (when-not (and (seq items) (every? iroute? items))
           (throw (ex-info "choice expects a non-empty sequence of IRoute items" {:got items})))
         (->Choice (vec items)))))

#?
(:cljs
 (defrecord Slurp [name]
   IRoute
   (match [self pieces places]
     (-> places
         (assoc! :crouton/route name)
         (assoc! :crouton/slurp pieces)
         persistent!))))

(defn make-slurp [name]
  #?(:clj
     (Crouton/slurp name)
     :cljs
     (do (when (nil? name)
           (throw (ex-info "slurp expects a non-nil name (keyword recommended)" {:got name})))
         (->Slurp name))))

#?
(:cljs
 (defrecord RouteMap [routes]
   IRoute
   (match [self pieces places]
     (when (seq pieces)
       (let [f (nth pieces 0)]
         (when-let [r (routes f)]
           (match r (subvec pieces 1) places)))))))

(defn make-routemap [items]
  #?(:clj
     (Crouton/routemap items)
     :cljs
     (do (when-not (and (map? items)
                        (seq items)
                        (every? (fn [[k v]] (and (string? k) (iroute? v))) items))
           (throw (ex-info "routemap expects a non-empty map of String keys and IRoute values" {:got items})))
         (->RouteMap items))))

(declare compile-route)

(defn key-type [k]
  (cond
    (#{:/ :&} k) k
    (place? k) :ph
    (string? k)      :str
    :else
    (throw (ex-info "Don't know what to do with this" {:got k :type (type k)}))))

(defn group-keys [m]
  (group-by (comp key-type first) m))

(defn compile-strings [ss]
  (->> ss
       (into {} (map (fn [[k v]] [k (compile-route v)])))
       make-routemap))

(defn make-precanned [name validator next]
  (ex-info "todo" {}))

(defn compile-place [{:keys [name validator]} next]
  (cond (nil? validator)     (make-placeholder name next)
        (regex? validator)   (make-regex name validator next)
        (keyword? validator) (make-precanned name validator next)
        (ifn? validator)     (make-clojure name validator next)
        :else (throw (ex-info "don't know what to do with this validator" {:got validator :name name}))))

(defn compile-places [ps]
  (when (seq ps)
    (->> ps
         (sort-by (comp nil? :validator first))
         (map (fn [[k v]] (compile-place k (compile-route v)))))))

(defn compile-map [m]
  (let [{slash :/ slurp :& :keys [ph str]} (group-by key-type m)
        a (when slash [(make-endpoint slash)])
        b (compile-strings str)
        c (compile-places ph)
        d (when slurp [(make-slurp slurp)])
        routes (concat a b c d)]
    (->> (into [] routes)
         #?(:clj Crouton/choice :cljs ->Choice))))

(defn compile-route [v]
  (if (map? v)
    (compile-map v)
    (make-endpoint v)))

