package irresponsible.crouton;

import clojure.lang.IFn;
import clojure.lang.IPersistentVector;
import clojure.lang.ITransientMap;
import java.util.regex.Pattern;
import java.util.Map;

public class Crouton {
  /**
   * Factory function for an Endpoint.
   * An Endpoint will match when there are no more segments in the url
   * On successful match, it will return a map of all placeholders encountered
   * Plus an additional key `:crouton/route` which contains the provided `value`
   * @param Object value - the value to assoc at `:crouton/route` on match
   * @return Endpoint
   */
  public static Endpoint endpoint(Object value) {
    return new Endpoint(value);
  }
  /**
   * Factory function for a Slurp
   * A slurp will always match. Returns a map of all placeholders encountered,
   * plus two additional keys:
   *   * `:crouton/route` which contains the provided `value`
   *   * `:crouton/slurp` which contains any remaining url segments as a vector
   * @param Object value - the value to assoc at `:crouton/route` on match
   * @return Slurp
   */
  public static Slurp slurp(Object name) {
    return new Slurp(name);
  }
  /**
   * Factory function for a Placeholder
   * A Placeholder will match one segment unconditionally
   * On successful match (i.e. there is at least one segment remaining),
   * it forwards to the provided 'next' IRoute, adding to
   * the places map the matched segment string under the given `name`
   * @param Object name - the name to assoc the matched segment under
   * @param IRoute next - the route to forward to on successful match
   * @return Placeholder
   */
  public static Placeholder placeholder(Object name, IRoute next) {
    return new Placeholder(name,next);
  }
  /**
   * Factory function for a RegexPH
   * A RegexPH will match one segment if it matches the provided regex
   * On successful match, it will forward to the provided `next` IRoute, adding to
   * the places map the matched segment string under the given `name`
   * Note that the pattern is anchored as if it was wrapped in ^ and $, that is
   * your regex must match the whole segment
   * @param Object name - the name to assoc the matched segment under
   * @param Pattern pat - the pattern to match against
   * @param IRoute next - the route to forward to on successful match
   * @return RegexPH
   */
  public static RegexPH regex(Object name, Pattern pat, IRoute next) {
    return new RegexPH(name, pat, next);
  }
  /**
   * Factory function for a LambdaPH
   * A LambdaPH will match one segment if it the provided Predicate returns non-nil
   * On successful match, it will forward to the provided `next` IRoute, adding to
   * the places map the return of the provided Predicate
   * @param Object name - the name to assoc the matched segment under
   * @param Predicate p - The predicate to test against
   * @param IRoute next - the route to forward to on successful match
   * @return LambdaPH
   */
  public static LambdaPH lambda(Object name, Predicate l, IRoute next) {
    return new LambdaPH(name, l, next);
  }
  /**
   * Factory function for a ClojurePH
   * A ClojurePH will match one segment if it the provided IFn returns non-nil
   * On successful match, it will forward to the provided `next` IRoute, adding to
   * the places map the return of the provided Predicate
   * @param Object name - the name to assoc the matched segment under
   * @param IFn ifn - The predicate to test against
   * @param IRoute next - the route to forward to on successful match
   * @return ClojurePH
   */
  public static ClojurePH clojure(Object name, IFn ifn, IRoute next) {
    return new ClojurePH(name, ifn, next);
  }
  /**
   * Factory function for a RouteMap
   * A RouteMap will look the segment up in the provided `map` of String to IRoute
   * On successful match, it will forward to IRoute returned from the map
   * @param Map name - a map of String to IRoute
   * @return RouteMap
   */
  public static RouteMap routemap(Map<String,IRoute> map) {
    return new RouteMap(map);
  }
  /**
   * Factory function for a Fallback
   * A Fallback will attempt to match the provided `first` IRoute. If this fails,
   * It will attempt to match the provided `second` IRoute.
   * @param IRoute first - the route to match first
   * @param IRoute second - the route to match otherwise
   * @return Fallback
   */
  public static Fallback fallback(IRoute first, IRoute second) {
    return new Fallback(first,second);
  }
}
