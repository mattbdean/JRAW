package net.dean.jraw.models;

import net.dean.jraw.models.attr.Distinguishable;
import net.dean.jraw.models.attr.Gildable;
import net.dean.jraw.models.attr.Votable;
import org.codehaus.jackson.JsonNode;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to separate public contributions (submissions and comments) from private contributions (messages)
 */
public abstract class PublicContribution extends Contribution implements Distinguishable, Gildable, Votable {
    /**
     * Instantiates a new PublicContribution
     *
     * @param dataNode The node to parse data from
     */
    public PublicContribution(JsonNode dataNode) {
        super(dataNode);
    }

    /**
     * Gets a map of reasons to the amount of times reported for that reason by normal users (not moderators)
     * @return A map of reports of this submission by users
     */
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

    /**
     * Gets a map of reasons to the moderator that used that reason to report this submission
     * @return A map of reports of this submission by users
     */
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
    public Integer getUpvotes() {
        return _getUpvotes();
    }

    @Override
    public Integer getDownvotes() {
        return _getDownvotes();
    }

    @Override
    public Integer getScore() {
        return _getScore();
    }

    @Override
    public VoteDirection getVote() {
        return _getVote();
    }
}
