package net.dean.jraw.models

enum class AccountStatus {
    /** The account exists and is not suspended */
    EXISTS,

    /** An account by the requested name has never been created */
    NON_EXISTENT,

    /** The account exists and has been suspended reddit-wide */
    SUSPENDED
}
