package irresponsible.crouton;

import clojure.lang.IPersistentVector;
import clojure.lang.IPersistentMap;

public abstract class AEndpoint implements IRoute {
  protected AEndpoint() {}
  public abstract Object match(IPersistentVector pieces, IPersistentMap places);
}
