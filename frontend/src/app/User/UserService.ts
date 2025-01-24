import {User, UserSummary} from "./UserType";

const baseurl = process.env.REACT_APP_USER_BASEURL;

const mockUser = {
    id: "be14b32e-9418-4be6-bebf-b56903d40578",
    username: "mockuser12",
    profilePicture: ""
}

const mockUsers = [
    {
        id: "ae14b32e-9418-4be6-bebf-b56903d40577",
        username: "mockuser1",
        profilePicture: "",
    },
    {
        id: "ae14b32e-9418-4be6-bebf-b56903d40578",
        username: "mockuser2",
        profilePicture: "",
    },
    {
        id: "ae14b32e-9418-4be6-bebf-b56903d40579",
        username: "mockuser3",
        profilePicture: "",
    },
    {
        id: "ae14b32e-9418-4be6-bebf-b56903d40580",
        username: "mockuser4",
        profilePicture: "",
    },
    {
        id: "ae14b32e-9418-4be6-bebf-b56903d40581",
        username: "mockuser5",
        profilePicture: "",
    },
    {
        id: "ae14b32e-9418-4be6-bebf-b56903d40582",
        username: "mockuser6",
        profilePicture: "",
    },
    {
        id: "ae14b32e-9418-4be6-bebf-b56903d40583",
        username: "mockuser7",
        profilePicture: "",
    },
    {
        id: "ae14b32e-9418-4be6-bebf-b56903d40584",
        username: "mockuser8",
        profilePicture: "",
    },
    {
        id: "ae14b32e-9418-4be6-bebf-b56903d40585",
        username: "mockuser9",
        profilePicture: "",
    },
    {
        id: "ae14b32e-9418-4be6-bebf-b56903d40586",
        username: "mockuser10",
        profilePicture: "",
    },
    {
        id: "ae14b32e-9418-4be6-bebf-b56903d40587",
        username: "mockuser11",
        profilePicture: "",
    },
    mockUser
]


export function getAllUsers(): Promise<UserSummary[]> {
    return fetch(baseurl + "users")
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            return data
        })
        .catch(error => {
            console.log(error)
            return new Promise((resolve) => {
                setTimeout(() => {
                    resolve(mockUsers);
                }, 1000)
            })
        })
}

export function createUser(user: User) {
    return fetch(baseurl + "users", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .catch(error => {
            console.log(error)
            console.log("Mock user created:");
            console.log(JSON.stringify(user));
        });
}

export function getUser(userId: string): Promise<User> {
    return fetch(baseurl + "users/id/" + userId)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            return data
        })
        .catch(error => {
            console.log(error);
            return new Promise((resolve) => {
                setTimeout(() => {
                    resolve(mockUser);
                }, 1000)
            })
        });
}

export function getUserByUsername(username: string): Promise<User> {
    return fetch(baseurl + "users/username/" + username)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            return data
        })
        .catch(error => {
            console.log(error);
            return new Promise((resolve) => {
                setTimeout(() => {
                    resolve(mockUser);
                }, 1000)
            })
        });
}

export async function updateUser(user: User, endpoint: string = "users"): Promise<boolean | null> {
    try {
        const response = await fetch(baseurl + endpoint + `/id/${user.id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(user)
        });

        if (!response.ok) {
            // If the response is not successful, throw an error
            throw new Error(`Failed to edit user: ${response.statusText}`);
        }

        return true;
    } catch (error) {
        // Handle errors gracefully
        console.error(`Error editing user ${user.username}: ${error}`);
        return false;
    }
}

export async function getProfilePic(userId: string, endpoint: string = "users"): Promise<string | null> {
    try {
        const response = await fetch(baseurl + endpoint + `/${userId}/profilePicture`, {
            method: 'GET',
        });

        if (!response.ok) {
            // If the response is not successful, throw an error
            throw new Error(`Failed to retrieve profile picture: ${response.statusText}`);
        }

        const responseBody = await response.text();

        if(responseBody === "") {
            return null;
        }

        // Return the response text (URL of the profile picture)
        return responseBody;
    } catch (error) {
        // Handle errors gracefully
        console.error(`Error retrieving profile picture: ${error}`);
        return null;
    }
}


export async function updateProfilePic(userId: string, profilePicture: File, endpoint: string = "users"): Promise<string | null> {
    try {
        const formData = new FormData();
        formData.append('profilePicture', profilePicture);

        const response = await fetch(baseurl + endpoint + `/${userId}/profilePicture`, {
            method: 'POST',
            headers: {
                'Accept': '*/*'
            },
            body: formData
        });

        if (!response.ok) {
            // If the response is not successful, throw an error
            throw new Error(`Failed to upload profile picture: ${response.statusText}`);
        }

        // Return the response text
        return await response.text();
    } catch (error) {
        // Handle errors gracefully
        console.error(`Error uploading profile picture: ${error}`);
        return null;
    }
}

const networkMockData = [
    {source: '0c83f09a-1c14-4e54-b7aa-48041fda6103', target: 'id2'},
    {source: 'id2', target: 'id3'},
    {source: 'id2', target: 'id4'},
    {source: 'id3', target: 'id5'},
    {source: 'id3', target: 'id6'},
    {source: 'id6', target: 'id7'},
    {source: '0c83f09a-1c14-4e54-b7aa-48041fda6103', target: 'id8'},
    {source: 'id9', target: 'id9'},
    {source: 'id9', target: 'id10'},
    {source: '0c83f09a-1c14-4e54-b7aa-48041fda6103', target: 'id3'}
]

const followerMockData: UserSummary[] = [
    {id: "testId1", username: "Test1", profilePicture: ""},
    {id: "testId2", username: "Test2", profilePicture: ""},
    {id: "testId3", username: "Test3", profilePicture: ""},
    {id: "testId4", username: "Test4", profilePicture: ""},
    {id: "testId5", username: "Test5", profilePicture: ""},
    {id: "testId6", username: "Test6", profilePicture: ""},
    {id: "testId7", username: "Test7", profilePicture: ""},
    {id: "testId8", username: "Test8", profilePicture: ""}
]

const networkMockData2: UserSummary[][] = [
    [
        {
            "id": "ae14b32e-9418-4be6-bebf-b56903d40511",
            "username": "DGERGELY11",
            "profilePicture": null
        },
        {
            "id": "ae14b32e-9418-4be6-bebf-b56903d40513",
            "username": "DGERGELY13",
            "profilePicture": null
        }
    ],
    [
        {
            "id": "ae14b32e-9418-4be6-bebf-b56903d40511",
            "username": "DGERGELY11",
            "profilePicture": null
        },
        {
            "id": "ae14b32e-9418-4be6-bebf-b56903d40512",
            "username": "DGERGELY12",
            "profilePicture": null
        }
    ]
]

export async function getNetwork(): Promise<UserSummary[][]> {
    try {
        const response = await fetch(baseurl + `users/follows`, {
            method: 'GET'
        });

        if (!response.ok) {
            // If the response is not successful, throw an error
            throw new Error(`Failed to retrieve followers: ${response.statusText}`);
        }

        // Return the response text
        return await response.json();
    } catch (error) {
        // Handle errors gracefully
        console.error(`Error retrieving followers: ${error}`);
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve(networkMockData2);
            }, 1000)
        });
    }
}

export async function getFollows(userId: string): Promise<UserSummary[]> {
    try {
        const response = await fetch(baseurl + `users/${userId}/follows`, {
            method: 'GET'
        });

        if (!response.ok) {
            // If the response is not successful, throw an error
            throw new Error(`Failed to retrieve follows: ${response.statusText}`);
        }

        // Return the response text
        return await response.json();
    } catch (error) {
        // Handle errors gracefully
        console.error(`Error retrieving follows: ${error}`);
        return []
    }
}

export async function getFollowers(userId: string): Promise<UserSummary[]> {
    try {
        const response = await fetch(baseurl + `users/${userId}/followers`, {
            method: 'GET'
        });

        if (!response.ok) {
            // If the response is not successful, throw an error
            throw new Error(`Failed to retrieve followers: ${response.statusText}`);
        }

        // Return the response text
        return await response.json();
    } catch (error) {
        // Handle errors gracefully
        console.error(`Error retrieving followers: ${error}`);
        return []

    }
}

export function followUser(userId: string, followId: string) {
    return fetch(baseurl + `users/${userId}/follows/${followId}`, {
        method: 'POST'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
        })
        .catch(error => {
            console.log(error);
        });
}

export function unfollowUser(userId: string, followId: string) {
    return fetch(baseurl + `users/${userId}/follows/${followId}`, {
        method: 'DELETE',
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
        })
        .catch(error => {
            console.log(error);
        });
}
