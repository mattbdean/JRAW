package net.dean.jraw.models.internal;

import com.google.auto.value.AutoValue;
import net.dean.jraw.databind.DynamicEnveloped;
import net.dean.jraw.databind.Enveloped;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.NestedIdentifiable;
import net.dean.jraw.models.Submission;

/**
 * This class attempts to model the response returned by {@code /comments/{id}}.
 */
@AutoValue
public abstract class SubmissionData {
    @Enveloped
    public abstract Listing<Submission> getSubmissions();

    @DynamicEnveloped
    public abstract Listing<NestedIdentifiable> getComments();

    public static SubmissionData create(Listing<Submission> submissions, Listing<NestedIdentifiable> comments) {
        return new AutoValue_SubmissionData(submissions, comments);
    }
}
