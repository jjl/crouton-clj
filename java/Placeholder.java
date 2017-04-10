package irresponsible.crouton;

import clojure.lang.IPersistentVector;
import clojure.lang.ITransientMap;

public class Placeholder extends PH implements Route {
  public Placeholder(Object n, Route next) {
    super(n,next);
  }
  public final Object match(IPersistentVector pieces, ITransientMap places) {
    String piece = (String) pieces.nth(0,null);
    return (piece != null) ? forward(pieces,places) : null;
  }
}
