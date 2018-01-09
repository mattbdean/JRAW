package net.dean.jraw.models

/** Sorting used specifically in [net.dean.jraw.pagination.SubredditSearchPaginator] */
enum class SubredditSearchSort : Sorting {
    /** Top results will more closely match the query */
    RELEVANCE,

    /**
     * Top results will have the most actions per unit time. An action could be a user submitting a post, submitting a
     * comment, or voting, but at the end of the day how reddit determines "activity" is really up to them.
     */
    ACTIVITY;

    override val requiresTimePeriod: Boolean = false
}
