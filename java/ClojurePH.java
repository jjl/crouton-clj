package irresponsible.crouton;

import clojure.lang.IFn;
import clojure.lang.IPersistentVector;
import clojure.lang.ITransientMap;

public class ClojurePH extends PH {
  private final IFn ifn;
  public ClojurePH(Object n, IFn i, IRoute next) {
    super(n,next);
    if (null == i)
      throw new IllegalArgumentException("lambda must not be nil");
    ifn = i;
  }
  public final Object match(IPersistentVector pieces, ITransientMap places) {
    String piece = (String) pieces.nth(0, null);
    if (piece == null) return null;
    Object ret = ifn.invoke(piece);
    return (ret != null) ? forward(piece, pieces, places) : null;
  }
}
