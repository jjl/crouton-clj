package irresponsible.crouton

import clojure.lang.IPersistentVector
import clojure.lang.IPersistentMap
import clojure.lang.RT

abstract class PH protected constructor(protected val name: Any?, nxt: IRoute) : AInvariant(nxt) {
    init {
        if (null == name)
            throw IllegalArgumentException("name must not be nil")
    }

    protected fun forward(`val`: Any, pieces: IPersistentVector, places: IPersistentMap): Any {
        return next.match(RT.subvec(pieces, 1, pieces.count()),
                places.assoc(name, `val`))
    }
}
