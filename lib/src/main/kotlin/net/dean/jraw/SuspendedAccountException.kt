package net.dean.jraw

/**
 * Thrown when directly querying a user who is suspended
 */
class SuspendedAccountException(val name: String, cause: Throwable? = null) :
    RuntimeException("Account '$name' is suspended", cause)
