package irresponsible.crouton;

public class Preds {
  public class Dec {
    public final Object test(String s) {
      try {
        return new Long(Long.parseLong(s));
      } catch (NumberFormatException e) {
        return null;
      }
    }
  }
  public class Hex {
    public final Object test(String s) {
      try {
        return new Long(Long.parseLong(s, 16));
      } catch (NumberFormatException e) {
        return null;
      }
    }
  }
  // public class Tag {
  //   public final Object test(String s) {
  //     try {
  //       return new Long(Long.parseLong(s, 16));
  //     } catch (NumberFormatException e) {
  //       return null;
  //     }
  //   }
  // }
}
