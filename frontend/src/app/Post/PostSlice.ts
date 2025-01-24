import {createAsyncThunk, createSelector, createSlice,} from '@reduxjs/toolkit';
import {createNewPost, getAllPosts, getPost} from "./PostService";
import {NewPost, Post} from "./PostType";
import {stringToDate} from "../Util/util";


interface IPostState {
    entities: any[];
    status: string;
}

const initialState: IPostState = {
    entities: [],
    status: 'idle'
};

export const fetchPosts = createAsyncThunk('posts/fetchPosts', async () => {
    return await getAllPosts()
})

export const createPost = createAsyncThunk('posts/createPost', async (newPost: NewPost) => {
    return await createNewPost(newPost);
})

export const fetchPost = createAsyncThunk('posts/fetchPost', async (id: string) => {
    return await getPost(id);
});

export const postsSlice = createSlice({
    name: 'posts',
    initialState,
    reducers: {},
    extraReducers(builder) {
        builder
            .addCase(fetchPosts.pending, (state) => {
                state.status = 'loading'
            })
            .addCase(fetchPosts.fulfilled, (state, {payload}) => {
                state.status = 'succeeded'
                state.entities = payload;
            })
            .addCase(fetchPosts.rejected, (state) => {
                state.status = 'failed';
            })
            .addCase(createPost.fulfilled, (state, {payload}) => {
                state.entities = [...state.entities, payload];
            })
            .addCase(fetchPost.fulfilled, (state, {payload}) => {
                if (payload !== undefined) {
                    const index = state.entities.findIndex((post) => post.id === payload.id);
                    if (index !== -1) {
                        state.entities[index] = payload;
                    }
                }
            })
    }
})

export default postsSlice.reducer

interface RootState {
    posts: ReturnType<typeof postsSlice.reducer>;
}

// export const selectAllPosts = (state: RootState) => state.posts.entities;
export const selectPostsById = (state: RootState, id: string): Post =>
    state.posts.entities.find((post: Post) => post.id === id);

export const selectPostsState = (state: RootState) => state.posts.status;

const selectPostsEntities = (state: RootState) => state.posts.entities;

export const selectAllPosts = (state: RootState) => state.posts.entities;
