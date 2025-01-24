import {SetStateAction} from "react";
import {StyledButton, StyledInput} from "../Pages/LoginPage";
import styled from "styled-components";


export const StyledTextArea = styled.textarea`
  font-size: 1.25rem;
  padding: 5px;
  border-radius: 15px;
  margin: 15px;
  box-shadow: -5px -5px 10px 2px rgba(0,0,0,.8);
`;

export const StyledSelect = styled.select`
  font-size: 1.5rem;
  padding: 5px;
  margin: 15px;
  box-shadow: -5px -5px 10px 2px rgba(0,0,0,.8);
  &:hover{
    cursor: pointer;
  }
`;

interface AppProps {
    firstName: string;
    setFirstName: React.Dispatch<SetStateAction<string>>;
    lastName: string;
    setLastName: React.Dispatch<SetStateAction<string>>;
    motto: string;
    setMotto: React.Dispatch<SetStateAction<string>>;
    university: string;
    setUniversity: React.Dispatch<SetStateAction<string>>;
    bio: string;
    setBio: React.Dispatch<SetStateAction<string>>;
    degreeProgram: string;
    setDegreeProgram: React.Dispatch<SetStateAction<string>>;
    birthday: string;
    setBirthday: React.Dispatch<SetStateAction<string>>;
    setPage: React.Dispatch<SetStateAction<number>>;
    disabled: boolean;
}

export function NexusNetSubPage(props: AppProps) {
    return(
        <>
            <StyledInput id="firstName"
                         value={props.firstName}
                         placeholder="First name*"
                   onChange={(event) => props.setFirstName(event.target.value)}/>
            <StyledInput id="lastName"
                         value={props.lastName}
                         placeholder="Last name*"
                   onChange={(event) => props.setLastName(event.target.value)}/>
            <StyledInput id="motto"
                         value={props.motto}
                         placeholder="Your motto in life"
                   onChange={(event) => props.setMotto(event.target.value)}/>
            <StyledInput id="university"
                         value={props.university}
                         placeholder="Your university*"
                   onChange={(event) => props.setUniversity(event.target.value)}/>
            <StyledSelect id="degreeProgram" name="degreeProgram" defaultValue={"Select your degree program"} onChange={(event) => props.setDegreeProgram(event.target.value)}>
                <option label="Select your degree program" value="" hidden></option>
                <option label="Bachelors" value="Bachelors"></option>
                <option label="Masters" value="Masters"></option>
                <option label="PhD" value="PhD"></option>
            </StyledSelect>
            <StyledTextArea id="bio"
                            rows={6}
                            cols={30}
                            value={props.bio}
                            placeholder="Tell us something about yourself!"
                   onChange={(event) => props.setBio(event.target.value)}/>
            <StyledInput id="birthday"
                         type="date"
                         value={props.birthday}
                      onChange={(event) => props.setBirthday(event.target.value)}/>
            {/*{error && <StyledError>{error}</StyledError>}*/}
            <StyledButton onClick={(event) => props.setPage(1)}>Back</StyledButton>
            <StyledButton disabled={props.disabled} type="submit">Register</StyledButton>
        </>
    )
}