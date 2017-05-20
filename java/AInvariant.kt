package irresponsible.crouton

import clojure.lang.IPersistentVector
import clojure.lang.IPersistentMap
import clojure.lang.RT

/**
 * When an invariant route matches, it always forwards to the same route
 */
abstract class AInvariant protected constructor(protected var next: IRoute?) : IRoute {
    init {
        if (null == next)
            throw IllegalArgumentException("next must not be nil")
    }

    abstract override fun match(pieces: IPersistentVector, places: IPersistentMap): Any
    protected fun forward(pieces: IPersistentVector, places: IPersistentMap): Any {
        return next.match(RT.subvec(pieces, 1, pieces.count()), places)
    }
}
