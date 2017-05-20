package irresponsible.crouton

import java.util.HashMap
import clojure.lang.IPersistentVector
import clojure.lang.IPersistentMap
import clojure.lang.RT

class RouteMap(m: Map<String, IRoute>) : IRoute {
    val m2 = HashMap(m);
    override fun match(pieces: IPersistentVector, places: IPersistentMap): Any? {
        val piece = pieces.nth(0, null) as String ?: return null
        val next = m2.get(piece)
        return next?.match(RT.subvec(pieces, 1, pieces.count()), places)
    }
}
