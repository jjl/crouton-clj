package irresponsible.crouton;

import clojure.lang.IPersistentVector;
import clojure.lang.ITransientMap;

public class Crouton {
  public static final Object match(Route r, Object pieces, Object places) {
    return r.match((IPersistentVector) pieces, (ITransientMap) places);      
  }
}
