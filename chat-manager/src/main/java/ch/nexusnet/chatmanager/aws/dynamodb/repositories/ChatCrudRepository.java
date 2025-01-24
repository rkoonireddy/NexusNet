package ch.nexusnet.chatmanager.aws.dynamodb.repositories;

import ch.nexusnet.chatmanager.chat.Chat;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

@EnableScan
public interface ChatCrudRepository extends CrudRepository<Chat, String>{
    Optional<Chat> findByParticipant1AndParticipant2(String participant1, String participant2);

    List<Chat> findByIdContaining(String participant1);
}
