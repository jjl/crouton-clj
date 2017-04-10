package irresponsible.crouton;

import clojure.lang.IPersistentVector;
import clojure.lang.PersistentArrayMap;
import clojure.lang.ITransientMap;
import clojure.lang.Keyword;
import java.lang.IllegalArgumentException;

public class Slurp extends AEndpoint {
  private final Object route = Wrapjure.CLOJURE.keyword.invoke("crouton","route");
  private final Object slurp = Wrapjure.CLOJURE.keyword.invoke("crouton","slurp");
  private final Object val;

  public Slurp(Object v) {
    if (null == v)
      throw new IllegalArgumentException("value must not be nil");
    val = v;
  }
  public final Object match(IPersistentVector pieces, ITransientMap places) {
    // if (0 == places.count()) return PersistentArrayMap.EMPTY.assoc(ret,val);
    return places.assoc(route,val).assoc(slurp,pieces).persistent();
  }
}
