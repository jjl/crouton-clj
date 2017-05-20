package irresponsible.crouton

import clojure.lang.IPersistentVector
import clojure.lang.IPersistentMap
import java.util.ArrayList

class Choice(choices: List<IRoute>) : IRoute {
    private val routes = ArrayList(choices)

    init {
      if (choices.size == 0) throw IllegalArgumentException("choices must contain at least one item")
    }

    override fun match(pieces: IPersistentVector, places: IPersistentMap): Any? {
        for (route in routes) {
          val ret = route.match(pieces, places)
	  if (ret != null) return ret
        }
        return null
    }
}
