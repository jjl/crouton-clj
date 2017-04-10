package irresponsible.crouton;

import clojure.lang.IPersistentVector;
import clojure.lang.ITransientMap;

public abstract class PH extends AInvariant {
  private final Object name;
  protected PH(Object n, Route nxt) {
    super(nxt);
    if (null == n)
      throw new IllegalArgumentException("name must not be nil");
    name = n;
  }
};
