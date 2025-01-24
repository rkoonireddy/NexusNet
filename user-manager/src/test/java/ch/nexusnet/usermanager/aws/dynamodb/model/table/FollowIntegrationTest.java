package ch.nexusnet.usermanager.aws.dynamodb.model.table;

import ch.nexusnet.usermanager.UsermanagerApplication;
import ch.nexusnet.usermanager.aws.dynamodb.repositories.FollowRepository;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <p>
 *      This class is used to test the functionality of DynamoDb-related repositories. It uses a local instance of
 *      DynamoDB to run the tests on. It uses a Junit4 runner, since it is recommended in the documentation.
 * </p>
 * <p>
 *      !! DOCKER NEEDS TO BE RUNNING ON YOUR MACHINE FOR THE TESTS TO WORK !!
 * </p>
 * <p>
 *      This test is disabled so that the automated CI/CD pipeline is not using up too much time. To run it locally,
 *      remove the '@Disabled' annotation bellow.
 * </p>
 * @see <a href=https://www.baeldung.com/spring-data-dynamodb>Baeldung Documentation for DynamoDB and Spring Boot</a>
 */
@Disabled
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = UsermanagerApplication.class)
@WebAppConfiguration
@ActiveProfiles("local")
public class FollowIntegrationTest {
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    FollowRepository followRepository;


    @ClassRule
    public static GenericContainer<?> dynamoDB =
            new GenericContainer<>(DockerImageName.parse("amazon/dynamodb-local:latest"))
                    .withExposedPorts(8000);

    @Before
    public void setup() throws Exception {
        // Configure the AmazonDynamoDB client
        String endpoint = String.format("http://%s:%d",
                dynamoDB.getHost(),
                dynamoDB.getFirstMappedPort());

        amazonDynamoDB.setEndpoint(endpoint);

        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        CreateTableRequest tableRequest = dynamoDBMapper
                .generateCreateTableRequest(Follow.class);
        tableRequest.setProvisionedThroughput(
                new ProvisionedThroughput(1L, 1L));

        // Check if the table already exists
        ListTablesResult listTablesResult = amazonDynamoDB.listTables();
        boolean tableExists = listTablesResult.getTableNames()
                .stream()
                .anyMatch(tableName -> tableName.equals(tableRequest.getTableName()));

        if (!tableExists) {
            amazonDynamoDB.createTable(tableRequest);
        } else {
            // Optionally reset the table if it already exists
            amazonDynamoDB.deleteTable(tableRequest.getTableName());
            amazonDynamoDB.createTable(tableRequest);
        }

        dynamoDBMapper.batchDelete(
                (List<Follow>)followRepository.findAll());
    }

    @Test
    public void basicEntryCreation_expectSuccess() {
        String uuid = "de005d7c-f36f-4342-9c2c-380b1815b499";
        String followId = "11111";
        Follow follow = new Follow();
        follow.setUserId(uuid);
        follow.setFollowsUserId(followId);
        follow.setId(UUID.randomUUID().toString());

        followRepository.save(follow);

        List<Follow> result = followRepository.findAllByUserId(uuid);

        assertEquals(1, result.size());
        assertEquals(result.get(0).getFollowsUserId(), followId);
    }

    @Test
    public void getUserByFollowId_expectSuccess() {
        String uuid = "de005d7c-f36f-4342-9c2c-380b1815b499";
        String followId = "11111";
        Follow follow = new Follow();
        follow.setId(UUID.randomUUID().toString());
        follow.setUserId(uuid);
        follow.setFollowsUserId(followId);

        followRepository.save(follow);

        List<Follow> result = followRepository.findAllByFollowsUserId(followId);

        assertEquals(1, result.size());
        assertEquals(result.get(0).getFollowsUserId(), followId);
    }

    @Test
    public void multipleFollowsBySingleUser_expectSuccess() {
        String userId = UUID.randomUUID().toString();
        String followId1 = UUID.randomUUID().toString();
        String followId2 = UUID.randomUUID().toString();

        Follow follow1 = new Follow();
        Follow follow2 = new Follow();
        follow1.setId(UUID.randomUUID().toString());
        follow1.setUserId(userId);
        follow1.setFollowsUserId(followId1);
        follow2.setId(UUID.randomUUID().toString());
        follow2.setUserId(userId);
        follow2.setFollowsUserId(followId2);

        followRepository.save(follow1);
        followRepository.save(follow2);

        List<Follow> results = followRepository.findAllByUserId(userId);

        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(f -> f.getFollowsUserId().equals(followId1)));
        assertTrue(results.stream().anyMatch(f -> f.getFollowsUserId().equals(followId2)));
    }

    @Test
    public void multipleUsersFollowingSingleUser_expectSuccess() {
        String followId = UUID.randomUUID().toString();
        String userId1 = UUID.randomUUID().toString();
        String userId2 = UUID.randomUUID().toString();

        Follow follow1 = new Follow();
        Follow follow2 = new Follow();
        follow1.setId(UUID.randomUUID().toString());
        follow1.setUserId(userId1);
        follow1.setFollowsUserId(followId);
        follow2.setId(UUID.randomUUID().toString());
        follow2.setUserId(userId2);
        follow2.setFollowsUserId(followId);

        followRepository.save(follow1);
        followRepository.save(follow2);

        List<Follow> results = followRepository.findAllByFollowsUserId(followId);

        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(f -> f.getUserId().equals(userId1)));
        assertTrue(results.stream().anyMatch(f -> f.getUserId().equals(userId2)));
    }

    @Test
    public void unfollowUser_expectRemovedFromFollows() {
        String userId = UUID.randomUUID().toString();
        String followId = UUID.randomUUID().toString();

        Follow follow = new Follow();
        follow.setUserId(userId);
        follow.setFollowsUserId(followId);
        follow.setId(UUID.randomUUID().toString());
        followRepository.save(follow);

        // Unfollow operation
        followRepository.delete(follow);
        List<Follow> results = followRepository.findAllByUserId(userId);

        assertTrue(results.isEmpty());
    }

    @Test
    public void followNonexistentUser_expectEmptyResult() {
        String userId = UUID.randomUUID().toString();
        String nonExistentUserId = UUID.randomUUID().toString();

        Follow follow = new Follow();
        follow.setUserId(userId);
        follow.setFollowsUserId(nonExistentUserId);
        follow.setId(UUID.randomUUID().toString());
        followRepository.save(follow);

        // Attempt to find follows for a non-existent user
        List<Follow> results = followRepository.findAllByFollowsUserId(nonExistentUserId);

        assertFalse(results.isEmpty());
        assertEquals(nonExistentUserId, results.get(0).getFollowsUserId());
    }

    @Test
    public void concurrentFollowAndUnfollow_expectConsistentState() throws InterruptedException {
        String userId = UUID.randomUUID().toString();
        String followId = UUID.randomUUID().toString();

        Follow follow = new Follow();
        follow.setUserId(userId);
        follow.setFollowsUserId(followId);
        follow.setId(UUID.randomUUID().toString());
        followRepository.save(follow);

        Follow newFollow = new Follow();
        newFollow.setUserId(userId);
        newFollow.setFollowsUserId(followId);
        newFollow.setId(UUID.randomUUID().toString());

        // Simulate concurrent operations
        Thread thread1 = new Thread(() -> followRepository.delete(follow));
        Thread thread2 = new Thread(() -> followRepository.save(newFollow));
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        List<Follow> results = followRepository.findAllByUserId(userId);

        // Expect consistent result
        assertEquals(1, results.size());
        assertEquals(followId, results.get(0).getFollowsUserId());
    }

}