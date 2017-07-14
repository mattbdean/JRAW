package net.dean.jraw.test

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class TestingCredentials(
    val script: ScriptStub,
    val app: AppStub
)

data class ScriptStub(
    val username: String,
    val password: String,
    val clientId: String,
    val clientSecret: String
)

data class AppStub(
    val clientId: String,
    val redirectUrl: String
)

