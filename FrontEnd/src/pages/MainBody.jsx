import FilterMain from "../Components/FilterMain/FilterMain";
import Footer from "../Components/Footer/Footer";
import Navbar from "../Components/Navbar/Navbar";
import SearchBar from "../Components/SearchBar/SearchBar";
import Main from "../Components/Main/Main";
import { useOutletContext } from 'react-router-dom';
function MainBody(){
    const {showVideo} = useOutletContext();
    return (
        <>
             <FilterMain/>
             <SearchBar/>
            <Main showvideo = {showVideo}/>
        </>
    );
}
export default MainBody;