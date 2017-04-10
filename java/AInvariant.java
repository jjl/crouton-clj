package irresponsible.crouton;

import clojure.lang.IPersistentVector;
import clojure.lang.ITransientMap;
import clojure.lang.RT;

/**
 * When an invariant route matches, it always forwards to the same route
 */
public abstract class AInvariant {
  private Route next;
  protected AInvariant(Route nxt) {
    if (null == nxt)
      throw new IllegalArgumentException("next must not be nil");
    next = nxt;
  }
  final protected Object forward(IPersistentVector pieces, ITransientMap places) {
    return next.match(RT.subvec(pieces, 1, pieces.count()), places);
  }
}
