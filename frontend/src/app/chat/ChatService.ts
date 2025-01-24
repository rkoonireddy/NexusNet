import {Chat, MessageType} from "./MessageType";

const baseurl = process.env.REACT_APP_CHAT_BASEURL;


export async function getChatsOfUser(userName: string): Promise<Chat[]> {
    return fetch(baseurl + "chats/" + userName)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json()
        })
        .then(data => {
            data.sort((a: Chat, b: Chat) => {
                return new Date(b.messages[b.messages.length - 1].timestamp).getTime() - new Date(a.messages[a.messages.length - 1].timestamp).getTime();
            })
            return data
        })
        .catch(error => {
            console.log(error);
        })
}

export async function getChatOfParticipants(participant1: string, participant2: string): Promise<Chat> {
    return fetch(baseurl + "chats/" + participant1 + "/" + participant2)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json()
        })
        .then(data => {
            return data
        })
        .catch(error => {
            console.log(error);
            throw new Error(`HTTP error! status: ${error}`);
        })
}

export async function sendMessage(message: MessageType): Promise<Chat> {
    return fetch(baseurl + "messages", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(message)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json()
        })
        .then(data => {
            return data
        })
        .catch(error => {
            console.log(error);
        })
}