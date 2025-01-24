import {Post} from "../Post/PostType";

export interface User {
    id: string;
    email: string;
    firstName: string;
    lastName: string;
    username: string;
    motto?: string;
    university: string;
    bio?: string;
    degreeProgram?: string;
    birthday?: string;
    profilePicture?: string;
    followedUsers: Array<String>;
}

export interface UserSummary {
    id: string;
    username: string;
    profilePicture: string | null;
}

export interface FollowUser {
    source: string,
    target: string
}

export interface FollowerData {
    nodes: { id: string; name: string; val: number; }[];
    links: { source: string; target: string; }[];
}
