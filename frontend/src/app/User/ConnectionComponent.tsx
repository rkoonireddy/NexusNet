import styled from "styled-components";
import {User, UserSummary} from "./UserType";
import {followUser, getUser, unfollowUser} from "./UserService";
import {StyledSVGContainer, StyledUserDetailsContainer, StyledUserSummary} from "./UserSummaryComponent";
import {useAppSelector} from "../hooks";
import {fetchFollows, selectActiveUser} from "./LoggedInUserSlice";
import {ReactComponent as UnfollowSVG} from "../../static/images/person-dash-fill.svg";
import {useState} from "react";
import {ReactComponent as MessageSVG} from "../../static/images/Message.svg";
import {getChatOfParticipants, sendMessage} from "../chat/ChatService";
import {MessageType} from "../chat/MessageType";
import {useNavigate} from "react-router-dom";


const StyledConnection = styled.div`
  display: flex;
  flex-direction: column;
  color: white;
  background-color: #282c34;
  padding: 5px;
  margin: 10px;
  box-shadow: 5px 5px 10px 2px rgba(0, 0, 0, .8);

  &:hover {
    cursor: pointer;
    box-shadow: 2px 2px 10px 2px rgba(0, 0, 0, .8);
  }

  width: calc(50% - 30px);
  height: fit-content;
`;

const StyledProfilePicture = styled.div<{ url?: string }>`
  border-radius: 50%;
  width: 75px;
  height: 75px;
  margin: auto 10px;
  background: url(${props => props.url ? props.url : process.env.PUBLIC_URL + '/profile.svg'}) center/cover no-repeat;
`;

function ConnectionComponent({connection}: { connection: UserSummary }) {
    const [expand, setExpand] = useState<boolean>(false);
    const [completeUser, setCompleteUser] = useState<User | null>(null);
    const loggedInUser = useAppSelector(selectActiveUser);
    const navigate = useNavigate();

    function reloadData() {
        fetchFollows(loggedInUser.id);
        setTimeout(() => window.location.reload(), 1000);
    }

    async function getUserData() {
        if (completeUser === null) {
            const compUser = await getUser(connection.id);
            setCompleteUser(compUser);
        }
        setExpand(!expand);
    }

    function sendFirstMessage() {
        const message: MessageType = {
            sender: loggedInUser.username,
            receiver: connection.username,
            content: "👋"
        }
        getChatOfParticipants(loggedInUser.username, connection.username)
            .then((data) => navigate(`/chat/${data.id}`))
            .catch((error) => {
                if (error.message.includes('404')) {
                    sendMessage(message).then((data) => navigate(`/chat/${data.id}`));
                }
            });
    }

    return (
        <StyledConnection onClick={getUserData}>
            <StyledUserSummary>
                <StyledProfilePicture></StyledProfilePicture>
                <h3>{connection.username}</h3>
                <StyledSVGContainer onClick={(event) => {
                    event.stopPropagation();
                    unfollowUser(loggedInUser.id, connection.id).then(() => reloadData());
                }}>
                    <UnfollowSVG style={{width: "35px", height: "35px"}}/>
                </StyledSVGContainer>
                <StyledSVGContainer onClick={(event) => {
                    event.stopPropagation();
                    sendFirstMessage();
                }}>
                    <MessageSVG style={{width: "35px", height: "35px"}}/>
                </StyledSVGContainer>
            </StyledUserSummary>
            {expand && completeUser && <StyledUserDetailsContainer>
                <p>{completeUser?.firstName} {completeUser?.lastName}</p>
                <p>{completeUser?.email}</p>
                <p>University: {completeUser?.university}</p>
                <p>Bio: {completeUser?.bio}</p>
            </StyledUserDetailsContainer>}
        </StyledConnection>
    );
}

export default ConnectionComponent;