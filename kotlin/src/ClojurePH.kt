package irresponsible.crouton

import clojure.lang.IFn
import clojure.lang.IPersistentVector
import clojure.lang.IPersistentMap

class ClojurePH(n: Any, private val ifn: IFn, next: IRoute) : PH(n, next) {
    override fun match(pieces: IPersistentVector, places: IPersistentMap): Any? {
      val piece = pieces.nth(0, null)
      if (piece == null) return null
      val piece2 = piece as String
      val ret = ifn.invoke(piece)
      return if (ret != null) forward(piece, pieces, places) else null
    }
}
