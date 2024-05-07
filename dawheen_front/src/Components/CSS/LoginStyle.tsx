import styled from "styled-components";

export const LoginContents = styled.div `
    width: 100%;
    height: 100%;
    display:flex ;

    & .lf_login{
        width: 40%;
        height: 100%;
        text-align: center;
        background-image: linear-gradient(to right, #a4bbe5, #e1e8fb);
        border-radius: 10px 0 0 10px;
        padding: 80px 0;
        box-sizing: border-box;
        position: relative;
        overflow: hidden;

        & .stoke {
            width: 80%;
            height: 4px;
            display: inline-block;
            background: #fff;
        }
        & .logo {
           width: 80%;
            margin-bottom: 15px;
        }

        & .logo2 {
            position: absolute;
            width: 350px;
            left: -50px;
            bottom: -20px;
        }
    }

    & .rt_login {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        width: 60%;
        box-sizing:border-box;
        padding: 0 140px;
        img {
            width: 120px;
            margin-bottom: 30px;
        }

        input {
            width: 100%;
            height: 50px;
            border-radius: 10px;
            border:1px solid #ccc;
            padding : 0 20px;
            font-size: 18px;
            outline: none;
            margin-bottom: 20px;
        }
        button {
            height: 50px;
            width: 100%;

        }
    }
`