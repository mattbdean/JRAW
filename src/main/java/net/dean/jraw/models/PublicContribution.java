package net.dean.jraw.models;

import com.fasterxml.jackson.databind.JsonNode;
import net.dean.jraw.models.attr.Distinguishable;
import net.dean.jraw.models.attr.Gildable;
import net.dean.jraw.models.attr.Votable;
import net.dean.jraw.models.meta.ContributionSerializer;
import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to separate public contributions (submissions and comments) from private contributions (messages)
 */
@Model(kind = Model.Kind.ABSTRACT, serializer = ContributionSerializer.class)
public abstract class PublicContribution extends Contribution implements Distinguishable, Gildable, Votable {
    /** Instantiates a new PublicContribution */
    public PublicContribution(JsonNode dataNode) {
        super(dataNode);
    }

    /** Gets a map of reasons to the amount of times reported for that reason by normal users (non-moderators) */
    @JsonProperty(nullable = true)
    public Map<String, Integer> getUserReports() {
        if (!data.has("user_reports")) {
            return null;
        }
        Map<String, Integer> userReports = new HashMap<>();
        for (JsonNode userReport : data.get("user_reports")) {
            userReports.put(userReport.get(0).asText(), userReport.get(1).asInt());
        }

        return userReports;
    }

    /** Gets a map of reasons to the moderator that used that reason to report this submission */
    @JsonProperty(nullable = true)
    public Map<String, String> getModeratorReports() {
        if (!data.has("mod_reports")) {
            return null;
        }
        Map<String, String> modReports = new HashMap<>();
        for (JsonNode userReport : data.get("mod_reports")) {
            modReports.put(userReport.get(0).asText(), userReport.get(1).asText());
        }

        return modReports;
    }


    @Override
    public DistinguishedStatus getDistinguishedStatus() {
        return _getDistinguishedStatus();
    }

    @Override
    public Integer getTimesGilded() {
        return _getTimesGilded();
    }

    @Override
    public Integer getScore() {
        return _getScore();
    }

    @Override
    public VoteDirection getVote() {
        return _getVote();
    }

    /**
     * Checks if this contribution has been archived. If so, then it is no longer open for voting.
     */
    @JsonProperty
    public boolean isArchived() {
        return data("archived", Boolean.class);
    }
}
