import { Route , Routes } from "react-router-dom";
import Login from "./pages/Login";
import Home from "./pages/Home";
import SignUp from "./pages/Regist";
import Upload from "./Components/Upload/upload";
import Main from "./Components/Main/Main";
import About from "./pages/About";
import AboutData from "./pages/AboutData";
import AboutFollower from "./pages/AboutFollower";
import AboutCollection from "./pages/AboutCollection";
import Collection from "./pages/Collection";
import GallerySingle from "./Components/GallerySingle/GallerySingle";
import AboutWatching from "./pages/AboutWatching";
import Sidebar from "./Components/Administrator/Sidebar";
import Footer from "./Components/Administrator/Footer";
import Topbar from "./Components/Administrator/Topbar";
import EditProfile from "./pages/EditProfile";
import FormAbout from "./Components/Form/FormAbout";
import FormNotification from "./Components/Form/FormNotification";
import EditNotification from "./pages/EditNotification";
function App(){
      return (
        <>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/Upload" element={<Upload/>}/>
                <Route path="/" element={<Home/>}>
                    <Route index element={<Main/>}/>
                     <Route path="/Upload" element={<Upload/>}/>
                </Route>
                <Route path="/Login" element={<Login/>}/>
                <Route path="/SignUp" element={<SignUp/>}/>
                <Route path="/" element={<About/>}/>
                <Route path="/aboutdata" element={<AboutData/>}/>
                <Route path="/aboutfollower" element={<AboutFollower/>}/>
                <Route path="/aboutcollection" element={<AboutCollection/>}/>
                <Route path="/aboutwatching" element={<AboutWatching/>}/>
                <Route path="/aboutprofile" element={<EditProfile/>}/>
                <Route path="/aboutnotification" element={<EditNotification/>}/>
            </Routes>
           

        </>
      )
}
export default App;