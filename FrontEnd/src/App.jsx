import { Route , Routes } from "react-router-dom";
import Login from "./pages/Login";
import Home from "./pages/Home";
import SignUp from "./pages/Regist";
import Upload from "./Components/Upload/upload";
import Main from "./Components/Main/Main";
function App(){
      return (
        <>
            <Routes>
                <Route path="/" element={<Home/>}>
                    <Route index element={<Main/>}/>
                     <Route path="/Upload" element={<Upload/>}/>
                </Route>
                <Route path="/Login" element={<Login/>}/>
                <Route path="/SignUp" element={<SignUp/>}/>
            </Routes>
        </>
      )
}
export default App;