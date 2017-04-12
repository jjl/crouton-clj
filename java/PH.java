package irresponsible.crouton;

import clojure.lang.IPersistentVector;
import clojure.lang.ITransientMap;
import clojure.lang.RT;

public abstract class PH extends AInvariant {
  protected final Object name;
  protected PH(Object n, IRoute nxt) {
    super(nxt);
    if (null == n)
      throw new IllegalArgumentException("name must not be nil");
    name = n;
  }
  final protected Object forward(Object val, IPersistentVector pieces, ITransientMap places) {
    return next.match(RT.subvec(pieces, 1, pieces.count()),
		      places.assoc(name, val));
  }
};
