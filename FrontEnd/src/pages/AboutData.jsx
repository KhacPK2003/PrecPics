import React , {useEffect , useState}from 'react';
import LineChart from '../Components/Chart/LineChart'
import Ranking from '../Components/Table/Ranking';
import Data from '../Components/Table/Data';
import { useNavigate , useParams } from 'react-router-dom';
const AboutData = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const [user,setUser] = useState([]);
    useEffect(() => {
        fetch(`http://localhost:8080/public/api/users/${id}`)
            .then((response) => response.json())
            .then(({ payload }) => {
                    setUser(payload);
                // Kiểm tra xem người dùng đã theo dõi chưa
            });
    }, [id]);
    return (
        <div className='bg-white min-h-screen'>
        <div className='p-[15px] rounded-[30px] border border-[#dfdfe0] mt-12 mr-6 ml-6'>
            <div className='text-[25px] mt-3'>
                <h4>Tổng quan</h4>
            </div>
            <div className='mt-5'>
                <Data following = {user.totalFollowing} follower = {user.totalFollowers} contents = {user.totalContents} totalLikes ={user.totalLikes}></Data>
            </div>
            <div className='mt-5'>
                <LineChart></LineChart>
            </div>
        </div>
        <div className="flex mt-10 space-x-4 px-4">
            <div className='mt-10 w-1/2'>
           
            </div>
        </div>
        <div className='h-[500px]'/>
        
        </div>
    );
};

export default AboutData;