import styled from "styled-components";
import {Comment} from "./CommentType";
import {ReactComponent as LikeSVG} from "../../static/images/heart.svg";
import React from "react";
import {StyledIconContainer} from "../Post/PostComponent";
import {likeComment} from "./CommentService";
import {useAppSelector} from "../hooks";
import {selectActiveUser} from "../User/LoggedInUserSlice";
import {dateFormatter} from "../Util/util";


const StyledComment = styled.div`
  width: 80%;
  min-width: 200px;
  background-color: #FFC000;
  border: 1px solid #000000;
  display: flex;
  flex-direction: column;
  margin: 5px 10px 5px auto;
  position: relative;
`;

const StyledCommentContent = styled.div`
  color: #000000;
  padding: 5px;

`;

const StyledCommentAuthor = styled.div`
  color: #000000;
  margin-left: auto;
  padding: 5px;
  font-style: italic;
  font-size: 0.75rem;
`;

const StyledLikeContainer = styled.div`
  position: absolute;
  left: -3px;
  bottom: -12px;
  scale: 0.75;
  
  &:hover {
    cursor: pointer;
  }
`;

const StyledLikesCount = styled.div`
  position: absolute;
  left: 30px;
  bottom: 2px;
  color: #000000;
`;

type CommentComponentProps = {
    comment: Comment;
    setReloadComponent: (value: boolean | ((prevState: boolean) => boolean)) => void;
};

function CommentComponent({comment, setReloadComponent}: CommentComponentProps) {
    const activeUser = useAppSelector(selectActiveUser);

    function like() {
        if (activeUser) {
            likeComment(comment.id, activeUser.username).then(() => setReloadComponent(true));
        }
    }
    return (
        <StyledComment>
            <StyledCommentContent>{comment.content}</StyledCommentContent>
            <StyledCommentAuthor>{comment.authorId} - {dateFormatter(comment.createdAt)}</StyledCommentAuthor>
            <StyledLikeContainer onClick={like} title="Like">
                <LikeSVG style={{color: "#E72950"}}/>
            </StyledLikeContainer>
            <StyledLikesCount>
                {comment.likeNumber}
            </StyledLikesCount>
        </StyledComment>
    )
}

export default CommentComponent;