package irresponsible.crouton;
import clojure.lang.IFn;
import clojure.java.api.Clojure;

public enum PathParser {
  INSTANCE; // Singleton
  // clojure core functions
  private final IFn cvector = Clojure.var("clojure.core", "vector");
  private final IFn ctransient = Clojure.var("clojure.core", "transient");
  private final IFn cpersistent = Clojure.var("clojure.core", "persistent!");
  private final IFn cconj = Clojure.var("clojure.core","conj!");
  private final Object empty = cvector.invoke();

  public final Object parse(String path) {
    switch(path) {
    case "/":
    case "":
      return empty;
    default:
      int end = path.length();
      Object working = ctransient.invoke(empty);
      for (int start = 0 ; ;) {
        while (start < end && 47 == path.codePointAt(start)) start++; // avoid empty segments
        if (start < end) {
          int i = path.indexOf(47,start);
          if (-1 != i) {
            working = cconj.invoke(working, path.substring(start, i));
            start = i + 1;
          } else { // we fell off the end of the string
            working = cconj.invoke(working, path.substring(start));
            break;
          }
        } else break;
      }
      return cpersistent.invoke(working);
    }
  }
}
