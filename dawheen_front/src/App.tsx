import React, { useState } from 'react';
import './App.css';
import Home from './Pages/Home/Home';
import Navigate from './Components/Navigate';
import Login from './Components/Login/Login';

function App() {
  const [loginModal, setLoginModal] = useState<boolean>(false);
  console.log(loginModal)
  return (
    <div className="App">
        <Navigate setLoginModal = {setLoginModal} />
        <Home />
        {loginModal ? <Login /> : null}
    </div>
  );
}

export default App;
