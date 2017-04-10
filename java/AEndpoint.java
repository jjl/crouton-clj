package irresponsible.crouton;

import clojure.lang.IPersistentVector;
import clojure.lang.ITransientMap;

public abstract class AEndpoint {
  protected AEndpoint() {}
  public abstract Object match(IPersistentVector pieces, ITransientMap places);
}
