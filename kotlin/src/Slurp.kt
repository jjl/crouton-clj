package irresponsible.crouton

import clojure.lang.IPersistentVector
import clojure.lang.PersistentArrayMap
import clojure.lang.IPersistentMap
import clojure.lang.Keyword

class Slurp(private val value: Any) : AEndpoint() {
    private val route = Keyword.intern("crouton", "route")
    private val slurp = Keyword.intern("crouton", "slurp")

    override fun match(pieces: IPersistentVector, places: IPersistentMap): Any {
        // if (0 == places.count()) return PersistentArrayMap.EMPTY.assoc(ret,val);
        return places.assoc(route, value).assoc(slurp, pieces)
    }
}
