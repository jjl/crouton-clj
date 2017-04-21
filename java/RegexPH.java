package irresponsible.crouton;

import java.util.regex.*;
import clojure.lang.IPersistentVector;
import clojure.lang.IPersistentMap;

public final class RegexPH extends PH {
  private final Pattern pat;
  public RegexPH(Object n, Pattern p, IRoute next) {
    super(n,next);
    if (null == p)
      throw new IllegalArgumentException("pattern must not be nil");
    pat = p;
  }
  public final Object match(IPersistentVector pieces, IPersistentMap places) {
    String piece = (String) pieces.nth(0, null);
    if (piece == null) return null;
    Matcher m = pat.matcher(piece);
    return m.matches() ? forward(piece, pieces, places) : null;
  }
}
