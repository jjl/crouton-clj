package irresponsible.crouton;

import clojure.lang.IPersistentVector;
import clojure.lang.IPersistentMap;
import java.util.List;
import java.util.ArrayList;

public class Choice implements IRoute {
  private final ArrayList<IRoute> routes;
  public Choice(List<IRoute> choices) {
    if (null == choices)
      throw new IllegalArgumentException("choices must not be nil");
    int count = choices.size();
    if (count == 0)
      throw new IllegalArgumentException("choices must contain at least one item");
    routes = new ArrayList<IRoute>(choices);
  }
  public final Object match(IPersistentVector pieces, IPersistentMap places) {
    Object ret = null;
    for(IRoute route : routes) {
      ret = route.match(pieces, places);
      if (ret != null) return ret;
    }
    return null;
  }
}
