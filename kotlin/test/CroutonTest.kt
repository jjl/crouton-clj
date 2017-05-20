package test

import irresponsible.crouton.Crouton

/**
 * Created by james on 5/20/17.
 */
class CroutonTest {

    fun testCrouton() {
        val p = "/foo/bar"
        Crouton.parse_path(p)

    }
}