import {SetStateAction, useState} from "react";
import {StyledButton, StyledInput, StyledError} from "../Pages/LoginPage";
import styled from "styled-components";

interface AppProps {
    username: string;
    setUsername: React.Dispatch<SetStateAction<string>>;
    email: string;
    setEmail: React.Dispatch<SetStateAction<string>>;
    password: string;
    setPassword: React.Dispatch<SetStateAction<string>>;
    setPage: React.Dispatch<SetStateAction<number>>;
    disabled: boolean;
    setError: React.Dispatch<SetStateAction<string>>;
}

const StyledPasswordMessage = styled.p`
  color: #000000;
  font-size: 1rem;
  font-style: italic;
  width: fit-content;
  margin: 0 auto;
  max-width: 300px;
  background-color: #ffffff60;
`;

export function CognitoSubPage(props: AppProps) {
    const [validInput, setValidInput] = useState<boolean>(true);
    const [passwordMessage, setPasswordMessage] = useState<string>("");

    function validatePassword(password: string): boolean {
        const regex = /^[\S]+.*[\S]+$/;
        const hasUpperCase = /(?=.*[A-Z])/.test(password);
        const hasLowerCase = /(?=.*[a-z])/.test(password);
        const hasNumber = /(?=.*\d)/.test(password);
        const hasSymbol = /(?=.*[!@#$%^&*()_+\-=\[\]{}|'])/.test(password);
        return regex.test(password) && password.length >= 8 && hasUpperCase && hasLowerCase && hasNumber && hasSymbol;
    }

    function settingPassword(newPassword: string): void {
        if (!validatePassword(newPassword)) {
            setValidInput(false);
            setPasswordMessage("Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");
        } else {
            setValidInput(true);
            setPasswordMessage("");
        }
        if (newPassword === "") {
            setValidInput(true);
            setPasswordMessage("");
        }
        props.setPassword(newPassword);
    }

    const isButtonDisabled = props.username === "" || props.email === "" || props.password === "" || !validInput;

    return (
        <>
            <StyledInput id="email"
                         placeholder="Email"
                         value={props.email}
                         onChange={(event) => props.setEmail(event.target.value)}/>
            <StyledInput id="username"
                         placeholder="Username"
                         value={props.username}
                         onChange={(event) => props.setUsername(event.target.value)}/>
            <StyledInput id="password"
                         type="password"
                         placeholder="Password"
                         value={props.password}
                         $valid={validInput}
                         onChange={(event) => settingPassword(event.target.value)}/>
            <StyledPasswordMessage>{passwordMessage}</StyledPasswordMessage>
            <StyledButton disabled={isButtonDisabled} onClick={() => props.setPage(2)}>Next</StyledButton>
        </>
    )
}