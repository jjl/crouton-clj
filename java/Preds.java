package irresponsible.crouton;

public class Preds {
  public enum PosInt implements Predicate {
    INSTANCE;
    public final Object test(String s) {
      try {
	if (s.startsWith("-")) return null;
        return new Long(Long.parseLong(s));
      } catch (NumberFormatException e) {
        return null;
      }
    }
  }
  public enum PosHexInt implements Predicate {
    INSTANCE;
    public final Object test(String s) {
      try {
	if (s.startsWith("-")) return null;
        return new Long(Long.parseLong(s, 16));
      } catch (NumberFormatException e) {
        return null;
      }
    }
  }
}
