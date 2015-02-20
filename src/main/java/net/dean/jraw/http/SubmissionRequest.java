package net.dean.jraw.http;

import net.dean.jraw.models.CommentSort;

/**
 * Models the parameters of a call to {@code /comments/{article}}.
 */
public class SubmissionRequest {
    private final String id;
    private final Integer depth;
    private final Integer limit;
    private final Integer context;
    private final CommentSort sort;
    private final String focus;

    /** Creates a SubmissionRequest who only specifies the link's ID. */
    public SubmissionRequest(String id) {
        this(new Builder(id));
    }

    private SubmissionRequest(Builder b) {
        this.id = b.id;
        this.depth = b.depth;
        this.limit = b.limit;
        this.context = b.context;
        this.sort = b.sort;
        this.focus = b.focus;
    }

    /** Gets the submission's ID, such as "92dd8". */
    public String getId() {
        return id;
    }

    /**
     * Gets the maximum amount of subtrees returned by this request. If the number is less than 1, it is ignored by
     * the Reddit API and no depth restriction is enacted.
     */
    public Integer getDepth() {
        return depth;
    }

    /**
     * Gets the maximum amount of comments to return
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * Gets the number of parents shown in relation to the focused comment. For example, if the focused comment is
     * in the eighth level of the comment tree (meaning there are seven replies above it), and the context is set to
     * six, then the response will also contain the six direct parents of the given comment. For a better
     * understanding, play with
     * <a href="https://www.reddit.com/comments/92dd8?comment=c0b73aj&context=8">this link</a>.
     */
    public Integer getContext() {
        return context;
    }

    /**
     * Gets the sorting for the comments in the response
     * @return This SubmissionRequest
     */
    public CommentSort getSort() {
        return sort;
    }

    /**
     * Gets the ID of the comment to focus on. If this comment does not exist, then this parameter is ignored.
     * Otherwise, only one comment tree is returned: the one in which the given comment resides.
     */
    public String getFocus() {
        return focus;
    }

    public static class Builder {
        private final String id;
        private Integer depth;
        private Integer limit;
        private Integer context;
        private CommentSort sort;
        private String focus;

        /**
         * Instantiates a new Builder
         * @param id The link's ID, ex: "92dd8"
         */
        public Builder(String id) {
            if (id == null)
                throw new NullPointerException("id may not be null");
            this.id = id;
        }

        /** See {@link #getDepth()}. A null value will exclude this parameter from the final request. */
        public Builder depth(Integer depth) {
            this.depth = depth;
            return this;
        }

        /** See {@link #getLimit()}. A null value will exclude this parameter from the final request. */
        public Builder limit(Integer limit) {
            this.limit = limit;
            return this;
        }

        /** See {@link #getContext()}. A null value will exclude this parameter from the final request. */
        public Builder context(Integer context) {
            this.context = context;
            return this;
        }

        /** See {@link #getSort()}. A null value will exclude this parameter from the final request. */
        public Builder sort(CommentSort sort) {
            this.sort = sort;
            return this;
        }

        /** See {@link #getFocus()}. A null value will exclude this parameter from the final request. */
        public Builder focus(String commentId) {
            this.focus = commentId;
            return this;
        }

        /** Creates a new SubmissionRequest based off of this Builder */
        public SubmissionRequest build() {
            return new SubmissionRequest(this);
        }
    }
}
