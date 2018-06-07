package net.dean.jraw.meta

class EndpointOverview(val endpoints: List<EndpointMeta>) {
    val implemented: Int
    val planned: Int
    val notPlanned: Int
    val effectiveTotal: Int
    val effectiveCompletion: Double

    init {
        val endpointCountByStatus = endpoints
            .groupBy { it.status }
            .mapValues { it.value.size }

        implemented = endpointCountByStatus[ImplementationStatus.IMPLEMENTED] ?: 0
        planned = endpointCountByStatus[ImplementationStatus.PLANNED] ?: 0
        notPlanned = endpointCountByStatus[ImplementationStatus.NOT_PLANNED] ?: 0

        effectiveTotal = implemented + planned - notPlanned
        effectiveCompletion = (implemented.toDouble() / effectiveTotal.toDouble())
    }

    /**
     * Returns a new EndpointOverview containing only the endpoints with the given scope
     */
    fun byOAuthScope(scope: String): EndpointOverview {
        val endpoints = endpoints.filter { it.oauthScope == scope }
        if (endpoints.isEmpty())
            throw IllegalArgumentException("No endpoints for the scope '$scope'")

        return EndpointOverview(endpoints)
    }

    /**
     * Returns a list of all OAuth2 scopes represented by [endpoints].
     */
    fun scopes() = endpoints.map { it.oauthScope }.distinct()

    fun completionPercentage(decimals: Int = 0): String = String.format("%.${decimals}f", effectiveCompletion * 100)
}
