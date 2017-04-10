(ns irresponsible.crouton
  #?(:clj (:import [java.util.regex Pattern]
                   [irresponsible.crouton PathParser]))
  #?(:clj (:refer-clojure :exclude [compile])))

;; a string is a literal piece of url
;; a routes is compojure-style try in order
;; a wildcard is 

;; (s/def ::http-method #{:get :post :put :head :delete})

#?
(:clj
 (defn parse-path [^String path]
   (.parse PathParser/INSTANCE path))
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

;; (parse-path "///foo///bar///baz-123")

(defrecord Place [name validator])

(defn placeholder? [p]
  (instance? Place p))

(defn ?
  ([name]
   (? name nil))
  ([name validator]
   (->Place name validator)))

;; (defn ?-tag
;;   ([v]
;;    (cond (vec? v) (? v
;;   ([name validator]) 

(defn drop-piece
  [c]
  (subvec c 1))

(defn fst
  [i]
  (get i 0))

(defn make-fn
  [all-name one-name acc-name & exprs]
  `(fn [~all-name ~acc-name]
     (let [~one-name (fst ~all-name)] ;; this is the optimum place to put this
       (or ~@exprs))))

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

  
;; (use 'criterium.core)

;; (with-progress-reporting
;;   (bench
;;    (parse-path "/foo/bar/baz-123")
;;    :verbose))

;; {"admin" {"user" {#? [:id #""] :user}}
;;  "blogs" {"hello-world" {
;;                          :end :hello-world}
;;           :end :blogs-list}
;;  :crouton/end :home}
;; (pick "admin" (pick "user" (pick (? :id #"")
                    
                
;;        ""
;;        ""
;;        ""
;;        :crouton/end :home]

;; #routes []
;; #match  {"login" :auth/login}
;; #wildcard []
;; #constrain [{:require-perms #{:admin} :require-method #{:get}}]

;; [""
;;  "login"
;;  "logout"
;;  "find-a-home" (WRAP lemon.auth.auth-region []
;;                      [""        lemon.routes.front.home
;;                       (GET  "profile") lemon.routes.front/profile-get
;;                       (POST "profile") lemon.routes.front/profile-post
;;                       (GET  "docs")    lemon.routes.front/docs-get
;;                       (POST "docs")    lemon.routes.front/docs-post])
;;  "find-a-tenant" [""
;;                   "profile"
;;                   "docs"]]

;; (s/def ::string  string?)
;; (s/def ::keyword keyword?)
;; (s/def ::symbol  symbol?)
;; (s/def ::map     (s/map-of ::match any? :gen-max 3))
;; (s/def ::vector  (s/coll-of ::match :gen-max 3 :kind vector?))
;; (s/def ::route   (ss/some-spec ::string ::keyword ::symbol ::map ::vector))

;; (defrecord Done [val])

;; (defn done ^Done [v]
;;   (->Done v))

;; (defprotocol Cursor
;;   (advance [c])
;;   (look    [c]))

;; (defrecord ArrayCursor [^int count ^int index storage]
;;   (advance [c]
;;     (->ArrayCursor count (unchecked-add index 1) storage))
;;   (look [c]
;;     (when (< index count)
;;       (aget storage index))))

;; (defn new-array-cursor
;;   ([arr]
;;    (new-array-cursor arr 0 (alength arr)))
;;   ([arr index]
;;    (new-array-cursor arr index (alength arr)))
;;   ([arr index count]
;;    (->ArrayCursor count index arr)))

;; (defprotocol MatchStatey
;;   (with-matches [st m])
;;   (with-extra   [st e])
;;   (with-cursor  [st c]))

;; (deftype MatchState [cursor matches extra]
;;   MatchStatey
;;   (with-matches [st m] (->MatchState cursor m extra))
;;   (with-extra   [st e] (->MatchState cursor matches e))
;;   (with-cursor  [st c] (->MatchState c matches extra)))

;; (defn new-match-state [^String path]
;;   (-> (new-array-cursor (.split path "/"))
;;       (->MatchState {} {})))

;; (defmulti build-a (fn [k & _] (type k)))

;; (defmethod build-a Done
;;   [^Done d ms]
;;   (.-val d))

;; (defmethod build-a String
;;   [s ms nxt]
;;   (let [ms2 (with-meta ms {:tag MatchState})]
;;     `(let [cur# (.-cursor ~ms2)
;;            fst# (look cur#)]
;;        (if (= ~s fst#)
;;          (build-a

     
;; (defmethod build-a ::keyword [s] ...)
;; (defmethod build-a ::symbol [s] ...)
;; (defmethod build-a ::map [s] ...)
;; (defmethod build-a ::vector [s] ...)


;; (defn build [route]
;;   (let [st-name (with-meta `st# {:tag MatchState})]
;;     `(fn [^String path#]
;;        (let [~st-name  (new-match-state path#)
;;            ^String first# (peek-piece ~st-name)]
;;        (when #?(:clj (.isEmpty first#) :cljs (= "" first#))
;;            (move-cursor ~st-name))
;;        ~(build-a route st-name)))))

;; (defn make-matcher [route]

;; (defn split [path])

;; (defprotocol Router
;;   (add-route [self path method])
;;   (lookup    [self req]))

;; (defrecord Crouton [at]
;;   (add-route [self path method]
;;     (let [ps (clojure.string/split path #"/")]
      
;;       ))
;;   (lookup    [self req]
;;     ))

