package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.models.internal.SubredditElement;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AutoValue
public abstract class MultiredditPatch {
    @Nullable
    @Json(name = "description_md") public abstract String getDescription();

    @Nullable
    @Json(name = "display_name") public abstract String getDisplayName();

    @Nullable
    @Json(name = "icon_name") public abstract String getIconName();

    @Nullable
    @Json(name = "key_color") public abstract String getKeyColor();

    @Nullable
    @Json(name = "subreddits") public abstract List<SubredditElement> getSubreddits();

    @Nullable
    public abstract String getVisibility();

    @Nullable
    public abstract String getWeightingScheme();

    public static final class Builder {
        private String description;
        private String displayName;
        private String iconName;
        private String keyColor;
        private List<String> subreddits;
        private String visibility;
        private String weightingScheme;

        /** Markdown-formatted text */
        public Builder description(String description) { this.description = description; return this; }

        /** The name this multireddit will go by */
        public Builder displayName(String displayName) { this.displayName = displayName; return this; }

        /**
         * According to the API, one of:
         * <ul>
         *   <li>{@code art and design}
         *   <li>{@code ask}
         *   <li>{@code books}
         *   <li>{@code business}
         *   <li>{@code cars}
         *   <li>{@code comics}
         *   <li>{@code cute animals}
         *   <li>{@code diy}
         *   <li>{@code entertainment}
         *   <li>{@code food and drink}
         *   <li>{@code funny}
         *   <li>{@code games}
         *   <li>{@code grooming}
         *   <li>{@code health}
         *   <li>{@code life advice}
         *   <li>{@code military}
         *   <li>{@code models pinup}
         *   <li>{@code music}
         *   <li>{@code news}
         *   <li>{@code philosophy}
         *   <li>{@code pictures and gifs}
         *   <li>{@code science}
         *   <li>{@code shopping}
         *   <li>{@code sports}
         *   <li>{@code style}
         *   <li>{@code tech}
         *   <li>{@code travel}
         *   <li>{@code unusual stories}
         *   <li>{@code video}
         *   <li>{@code (empty string)}
         * </ul>
         */
        public Builder iconName(String iconName) { this.iconName = iconName; return this; }

        /**
         * A hex-formatted hex string, like `#CEE3F8`. This color is primarily used when viewing the multireddit on the
         * mobile site.
         */
        public Builder keyColor(String keyColor) { this.keyColor = keyColor; return this; }

        /** A list of subreddits to include in this multireddit. Do not include the `/r/` prefix. */
        public Builder subreddits(List<String> subreddits) { this.subreddits = subreddits; return this; }
        public Builder subreddits(String... subreddits) { return subreddits(Arrays.asList(subreddits)); }

        /** One of `public`, `private`, or `hidden` */
        public Builder visibility(String visibility) { this.visibility = visibility; return this; }

        /** Either `classic` or `fresh` */
        public Builder weightingScheme(String weightingScheme) { this.weightingScheme = weightingScheme; return this; }

        public MultiredditPatch build() {
            List<SubredditElement> subreddits = new ArrayList<>(this.subreddits == null ? 0 : this.subreddits.size());
            if (this.subreddits != null)
                for (String name : this.subreddits)
                    subreddits.add(SubredditElement.create(name));
            return new AutoValue_MultiredditPatch(description, displayName, iconName, keyColor, subreddits, visibility, weightingScheme);
        }
    }

    public static JsonAdapter<MultiredditPatch> jsonAdapter(Moshi moshi) {
        return new AutoValue_MultiredditPatch.MoshiJsonAdapter(moshi);
    }
}
