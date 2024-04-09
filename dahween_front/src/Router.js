import React from 'react';
import { Route, Routes } from 'react-router-dom';
import Login from './Pages/Login/Login';

const Router = () => {

    return (
        <Routes>
            <Route path = "/" element = {<Login />}/>
        </Routes>
    );
};

export default Router;