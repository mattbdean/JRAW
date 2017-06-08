package net.dean.jraw

/**
 * Shows that a function sends a request to an API endpoint.
 *
 * For JRAW developers: functions that are decorated with this annotation should contain an example of how to use it.
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
annotation class EndpointImplementation(val endpoint: Endpoint)
