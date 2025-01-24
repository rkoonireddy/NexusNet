package ch.nexusnet.usermanager.aws.dynamodb.model.table;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Setter;

@DynamoDBTable(tableName = "UserInfo")
@Setter
public class UserInfo {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String motto;
    private String birthday;
    private String bio;
    private String university;
    private String degreeProgram;
    @Setter
    private boolean privateProfile;

    @DynamoDBHashKey(attributeName = "id")
    public String getId() {
        return id;
    }

    @DynamoDBAttribute(attributeName = "username")
    public String getUsername() {
        return username;
    }

    @DynamoDBAttribute(attributeName = "firstname")
    public String getFirstName() {
        return firstName;
    }

    @DynamoDBAttribute(attributeName = "lastname")
    public String getLastName() {
        return lastName;
    }

    @DynamoDBAttribute(attributeName = "email")
    public String getEmail() {return email;}

    @DynamoDBAttribute(attributeName = "motto")
    public String getMotto() {return motto;}

    @DynamoDBAttribute(attributeName = "birthday")
    public String getBirthday() {
        return birthday;
    }

    @DynamoDBAttribute(attributeName = "bio")
    public String getBio() {
        return bio;
    }

    @DynamoDBAttribute(attributeName = "university")
    public String getUniversity() {
        return university;
    }

    @DynamoDBAttribute(attributeName = "degreeprogram")
    public String getDegreeProgram() {
        return degreeProgram;
    }

    @DynamoDBAttribute(attributeName = "privateprofile")
    public boolean getPrivateProfile() {
        return privateProfile;
    }
}
