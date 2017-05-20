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
     * @param Object name - the name to assoc at `:crouton/route` on match
     * *
     * @return Endpoint
     */
    fun endpoint(name: Any): Endpoint {
        return Endpoint(name)
    }

    /**
     * Factory function for a Slurp
     * A slurp will always match. Returns a map of all placeholders encountered,
     * plus two additional keys:
     * * `:crouton/route` which contains the provided `value`
     * * `:crouton/slurp` which contains any remaining url segments as a vector
     * @param Object value - the value to assoc at `:crouton/route` on match
     * *
     * @return Slurp
     */
    fun slurp(name: Keyword): Slurp {
        return Slurp(name)
    }

    /**
     * Factory function for a Placeholder
     * A Placeholder will match one segment unconditionally
     * On successful match (i.e. there is at least one segment remaining),
     * it forwards to the provided 'next' IRoute, adding to
     * the places map the matched segment string under the given `name`
     * @param Object name - the name to assoc the matched segment under
     * *
     * @param IRoute next - the route to forward to on successful match
     * *
     * @return Placeholder
     */
    fun placeholder(name: Any, next: IRoute): Placeholder {
        return Placeholder(name, next)
    }

    /**
     * Factory function for a RegexPH
     * A RegexPH will match one segment if it matches the provided regex
     * On successful match, it will forward to the provided `next` IRoute, adding to
     * the places map the matched segment string under the given `name`
     * Note that the pattern is anchored as if it was wrapped in ^ and $, that is
     * your regex must match the whole segment
     * @param Object name - the name to assoc the matched segment under
     * *
     * @param Pattern pat - the pattern to match against
     * *
     * @param IRoute next - the route to forward to on successful match
     * *
     * @return RegexPH
     */
    fun regex(name: Any, pat: Pattern, next: IRoute): RegexPH {
        return RegexPH(name, pat, next)
    }

    /**
     * Factory function for a LambdaPH
     * A LambdaPH will match one segment if it the provided Predicate returns non-nil
     * On successful match, it will forward to the provided `next` IRoute, adding to
     * the places map the return of the provided Predicate
     * @param Object name - the name to assoc the matched segment under
     * *
     * @param Predicate p - The predicate to test against
     * *
     * @param IRoute next - the route to forward to on successful match
     * *
     * @return LambdaPH
     */
    fun lambda(name: Any, l: Predicate, next: IRoute): LambdaPH {
        return LambdaPH(name, l, next)
    }

    /**
     * Factory function for a ClojurePH
     * A ClojurePH will match one segment if it the provided IFn returns non-nil
     * On successful match, it will forward to the provided `next` IRoute, adding to
     * the places map the return of the provided Predicate
     * @param Object name - the name to assoc the matched segment under
     * *
     * @param IFn ifn - The predicate to test against
     * *
     * @param IRoute next - the route to forward to on successful match
     * *
     * @return ClojurePH
     */
    fun clojure(name: Any, ifn: IFn, next: IRoute): ClojurePH {
        return ClojurePH(name, ifn, next)
    }

    /**
     * Factory function for a RouteMap
     * A RouteMap will look the segment up in the provided `map` of String to IRoute
     * On successful match, it will forward to IRoute returned from the map
     * @param Map name - a map of String to IRoute
     * *
     * @return RouteMap
     */
    fun routemap(map: Map<String, IRoute>): RouteMap {
        return RouteMap(map)
    }

    /**
     * Factory function for a Fallback
     * A Fallback will attempt to match the provided `first` IRoute. If this fails,
     * It will attempt to match the provided `second` IRoute.
     * @param IRoute first - the route to match first
     * *
     * @param IRoute second - the route to match otherwise
     * *
     * @return Fallback
     */
    fun fallback(first: IRoute, second: IRoute): Fallback {
        return Fallback(first, second)
    }

    /**
     * Factory function for a Choice
     * A choice will attempt to match a list of items in order
     * There must be at least one item in the list
     * @param IRoute first - the route to match first
     * *
     * @param IRoute second - the route to match otherwise
     * *
     * @return Fallback
     */
    fun choice(list: List<IRoute>): Choice {
        return Choice(list)
    }

    /**
     * Returns a clojure persistent vector of path segments
     * @param String path
     * *
     * @return Object (actually PersistentVector, but Clojure...)
     */
    fun parse_path(path: String): Any {
        return PathParser.INSTANCE.parse(path)
    }
}
