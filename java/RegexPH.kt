package irresponsible.crouton

import java.util.regex.*
import clojure.lang.IPersistentVector
import clojure.lang.IPersistentMap

class RegexPH(n: Any, private val pat: Pattern?, next: IRoute) : PH(n, next) {
    init {
        if (null == pat)
            throw IllegalArgumentException("pattern must not be nil")
    }

    override fun match(pieces: IPersistentVector, places: IPersistentMap): Any? {
        val piece = pieces.nth(0, null) as String ?: return null
        val m = pat.matcher(piece)
        return if (m.matches()) forward(piece, pieces, places) else null
    }
}
