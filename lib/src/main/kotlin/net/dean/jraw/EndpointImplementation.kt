package net.dean.jraw

/**
 * Shows that a function sends a request to an API endpoint. This function should be considered to be a
 * **blocking call** and have the potential to throw a [net.dean.jraw.http.NetworkException]
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
annotation class EndpointImplementation(val endpoint: Endpoint)
