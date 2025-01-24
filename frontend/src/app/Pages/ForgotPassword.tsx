import React, { useState, FormEvent } from "react";
import { forgotPassword, confirmPassword } from "../Util/auth";
import {
  StyledButton,
  StyledForm,
  StyledInput,
  StyledLoginContainer,
  StyledLogo,
  StyledLogoContainer,
  StyledSlogan
} from "./LoginPage";
import { useNavigate } from "react-router-dom";
import styled, { css } from "styled-components";


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

export const StyledButton2 = styled.button<{ margin?: string, disabled?: boolean }>`
  border: 1px solid #ff0000;
  background-color: #000000;
  color: #ffffff;
  border-radius: 5px;
  width: 300px;
  height: 40px;
  font-size: 1.5rem;
  font-weight: bold;
  box-shadow: -5px -5px 10px 2px rgba(0,0,0,.8);
  margin: ${props => props.margin || '15px auto'};
  ${props => props.disabled && css`
     opacity: 0.5;`
  }
  
  &:hover {
    ${props => !props.disabled && css`
      box-shadow: -2px -2px 5px 2px rgba(0, 0, 0, .8);
      cursor: pointer;
      scale: 0.95;`
    }
  }
`;

export default function ForgotPassword() {
  const [username, setUsername] = useState("");
  const [code, setCode] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [step, setStep] = useState(0); // 0: Enter username, 1: Enter code, 2: Set new password
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);
  const navigate = useNavigate();

  const handleSendCode = async () => {
    try {
      await forgotPassword(username);
      setStep(1); // Move to next step: Enter code
      setError("");
    } catch (err: any) {
      setError(err.message);
    }
  };

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError("");

    try {
      await confirmPassword(username, code, newPassword);
      setSuccess(true);
    } catch (err: any) {
      setError(err.message);
    }
  };

  const handleResetPassword = () => {
    // Perform validation if needed
    setStep(2); // Move to next step: Set new password
  };

  if (success) {
    return (
      <StyledLoginContainer>
        <StyledConfirmMessageTitle>Password reset successful!</StyledConfirmMessageTitle>
        <StyledConfirmMessage>You can now log in with your new password.</StyledConfirmMessage>
        <StyledButton2 onClick={() => navigate("/login")}>Back to Login</StyledButton2>
      </StyledLoginContainer>
    );
  }

  return (
    <StyledLoginContainer>
      <StyledLogoContainer onClick={() => navigate("/")} margin={"0"}>
        <StyledLogo src="/logo.png" />
        <StyledSlogan>Forgot Password</StyledSlogan>
      </StyledLogoContainer>
      {step === 0 && (
        <StyledForm>
          <StyledInput
            id="username"
            type="text"
            placeholder="Username"
            value={username}
            onChange={(event) => setUsername(event.target.value)}
          />
          <StyledButton2 type="button" onClick={handleSendCode}>Send Confirmation Code</StyledButton2>
          {error && <p>{error}</p>}
        </StyledForm>
      )}
      {step === 1 && (
        <StyledForm onSubmit={handleSubmit}>
          <StyledInput
            id="confirmation_code"
            type="text"
            placeholder="Confirmation Code"
            value={code}
            onChange={(event) => setCode(event.target.value)}
          />
          <StyledInput
            id="new_password"
            type="password"
            placeholder="New Password"
            value={newPassword}
            onChange={(event) => setNewPassword(event.target.value)}
          />
          <StyledButton2 type="submit">Reset Password</StyledButton2>
          {error && <p>{error}</p>}
        </StyledForm>
      )}
      {step === 2 && (
        <StyledForm>
          <StyledInput
            id="new_password"
            type="password"
            placeholder="New Password"
            value={newPassword}
            onChange={(event) => setNewPassword(event.target.value)}
          />
          <StyledButton2 type="button" onClick={handleResetPassword}>Set New Password</StyledButton2>
          {error && <p>{error}</p>}
        </StyledForm>
      )}
      <StyledButton2 onClick={() => navigate("/login")}>Back to Login</StyledButton2>
    </StyledLoginContainer>
  );
}
