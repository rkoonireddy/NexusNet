package ch.nexusnet.usermanager.aws.dynamodb.model.mapper;

import ch.nexusnet.usermanager.aws.dynamodb.model.table.UserInfo;
import org.openapitools.model.User;

public class UserToUserInfoMapper {

    private UserToUserInfoMapper() {}

    /**
     * Maps a User object to a UserInfo object.
     *
     * @param user The source User object.
     * @return A UserInfo object populated with user's data.
     */
    public static UserInfo map(User user) {
        UserInfo userInfo = new UserInfo();

        if (user.getId() != null) {
            userInfo.setId(user.getId().toString());
        }

        if (user.getBirthday() != null) {
            // Convert LocalDate to String
            userInfo.setBirthday(user.getBirthday().toString());
        } else {
            userInfo.setBirthday(null);
        }

        if (user.getPrivateProfile() != null) {
            userInfo.setPrivateProfile(user.getPrivateProfile());
        } else {
            userInfo.setPrivateProfile(false);
        }

        userInfo.setUsername(user.getUsername());
        userInfo.setFirstName(user.getFirstName());
        userInfo.setLastName(user.getLastName());
        userInfo.setEmail(user.getEmail());
        userInfo.setMotto(user.getMotto());
        userInfo.setBio(user.getBio());
        userInfo.setUniversity(user.getUniversity());
        userInfo.setDegreeProgram(user.getDegreeProgram());

        return userInfo;
    }
}
