package irresponsible.crouton

import clojure.lang.IFn
import clojure.lang.IPersistentVector
import clojure.lang.IPersistentMap

class ClojurePH(n: Any, private val ifn: IFn?, next: IRoute) : PH(n, next) {
    init {
        if (null == ifn)
            throw IllegalArgumentException("lambda must not be nil")
    }

    override fun match(pieces: IPersistentVector, places: IPersistentMap): Any? {
        val piece = pieces.nth(0, null) as String ?: return null
        val ret = ifn.invoke(piece)
        return if (ret != null) forward(piece, pieces, places) else null
    }
}
