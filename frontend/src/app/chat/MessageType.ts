export interface MessageType {
    content: string,
    sender: string,
    receiver: string
}

export interface Message {
    id: string;
    content: string;
    sender: string;
    receiver: string;
    timestamp: string;
}

export interface Chat {
    participant1: string;
    participant2: string;
    messages: Message[];
    id: string;
}