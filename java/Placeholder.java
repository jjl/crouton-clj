package irresponsible.crouton;

import clojure.lang.IPersistentVector;
import clojure.lang.IPersistentMap;

public class Placeholder extends PH implements IRoute {
  public Placeholder(Object n, IRoute next) {
    super(n,next);
  }
  public final Object match(IPersistentVector pieces, IPersistentMap places) {
    String piece = (String) pieces.nth(0,null);
    return (piece != null) ? forward(piece, pieces,places) : null;
  }
}
