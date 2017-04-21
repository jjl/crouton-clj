package irresponsible.crouton;

import clojure.lang.IPersistentVector;
import clojure.lang.IPersistentMap;

public interface IRoute {
  public Object match(IPersistentVector pieces, IPersistentMap places);
}
