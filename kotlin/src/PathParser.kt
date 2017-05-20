package irresponsible.crouton

import clojure.lang.PersistentVector
import clojure.lang.ITransientCollection

object PathParser {
    @Suppress("unused")
    fun parse(path: String): Any {
        when (path) {
            "/", "" -> return PersistentVector.EMPTY
            else -> {
                val end = path.length
                var working = PersistentVector.EMPTY.asTransient() as ITransientCollection
                var start = 0
                while (true) {
                    while (start < end && 47 == path.codePointAt(start)) start++ // avoid empty segments
                    if (start < end) {
                        val i = path.indexOf('/', start)
                        if (-1 != i) {
                            working = working.conj(path.substring(start, i))
                            start = i + 1
                        } else { // we fell off the end of the string
                            working = working.conj(path.substring(start))
                            break
                        }
                    } else
                        break
                }
                return working.persistent()
            }
        }
    }
}
