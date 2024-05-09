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
        position: relative;
        svg {
            cursor: pointer;
            position: absolute;
            top: 40px;
            right: 40px;
            font-size: 24px;
            color:#999;
        }
        img {
            width: 120px;
            margin-bottom: 30px;
        }

        input {
            width: 100%;
            height: 50px;
            border-radius: 10px;
            border:1px solid #ccc;
            font-size: 18px;
            outline: none;
            margin-bottom: 20px;
            box-sizing: border-box;
            padding:0 20px;
        }
        button {
            height: 55px;
            width: 100%;
            border-radius: 10px;
            border: none;
            outline: none;
            font-size: 18px;
            background: linear-gradient(to right, #a4bbe5, #e1e8fb);
            font-weight: bold;
            color:#fff;
            cursor: pointer;;
        }

        & .login_option{
            margin-top: 20px;
            margin-bottom: 20px;
            span {
                display: inline-block;
                cursor: pointer;
            }
            span ~ span {
                margin-left: 20px;
                border-left: 1px solid #999;
                padding-left: 20px;
            }
        }

        & .sns_login {
            width: 100%;
        
            & .kakao{
                margin-top: 20px;
                text-align: center;
                border-radius: 10px;
                height: 55px;
                position: relative;
                padding: 0 20px;
                line-height: 55px;
                background: #FAE300;
                font-weight:bold;
                font-size: 18px;
                color:#3E2723;
                img {
                    width: 35px;
                    position: absolute;
                    left: 20px;
                    top: 10px;
                }
            }

            & .google{
                margin-top: 20px;
                text-align: center;
                border-radius: 10px;
                height: 55px;
                position: relative;
                padding: 0 20px;
                line-height: 55px;
                font-weight:bold;
                color:#fff;
                font-size: 18px;
                background: #03CF5D;
                img {
                    width: 35px;
                    position: absolute;
                    left: 20px;
                    top: 10px;
                }
            }
        }
    }
`