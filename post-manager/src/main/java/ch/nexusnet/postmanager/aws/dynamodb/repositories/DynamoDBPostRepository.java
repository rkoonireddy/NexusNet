package ch.nexusnet.postmanager.aws.dynamodb.repositories;

import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBPost;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@EnableScan
public interface DynamoDBPostRepository extends CrudRepository<DynamoDBPost, String> {
    List<DynamoDBPost> findByAuthorId(String authorId);

    List<DynamoDBPost> findDynamoDBPostsByIdStartingWith(String id);
}

