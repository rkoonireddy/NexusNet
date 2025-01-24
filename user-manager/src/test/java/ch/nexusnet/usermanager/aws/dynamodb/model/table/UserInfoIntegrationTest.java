package ch.nexusnet.usermanager.aws.dynamodb.model.table;


import ch.nexusnet.usermanager.UsermanagerApplication;
import ch.nexusnet.usermanager.aws.dynamodb.model.mapper.UserToUserInfoMapper;
import ch.nexusnet.usermanager.aws.dynamodb.repositories.UserInfoRepository;
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
import org.openapitools.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;


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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserInfoIntegrationTest {
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    UserInfoRepository userInfoRepository;


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
                .generateCreateTableRequest(UserInfo.class);
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
                (List<UserInfo>) userInfoRepository.findAll());
    }

    @Test
    public void basicEntryCreation_expectSuccess() {
        // arrange
        User testUser = getUserWithId();
        UserInfo userInfo = UserToUserInfoMapper.map(testUser);
        userInfoRepository.save(userInfo);

        // act
        Optional<UserInfo> result = userInfoRepository.findById(testUser.getId().toString());

        // assert
        assertTrue(result.isPresent());
        assertEquals(result.get().getUsername(), testUser.getUsername());
    }

    @Test
    public void findAllUsers_expectSuccess() {
        // arrange
        User testUser1 = getUserWithId();
        User testUser2 = getUserWithId();
        testUser2.setUsername("testuser2");

        List<UserInfo> expected = new ArrayList<>();
        expected.add(UserToUserInfoMapper.map(testUser1));
        expected.add(UserToUserInfoMapper.map(testUser2));

        userInfoRepository.saveAll(expected);

        // act
        List<UserInfo> result =
                StreamSupport.stream(userInfoRepository.findAll().spliterator(), false)
                        .toList();


        assertThat(result).extracting(UserInfo::getUsername)
                .containsExactlyInAnyOrder(expected.get(0).getUsername(), expected.get(1).getUsername());
    }

    @Test
    public void updateUser_expectUpdatedInfo() {
        // arrange
        User testUser = getUserWithId();
        UserInfo userInfo = UserToUserInfoMapper.map(testUser);
        userInfoRepository.save(userInfo);

        String updatedUsername = "updatedUsername";
        userInfo.setUsername(updatedUsername);
        userInfoRepository.save(userInfo);

        // act
        Optional<UserInfo> updatedResult = userInfoRepository.findById(testUser.getId().toString());

        // assert
        assertTrue(updatedResult.isPresent());
        assertEquals(updatedResult.get().getUsername(), updatedUsername);
    }

    @Test
    public void deleteUser_expectNoUserFound() {
        // arrange
        User testUser = getUserWithId();
        UserInfo userInfo = UserToUserInfoMapper.map(testUser);
        userInfoRepository.save(userInfo);

        // act
        userInfoRepository.delete(userInfo);
        Optional<UserInfo> result = userInfoRepository.findById(testUser.getId().toString());

        // assert
        assertFalse(result.isPresent());
    }

    @Test
    public void findAllUsersWhenEmpty_expectEmpty() {
        // arrange
        // No users added in this case

        // act
        List<UserInfo> result = StreamSupport.stream(userInfoRepository.findAll().spliterator(), false)
                .toList();

        // assert
        assertTrue(result.isEmpty());
    }

    private User getUserWithoutId() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("jhnd");
        return user;
    }
    private User getUserWithId() {
        User user = getUserWithoutId();
        user.setId(UUID.randomUUID());
        return user;
    }
}
