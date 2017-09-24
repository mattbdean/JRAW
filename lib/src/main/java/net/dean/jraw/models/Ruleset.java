package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.List;

@AutoValue
public abstract class Ruleset {
    /** Rules that apply to a specific subreddit */
    @Json(name = "rules") public abstract List<SubredditRule> getSubredditRules();

    /** Rules for the entire website */
    @Json(name = "site_rules") public abstract List<String> getSiteRules();

    public static Ruleset create(List<SubredditRule> newRules, List<String> newSiteRules) {
        return new AutoValue_Ruleset(newRules, newSiteRules);
    }

    public static JsonAdapter<Ruleset> jsonAdapter(Moshi moshi) {
        return new AutoValue_Ruleset.MoshiJsonAdapter(moshi);
    }
}
