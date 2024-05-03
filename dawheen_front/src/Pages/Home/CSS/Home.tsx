import styled from "styled-components";
export const HomeWrapper = styled.div`
  width: 100%;
  height: 100%;
`;

export const HomeContents = styled.div`
  box-sizing: border-box;
  width: 100%;
  height: 100vh;
`;

export const NavigateMenu = styled.div`
  width: 100%;
  height: 78px;
  background: #fff;
  display: flex;
  box-sizing: border-box;
  align-items: center;
  justify-content: space-around;
  color: #fff;
  ul {
    display: flex;
    align-items: center;
    height: 78px;
  }
  li {
    padding: 0 20px;
    color: #333;
    cursor: pointer;
    &.memu {
      padding: 0 40px;
    }
    & .logo {
      width: 120px;
      filter: invert(21%) sepia(6%) saturate(5309%) hue-rotate(179deg)
        brightness(96%) contrast(92%);
    }

   
  }
  & .login{
        color: #333;
    }
`;

export const HomeBodyContents = styled.div<{backgroundImage : any}>`
    background-image: url(${props => props.backgroundImage});
    width: 100vw;
    height: calc(100vh - 78px);
    background-repeat: no-repeat;
    display: flex;
    flex-direction: column;
    align-items: center;
    background-size: cover;
    background-position: center center; 

    & .title{
      font-size: 48px;
      text-align: center;
      line-height: 70px;
      margin-top: 60px;
      font-family: 'GangwonEdu_OTFBoldA';
    }

    input {
      width: 25%;
      margin-top: 40px;
      height: 40px;
    }
  
`;