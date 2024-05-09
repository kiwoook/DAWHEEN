import React, { useState } from 'react';
import './App.css';
import Home from './Pages/Home/Home';
import Navigate from './Components/Navigate';
import Login from './Components/Login/Login';
import { config } from '@fortawesome/fontawesome-svg-core'
import '@fortawesome/fontawesome-svg-core/styles.css'
import { Route, Routes } from 'react-router-dom';
import Volunteer from './Pages/Volunteer/Volunteer';
config.autoAddCss = false

function App() {
  const [loginModal, setLoginModal] = useState<boolean>(false);
  return (
    <div className="App">
        <Navigate setLoginModal = {setLoginModal} />
        {loginModal ? <Login setLoginModal = {setLoginModal}/> : null}
        <Routes>
          <Route path = "/" element={<Home />}/>
          <Route path = "/volunteer" element={<Volunteer />}/>
        </Routes>
    </div>
  );
}

export default App;
