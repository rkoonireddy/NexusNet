import styled, {css} from "styled-components";
import {useAppSelector} from "../hooks";
import {fetchPosts, selectPostsState} from "../Post/PostSlice";
import Header from "./Header";
import {selectActiveUser} from "../User/LoggedInUserSlice";
import {useNavigate} from "react-router-dom";
import {PostsComponent} from "../Post/PostsComponent";
import {store} from "../store";
import {useEffect, useState} from "react";


const StyledMainPage = styled.div`
  display: flex;
  flex-direction: row;
  height: calc(100vh - 64px);
`;

const StyledMenuContainer = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  width: 25%;
  max-width: 25%;
  font-size: 2rem;
  padding: 20px;
`;

export const StyledMenuButton = styled.button`
  background-color: #000000;
  color: #ffffff;
  border-radius: 5px;
  width: 250px;
  height: 30px;
  font-size: 1.5rem;
  font-weight: bold;
  box-shadow: -2px -2px 10px 2px rgba(255, 255, 255, .8);
  margin: 15px 50px;

  &:hover {
    box-shadow: -1px -1px 5px 2px rgba(255, 255, 255, .8);
    cursor: pointer;
    scale: 0.95;
  }`;

export const StyledFilterButton = styled.button<{ selected: boolean }>`
  background-color: #000000;
  color: #ffffff;
  border-radius: 5px;
  width: 100px;
  height: 30px;
  font-size: 1rem;
  font-weight: bold;
  box-shadow: -2px -2px 10px 2px rgba(0, 0, 0, .8);
  margin: 0;

  ${props => props.selected && css`
    opacity: 0.8;
    background-color: #696969;
    box-shadow: none;`
  }
  &:hover {
    ${props => !props.disabled && css`
      box-shadow: -1px -1px 5px 2px rgba(0, 0, 0, .8);
      cursor: pointer;
      scale: 0.95;`
    }

  }`;

function Home() {
    const activeUser = useAppSelector(selectActiveUser);
    const navigate = useNavigate();
    const postState = useAppSelector(selectPostsState);
    const [postsFetched, setPostsFetched] = useState<boolean>(false);

    useEffect(() => {
        store.dispatch(fetchPosts()).then(() => setPostsFetched(true));
    }, [])

    return (
        <>
            <Header/>
            <StyledMainPage>
                <StyledMenuContainer>
                    {activeUser !== null ?
                        <>
                            <StyledMenuButton onClick={() => navigate("/post/create")}>New Post</StyledMenuButton>
                            <StyledMenuButton onClick={() => navigate("/chat")}>My Chats</StyledMenuButton>
                            <StyledMenuButton onClick={() => navigate("/network")}>Connections</StyledMenuButton>
                        </> :
                        <></>}
                </StyledMenuContainer>
                {postState !== "loading" && postsFetched && <PostsComponent/>}
            </StyledMainPage>
        </>
    );
}

export default Home;