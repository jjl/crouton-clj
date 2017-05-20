package irresponsible.crouton

import clojure.lang.IPersistentVector
import clojure.lang.IPersistentMap

class LambdaPH(n: Any, private val lambda: Predicate?, next: IRoute) : PH(n, next) {
    init {
        if (null == lambda)
            throw IllegalArgumentException("lambda must not be nil")
    }

    override fun match(pieces: IPersistentVector, places: IPersistentMap): Any? {
        val piece = pieces.nth(0, null) as String ?: return null
        val ret = lambda.test(piece)
        return if (ret != null) forward(ret, pieces, places) else null
    }
}
