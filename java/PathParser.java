package irresponsible.crouton;

import clojure.lang.IFn;
import clojure.java.api.Clojure;

public enum PathParser {
  INSTANCE; // Singleton
  // clojure core functions
  private final Object empty = Wrapjure.CLOJURE.vector.invoke();

  public final Object parse(String path) {
    switch(path) {
    case "/":
    case "":
      return empty;
    default:
      int end = path.length();
      Object working = Wrapjure.CLOJURE.transient_.invoke(empty);
      for (int start = 0 ; ;) {
        while (start < end && 47 == path.codePointAt(start)) start++; // avoid empty segments
        if (start < end) {
          int i = path.indexOf(47,start);
          if (-1 != i) {
            working = Wrapjure.CLOJURE.conj_.invoke(working, path.substring(start, i));
            start = i + 1;
          } else { // we fell off the end of the string
            working = Wrapjure.CLOJURE.conj_.invoke(working, path.substring(start));
            break;
          }
        } else break;
      }
      return Wrapjure.CLOJURE.persistent_.invoke(working);
    }
  }
}
