package irresponsible.crouton;

import clojure.lang.IPersistentVector;
import clojure.lang.IPersistentMap;
import clojure.lang.RT;

/**
 * When an invariant route matches, it always forwards to the same route
 */
public abstract class AInvariant implements IRoute {
  protected IRoute next;
  protected AInvariant(IRoute nxt) {
    if (null == nxt)
      throw new IllegalArgumentException("next must not be nil");
    next = nxt;
  }
  public abstract Object match(IPersistentVector pieces, IPersistentMap places);
  final protected Object forward(IPersistentVector pieces, IPersistentMap places) {
    return next.match(RT.subvec(pieces, 1, pieces.count()), places);
  }
}
