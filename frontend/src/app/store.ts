import {combineReducers, configureStore} from '@reduxjs/toolkit'
import { persistStore, persistReducer } from 'redux-persist';
import storage from 'redux-persist/lib/storage';
import usersReducer from './User/UserSlice'
import postsReducer from './Post/PostSlice'
import loggedInUserReducer from './User/LoggedInUserSlice'

const loggedInUserPersistConfig = {
    key: 'loggedInUser',
    storage,
}

const persistedLoggedInUserReducer = persistReducer(loggedInUserPersistConfig, loggedInUserReducer);

const rootReducer = combineReducers({
    users: usersReducer,
    loggedInUser: persistedLoggedInUserReducer,
    posts: postsReducer,
});

export const store = configureStore({
    reducer: rootReducer,
    middleware: (getDefaultMiddleware) => getDefaultMiddleware({
        serializableCheck: {
            ignoredActions: ['persist/PERSIST'],
        },
    }),
})

export const persistor = persistStore(store);
export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch