package irresponsible.crouton;

import clojure.lang.IPersistentVector;
import clojure.lang.IPersistentMap;

public class LambdaPH extends PH {
  private final Predicate lambda;
  public LambdaPH(Object n, Predicate l, IRoute next) {
    super(n,next);
    if (null == l)
      throw new IllegalArgumentException("lambda must not be nil");
    lambda = l;
  }
  public final Object match(IPersistentVector pieces, IPersistentMap places) {
    String piece = (String) pieces.nth(0, null);
    if (piece == null) return null;
    Object ret = lambda.test(piece);
    return (ret != null) ? forward(ret, pieces, places) : null;
  }
}
