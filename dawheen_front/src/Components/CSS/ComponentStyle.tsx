import styled from "styled-components";

export const BtnGroup = styled.div `
    display: flex;
    margin-top: 40px;
    button {
        width: 280px;
        height: 60px;
        border: none;
        border-radius: 10px;
        box-shadow: 0 3px 5px rgba(0,0,0,0.2);
        background: #fff;
        cursor: pointer;
         font-family: 'GangwonEdu_OTFBoldA';
        font-size: 24px;
        line-height: 65px;
        color:#333;
    }

`

export const ModalWarp = styled.div `
  width: 100%;
  height: 100%;
  position: fixed;
  top: 0;
  left: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  background: rgba(0, 0, 0, 0.2);
`

export const ModalContent = styled.div `
  position: absolute;
  background-color: #ffffff;
  width: 50%;
  height: 750px;
  border-radius: 10px;

`