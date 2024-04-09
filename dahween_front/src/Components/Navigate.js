import React from 'react';
import { MenuList, NavigateWrap } from '../CSS/Components/Navigate';
import { faBuilding, faCamera, faChartColumn, faHandshake, faHandshakeAngle, faHome } from "@fortawesome/free-solid-svg-icons"
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome"
const Navigate = () => {
    return (
        <NavigateWrap>
            <h3>로고</h3>
            <MenuList>
                <li><span><FontAwesomeIcon icon={faChartColumn} className='icon'/>메인 페이지</span></li>
                <li><span><FontAwesomeIcon icon = {faHandshakeAngle} className='icon'/>봉사 찾기</span></li>
                <li><span><FontAwesomeIcon icon = {faBuilding} className='icon'/>기관 찾기</span></li>

            </MenuList>
        </NavigateWrap>
    );
};

export default Navigate;