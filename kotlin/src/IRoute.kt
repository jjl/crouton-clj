package irresponsible.crouton

import clojure.lang.IPersistentVector
import clojure.lang.IPersistentMap

interface IRoute {
    fun match(pieces: IPersistentVector, places: IPersistentMap): Any?
}
