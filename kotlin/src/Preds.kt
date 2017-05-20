package irresponsible.crouton

class Preds {
    enum class PosInt : Predicate {
        INSTANCE;

        override fun test(s: String): Any? {
            try {
                if (s.startsWith("-")) return null
                return java.lang.Long.parseLong(s)
            } catch (e: NumberFormatException) {
                return null
            }

        }
    }

    enum class PosHexInt : Predicate {
        INSTANCE;

        override fun test(s: String): Any? {
            try {
                if (s.startsWith("-")) return null
                return java.lang.Long.parseLong(s, 16)
            } catch (e: NumberFormatException) {
                return null
            }

        }
    }
}
