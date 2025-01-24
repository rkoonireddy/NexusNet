import Header from "./Header";
import styled, {css} from "styled-components";
import {useEffect, useRef, useState} from "react";
import {Chat, MessageType} from "../chat/MessageType";
import {getChatOfParticipants, getChatsOfUser, sendMessage} from "../chat/ChatService";
import {useAppSelector} from "../hooks";
import {selectActiveUser} from "../User/LoggedInUserSlice";
import {dateFormatter} from "../Util/util";
import {ReactComponent as MessageSVG} from "../../static/images/Message.svg";
import {useParams} from "react-router-dom";


const StyledMainContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: row;
  width: 100vw;
  height: calc(100vh - 60px);
`;

const StyledChatsContainer = styled.div`
  display: flex;
  align-items: center;
  flex-direction: column;
  width: 400px;
  height: 100%;
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;

  &::-webkit-scrollbar {
    display: none;
  }
`;

const StyledActiveChatContainer = styled.div`
  display: flex;
  justify-content: flex-start;
  align-items: center;
  flex-direction: column;
  width: 100%;
  height: 100%;
  background: rgb(255, 255, 255, 0.5);

`;

const StyledMessagesContainer = styled.div`
  display: flex;
  justify-content: flex-start;
  align-items: center;
  flex-direction: column;
  padding: 20px;
  height: calc(100% - 250px);
  width: calc(100% - 40px);

  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;

  &::-webkit-scrollbar {
    display: none;
  }
`;

const StyledOverlay = styled.div`
  content: "";
  position: sticky;
  top: -20px;
  min-height: 100px;
  height: 100px;
  width: 100%;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.75), transparent);
  pointer-events: none;
  z-index: 1;
`;

const StyledChatHeader = styled.div<{ $active: boolean }>`
  display: flex;
  justify-content: center;
  width: 100%;
  color: #ffffff;
  height: 60px;
  background-color: ${props => props.$active ? "rgb(255, 255, 255, 0.5)" : "rgb(255, 255, 255, 0.3)"};
  font-size: 2rem;
  text-align: center;
  align-items: center;

  &:hover {
    background: rgb(255, 255, 255, 0.5);
    border-bottom: 1px solid white;
    cursor: pointer;
  }
`;

const StyledHeading = styled.h2`
  font-size: 24px;
  margin-bottom: 20px;
  color: #fff;
`;

const StyledLargeHeading = styled.h1`
  font-size: 36px;
  margin-bottom: 20px;
  color: #fff;

`;

const StyledChatMessage = styled.div<{ $active: boolean }>`
  position: relative;
  display: flex;
  min-width: 150px;
  width: fit-content;
  max-width: 80%;
  margin: ${props => props.$active ? "5px 0 5px auto" : "5px auto 5px 0"};
  background-color: ${props => props.$active ? "rgba(0,171,255,1)" : "rgba(150,150,150,1)"};
  padding: 10px 10px 25px 10px;
  border-radius: 15px;
  color: #ffffff;
  font-size: 1.5rem;
`;

const StyledMessageDate = styled.div`
  position: absolute;
  bottom: 5px;
  right: 15px;
  font-style: italic;
  font-size: 1rem;
`;

const StyledMessageInputContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  width: calc(100% - 40px);
  height: 100px;
  padding: 0 20px;
`;

const StyledMessageInput = styled.input`
  font-size: 1.5rem;
  background-color: rgba(0, 0, 0, 0.3);
  border-radius: 15px;
  padding: 0 10px;
  color: #ffffff;
  margin: auto 20px;
  height: 40px;
  width: calc(100% - 100px);
  min-width: 20px;

  &::placeholder {
    color: #ffffff;
    opacity: 1; /* Firefox */
  }

  &::-ms-input-placeholder { /* Edge 12 -18 */
    color: #ffffff;
  }
`;

const StyledSendMessageSVGContainer = styled.div<{ disabled: boolean }>`
  display: flex;
  color: white;
  background-color: ${props => props.disabled ? "#6e6e6e" : "#00ABFF"};;
  border-radius: 50%;

  &:hover {
    ${props => !props.disabled && css`
      cursor: pointer;
      scale: 1.15;
    `}
  }
`;


export function ChatPage() {
    const {chatId} = useParams();
    const [cId, setCId] = useState<string | undefined>(chatId);
    const loggedInUser = useAppSelector(selectActiveUser);
    const [chats, setChats] = useState<Chat[]>([]);
    const [activeChat, setActiveChat] = useState<Chat | null>(null);
    const [isFirstLoad, setIsFirstLoad] = useState(true);
    const messagesEndRef = useRef<null | HTMLDivElement>(null);
    const inputRef = useRef<HTMLInputElement | null>(null);
    const [message, setMessage] = useState("");

    // Fetch chats and set active chat if chatId is provided
    const fetchChats = async () => {
        const fetchedChats = await getChatsOfUser(loggedInUser.username);
        setChats(fetchedChats);

        if (chatId) {
            const chat = fetchedChats.find(chat => chat.id === chatId);
            if (chat) {
                setActiveChat(chat);
            }
        }
    };

// Fetch chat of active participants
    const fetchChat = async () => {
        if (activeChat) {
            const participant1 = activeChat.participant1 === loggedInUser.username ? activeChat.participant1 : activeChat.participant2;
            const participant2 = activeChat.participant1 === loggedInUser.username ? activeChat.participant2 : activeChat.participant1;
            const chat = await getChatOfParticipants(participant1, participant2);
            setActiveChat(chat);
        }
    };

// Fetch chats every 5 seconds
    useEffect(() => {
        setCId(chatId);
        fetchChats();

        const interval = setInterval(fetchChats, 5000);
        return () => clearInterval(interval);
    }, []);

// Fetch chat every 2 seconds if activeChat is present
    useEffect(() => {
        if (activeChat) {
            const interval = setInterval(fetchChat, 2000);
            return () => clearInterval(interval);
        }
    }, [activeChat, chatId]);

// Scroll and focus when activeChat changes or on first load
    useEffect(() => {
        if (messagesEndRef.current) {
            messagesEndRef.current.scrollIntoView({behavior: "smooth"});
        }
        if (inputRef.current) {
            inputRef.current.focus();
        }
    }, [activeChat, isFirstLoad]);

// Send new message
    const sendNewMessage = async () => {
        if (message !== "" && activeChat) {
            const newMessage: MessageType = {
                content: message,
                sender: loggedInUser.username,
                receiver: activeChat.participant1 === loggedInUser.username ? activeChat.participant2 : activeChat.participant1
            };
            await sendMessage(newMessage);
            setMessage("");
            fetchChat();
        }
    };

// Handle key down event
    const onKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === "Enter" && message !== "") {
            e.preventDefault();
            sendNewMessage();
        }
    };

    return (
        <>
            <Header/>
            <StyledMainContainer>
                <StyledChatsContainer>
                    <StyledHeading>Chats</StyledHeading>
                    {chats.length > 0 && chats.map(chat => (
                        <StyledChatHeader onClick={() => {
                            setActiveChat(chat);
                            setCId(chat.id);
                            setIsFirstLoad(true);
                        }}
                                          $active={chat.id === activeChat?.id}
                                          key={chat.id}>{chat.participant2 === loggedInUser.username ? chat.participant1 : chat.participant2}</StyledChatHeader>
                    ))}
                </StyledChatsContainer>
                <StyledActiveChatContainer>
                    {activeChat !== null ?
                        <>
                            <StyledLargeHeading>{activeChat.participant2 === loggedInUser.username ? activeChat.participant1 : activeChat.participant2}</StyledLargeHeading>
                            <StyledMessagesContainer>
                                <StyledOverlay/>
                                {activeChat.messages.map(message => (
                                    <StyledChatMessage $active={message.sender === loggedInUser.username}
                                                       key={message.id}>{message.content}
                                        <StyledMessageDate>{dateFormatter(message.timestamp)}</StyledMessageDate>
                                    </StyledChatMessage>
                                ))}
                                <div ref={messagesEndRef}/>
                            </StyledMessagesContainer>
                            <StyledMessageInputContainer>
                                <StyledMessageInput id="message"
                                                    ref={inputRef}
                                                    value={message}
                                                    placeholder=""
                                                    onChange={(event) => setMessage(event.target.value)}
                                                    onKeyDown={onKeyDown}/>
                                <StyledSendMessageSVGContainer title="Send message" disabled={message === ""}
                                                               onClick={sendNewMessage}>
                                    <MessageSVG style={{width: "50px", height: "50px", margin: "5px -3px -5px 3px"}}/>
                                </StyledSendMessageSVGContainer>
                            </StyledMessageInputContainer>
                        </> : <div>Select a chat to start messaging</div>}
                </StyledActiveChatContainer>
            </StyledMainContainer>
        </>
    )
}