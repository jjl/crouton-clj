package irresponsible.crouton;

import clojure.lang.IFn;
import clojure.java.api.Clojure;
    
public enum Wrapjure {
  CLOJURE;
  public final IFn assoc        = Clojure.var("clojure.core", "assoc");
  public final IFn assoc_       = Clojure.var("clojure.core", "assoc!");
  public final IFn conj_        = Clojure.var("clojure.core", "conj!");
  public final IFn get          = Clojure.var("clojure.core", "get");
  public final IFn empty_       = Clojure.var("clojure.core", "empty?");
  public final IFn keyword      = Clojure.var("clojure.core", "keyword");
  public final IFn persistent_  = Clojure.var("clojure.core", "persistent!");
  public final IFn subvec       = Clojure.var("clojure.core", "subvec");
  public final IFn transient_   = Clojure.var("clojure.core", "transient");
  public final IFn vector       = Clojure.var("clojure.core", "vector");
}
