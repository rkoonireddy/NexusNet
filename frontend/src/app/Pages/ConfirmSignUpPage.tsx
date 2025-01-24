import React, {useEffect, useState} from "react"
import {confirmSignUp} from "../Util/auth";
import {useSearchParams} from "react-router-dom";
import {
    StyledButton,
    StyledForm,
    StyledInput,
    StyledLoginContainer,
    StyledLogo,
    StyledLogoContainer,
    StyledSlogan
} from "./LoginPage";
import {useNavigate} from "react-router-dom";
import styled from "styled-components";

const StyledConfirmMessageTitle = styled.h2`
  font-size: 2.5rem;
  color: #ffffff;
  margin-top: -150px;
`;

const StyledConfirmMessage = styled.p`
  font-size: 1.5rem;
  color: #000000;
  background: rgba(255, 255, 255, 0.2);
  margin-top: -25px;
`;

export default function ConfirmSignUp() {
    const [username, setUsername] = useState("");
    const [code, setCode] = useState("");
    const [error, setError] = useState("");
    const [success, setSuccess] = useState(false);
    const navigate = useNavigate();

    const [searchParams] = useSearchParams();

    useEffect(() => {
        const query = searchParams.get('username');
        if (query !== null) {
            setUsername(query);
        }
    }, [searchParams]);

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()
        setError("")

        try {
            await confirmSignUp(username, code)
            setSuccess(true)
        } catch (err: any) {
            setError(err.message)
        }
    }

    if (success) {
        return (
            <StyledLoginContainer>
                <StyledConfirmMessageTitle>Confirmation successful!</StyledConfirmMessageTitle>
                <StyledConfirmMessage>You can now log in with your credentials</StyledConfirmMessage>
                <StyledButton onClick={() => navigate("/login")}>Login</StyledButton>
            </StyledLoginContainer>
        )
    }

    return (
        <StyledLoginContainer>
            <StyledLogoContainer  onClick={() => navigate("/")} margin={"0"}>
                <StyledLogo src="/logo.png"/>
                <StyledSlogan>Sign up to create</StyledSlogan>
            </StyledLogoContainer>
            <StyledForm onSubmit={handleSubmit}>
                <StyledInput id="username"
                             type="text"
                             placeholder="Username"
                             value={username}
                             onChange={(event) => setUsername(event.target.value)}/>
                <StyledInput id="confirmation_code"
                             type="text"
                             placeholder="confirmation code"
                             value={code}
                             onChange={(event) => setCode(event.target.value)}/>
                <StyledButton type="submit">Confirm</StyledButton>
            </StyledForm>
            {error && <p>{error}</p>}
        </StyledLoginContainer>
    )
}