package net.dean.jraw.pagination;

import net.dean.jraw.models.ThingType;
import net.dean.jraw.models.core.Comment;
import net.dean.jraw.models.core.Submission;
import net.dean.jraw.models.core.Thing;

/**
 * This class functions as a "hacky" way to have more than one return type for {@link UserContributionPaginator}, which
 * will return both Submissions and Comments. This class will mirror the Submission's or Comment's fullname, ID, and type.
 * It is recommended to discard this object as soon as possible in favor of using {@link #getSubmission()} or {@link #getComment()}.
 * To determine whether this class is wrapping a Submission or a Comment, use {@code contribution.getType() == ThingType.SUBMISSION}
 * or {@code contribution.getType() == net.dean.jraw.models.ThingType.COMMENT}
 */
public class Contribution extends Thing {
    private final Thing activeThing;
    private final Submission submission;
    private final Comment comment;

    /**
     * Instantiates a new Contribution
     * @param submission The submission
     * @return A new Contribution
     */
    public static Contribution of(Submission submission) {
        return new Contribution(submission, null);
    }

    /**
     * Instantiates a new Contribution
     * @param comment The comment
     * @return A new Contribution
     */
    public static Contribution of(Comment comment) {
        return new Contribution(null, comment);
    }


    private Contribution(Submission submission, Comment comment) {
        super(submission != null ? submission.getDataNode() : comment.getDataNode());
        if (submission == null && comment == null) {
            throw new NullPointerException("Both the submission and the comment cannot be null");
        }
        this.submission = submission;
        this.comment = comment;

        if (this.submission != null) {
            activeThing = submission;
        } else {
            activeThing = comment;
        }
    }

    public Submission getSubmission() {
        return submission;
    }

    @Override
    public <T> T data(String name, Class<T> type) {
        throw new UnsupportedOperationException("Use either the Submission or Comment that is being wrapped around this Contribution");
    }

    public Comment getComment() {
        return comment;
    }

    /**
     * Gets the Thing this class is wrapping, in which either {@code getActiveThing() == getSubmission()} or
     * {@code getActiveThing() == getComment()} will return true.
     *
     * @return The Thing this class is wrapping
     */
    public Thing getActiveThing() {
        return activeThing;
    }

    @Override
    public ThingType getType() {
        return activeThing.getType();
    }

    @Override
    public String toString() {
        return activeThing.toString();
    }
}
