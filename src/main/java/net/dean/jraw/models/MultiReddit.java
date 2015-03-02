package net.dean.jraw.models;

import com.fasterxml.jackson.annotation.JsonValue;
import net.dean.jraw.NoSuchEnumConstantException;
import net.dean.jraw.models.attr.Created;
import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a collection of subreddits. See <a href="http://www.reddit.com/r/multihub">here</a> for some examples.
 */
@Model(kind = Model.Kind.MULTIREDDIT)
public class MultiReddit extends Thing implements Created {

    /** Instantiates a new MultiReddit */
    public MultiReddit(JsonNode dataNode) {
        super(dataNode);
    }

    /** Checks if the logged-in user can edit this MultiReddit */
    @JsonProperty
    public boolean canEdit() {
        return data("can_edit", Boolean.class);
    }

    @Override
    @JsonProperty
    public String getFullName() {
        return data("name");
    }

    /**
     * Gets the subreddits that are a part of this multireddit
     * @return A list of subreddits
     */
    @JsonProperty
    public List<MultiSubreddit> getSubreddits() {
        List<MultiSubreddit> subreddits = new ArrayList<>();

        JsonNode node = data.get("subreddits");
        for (JsonNode subredditNode : node) {
            subreddits.add(new MultiSubreddit(subredditNode));
        }

        return subreddits;
    }

    /**
     * Checks if this multireddit is restricted to its owner
     * @return If this mutlireddit is private
     */
    @JsonProperty
    public Visibility getVisibility() {
        return Visibility.byJsonValue(data("visibility"));
    }

    /** Gets this multireddit's description, formatted with Markdown. */
    @JsonProperty
    public String getDescription() {
        return data("description_md");
    }

    @JsonProperty
    public String getOrigin() {
        return data("copied_from");
    }

    @JsonProperty(nullable = true)
    public String getIconUrl() {
        return data("icon_url");
    }

    @JsonProperty
    public WeightingScheme getWeightingScheme() {
        return WeightingScheme.byJsonValue(data("weighting_scheme"));
    }

    @JsonProperty
    public String getKeyColor() {
        return data("key_color");
    }

    @JsonProperty
    public String getDisplayName() {
        return data("display_name");
    }

    /**
     * Gets the relative path to this multireddit. It will be in the format of {@code /user/{username}/m/{multiname}}
     * @return The relative path
     */
    @JsonProperty
    public String getPath() {
        return data("path");
    }

    @Override
    public Date getCreated() {
        return _getCreated();
    }

    @Override
    public Date getCreatedUtc() {
        return _getCreatedUtc();
    }

    public static enum WeightingScheme {
        CLASSIC,
        FRESH;

        @Override
        @JsonValue
        public String toString() {
            return name().toLowerCase();
        }

        public static WeightingScheme byJsonValue(String json) {
            for (WeightingScheme scheme : values()) {
                if (scheme.toString().equals(json)) {
                    return scheme;
                }
            }

            throw new NoSuchEnumConstantException(WeightingScheme.class, json);
        }
    }

    public static enum Visibility {
        /** Available to everyone */
        PUBLIC,
        /** Will not appear with the rest of the multireddits */
        HIDDEN,
        /** Available to only the owner */
        PRIVATE;

        @Override
        @JsonValue
        public String toString() {
            return name().toLowerCase();
        }

        public static Visibility byJsonValue(String json) {
            for (Visibility vis : values()) {
                if (vis.toString().equals(json)) {
                    return vis;
                }
            }

            throw new NoSuchEnumConstantException(Visibility.class, json);
        }
    }

    public static enum Icon {
        ABSENT {
            @Override
            public String toString() {
                return "";
            }
        },
        ART_AND_DESIGN,
        ASK,
        BOOKS,
        BUSINESS,
        CARS,
        COMICS,
        CUTE_ANIMALS,
        DIY,
        ENTERTAINMENT,
        FOOD_AND_DRINK,
        FUNNY,
        GAMES,
        GROOMING,
        HEALTH,
        LIFE_ADVICE,
        MILITARY,
        MODELS_PINUP,
        MUSIC,
        NEWS,
        PHILOSOPHY,
        PICTURES_AND_GIFS,
        SCIENCE,
        SHOPPING,
        SPORTS,
        STYLE,
        TECH,
        TRAVEL,
        UNUSUAL_STORIES,
        VIDEO,
        NONE {
            @Override
            public String toString() {
                return "None";
            }
        };

        @Override
        @JsonValue
        public String toString() {
            return name().toLowerCase().replace("_", " ");
        }

        public static Icon byJsonValue(String json) {
            for (Icon name : values()) {
                if (name.toString().equals(json)) {
                    return name;
                }
            }

            throw new NoSuchEnumConstantException(Icon.class, json);
        }
    }
}
