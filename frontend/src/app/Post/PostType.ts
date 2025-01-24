import {Comment} from "../Comment/CommentType";

export interface Post {
    id: string;
    authorId: string;
    type: string;
    status: string;
    title: string;
    shortDescription?: string;
    description: string;
    image?: string;
    createdDateTime: string;
    edited: boolean;
    editedDateTime: string;
    comments: Array<Comment>
    likeNumber: number;
    hashtags: Array<string>;
    fileUrls: Array<string>;
}

export interface NewPost {
    authorId: string;
    type: string;
    status: string;
    title: string;
    shortDescription?: string;
    description: string;
    image?: string;
    hashtags: Array<string>
}

export interface PostUpdate {
    postId: string;
    type: string;
    status: string;
    title: string;
    shortDescription?: string;
    description: string;
    image?: string;
    hashtags: Array<string>
}
