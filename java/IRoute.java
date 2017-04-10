package irresponsible.crouton;

import clojure.lang.IPersistentVector;
import clojure.lang.ITransientMap;

public interface IRoute {
  public Object match(IPersistentVector pieces, ITransientMap places);
}
