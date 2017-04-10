package irresponsible.crouton;

import clojure.lang.IFn;
import clojure.java.api.Clojure;
import java.lang.IllegalArgumentException;
import java.util.HashMap;
import java.util.Map;
import clojure.lang.IPersistentVector;
import clojure.lang.ITransientMap;
import clojure.lang.RT;

public class RouteMap implements IRoute {
  private final HashMap<String, IRoute> map;
  public RouteMap(Map<String, IRoute> m) {
    if (null == m)
      throw new IllegalArgumentException("map must not be nil");
    map = new HashMap<String,IRoute>(m);
  }
  public final Object match(IPersistentVector pieces, ITransientMap places) {
    String piece = (String) pieces.nth(0, null);
    if (piece == null) return null;
    IRoute next = map.get(piece);
    return (next != null) ? next.match(RT.subvec(pieces, 1, pieces.count()), places) : null;
  }
}
