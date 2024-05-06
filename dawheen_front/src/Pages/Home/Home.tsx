import React, { useState } from "react";
import { HomeBodyContents, HomeContents, HomeWrapper, NavigateMenu } from "./CSS/Home";
import Background1 from "./Images/Background.jpg"
import { BtnGroup } from "../../Components/CSS/ComponentStyle";
import Navigate from "../../Components/Navigate";

const Home: React.FC = () => {

    return(
        <HomeWrapper>
            <HomeContents>
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