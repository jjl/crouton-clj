package irresponsible.crouton

import clojure.lang.IPersistentVector
import clojure.lang.PersistentArrayMap
import clojure.lang.IPersistentMap
import clojure.lang.Keyword

class Endpoint(private val `val`: Any?) : AEndpoint() {
    private val ret = Keyword.intern("crouton", "route")

    init {
        if (null == `val`)
            throw IllegalArgumentException("value must not be nil")
    }

    override fun match(pieces: IPersistentVector, places: IPersistentMap): Any? {
        if (0 != pieces.length()) return null
        if (0 == places.count()) return PersistentArrayMap.EMPTY.assoc(ret, `val`)
        return if (0 == pieces.count()) places.assoc(ret, `val`) else null
    }
}
