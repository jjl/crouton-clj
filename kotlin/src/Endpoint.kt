package irresponsible.crouton

import clojure.lang.IPersistentVector
import clojure.lang.PersistentArrayMap
import clojure.lang.IPersistentMap
import clojure.lang.Keyword

class Endpoint(private val value: Any) : AEndpoint() {
    private val ret = Keyword.intern("crouton", "route")
    override fun match(pieces: IPersistentVector, places: IPersistentMap): Any? {
        if (0 != pieces.length()) return null
        if (0 == places.count()) return PersistentArrayMap.EMPTY.assoc(ret, value)
        return if (0 == pieces.count()) places.assoc(ret, value) else null
    }
}
