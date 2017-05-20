package irresponsible.crouton

import clojure.lang.IPersistentVector
import clojure.lang.IPersistentMap

class Fallback(private val first: IRoute?, private val second: IRoute?) : IRoute {
    init {
        if (null == first || null == second)
            throw IllegalArgumentException("Both routes provided to Either must not be non-nil")
    }

    override fun match(pieces: IPersistentVector, places: IPersistentMap): Any {
        val ret = first.match(pieces, places)
        return ret ?: second.match(pieces, places)
    }
}
