import React, { useState } from 'react';
import { ModalContent, ModalWarp } from '../CSS/ComponentStyle';
import { LoginContents } from '../CSS/LoginStyle';
import Logo from "../../Images/logo2.png";
import Logo2 from "../../Images/logo3.png";
import { faClose } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import kakao from "../../Images/kakao_login.png";
import Naver from "../../Images/naver_login.png";

interface loginProps {
    setLoginModal: React.Dispatch<React.SetStateAction<boolean>>;
}

const Login = ({setLoginModal} : loginProps) => {
    const [id, setId] = useState<string>("");

 


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
                        <FontAwesomeIcon icon = {faClose} onClick={()=>{setLoginModal(false)}}/>
                        <img src = {Logo2} alt = "로고2" className='logo' />
                        <input type = "text" placeholder='아이디를 입력해 주세요.'/>
                        <input type = "text" placeholder='비밀번호를 입력해 주세요.'/>
                        <button>로그인</button>
                       
                        <div className='sns_login'>
                            <div className='kakao'>
                                <img src = {kakao} alt = "카카오" />
                                <span>카카오 로그인</span>
                            </div>
                            <div className='google'>
                                <img src = {Naver} alt = "네이버" />
                                <span>네이버 로그인</span>
                            </div>
                        </div>
                        <div className='login_option'>
                            <span>비밀번호 찾기</span>
                            <span>아이디 찾기</span>
                            <span>회원가입</span>
                        </div>
                    </div>
                </LoginContents>
            </ModalContent>
        </ModalWarp>
    );
};

export default Login;