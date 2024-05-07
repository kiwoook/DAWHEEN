import React from 'react';
import { ModalContent, ModalWarp } from '../CSS/ComponentStyle';
import { LoginContents } from '../CSS/LoginStyle';
import Logo from "../../Images/logo2.png";
import Logo2 from "../../Images/logo3.png";
const Login = () => {
    return (
        <ModalWarp>
            <ModalContent>
                <LoginContents>
                    <div className='lf_login'>
                        <img src = {Logo} alt = "로고" className='logo'/>
                        <div className='stoke' />
                        <img src = {Logo2} alt = "로고2" className='logo2' />
                    </div>
                    <div className='rt_login'>
                        <img src = {Logo2} alt = "로고2" className='logo' />
                        <input type = "text" placeholder='아이디를 입력해 주세요.' />
                        <input type = "text" placeholder='비밀번호를 입력해 주세요.'/>
                        <button>로그인</button>
                    </div>
                </LoginContents>
            </ModalContent>
        </ModalWarp>
    );
};

export default Login;