package irresponsible.crouton;

import clojure.lang.PersistentVector;
import clojure.lang.ITransientCollection;

public enum PathParser {
  INSTANCE; // Singleton
  // clojure core functions

  public final Object parse(String path) {
    switch(path) {
    case "/":
    case "":
      return PersistentVector.EMPTY;
    default:
      int end = path.length();
      ITransientCollection working = (ITransientCollection) PersistentVector.EMPTY.asTransient();
      for (int start = 0 ; ;) {
        while (start < end && 47 == path.codePointAt(start)) start++; // avoid empty segments
        if (start < end) {
          int i = path.indexOf(47,start);
          if (-1 != i) {
            working = working.conj(path.substring(start, i));
            start = i + 1;
          } else { // we fell off the end of the string
            working = working.conj(path.substring(start));
            break;
          }
        } else break;
      }
      return working.persistent();
    }
  }
}
