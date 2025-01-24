package ch.nexusnet.usermanager.aws.dynamodb.model.mapper;

import ch.nexusnet.usermanager.aws.dynamodb.model.table.UserInfo;
import org.openapitools.model.UserSummary;

public class UserInfoToUserSummaryMapper {

    private UserInfoToUserSummaryMapper() {}

    /**
     * Maps a UserInfo Object to a UserSummary object
     *
     * @param userInfo The source UserInfo object
     * @return A UserSummary object populated with userInfo's data.
     */
    public static UserSummary map(UserInfo userInfo) {
        UserSummary userSummary = new UserSummary();

        userSummary.setId(userInfo.getId());
        userSummary.setUsername(userInfo.getUsername());

        return userSummary;
    }

}
