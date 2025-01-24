import styled from "styled-components";
import {StyledButton, StyledForm, StyledInput} from "./LoginPage";
import Header from "./Header";
import {useEffect, useState} from "react";
import {useAppDispatch, useAppSelector} from "../hooks";
import {selectActiveUser} from "../User/LoggedInUserSlice";
import {NewPost} from "../Post/PostType";
import {createPost} from "../Post/PostSlice";
import {useNavigate} from "react-router-dom";
import {StyledSelect, StyledTextArea} from "../Register/NexusNetUserData";
import {ReactComponent as XSVG} from "../../static/images/x.svg";


export const StyledCreatePostContainer = styled.div`
  display: flex;
  margin: 100px 0 0 0;
  justify-content: center;
  align-items: center;
  flex-direction: column;
`;

export const StyledPageTitle = styled.div`
  font-size: 2.5rem;
  font-weight: bold;
  color: #ffffff;
`;

export const StyledHashtagsContainer = styled.div`
  width: 100%;
  display: flex;
  flex-direction: row;
  margin-bottom: -10px;
  padding: 0 15px;
`;

export const StyledHashtag = styled.div`
  background-color: #282c34;
  font-size: 0.75rem;
  width: fit-content;
  border-radius: 10px;
  margin: 5px;
  padding: 5px;
  color: #ffffff;
  display: flex;
  flex-direction: row;
  align-items: center;
`;

export default function CreatePost() {
    const activeUser = useAppSelector(selectActiveUser);
    const [type, setType] = useState("");
    const [title, setTitle] = useState("");
    const [shortDescription, setShortDescription] = useState("");
    const [description, setDescription] = useState("");
    const [hashtags, setHashtags] = useState<string[]>([]);
    const [hashtag, setHashtag] = useState("");
    const [disabled, setDisabled] = useState(true);


    const dispatch = useAppDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        if(type !== "" &&
        title !== "" &&
        description !== "") {
            setDisabled(false);
        }
        else {
            setDisabled(true);
        }

    }, [type, title, description])


    function newPost(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault();
        const newP: NewPost = {
            authorId: activeUser.username,
            type: type,
            status: "NEW",
            title: title,
            shortDescription: shortDescription,
            description: description,
            image: "",
            hashtags: hashtags
        }
        dispatch(createPost(newP));
        console.log(newP);
        navigate("/");
    }

    const onKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === "Tab") {
            e.preventDefault();
            if (!hashtags.includes(hashtag)) {
                setHashtags([...hashtags, hashtag])
            }
            setHashtag("");
        }
    }

    const removeHashtag = (ht: string) => {
        const updatedHashtags = hashtags.filter(h => h !== ht);
        setHashtags(updatedHashtags);
    }

    return (
        <>
            <Header/>
            <StyledCreatePostContainer>
                <StyledButton onClick={() => navigate("/")}>Back</StyledButton>
                <StyledPageTitle>Create Post</StyledPageTitle>
                <StyledForm onSubmit={newPost}>
                    <StyledInput id="title"
                                 type="text"
                                 placeholder="Title"
                                 value={title}
                                 onChange={(event) => setTitle(event.target.value)}/>
                    <StyledSelect id="type" name="type" defaultValue={"Select type"} onChange={(event) => setType(event.target.value)}>
                        <option label="Select type" value="" hidden ></option>
                        <option label="Post" value="POST"></option>
                        <option label="Project" value="PROJECT"></option>
                    </StyledSelect>
                    <StyledInput id="shortDescription"
                                 type="text"
                                 placeholder="Short Description"
                                 value={shortDescription}
                                 onChange={(event) => setShortDescription(event.target.value)}/>
                    <StyledTextArea id="description"
                                    rows={6}
                                    cols={30}
                                    value={description}
                                    placeholder="Description of your post"
                                    onChange={(event) => setDescription(event.target.value)}/>
                    <StyledHashtagsContainer>
                        {hashtags.map((h, index) => (
                            <StyledHashtag key={index}>#{h}
                                <XSVG onClick={() => removeHashtag(h)} style={{color: "#ffffff", width: "20px", height: "20px"}}/>
                            </StyledHashtag>
                        ))}
                    </StyledHashtagsContainer>
                    <StyledInput id="hashtags"
                                 type="text"
                                 placeholder="Hashtags - press Tab to add"
                                 value={hashtag}
                                 onChange={(event) => setHashtag(event.target.value)}
                                 onKeyDown={onKeyDown}/>

                    <StyledButton disabled={disabled} type={"submit"}>Create</StyledButton>
                </StyledForm>
            </StyledCreatePostContainer>
        </>
    )
}