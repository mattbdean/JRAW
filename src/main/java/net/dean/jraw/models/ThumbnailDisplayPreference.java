package net.dean.jraw.models;

import net.dean.jraw.AccountPreferencesEditor;

/**
 * Represents the different ways a user can set their preference on how thumbnails are displayed. See
 * {@link AccountPreferences#getThumbnailDisplayPreference()} and
 * {@link AccountPreferencesEditor#thumbnailDisplayPreference(ThumbnailDisplayPreference)}
 */
public enum ThumbnailDisplayPreference {
    /** Always display thumbnails */
    ON,
    /** Never display thumbnails */
    OFF,
    /** Display thumbnails based off subreddit preferences */
    SUBREDDIT
}
