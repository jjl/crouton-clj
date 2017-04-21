package irresponsible.crouton;

import clojure.lang.IPersistentVector;
import clojure.lang.PersistentArrayMap;
import clojure.lang.IPersistentMap;
import clojure.lang.Keyword;

public class Slurp extends AEndpoint {
  private final Object route = Keyword.intern("crouton","route");
  private final Object slurp = Keyword.intern("crouton","slurp");
  private final Object val;

  public Slurp(Object v) {
    if (null == v)
      throw new IllegalArgumentException("value must not be nil");
    val = v;
  }
  public final Object match(IPersistentVector pieces, IPersistentMap places) {
    // if (0 == places.count()) return PersistentArrayMap.EMPTY.assoc(ret,val);
    return places.assoc(route,val).assoc(slurp,pieces);
  }
}
