import React ,{useState, useEffect} from 'react';
import CardUser from '../Components/Card/CardUser';
import { useNavigate , useParams } from 'react-router-dom';
const AboutWatching = () => {
    const navigate = useNavigate();
    const [following, setFollowing] = useState([]);
    const { id } = useParams();
    useEffect(() => {
        fetch(`http://localhost:8080/public/api/users/${id}`)
            .then((response) => response.json())
            .then(({ payload }) => {
                setFollowing(payload.followees);
                // Kiểm tra xem người dùng đã theo dõi chưa
            });
    }, [id]);
    return (
        <div className='bg-white min-h-screen'>
            <div className='grid grid-cols-4 gap-5 p-5' >    
                {
                    following.map((user) => (
                        <CardUser key={user.id} id = {user.followeeId}/>
                    ))
                }
            </div>
        </div>
    );
};

export default AboutWatching;