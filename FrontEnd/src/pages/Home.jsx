import FilterMain from "../Components/FilterMain/FilterMain";
import Footer from "../Components/Footer/Footer";
import Navbar from "../Components/Navbar/Navbar";
import SearchBar from "../Components/SearchBar/SearchBar";
import Main from "../Components/Main/Main";
import React, { useState , useEffect } from 'react';
import {  auth, onAuthStateChanged, signOut } from '../firebaseconfig';
import { Outlet } from 'react-router-dom';
function Home(){
    const [user, setUser] = useState(null);
    const [Login , setLogin] = useState(false);
    useEffect(() => {
        // Kiểm tra trạng thái đăng nhập khi ứng dụng load
        const unsubscribe = onAuthStateChanged(auth, (currentUser) => {
          if (currentUser) {
            setUser(currentUser);  // Người dùng đã đăng nhập
            setLogin(true); // Da login
          } else {
            setUser(null);  // Người dùng chưa đăng nhập
            setLogin(false);
          }
        });
        // Cleanup khi component unmount
        return () => unsubscribe();
    }, []);
    
    const handleLogout = async () => {
        try {
          localStorage.clear();
          await signOut(auth);
          setUser(null);  // Đăng xuất thành công, cập nhật lại state
        } catch (error) {
          console.error("Error during logout", error);
        }
      };

    const [showVideo, setShowVideo] = useState(false);
    const handleVideoClick = (name) => {
        if(name == 'Video' && !showVideo) setShowVideo(true); // Khi click vào button video, sẽ set showVideo = true
        if(name == 'Ảnh' && showVideo) setShowVideo(false);
    };
    return (
        <>
            <Navbar UserInfo = {user} onClickVideo = {handleVideoClick} handleLogOut = {handleLogout} isLogin = {Login}/>
              <Outlet context={{showVideo}}/>
            <Footer/>
        </>
    );
}
export default Home;