import {Comment} from "./CommentType";
import {Post} from "../Post/PostType";

const baseurl = process.env.REACT_APP_POST_BASEURL;

const mockComments = [
    {
        id: "ae14b32e-9418-4be6-bebf-b56903d40573",
        postId: "ae14b32e-9418-4be6-bebf-b56903d40576",
        authorId: "mockuser1",
        content: "The best comment in the world! Awesome text and some more awesome text.",
        likes: 5
    },
    {
        id: "ae14b32e-9418-4be6-bebf-b56903d40574",
        postId: "ae14b32e-9418-4be6-bebf-b56903d40574",
        authorId: "mockuser2",
        content: "The best comment 2 in the world! Awesome text and some more awesome text.",
        likes: 0
    },
    {
        id: "ae14b32e-9418-4be6-bebf-b56903d40575",
        postId: "ae14b32e-9418-4be6-bebf-b56903d40573",
        authorId: "mockuser1",
        content: "The best comment 3 in the world! Awesome text and some more awesome text.",
        likes: 3
    },
    {
        id: "ae14b32e-9418-4be6-bebf-b56903d40576",
        postId: "ae14b32e-9418-4be6-bebf-b56903d40573",
        authorId: "DG11",
        content: "The best comment 4 in the world! Awesome text and some more awesome text.",
        likes: 50
    }
]

export function commentOnPost(comment: Comment, postId: string): Promise<Comment> {
    return fetch(baseurl + "comments/posts/" + postId, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(comment)
    })
        .then(response => response.json())
        .then(data => {
            return comment
        })
        .catch(error => {
            console.log(error);
            return new Promise((resolve) => {
                setTimeout(() => {
                    resolve(comment);
                }, 1000); // Simulate a 1 second delay
            });
        })
}

export function getComments(postId: string): Promise<Comment[]> {
    return fetch(baseurl + "comments/posts/" + postId)
        .then(response => response.json())
        .then(data => {
            return data
        })
        .catch(error => {
            console.log(error);
        })
}

export function likeComment(commentId: string, userId: string): Promise<any | Response> {
    return fetch(baseurl + "likes/comment/" + commentId + "?userId=" + userId, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response)
        .catch(error => {
            console.log(error);
        })
}
