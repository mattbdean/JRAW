package net.dean.jraw

/**
 * Thrown when directly querying a user who is suspended
 */
class SuspendedAccountException(val name: String, cause: Throwable? = null) :
    Exception("Account '$name' is suspended", cause)
