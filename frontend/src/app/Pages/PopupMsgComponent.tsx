import styled from "styled-components";
import {StyledFilterButton} from "./MainPage";

const StyledModalBackground = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 100;
`;

const StyledModalContainer = styled.div`
  background-color: white;
  padding: 20px;
  border-radius: 10px;
  max-width: 500px;
  width: 80%;
  text-align: center;
`;

const StyledModalButtonContainer = styled.div`
  display: flex;
  justify-content: space-between;
`;


export function Modal({ text, action, setModal }: {
    text: string,
    action: () => void,
    setModal: (value: boolean) => void
}) {

    function performAction() {
        action();
    }

    return (
        <StyledModalBackground>
            <StyledModalContainer onClick={(e) => e.stopPropagation()}>
                {text}
                <StyledModalButtonContainer>
                    <StyledFilterButton selected={false} onClick={performAction}>Yes</StyledFilterButton>
                    <StyledFilterButton selected={false} onClick={() => setModal(false)}>No</StyledFilterButton>
                </StyledModalButtonContainer>
            </StyledModalContainer>
        </StyledModalBackground>
    );
}