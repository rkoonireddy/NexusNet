import React from 'react';
import Home from "./app/Pages/MainPage";
import {BrowserRouter, Link, Route, Routes} from "react-router-dom";
import Register from "./app/Pages/RegisterPage";
import ConfirmSignUp from "./app/Pages/ConfirmSignUpPage";
import Login from "./app/Pages/LoginPage";
import ProfilePage from "./app/Pages/ProfilePage";
import styled from "styled-components";
import CreatePost from "./app/Pages/CreatePostPage";
import MyNetwork from "./app/Pages/MyNetwork";
import EditPost from "./app/Pages/EditPostPage";
import {ChatPage} from "./app/Pages/ChatPage";
import ForgotPassword from "./app/Pages/ForgotPassword";

const StyledMain = styled.main`
  background: center no-repeat url("/background.jpg");
  background-size: cover;
  height: 100vh;
`;

function App() {
    return (
        <BrowserRouter>
            <StyledMain>
                <Routes>
                    <Route path="/" element={<Home/>}/>
                    <Route path="/register" element={<Register/>}/>
                    <Route path="/confirm-sign-up" element={<ConfirmSignUp/>}/>
                    <Route path="/login" element={<Login/>}/>
                    <Route path="/profile" element={<ProfilePage/>}/>
                    <Route path="/post/create" element={<CreatePost/>}/>
                    <Route path="/post/edit/:postId" element={<EditPost />}/>
                    <Route path="/network" element={<MyNetwork/>}/>
                    <Route path="/chat/:chatId?" element={<ChatPage />}/>
                    <Route path="/forgot-password" element={<ForgotPassword/>}/>
                </Routes>
            </StyledMain>
        </BrowserRouter>
    );
}

export default App;
