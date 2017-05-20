package irresponsible.crouton

import java.util.HashMap
import clojure.lang.IPersistentVector
import clojure.lang.IPersistentMap
import clojure.lang.RT

class RouteMap(m: Map<String, IRoute>?) : IRoute {
    private val map: HashMap<String, IRoute>

    init {
        if (null == m)
            throw IllegalArgumentException("map must not be nil")
        map = HashMap(m)
    }

    override fun match(pieces: IPersistentVector, places: IPersistentMap): Any? {
        val piece = pieces.nth(0, null) as String ?: return null
        val next = map[piece]
        return next?.match(RT.subvec(pieces, 1, pieces.count()), places)
    }
}
