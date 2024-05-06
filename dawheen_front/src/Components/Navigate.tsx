import React from 'react';
import { NavigateMenu } from '../Pages/Home/CSS/Home';
import Logo from "../Images/fontLogo.png";

const Navigate: React.FC = () => {
  return (
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
  );
}

export default Navigate;