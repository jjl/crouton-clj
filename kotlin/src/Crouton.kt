package irresponsible.crouton

import clojure.lang.IFn
import clojure.lang.IPersistentVector
import clojure.lang.IPersistentMap
import clojure.lang.Keyword
import java.util.regex.Pattern

/**
 * The Crouton class has *everything* you need to use Crouton effectively
 * Most of the methods are factories for constructing instances of IRoute
 */
object Crouton {
    /**
     * Factory function for an Endpoint.
     * An Endpoint will match when there are no more segments in the url
     * On successful match, it will return a map of all placeholders encountered
     * Plus an additional key `:crouton/route` which contains the provided `value`
     * @param name Object - the name to assoc at `:crouton/route` on match
     * @return Endpoint
     */
    @Suppress("unused")
    fun endpoint(name: Any): Endpoint = Endpoint(name)

    /**
     * Factory function for a Slurp
     * A slurp will always match. Returns a map of all placeholders encountered,
     * plus two additional keys:
     * * `:crouton/route` which contains the provided `value`
     * * `:crouton/slurp` which contains any remaining url segments as a vector
     * @param name Object - the value to assoc at `:crouton/route` on match
     * @return Slurp
     */
    @Suppress("unused")
    fun slurp(name: Keyword): Slurp = Slurp(name)

    /**
     * Factory function for a Placeholder
     * A Placeholder will match one segment unconditionally
     * On successful match (i.e. there is at least one segment remaining),
     * it forwards to the provided 'next' IRoute, adding to
     * the places map the matched segment string under the given `name`
     * @param name Object - the name to assoc the matched segment under
     * @param next IRoute - the route to forward to on successful match
     * @return Placeholder
     */
    @Suppress("unused")
    fun placeholder(name: Any, next: IRoute): Placeholder = Placeholder(name, next)

    /**
     * Factory function for a RegexPH
     * A RegexPH will match one segment if it matches the provided regex
     * On successful match, it will forward to the provided `next` IRoute, adding to
     * the places map the matched segment string under the given `name`
     * Note that the pattern is anchored as if it was wrapped in ^ and $, that is
     * your regex must match the whole segment
     * @param name Object - the name to assoc the matched segment under
     * @param pat Pattern - the pattern to match against
     * @param next IRoute - the route to forward to on successful match
     * @return RegexPH
     */
    @Suppress("unused")
    fun regex(name: Any, pat: Pattern, next: IRoute): RegexPH = RegexPH(name, pat, next)

    /**
     * Factory function for a LambdaPH
     * A LambdaPH will match one segment if it the provided Predicate returns non-nil
     * On successful match, it will forward to the provided `next` IRoute, adding to
     * the places map the return of the provided Predicate
     * @param name Object - the name to assoc the matched segment under
     * @param l Predicate - The predicate to test against
     * @param next IRoute - the route to forward to on successful match
     * @return LambdaPH
     */
    @Suppress("unused")
    fun lambda(name: Any, l: Predicate, next: IRoute): LambdaPH = LambdaPH(name, l, next)

    /**
     * Factory function for a ClojurePH
     * A ClojurePH will match one segment if it the provided IFn returns non-nil
     * On successful match, it will forward to the provided `next` IRoute, adding to
     * the places map the return of the provided Predicate
     * @param name Object - the name to assoc the matched segment under
     * @param ifn IFn - The predicate to test against
     * @param next IRoute - the route to forward to on successful match
     * @return ClojurePH
     */
    @Suppress("unused")
    fun clojure(name: Any, ifn: IFn, next: IRoute): ClojurePH = ClojurePH(name, ifn, next)

    /**
     * Factory function for a RouteMap
     * A RouteMap will look the segment up in the provided `map` of String to IRoute
     * On successful match, it will forward to IRoute returned from the map
     * @param map Map - a map of String to IRoute
     * @return RouteMap
     */
    @Suppress("unused")
    fun routemap(map: Map<String, IRoute>): RouteMap = RouteMap(map)

    /**
     * Factory function for a Fallback
     * A Fallback will attempt to match the provided `first` IRoute. If this fails,
     * It will attempt to match the provided `second` IRoute.
     * @param first IRoute - the route to match first
     * @param second IRoute - the route to match otherwise
     * @return Fallback
     */
    @Suppress("unused")
    fun fallback(first: IRoute, second: IRoute): Fallback = Fallback(first, second)

    /**
     * Factory function for a Choice
     * A choice will attempt to match a list of items in order
     * There must be at least one item in the list
     * @param list List<IRoute> - the route to match first
     * @return Fallback
     */
    @Suppress("unused")
    fun choice(list: List<IRoute>): Choice = Choice(list)

    /**
     * Returns a clojure persistent vector of path segments
     * @param path String
     * @return Object (actually PersistentVector, but Clojure...)
     */
    @Suppress("unused")
    fun parse_path(path: String): Any = PathParser.parse(path)
}
