import FilterMain from "../Components/FilterMain/FilterMain";
import Footer from "../Components/Footer/Footer";
import Navbar from "../Components/Navbar/Navbar";
import SearchBar from "../Components/SearchBar/SearchBar";
import Main from "../Components/Main/Main";
import React, { useState } from 'react';
function Home(){
    const [showVideo, setShowVideo] = useState(false);
    const handleVideoClick = (name) => {
        if(name == 'Video' && !showVideo) setShowVideo(true); // Khi click vào button video, sẽ set showVideo = true
        if(name == 'Ảnh' && showVideo) setShowVideo(false); // Khi click vào button video, sẽ set showVideo = true
    };
    return (
        <>
            <Navbar isLoggedIn={false} onClickVideo = {handleVideoClick} />
            <FilterMain/>
            <SearchBar/>
            <Main showvideo = {showVideo}/>
            <Footer/>
        </>
    );
}
export default Home;