package net.dean.jraw.meta

fun main(args: Array<String>) {
    val endpoints = EndpointParser().fetch()
    EnumCreator(endpoints).writeTo(System.out)
}
