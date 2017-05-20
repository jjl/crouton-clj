package irresponsible.crouton

import clojure.lang.IPersistentVector
import clojure.lang.IPersistentMap
import java.util.ArrayList

class Choice(choices: List<IRoute>?) : IRoute {
    private val routes: ArrayList<IRoute>

    init {
        if (null == choices)
            throw IllegalArgumentException("choices must not be nil")
        val count = choices.size
        if (count == 0)
            throw IllegalArgumentException("choices must contain at least one item")
        routes = ArrayList(choices)
    }

    override fun match(pieces: IPersistentVector, places: IPersistentMap): Any? {
        var ret: Any? = null
        for (route in routes) {
            ret = route.match(pieces, places)
            if (ret != null) return ret
        }
        return null
    }
}
