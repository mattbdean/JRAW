package net.dean.jraw.models.meta;

import net.dean.jraw.models.Comment;
import net.dean.jraw.models.Contribution;
import net.dean.jraw.models.PrivateMessage;
import net.dean.jraw.models.Submission;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class is responsible for serializing Contribution subclasses. It accepts JsonNodes whose "kind" node are equal
 * to {@link Model.Kind#LINK}, {@link Model.Kind#COMMENT}, and {@link Model.Kind#MESSAGE}.
 */
public class ContributionSerializer implements JsonSerializer<Contribution> {

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Contribution> T parse(JsonNode node, Class<T> clazz, Model.Kind kind) {
        switch (kind) {
            case LINK:
                return (T) new Submission(node.get("data"));
            case COMMENT:
                return (T) new Comment(node.get("data"));
            case MESSAGE:
                return (T) new PrivateMessage(node.get("data"));
            default:
                throw new IllegalArgumentException("Model kind '" + kind +
                        "' is not applicable for Contribution");
        }
    }
}
