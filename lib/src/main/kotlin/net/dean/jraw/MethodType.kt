package net.dean.jraw

/**
 * Used exclusively with [EndpointImplementation] for documentation purposes. Not intended to be used elsewhere.
 */
enum class MethodType {
    /**
     * This method will likely block the current thread because of some slow activity (most likely executing an HTTP
     * request
     */
    BLOCKING_CALL,

    /** This method returns a value very quickly and does not do any I/O or networking. */
    NON_BLOCKING_CALL
}
