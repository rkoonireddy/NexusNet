import {createAsyncThunk, createSlice} from '@reduxjs/toolkit';
import {createUser, getFollowers, getFollows, getUser, getUserByUsername} from "./UserService";

interface IUserState {
    value: any;
    connections: any[];
}

const initialState: IUserState = {
    value: null,
    connections: []
};

export const getLoggedInUserThunk = createAsyncThunk('users/getLoggedInUser', async (username: string, thunkAPI) => {
    const loggedInUser = await getUserByUsername(username);
    thunkAPI.dispatch(fetchFollows(loggedInUser.id));
    return loggedInUser;
});

export const fetchFollows = createAsyncThunk('users/getConnections', async (userId: string) => {
    const follows = await getFollows(userId);
    const followers = await getFollowers(userId);
    const filteredFollowers = followers.filter(follower => !follows.some(follow => follow.id === follower.id));
    return follows.concat(filteredFollowers);
});

export const loggedInUserSlice = createSlice({
    name: 'loggedInUser',
    initialState,
    reducers: {
        setLoggedInUser: (state, action) => {
            const newUser = action.payload;
            createUser(newUser).then();
            state.value = newUser;
        },
        logOut: (state) => {
            state.value = null;
        },
    },
    extraReducers(builder) {
        builder
            .addCase(getLoggedInUserThunk.fulfilled, (state, {payload}) => {
                state.value = payload;
            })
            .addCase(fetchFollows.fulfilled, (state, {payload}) => {
                state.connections = payload;
            })
    }
})

export const {setLoggedInUser, logOut} = loggedInUserSlice.actions

export default loggedInUserSlice.reducer

interface RootState {
    loggedInUser: ReturnType<typeof loggedInUserSlice.reducer>;
}

export const selectActiveUser = (state: RootState) => state.loggedInUser.value;

export const selectConnections = (state: RootState) => state.loggedInUser.connections;
