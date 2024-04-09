import styled from "styled-components";

export const NavigateWrap = styled.div `
    width: 15%;
    min-width: 300px;
    height: 100vh;
    background: #6884ff;
    box-shadow: 0 3px 6px rgba(0, 0, 0, 0.16), 0 3px 6px rgba(0, 0, 0, 0.23);
    h3 {
        text-align: center;
        padding: 30px 0;
        font-size: 24px;
        font-weight: bold;
        color:#fff;
    }
`

export const MenuList = styled.ul `
    margin-left: 20px;
    color:#fff;

    li {
        padding: 16px 0 ;
        display: flex;
        align-items: center;
        font-size: 20px;

       &:hover {
            color:#6884ff;
            transition: all 0.2s;
            cursor: pointer;
            background: #fff;
            border-radius: 20px 0 0 20px;
       } 

       & .icon{
        position: relative;
        top: -2px;
        margin-right:10px;
       }
       span {
        display: block;
        padding: 0 20px;
       }
    }

`