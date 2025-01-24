package ch.nexusnet.usermanager.aws.dynamodb.model.mapper;

import ch.nexusnet.usermanager.aws.dynamodb.model.table.UserInfo;
import org.openapitools.model.User;

import java.time.LocalDate;
import java.util.UUID;

public class UserInfoToUserMapper {

    private UserInfoToUserMapper() {}

    /**
     * Maps a UserInfo Object to a User object
     *
     * @param userInfo The source UserInfo object
     * @return A User object populated with userInfo's data.
     */
    public static User map(UserInfo userInfo) {
        User user = new User();

        // Assuming userInfo.id is a valid UUID string.
        user.setId(UUID.fromString(userInfo.getId()));

        if (userInfo.getBirthday() != null && !userInfo.getBirthday().isEmpty()) {
            user.setBirthday(LocalDate.parse(userInfo.getBirthday()));
        } else {
            user.setBirthday(null);
        }

        user.setUsername(userInfo.getUsername());
        user.setFirstName(userInfo.getFirstName());
        user.setLastName(userInfo.getLastName());
        user.setEmail(userInfo.getEmail());
        user.setMotto(userInfo.getMotto());
        user.setBio(userInfo.getBio());
        user.setUniversity(userInfo.getUniversity());
        user.setDegreeProgram(userInfo.getDegreeProgram());
        user.setPrivateProfile(userInfo.getPrivateProfile());


        return user;
    }
}