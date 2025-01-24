import styled from "styled-components";
import {ReactComponent as LikeSVG} from "../../static/images/heart.svg";
import {ReactComponent as FireSVG} from "../../static/images/fire.svg";
import {ReactComponent as ReportSVG} from "../../static/images/report.svg";
import {ReactComponent as ProjectSVG} from "../../static/images/project.svg";
import {ReactComponent as PostSVG} from "../../static/images/camera.svg";
import {dateFormatter, stringToDate} from "../Util/util";
import CommentComponent from "../Comment/CommentComponent";
import {useEffect, useState} from "react";
import {commentOnPost, getComments} from "../Comment/CommentService";
import {Comment} from "../Comment/CommentType";
import {StyledButtonSmall, StyledInput} from "../Pages/LoginPage";
import {useAppDispatch, useAppSelector} from "../hooks";
import {selectActiveUser} from "../User/LoggedInUserSlice";
import {v4 as uuidv4} from 'uuid';
import {likePost} from "./PostService";
import {fetchPost, selectPostsById} from "./PostSlice";
import {ReactComponent as EditSVG} from "../../static/images/edit_pen.svg";
import {useNavigate} from "react-router-dom";
import {StyledFilesLink} from "../Pages/EditPostPage";


const StyledPost = styled.div`
  display: flex;
  color: white;
  background-color: #282c34;
  justify-content: center;
  margin: 10px;
  flex-direction: row;
`;

const StyledPostContent = styled.div`
  display: flex;
  width: 100%;
  justify-content: center;
  flex-direction: column;
  padding: 25px;
`;

const StyledPostHeader = styled.div`
  display: flex;
  justify-content: space-between;
  margin: -10px 0 10px 0;
  font-style: italic;
`;

const StyledPostType = styled.div<{ color: string }>`
  display: flex;
  justify-content: center;
  flex-direction: column;
  color: ${props => props.color};
`;

const StyledPostAuthor = styled.div`
  width: fit-content;
  margin-left: auto;
`;

const StyledPostTitle = styled.div`
  font-size: 2rem;
  text-align: left;
  margin-bottom: 10px;
  border-bottom: 1px solid white;
`;

const StyledPostShortDescription = styled.div`
  font-size: 1.5rem;
  text-align: left;
  margin: 0 5px;
  padding: 10px 0;
  font-style: italic;
  color: #00aaff;
`;

const StyledPostDescription = styled.div`
  font-size: 1.5rem;
  text-align: left;
  margin: 0 5px;
`;

const StyledInteractionsContainer = styled.div`
  display: flex;
  justify-content: center;
  flex-direction: column;
  background-color: #000000;
  min-width: 50px;
  padding-left: 10px;
  position: relative;
`;


export const StyledIconContainer = styled.div<{ last?: string; }>`
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: ${props => props.last || "0"};

  &:hover {
    cursor: pointer;
    scale: 1.025;
  }
`;

const StyledLikes = styled.div`
  position: absolute;
  top: 18px;
  right: 8px;
  font-size: 1.25rem;
`;

const StyledCommentsContainer = styled.div`
  height: fit-content;
  width: 100%;
  display: flex;
  flex-direction: column;
`;

const StyledNoCommentsContainer = styled.div`
  width: 100%;
  color: #000000;
  margin: 10px 0;
`;

const StyledCommentForm = styled.form`
  margin: 5px 5px 5px auto;
`;

const StyledFilesTitle = styled.div`
  font-size: 1.5rem;
  margin: 20px 0 10px 0;
  font-style: italic;
  border-bottom: 1px solid white;
`;

function PostComponent({postId, edit}: { postId: string, edit?: boolean }) {
    const activeUser = useAppSelector(selectActiveUser);
    const post = useAppSelector(state => selectPostsById(state, postId));
    const [comments, setComments] = useState<Comment[]>([]);
    const [postDate, setPostDate] = useState<string>();
    const [showComments, setShowComments] = useState(false);
    const [commentContent, setCommentContent] = useState("");
    const [reloadComponent, setReloadComponent] = useState(true);
    const navigate = useNavigate();
    const dispatch = useAppDispatch();

    async function fetchData() {
        await dispatch(fetchPost(postId));
        const postComments = await getComments(postId);
        if (postComments !== undefined) {
            postComments.sort((a, b) => {
                const d1 = new Date(a.createdAt);
                const d2 = new Date(b.createdAt);
                return d1.getTime() - d2.getTime();
            });
            setComments(postComments);
            setReloadComponent(false);
        }
    }

    useEffect(() => {
        if (reloadComponent) {
            fetchData().then();
        }
    }, [reloadComponent]);


    useEffect(() => {
        if (post) {
            setPostDate(dateFormatter(post.edited ? post.editedDateTime : post.createdDateTime));
        }
    }, [post])

    const handleCommentFormSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        const commentId = uuidv4();
        const newComment: Comment = {
            id: commentId,
            postId: post.id,
            authorId: activeUser.username,
            content: commentContent,
            likeNumber: 0,
            createdAt: ""
        }
        await commentOnPost(newComment, post.id).then();
        setReloadComponent(true);
        setCommentContent("");
    }

    function like() {
        if (activeUser) {
            likePost(post.id, activeUser.username).then(() => setReloadComponent(true));
        }
    }

    return (
        <>
            {post && <StyledPost>
                <StyledPostContent>
                    <StyledPostHeader>
                        <StyledPostType color={post.type === "POST" ? "#00aaff" : "#ff0000"}>
                            {post.type === "POST" ?
                                <PostSVG style={{width: "30px", height: "30px", color: "#00aaff"}}/> :
                                <ProjectSVG style={{width: "30px", height: "30px", color: "#ff0000"}}/>}
                            {post.type}
                        </StyledPostType>
                        <StyledPostAuthor>{post.authorId} - {postDate}</StyledPostAuthor>
                    </StyledPostHeader>
                    <StyledPostTitle>{post.title}</StyledPostTitle>
                    <StyledPostShortDescription>{post.shortDescription}</StyledPostShortDescription>
                    <StyledPostDescription>{post.description}</StyledPostDescription>
                    {post.fileUrls.length > 0 ?
                        <>
                            <StyledFilesTitle>Attachments</StyledFilesTitle>
                            {post.fileUrls.map((url) => (
                                <StyledFilesLink key={url} onClick={() => window.open(url, "_blank")}>
                                    {url.split("/").pop()}
                                </StyledFilesLink>
                            ))}
                        </> : null
                    }
                </StyledPostContent>
                <StyledInteractionsContainer>
                    <StyledIconContainer onClick={like} title="Like">
                        <LikeSVG style={{color: "#E72950", width: "45px", height: "45px"}}/>
                    </StyledIconContainer>
                    <StyledIconContainer onClick={() => setShowComments(!showComments)} title="Comments">
                        <FireSVG style={{width: "45px", height: "45px"}}/>
                    </StyledIconContainer>
                    {edit && <StyledIconContainer title="Edit post" onClick={() => navigate("/post/edit/" + postId)}>
                        <EditSVG style={{width: "40px", height: "40px", color: "#ffffff"}}/>
                    </StyledIconContainer>}
                    <StyledIconContainer last={"auto"} >
                        {/*<ReportSVG style={{width: "45px", height: "45px"}}/>*/}
                    </StyledIconContainer>
                    <StyledLikes>{post.likeNumber}</StyledLikes>
                </StyledInteractionsContainer>

            </StyledPost>}
            {post && showComments &&
                <StyledCommentsContainer>
                    {comments.length > 0 ? comments.map((comment) => (
                        <CommentComponent key={comment.id} comment={comment} setReloadComponent={setReloadComponent}/>
                    )) : <StyledNoCommentsContainer>No comments yet...</StyledNoCommentsContainer>}
                    {activeUser !== null ?
                        <StyledCommentForm onSubmit={handleCommentFormSubmit}>
                            <StyledInput type="text" value={commentContent}
                                         onChange={(e) => setCommentContent(e.target.value)}/>
                            <StyledButtonSmall type="submit">Comment</StyledButtonSmall>
                        </StyledCommentForm> :
                        <StyledNoCommentsContainer>Sign in to comment</StyledNoCommentsContainer>}
                </StyledCommentsContainer>
            }
        </>
    )
}

export default PostComponent;