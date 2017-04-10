package irresponsible.crouton;

import clojure.lang.IPersistentVector;
import clojure.lang.PersistentArrayMap;
import clojure.lang.ITransientMap;
import clojure.lang.Keyword;
import java.lang.IllegalArgumentException;

public class Endpoint extends AEndpoint implements Route {
  private final Object ret = Wrapjure.CLOJURE.keyword.invoke("crouton","route");
  private final Object val;

  public Endpoint(Object v) {
    if (null == v)
      throw new IllegalArgumentException("value must not be nil");
    val = v;
  }
  public final Object match(IPersistentVector pieces, ITransientMap places) {
    if (0 != pieces.length()) return null;
    if (0 == places.count()) return PersistentArrayMap.EMPTY.assoc(ret,val);
    return 0 == pieces.count() ? places.assoc(ret,val).persistent() : null;
  }
}