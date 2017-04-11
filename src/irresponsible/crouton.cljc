(ns irresponsible.crouton
#?(:clj (:import [java.util.regex Pattern]
                 [irresponsible.crouton Crouton]))
  (:refer-clojure :exclude [*]))

(defrecord Place [name validator])

#?
(:clj
 (defn parse-path [^String path]
   (Crouton/parse_path path))
 :cljs
 (defn parse-path [path]
   (binding [*unchecked-math* true]
     (let [end (.length path)]
       (loop [acc (transient [])
              start 0]
         (if (>= start end)
           (persistent! acc)
           (let [i (.indexOf path 47 start)
                 new-start (inc i)]
             (cond (= -1 i) (->> (.substring path start) (conj! acc) persistent!)
                   (= start i) (recur acc new-start)
                   :else (recur (conj! acc (.substring path start i)) new-start)))))))))

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
   (route [self pieces places])))

#?
(:cljs
 (defrecord Placeholder [name next]
   IRoute
   (route [self pieces places]
     (when (seq pieces)
       (next (subvec pieces 1) (assoc! places name (nth pieces 0)))))))
#?
(:cljs
 (defrecord RegexPH [name regex next]
   IRoute
   (route [self pieces places]
     (when (seq pieces)
       (let [f (nth pieces 0)]
         (when (.test regex f)
           (next (subvec pieces 1) (assoc! places name f))))))))
#?
(:cljs
 (defrecord ClojurePH [name ifn next]
   IRoute
   (route [self pieces places]
     (when (seq pieces)
       (let [f (nth pieces 0)]
         (when (.test regex f)
           (next (subvec pieces 1) (assoc! places name f))))))))
#?
(:cljs
 (defrecord Endpoint [name]
   IRoute
   (route [self pieces places]
     (when (empty? pieces)
       (-> places
           (assoc! :crouton/route name)
           persistent!)))))
#?
(:cljs
 (defrecord Fallback [f s]
   IRoute
   (route [self pieces places]
     (let [r1 (f pieces places)]
       (if (nil? r1)
         (s pieces places)
         r1)))))
#?
(:cljs
 (defrecord Choice [items]
   IRoute
   (route [self pieces places]
     (when (seq pieces)
       (let [f (nth pieces 0)]
         (loop [items items]
           (when (seq items)
             (let [i (nth items 0)
                   r (i f)]
               (if (nil? r)
                 (recur (subvec pieces 0))
                 r)))))))))
#?
(:cljs
 (defrecord Slurp [name]
   IRoute
   (route [self pieces places]
     (-> places
         (assoc! :crouton/route name)
         (assoc! :crouton/slurp pieces)
         persistent!))))

(defn compile-map [m])

;; (defn compile-a [a]
;;   (cond (map? a) (compile-map a)
;;         (place? a) (compile-place a)
        
        
 ;; (defn compile-strings
;;   "Builds a lookup map of strings to functions and returns code that runs against it
;;    args: [all-name one-name acc-name strings]
;;    returns: code"
;;   [all-name one-name acc-name ss]
;;   (when (seq ss)
;;     (let [lookup (into {}
;;                        (map (fn [[k v]]
;;                               (->> (compile-a all-name one-name acc-name v)
;;                                    (apply make-fn all-name one-name acc-name)
;;                                    (vector k))))
;;                        ss)]
;;       `(when-let [h (~lookup ~one-name)] (h ~all-name ~acc-name)))))

;; (defn compile-vecs
;;   "Builds code that tries a resorted list of vectors.
;;    A vector holds a prefix or suffix string and a placeholder
;;    We sort by the presence of a validator function in the placeholder and then by string length
;;    args: [all-name one-name vs]
;;    returns: code"
;;   [all-name one-name acc-name vs]
;;   (when (seq vs)
;;     (map (fn [[k v]] ;
;;            (->> (sort-by (juxt (comp nil? :validator second) (comp count first)) v)
;;                 (map #(compile-vec all-name one-name acc-name %))
;;                 (apply make-fn ps-name)
;;                 (vector k))))
  
;; (defn key-type-of [k]
;;   (cond
;;     (placeholder? k) :ph
;;     (vector? k)      :vec
;;     (string? k)      :str
;;     :else
;;     (throw (ex-info "Don't know what to do with this" {:got k :type (type k)}))))

;; (defn compile-placeholder [all-name one-name acc-name {:keys [name validator]} next]
;;   (let [n (compile-a all-name one-name acc-name next)]
;;     (if validator
;;       `(when (~validator ~one-name)
;;          (~next (drop-piece ~all-name) ~acc-name
       

;; (defn compile-placeholders [all-name one-name ps]
;;   (some->> ps
;;            (#(sort-by (comp nil? :validator) %))
;;            (map #(compile-placeholder all-name one-name %))
;;            (apply make-fn all-name one-name)
;;            (vector k)))

;; (defn compile-map [all-name one-name m]
;;   (let [{:keys [ph vec str]} (group-by key-type-of m)
;;         sp (compile-strings all-name one-name str)
;;         vp (compile-vecs all-name one-name vec)
;;         pp (compile-placeholders all-name one-name ph)]
;;     (->> (concat sp vp pp)
;;          (apply make-fn all-name one-name))))

;; (defn compile-vec [^String all-name ^String one-name v])
;;   ;; (match v
;;   ;;   [(s :guard string?) (p :guard placeholder?)]
;;   ;;   `(and (str/starts-with? ~one-name ~s)
;;   ;;         (let [~one-name (subs ~one-name ~(count s))]
;;   ;;           ~(compile-a all-name one-name p)))
;;   ;;   [(p :guard placeholder?) (s :guard string?)]
;;   ;;   `(and (str/ends-with? ~one-name ~s)
;;   ;;         (let [~one-name (subs ~one-name 0 ~(count s))]
;;   ;;           ~(compile-a all-name one-name p)))))

;; (defn type-of [v]
;;   (cond (map? v) :map
;;         (vector? v) :vector
;;         :else (type v)))

;; (defmulti compile-a (fn [_ _ t] (type-of t)))

;; (defmethod compile-a :map
;;   [all-name one-name m]
;;   (compile-map all-name one-name m))

;; (defmethod compile-a :vector
;;   [all-name one-name v]
;;   (compile-vec all-name one-name v))

;; (defmethod compile-a String
;;   [all-name one-name v]
  
  
;; (defmethod compile-a :default ;; it's a value, we're done
;;   [_ _ v]
;;   v)

;; (defmacro compile [f]
;;   (compile-a `crouton-all# `crouton-one# f))

