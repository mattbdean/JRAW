package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.databind.RedditModel;
import net.dean.jraw.databind.UnixTime;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Date;

@AutoValue
@RedditModel
public abstract class Message implements Created, Distinguishable, Identifiable, Votable, Serializable {
    /** The full name of the message that kicked off this private message thread, or null if this message isn't a PM */
    @Nullable
    @Json(name = "first_message_name") public abstract String getFirstMessage();

    /** The name of the user that created this message */
    @Nullable
    public abstract String getAuthor();

    /** The markdown-formatted message content */
    public abstract String getBody();

    /** If this message is for a comment, the permalink to said comment with the query "{@code ?context=3}"  */
    public abstract String getContext();

    @NotNull
    @Override
    @Json(name = "created_utc") @UnixTime public abstract Date getCreated();

    /** The name of the user that started the conversation */
    public abstract String getDest();

    @NotNull
    @Override
    @Json(name = "name") public abstract String getFullName();

    /** True if this message represents a reply to a comment made by the logged-in user */
    @Json(name = "was_comment") public abstract boolean isComment();

    /** If this message has not been marked as read */
    // we can't use isNew() since 'new' is a reserved keyword
    @Json(name = "new") public abstract boolean isUnread();

    /** The total amount of comments in the submission, or null if this message is not for a comment */
    @Nullable
    public abstract Integer getNumComments();

    /** The full name of the comment or PM that is the predecessor to this one. */
    @Nullable
    public abstract String getParentId();

    /** The subject line of the private message, or the string "`comment reply`" for comments. */
    public abstract String getSubject();

    /**
     * The name of the subreddit where this comment or username mention was created, or null if this message isn't a
     * comment or username mention
     */
    @Nullable
    public abstract String getSubreddit();

    @NotNull
    @Override
    @Json(name = "likes") public abstract VoteDirection getVote();

    public static JsonAdapter<Message> jsonAdapter(Moshi moshi) {
        return new AutoValue_Message.MoshiJsonAdapter(moshi);
    }
}
