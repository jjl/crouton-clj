package irresponsible.crouton

import clojure.lang.IPersistentVector
import clojure.lang.IPersistentMap

class Placeholder(n: Any, next: IRoute) : PH(n, next), IRoute {
    override fun match(pieces: IPersistentVector, places: IPersistentMap): Any? {
        val piece = pieces.nth(0, null)
        return if (piece != null) forward(piece, pieces, places) else null
    }
}
