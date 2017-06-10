package net.dean.jraw.models

import java.util.*

/**
 * Mirrors the [created](https://github.com/reddit/reddit/wiki/JSON#created-implementation) interface defined by the
 * reddit wiki, except using only `created_utc`.
 */
interface Created {
    /** When this object was created */
    val created: Date
}
