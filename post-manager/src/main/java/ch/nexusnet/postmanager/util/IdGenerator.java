package ch.nexusnet.postmanager.util;

import java.util.UUID;

public class IdGenerator {

    private IdGenerator() {
        // Private constructor to prevent instantiation
    }

    /**
     * Generates a unique identifier for a post.
     *
     * @return A string that starts with "POST-" followed by a UUID.
     */
    public static String generatePostId() {
        return "POST-" + UUID.randomUUID();
    }

    /**
     * Generates a unique identifier for a comment.
     *
     * @return A string that starts with "COMMENT-" followed by a UUID.
     */
    public static String generateCommentId() {
        return "COMMENT-" + UUID.randomUUID();
    }

    /**
     * Generates a unique identifier for a like.
     *
     * @return A string that starts with "LIKE-" followed by a UUID.
     */
    public static String generateLikeId() {
        return "LIKE-" + UUID.randomUUID();
    }

    /**
     * Generates a unique identifier for a file.
     *
     * @return A string that starts with "FILE-" followed by a UUID.
     */
    public static String generateFileId() {
        return "FILE-" + UUID.randomUUID();
    }
}
