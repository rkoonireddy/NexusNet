package ch.nexusnet.postmanager.aws.dynamodb.repositories;

import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBLike;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

@EnableScan
public interface DynamoDBLikeRepository extends CrudRepository<DynamoDBLike, String> {

    void deleteAllByTargetId(String targetId);
    void deleteByTargetIdAndTargetTypeAndUserId(String targetId, String targetType, String userId);

    Optional<DynamoDBLike> findByTargetIdAndTargetTypeAndUserId(String targetId, String targetType, String userId);

    List<DynamoDBLike> findAllByTargetTypeAndUserId(String targetType, String userId);

}
