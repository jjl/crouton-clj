package irresponsible.crouton;

import clojure.lang.IFn;
import clojure.java.api.Clojure;
import java.lang.IllegalArgumentException;
import java.util.HashMap;
import clojure.lang.IPersistentVector;
import clojure.lang.ITransientMap;
import clojure.lang.RT;


public class RouteMap implements Route {
  private final HashMap<String, Route> map;
  public RouteMap(HashMap<String, Route> m) {
    if (null == m)
      throw new IllegalArgumentException("map must not be nil");
    map = m;
  }
  public final Object match(IPersistentVector pieces, ITransientMap places) {
    String piece = (String) pieces.nth(0, null);
    if (piece == null) return null;
    Route next = map.get(piece);
    return (next != null) ? next.match(RT.subvec(pieces, 1, pieces.count()), places) : null;
  }
}
