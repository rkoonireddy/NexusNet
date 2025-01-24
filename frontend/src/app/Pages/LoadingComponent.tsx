import React from 'react';
import styled, {keyframes} from 'styled-components';

const rotate = keyframes`
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
`;

const RotatingCircle = styled.div`
  display: inline-block;
  width: 50px;
  height: 50px;
  border: 3px solid #333;
  border-radius: 50%;
  border-top-color: transparent;
  animation: ${rotate} 2s linear infinite;
`;

const StyledLoadingContainer = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
`;

const StyledLoadingText = styled.p`
  font-size: 1.5em;
  color: #333;
`;

function Loading() {
    return (
        <StyledLoadingContainer>
            <RotatingCircle/>
            <StyledLoadingText>Loading...</StyledLoadingText>
        </StyledLoadingContainer>
    )
}

export default Loading;