package ch.nexusnet.usermanager.aws.dynamodb.repositories;

import ch.nexusnet.usermanager.aws.dynamodb.model.table.UserInfo;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@EnableScan
public interface UserInfoRepository extends
        CrudRepository<UserInfo, String> {

    List<UserInfo> findUserInfoByUsername(String username);
}
