package net.dean.jraw.meta

fun main(args: Array<String>) {
    EndpointParser().fetch().forEach(::println)
}
