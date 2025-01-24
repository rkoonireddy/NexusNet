package ch.nexusnet.postmanager.aws.dynamodb.repositories;

import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBComment;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@EnableScan
public interface DynamoDBCommentRepository extends CrudRepository<DynamoDBComment, String> {
    void deleteAllByPostId(String postId);
    List<DynamoDBComment> findByPostId(String postId);

    List<DynamoDBComment> findByAuthorId(String authorId);

}
