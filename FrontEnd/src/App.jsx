import { Route , Routes } from "react-router-dom";
import 'bootstrap/dist/css/bootstrap.min.css';
import Login from "./pages/Login";
import Home from "./pages/Home";
import SignUp from "./pages/Regist";
import Upload from "./Components/Upload/upload";
import Main from "./Components/Main/Main";
import About from "./pages/About";
import Collection from "./pages/Collection";
import AboutWatching from "./pages/AboutWatching";
import Sidebar from "./Components/Administrator/Sidebar";
import Footer from "./Components/Administrator/Footer";
import Topbar from "./Components/Administrator/Topbar";
import EditProfile from "./pages/EditProfile";
import FormAbout from "./Components/Form/FormAbout";
import FormNotification from "./Components/Form/FormNotification";
import EditNotification from "./pages/EditNotification";
import AboutFollower from "./pages/AboutFollower"
import AboutCollection from "./pages/AboutCollection"
import AboutData from "./pages/AboutData"
import Dashboard from "./Components/ContentAdmin/Dashboard";
import Index from "./Admin/Index";
import UserManager from "./Admin/UserManager";
import CollectionManager from "./Admin/CollectionManager";
import InCollectionManager from "./Admin/InCollectionManager";
import GalleryManager from "./Admin/GalleryManager";
import GotTagManager from "./Admin/GotTagManager";
import MainBody from "./pages/MainBody";
import OutStanding from "./Components/About/OutStanding";
function App(){
      return (
        <>
                <Routes>
                    <Route path="/" element={<Home />}>
                    <Route index element={<MainBody />} />
                    <Route path="about/:id" element={<About />}>
                        <Route index element={<OutStanding />} />
                        <Route path="aboutdata" element={<AboutData />} />
                        <Route path="aboutfollower" element={<AboutFollower />} />
                        <Route path="aboutcollection" element={<AboutCollection />} />
                        <Route path="aboutwatching" element={<AboutWatching />} />
                    </Route>
                    <Route path="collection/:id" element={<Collection />} /> {/* Định nghĩa route cho Collection */}
                    </Route>
                    <Route path="/Login" element={<Login />} />
                    <Route path="/Upload" element={<Upload />} />
                    <Route path="/SignUp" element={<SignUp />} />
                    <Route path="/aboutnotification/:id" element={<EditNotification />} />
                    <Route path="/aboutprofile/:id" element={<EditProfile />} />
                    <Route path="/dashboard" element={<Index/>}/>
                    <Route path="/usermanager" element={<UserManager/>}/>
                    <Route path="/collectionmanager" element={<CollectionManager/>}/>
                    <Route path="/gottagmanager" element={<GotTagManager/>}/>
                    <Route path="/gallerymanager" element={<GalleryManager/>}/>
                    <Route path="/videos" element={<InCollectionManager/>}/>
                </Routes>
            {/* <Routes>
                <Route path="/" element={<About/>}/>
            </Routes> */}
            {/* <Routes>
                <Route path="/" element={<Index/>}/>
            </Routes> */}
        </>
      )
}
export default App;