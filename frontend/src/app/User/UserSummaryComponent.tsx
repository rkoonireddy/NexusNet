import styled from "styled-components";
import {User, UserSummary} from "./UserType";
import {ReactComponent as FollowSVG} from "../../static/images/person-plus-fill.svg";
import {useAppSelector} from "../hooks";
import {fetchFollows, selectActiveUser} from "./LoggedInUserSlice";
import {followUser, getUser} from "./UserService";
import {useState} from "react";


const StyledUserSummaryContainer = styled.div`
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

  width: calc(33% - 30px);
  min-width: 240px;
  height: fit-content;
`;

const StyledProfilePicture = styled.div<{ url?: string }>`
  border-radius: 50%;
  width: 50px;
  height: 50px;
  margin: auto 10px;
  background: url(${props => props.url ? props.url : process.env.PUBLIC_URL + '/profile.svg'}) center/cover no-repeat;
`;

export const StyledSVGContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  width: fit-content;
  height: fit-content;
  margin: auto 10px;

  &:hover {
    cursor: pointer;
    color: #00f8cf;
  }
`;

export const StyledUserSummary = styled.div`
  display: flex;
  flex-direction: row;
`;

export const StyledUserDetailsContainer = styled.div`
  display: flex;
  flex-direction: column;
  margin: 10px;
  justify-content: center;
  
  p {
    margin: 4px;
  }
`;

function UserSummaryComponent({user}: { user: UserSummary }) {
    const [expand, setExpand] = useState<boolean>(false);
    const [completeUser, setCompleteUser] = useState<User | null>(null);
    const loggedInUser = useAppSelector(selectActiveUser);

    function reloadData() {
        fetchFollows(loggedInUser.id);
        setTimeout(() => window.location.reload(), 1000);
    }

    async function getUserData() {
        if (completeUser === null) {
            const compUser = await getUser(user.id);
            setCompleteUser(compUser);
        }
        setExpand(!expand);
    }

    return (
        <StyledUserSummaryContainer onClick={getUserData}>
            <StyledUserSummary>
                <StyledProfilePicture></StyledProfilePicture>
                <h3>{user.username}</h3>
                <StyledSVGContainer onClick={(event) => {
                    event.stopPropagation();
                    followUser(loggedInUser.id, user.id).then(() => reloadData());
                }}>
                    <FollowSVG style={{width: "35px", height: "35px"}}/>
                </StyledSVGContainer>
            </StyledUserSummary>
            {expand && completeUser && <StyledUserDetailsContainer>
                <p>{completeUser?.firstName} {completeUser?.lastName}</p>
                <p>{completeUser?.email}</p>
                <p>University: {completeUser?.university}</p>
                <p>Bio: {completeUser?.bio}</p>
            </StyledUserDetailsContainer>}
        </StyledUserSummaryContainer>
    );
}

export default UserSummaryComponent;