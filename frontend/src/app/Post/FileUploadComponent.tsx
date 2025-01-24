import React, { ChangeEvent, FC } from 'react';
import styled from "styled-components";

const StyledFileUpload = styled.div`
  display: flex;
  justify-content: center;
  color: white;
  
  div {
    margin-right: 10px;
  }
`;

interface FileUploadProps {
    onFileSelect: (files: FileList | null) => void;
}

export const FileUpload: FC<FileUploadProps> = ({ onFileSelect }) => {
    const handleFileInput = (e: ChangeEvent<HTMLInputElement>) => {
        onFileSelect(e.target.files);
    };

    return (
        <StyledFileUpload>
            <div>Upload (a) file(s): </div> <input type="file" name="file" onChange={handleFileInput} multiple />
        </StyledFileUpload>
    );
};