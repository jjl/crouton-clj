package irresponsible.crouton;

public class Preds {
  public class PosDecInt {
    public final Object test(String s) {
      try {
	if (s.startsWith("-")) return null;
        return new Long(Long.parseLong(s));
      } catch (NumberFormatException e) {
        return null;
      }
    }
  }
  public class PosHexInt {
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
