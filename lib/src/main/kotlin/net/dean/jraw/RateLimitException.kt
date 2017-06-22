package net.dean.jraw

class RateLimitException(val cooldown: Double) :
    RuntimeException("reddit is ratelimiting this action, try again in ~${cooldown.toInt()} seconds")
