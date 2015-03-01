package net.dean.jraw.http;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.dean.jraw.models.MultiReddit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class holds all the data necessary to create or update a multireddit.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class MultiRedditUpdateRequest {
    private final String owner;
    private final String name;
    private final String description;
    private final String displayName;
    private final MultiReddit.Icon icon;
    private final String keyColor;
    private final List<SubredditModel> subreddits;
    private final MultiReddit.Visibility visibility;
    private final MultiReddit.WeightingScheme weightingScheme;

    public MultiRedditUpdateRequest(Builder b) {
        this.owner = b.owner;
        this.name = b.name;
        this.description = b.description;
        this.displayName = b.displayName;
        this.icon = b.icon;
        this.keyColor = b.keyColor;
        this.subreddits = b.subreddits;
        this.visibility = b.visibility;
        this.weightingScheme = b.weightingScheme;
    }

    @JsonIgnore
    public String getOwner() {
        return owner;
    }

    @JsonIgnore
    public String getName() {
        return name;
    }

    @JsonProperty("description_md")
    public String getDescription() {
        return description;
    }

    @JsonProperty("display_name")
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty("icon_name")
    public MultiReddit.Icon getIcon() {
        return icon;
    }

    @JsonProperty("key_color")
    public String getKeyColor() {
        return keyColor;
    }

    @JsonIgnore
    public List<String> getSubreddits() {
        List<String> subs = new ArrayList<>(subreddits.size());
        for (SubredditModel model : subreddits) {
            subs.add(model.getName());
        }

        return subs;
    }

    @JsonProperty("subreddits")
    List<SubredditModel> getSubredditModels() {
        return subreddits;
    }

    @JsonProperty("visibility")
    public MultiReddit.Visibility getVisibility() {
        return visibility;
    }

    @JsonProperty("weighting_scheme")
    public MultiReddit.WeightingScheme getWeightingScheme() {
        return weightingScheme;
    }

    public static final class Builder {
        private final String owner;
        private final String name;
        private String description;
        private String displayName;
        private MultiReddit.Icon icon;
        private String keyColor;
        private List<SubredditModel> subreddits;
        private MultiReddit.Visibility visibility;
        private MultiReddit.WeightingScheme weightingScheme;

        public Builder(String owner, String name) {
            this.owner = owner;
            this.name = name;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder icon(MultiReddit.Icon icon) {
            this.icon = icon;
            return this;
        }

        public Builder keyColor(String keyColor) {
            this.keyColor = keyColor;
            return this;
        }

        public Builder subreddits(String... subreddits) {
            return subreddits(Arrays.asList(subreddits));
        }

        public Builder subreddits(List<String> subreddits) {
            List<SubredditModel> models = new ArrayList<>(subreddits.size());
            for (String sub : subreddits) {
                models.add(new SubredditModel(sub));
            }
            this.subreddits = models;
            return this;
        }

        public Builder visibility(MultiReddit.Visibility visibility) {
            this.visibility = visibility;
            return this;
        }

        public Builder weightingScheme(MultiReddit.WeightingScheme weightingScheme) {
            this.weightingScheme = weightingScheme;
            return this;
        }

        public MultiRedditUpdateRequest build() {
            return new MultiRedditUpdateRequest(this);
        }
    }

    // For internal use only.
    public static final class SubredditModel {
        private String name;

        public SubredditModel(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
