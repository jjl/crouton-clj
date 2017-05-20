package irresponsible.crouton

import clojure.lang.IPersistentVector
import clojure.lang.IPersistentMap

class LambdaPH(n: Any, private val lambda: Predicate, next: IRoute) : PH(n, next) {
  override fun match(pieces: IPersistentVector, places: IPersistentMap): Any? {
    val piece = pieces.nth(0, null)
    if (piece == null) return null
    val piece2 = piece as String
    val ret = lambda.test(piece)
    return if (ret != null) forward(ret, pieces, places) else null
  }
}
