import {useEffect, useState} from "react";
import {signIn} from "../Util/auth";
import {useNavigate} from "react-router-dom";
import {getLoggedInUserThunk} from "../User/LoggedInUserSlice";
import {store} from "../store";
import styled, {css} from "styled-components";

export const StyledLoginContainer = styled.div`
  width: 100%;
  height: 100%;
  display: flex;
  margin: 0;
  justify-content: center;
  align-items: center;
  flex-direction: column;
`;

export const StyledLogoContainer = styled.div<{ margin?: string }>`
  margin-top: ${props => props.margin || '-150px'};
  position: relative;

  &:hover {
    cursor: pointer;
  }
`;

export const StyledLogo = styled.img`
  width: 400px;
`;

export const StyledSlogan = styled.div`
  position: absolute;
  color: #06f3d7;
  top: -25px;
  right: 0;
  font-size: 1.5rem;
`;

export const StyledForm = styled.form`
  display: flex;
  flex-direction: column;
  margin: 15px 0;
`;

export const StyledInput = styled.input<{ $valid?: boolean }>`
  font-size: 1.5rem;
  padding: 5px;
  border-radius: 5px;
  margin: 15px;
  box-shadow: -5px -5px 10px 2px rgba(0, 0, 0, .8);
  ${props => props.$valid === false && css`
    border: 3px solid #ff0000;`
  }
`;

const StyledButtonContainer = styled.div`
  display: flex;
  justify-content: space-between;
`;

export const StyledButton = styled.button<{ margin?: string, disabled?: boolean }>`
  border: 1px solid #ff0000;
  background-color: #000000;
  color: #ffffff;
  border-radius: 5px;
  width: 150px;
  height: 40px;
  font-size: 1.5rem;
  font-weight: bold;
  box-shadow: -5px -5px 10px 2px rgba(0, 0, 0, .8);
  margin: ${props => props => props.margin || '15px auto'};

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

export const StyledButtonSmall = styled(StyledButton)`
  font-size: 1rem;
  width: 100px;
  height: 30px;
`;

export const StyledError = styled.p`
  color: #ff0000;
  background-color: #00000080;
  font-style: italic;
  font-size: 1.5rem;
  width: fit-content;
  margin: 10px auto;
`;

const StyledForgotPassword = styled.div`
  font-size: 1rem;
  color: #000000;
  text-decoration: underline;

  &:hover {
    cursor: pointer;
  }
`;

export default function Login() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();
    const [disabled, setDisabled] = useState(true);

    useEffect(() => {
        setDisabled(username === "" || password === "");
    }, [username, password])

    const onSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setError("")

        try {
            await signIn(username, password);
            store.dispatch(getLoggedInUserThunk(username));
            navigate("/");

        } catch (err: any) {
            setError(err.message)
        }
    }

    const handleForgotPassword = () => {
        // Navigate to the forgot password page
        navigate("/forgot-password");
    }

    return (
        <StyledLoginContainer>
            <StyledLogoContainer onClick={() => navigate("/")}>
                <StyledLogo src="/logo.png"/>
                <StyledSlogan>Enter to create</StyledSlogan>
            </StyledLogoContainer>
            <StyledForm onSubmit={onSubmit}>
                <StyledInput id="username"
                             type="text"
                             placeholder="Username"
                             value={username}
                             onChange={(event) => setUsername(event.target.value)}/>
                <StyledInput id="password"
                             type="password"
                             placeholder="Password"
                             value={password}
                             onChange={(event) => setPassword(event.target.value)}/>
                <StyledButtonContainer>
                    <StyledButton type="button" margin={"15px"}
                                  onClick={() => navigate("/register")}>Register</StyledButton>
                    <StyledButton disabled={disabled} margin={"15px"} type={"submit"}>Login</StyledButton>
                </StyledButtonContainer>
                {error && <StyledError>{error}</StyledError>}
                <StyledForgotPassword
                    onClick={handleForgotPassword}>Forgot your password?</StyledForgotPassword>
            </StyledForm>
        </StyledLoginContainer>
    )
}
