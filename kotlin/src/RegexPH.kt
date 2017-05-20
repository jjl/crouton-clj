package irresponsible.crouton

import java.util.regex.*
import clojure.lang.IPersistentVector
import clojure.lang.IPersistentMap

class RegexPH(n: Any, private val pat: Pattern, next: IRoute) : PH(n, next) {
  override fun match(pieces: IPersistentVector, places: IPersistentMap): Any? {
    val piece = pieces.nth(0, null)
    if (piece == null) return null
    val piece2 = piece as String // fucking kotlin
    val m = pat.matcher(piece)
    return if (m.matches()) forward(piece, pieces, places) else null
  }
}
