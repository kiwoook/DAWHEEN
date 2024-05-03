import React from "react";
import { HomeBodyContents, HomeContents, HomeWrapper, NavigateMenu } from "./CSS/Home";
import Logo from "../../Images/fontLogo.png";
import Background1 from "./Images/Background.jpg"
import { BtnGroup } from "../../Components/CSS/ComponentStyle";

const Home: React.FC = () => {
    return(
        <HomeWrapper>
            <HomeContents>
                <NavigateMenu>
                   <ul>
                        <li>
                            <img src= {Logo} alt = "로고" className='logo'/>
                        </li>
                        <li className='menu'>봉사찾기</li>
                        <li className='menu'>커뮤니티</li>
                        <li className='menu'>고객센터</li>
                   </ul>
                    <div>
                        <span className='login'>로그인</span>
                    </div>
                </NavigateMenu>
                <HomeBodyContents backgroundImage={Background1}>
                    <h1 className="title">흰 눈처럼 포근하게<br /> 봉사로 세상을 밝혀요.</h1>
                    <BtnGroup>
                        <button>봉사하기</button>
                    </BtnGroup>
                </HomeBodyContents>
            </HomeContents>
        </HomeWrapper>
    )
}

export default Home;