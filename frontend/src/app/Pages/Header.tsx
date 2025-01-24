import {useAppSelector} from "../hooks";
import {selectActiveUser} from "../User/LoggedInUserSlice";
import styled from "styled-components";
import {ReactComponent as UserSVG} from "../../static/images/user.svg";
import {ReactComponent as MessageSVG} from "../../static/images/Message.svg";
import {ReactComponent as NetworkSVG} from "../../static/images/network.svg";
import {useNavigate} from "react-router-dom";


const StyledHeaderContainer = styled.div`
  width: calc(100vw - 30px);
  height: 50px;
  display: flex;
  flex-direction: row;
  background-color: #202020;
  justify-content: space-between;
  align-items: center;
  padding: 5px 15px;
`;

const StyledLogo = styled.img`
  width: 200px;
  
  &:hover {
    cursor: pointer;
  }
`;

const StyledIconsContainer = styled.div`
  display: flex;
  flex-direction: row;
`;

const StyledIconContainer = styled.div`
  margin: 0 15px;
  display: flex;
  align-items: center;

  &:hover {
    cursor: pointer;
    scale: 1.025;
  }
`;

const StyledLoginBtn = styled.button`
  width: 80px;
  height: 40px;
  border-radius: 15px;
  background-color: #ffffff;
  color: #000000;
  margin: 5px 25px 0;
  font-size: 1rem;

  &:hover {
    cursor: pointer;
    scale: 1.025;
  }
`;

function Header() {
    const activeUser = useAppSelector(selectActiveUser);
    const navigate = useNavigate();

    return (
        <StyledHeaderContainer>
            <StyledLogo src="/logo.png" onClick={() => navigate("/")}/>
            <StyledIconsContainer>
                {activeUser && <StyledIconContainer title="Connections" onClick={() => navigate("/network")}>
                    <NetworkSVG style={{color: "#ffffff", width: "45px", height: "45px"}}/>
                </StyledIconContainer>}
                {activeUser && <StyledIconContainer title="My chats" onClick={() => navigate("/chat")}>
                    <MessageSVG style={{color: "#ffffff", width: "45px", height: "45px"}}/>
                </StyledIconContainer>}
                {activeUser !== null ?
                    <StyledIconContainer title="My Profile" onClick={() => navigate("/profile")}>
                        <UserSVG style={{color: "#ffffff", width: "45px", height: "45px"}}/>
                    </StyledIconContainer> :
                    <StyledLoginBtn onClick={() => navigate("/login")}>Login</StyledLoginBtn>}

            </StyledIconsContainer>
        </StyledHeaderContainer>
    )
}

export default Header;