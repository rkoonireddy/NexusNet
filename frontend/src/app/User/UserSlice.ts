import {createAsyncThunk, createSelector, createSlice,} from '@reduxjs/toolkit';
import {getAllUsers, getNetwork} from "./UserService";
import { UserSummary} from "./UserType";
import {loggedInUserSlice, selectActiveUser} from "./LoggedInUserSlice";
import {useSelector} from "react-redux";

interface IUserState {
    entities: any[];
    network: UserSummary[][];
    status: string;
    networkStatus: string;
}
const initialState: IUserState = {
    entities: [],
    network: [],
    status: 'idle',
    networkStatus: 'idle'
};
export const fetchUsers = createAsyncThunk('users/fetchUsers', async () => {
    return await getAllUsers()
})

export const fetchNetwork = createAsyncThunk('users/fetchNetwork', async () => {
    return await getNetwork()
})

export const usersSlice = createSlice({
    name: 'users',
    initialState,
    reducers: {},
    extraReducers(builder) {
        builder
            .addCase(fetchUsers.pending, (state, {payload}) => {
                state.status = 'loading'
            })
            .addCase(fetchUsers.fulfilled, (state, {payload}) => {
                state.status = 'succeeded'
                state.entities = payload;
            })
            .addCase(fetchUsers.rejected, (state, action) => {
                state.status = 'failed';
            })
            .addCase(fetchNetwork.pending, (state, {payload}) => {
                state.networkStatus = 'loading'
            })
            .addCase(fetchNetwork.fulfilled, (state, {payload}) => {
                state.networkStatus = 'succeeded'
                state.network = payload;
            })
            .addCase(fetchNetwork.rejected, (state, action) => {
                state.networkStatus = 'failed';
            })
    }
})

function findUsernameById(network: UserSummary[][], userId: string) {
    for (let i = 0; i < network.length; i++) {
        for (let j = 0; j < network[i].length; j++) {
            if (network[i][j].id === userId) {
                return network[i][j].username;
            }
        }
    }
    return "";
}


export const selectNetwork = createSelector(
    // Input selector
    (state: RootState) => state.users.network,
    (state: RootState) => state.users.entities,
    (state: RootState) => selectActiveUser(state),

    // Result function
    (network, entities) => {
        // Create counts object and links array
        const { counts, links } = network.reduce((acc, [source, target]) => {
            acc.counts[source.id] = (acc.counts[source.id] || 0) + 1;
            acc.counts[target.id] = (acc.counts[target.id] || 0) + 1;
            acc.links.push({
                source: source.id,
                target: target.id
            });
            return acc;
        }, { counts: {} as Record<string, number>, links: [] as Array<{ source: string, target: string }> });

        // Create nodes array
        const nodes = Object.entries(counts).map(([id, val]) => {
            const user = network.find(([source, target]) => source.id === id || target.id === id);
            return {
                id,
                name: user ? findUsernameById(network, id) : "",
                val
            };
        });
        entities.forEach((entity) => {
            if (!nodes.find((node) => node.id === entity.id)) {
                nodes.push({
                    id: entity.id,
                    name: entity.username,
                    val: 1
                })
            }
        })
        console.log({ nodes, links })
        return { nodes, links };
    }
);

export const selectNetworkStatus = (state: RootState) => state.users.networkStatus;

export default usersSlice.reducer

interface RootState {
    users: ReturnType<typeof usersSlice.reducer>;
    loggedInUser: ReturnType<typeof loggedInUserSlice.reducer>;
}

export const selectAllUsers = (state: RootState) => state.users.entities;

export const selectUserById = (state: RootState, id: number) =>
    state.users.entities.find((user) => user.id === id);
