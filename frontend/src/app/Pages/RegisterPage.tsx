import {useEffect, useState} from "react";
import {v4 as uuidv4} from 'uuid';
import {signUp} from "../Util/auth";
import {CognitoSubPage} from "../Register/CognitoUserData";
import {NexusNetSubPage} from "../Register/NexusNetUserData";
import {useDispatch} from "react-redux";
import {setLoggedInUser} from "../User/LoggedInUserSlice";
import {useNavigate} from "react-router-dom";
import {
    StyledError,
    StyledForm,
    StyledLoginContainer,
    StyledLogo,
    StyledLogoContainer,
    StyledSlogan
} from "./LoginPage";


export default function Register() {
    const userId = uuidv4();
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [motto, setMotto] = useState("");
    const [university, setUniversity] = useState("");
    const [bio, setBio] = useState("");
    const [degreeProgram, setDegreeProgram] = useState("");
    const [birthday, setBirthday] = useState("");

    const [error, setError] = useState("");
    const [page, setPage] = useState(1);

    const dispatch = useDispatch();
    const navigate = useNavigate();
    const [disabled1, setDisabled1] = useState(true);
    const [disabled2, setDisabled2] = useState(true);

    const [isSubmitted, setIsSubmitted] = useState(false);

    useEffect(() => {
        setDisabled1(
            username === "" ||
            password === "" ||
            email === "");
    }, [username, password, email])

    useEffect(() => {
        setDisabled2(
            firstName === "" ||
            lastName === "" ||
            university === "" ||
            degreeProgram === ""
        )
    }, [firstName, lastName, university, degreeProgram])

    const onSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setError("")

        try {
            await signUp(username, email, password);
            setIsSubmitted(true);
        } catch (err: any) {
            console.log(err)
            setError(err.message)
        }
    }

    useEffect(() => {
        if (isSubmitted) {
            const newUser = {
                id: userId,
                email: email,
                firstName: firstName,
                lastName: lastName,
                username: username,
                motto: motto,
                university: university,
                bio: bio,
                degreeProgram: degreeProgram,
                birthday: birthday,
                profilePicture: "",
                followedUsers: []
            }
            dispatch(setLoggedInUser(newUser));
            navigate(`/confirm-sign-up?username=${username}`);
        }
    }, [isSubmitted]);

    return (
        <StyledLoginContainer>
            <StyledLogoContainer onClick={() => navigate("/")} margin={"0"}>
                <StyledLogo src="/logo.png"/>
                <StyledSlogan>Sign up to create</StyledSlogan>
            </StyledLogoContainer>
            <StyledForm onSubmit={onSubmit}>
                {page === 1 && <CognitoSubPage
                    username={username} setUsername={setUsername}
                    email={email} setEmail={setEmail}
                    password={password} setPassword={setPassword}
                    setPage={setPage} disabled={disabled1}
                    setError={setError}/>}
                {page === 2 && <NexusNetSubPage firstName={firstName} setFirstName={setFirstName}
                                                lastName={lastName} setLastName={setLastName}
                                                motto={motto} setMotto={setMotto}
                                                university={university} setUniversity={setUniversity}
                                                bio={bio} setBio={setBio}
                                                degreeProgram={degreeProgram} setDegreeProgram={setDegreeProgram}
                                                birthday={birthday} setBirthday={setBirthday}
                                                setPage={setPage} disabled={disabled2}/>}
                {error && <StyledError>{error}</StyledError>}
            </StyledForm>
        </StyledLoginContainer>
    )
}
