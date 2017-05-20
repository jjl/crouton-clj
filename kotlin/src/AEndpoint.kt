package irresponsible.crouton

import clojure.lang.IPersistentVector
import clojure.lang.IPersistentMap

abstract class AEndpoint protected constructor() : IRoute {
    abstract override fun match(pieces: IPersistentVector, places: IPersistentMap): Any?
}
