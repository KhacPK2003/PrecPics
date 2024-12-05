import React, { useState, useEffect } from 'react';
import Avatar from '../Avatar/Avatar';
import Button from '../Button/Button';
import { useNavigate , useParams} from 'react-router-dom';
import { auth } from '../../firebaseconfig';
import { onAuthStateChanged } from 'firebase/auth';

const PersonalPage = () => {
    const { id } = useParams();
    const [user, setUser] = useState({});
    const [isFollowing, setIsFollowing] = useState(false);  // Trạng thái theo dõi
    const userId = JSON.parse(localStorage.getItem('userID'));
    const navigate = useNavigate();
    const [token, setToken] = useState(null);

    useEffect(() => {
        const unsubscribe = onAuthStateChanged(auth, (currentUser) => {
            if (currentUser) {
                currentUser
                    .getIdToken()
                    .then((idToken) => setToken(idToken))
                    .catch((error) => console.error('Error getting ID token:', error));
            } else {
                setToken(null);
            }
        });
        return () => unsubscribe();
    }, []);

    useEffect(() => {
        fetch(`http://localhost:8080/public/api/users/${id}`)
            .then((response) => response.json())
            .then(({ payload }) => {
                    setUser(payload);
                // Kiểm tra xem người dùng đã theo dõi chưa
            });
    }, [id]);

    useEffect(() =>{
        if(userId === null){
            return;
        }
        fetch(`http://localhost:8080/public/api/users/${userId}`)
            .then((response) => response.json())
            .then(({ payload }) => {
                // Kiểm tra xem người dùng đã theo dõi chưa
                setIsFollowing(payload.followees.some(followee => followee.followeeId === id)); // Cập nhật trạng thái theo dõi
            });
    },[userId])

    const handleFollow = async () => {
        if(userId === null){
            navigate('/Login');
            return;
        }
        let url = `http://localhost:8080/public/api/users/${id}/follow`;
        if(isFollowing){
            url = `http://localhost:8080/public/api/users/${id}/unfollow/${userId}`;
        }
        const method = isFollowing ? 'DELETE' : 'POST';  // Nếu đang theo dõi thì sẽ gọi API unfollow

        try {
            const response = await fetch(url, {
                method,
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });
            if (response.ok) {
                setIsFollowing(!isFollowing);  // Cập nhật trạng thái sau khi follow/unfollow
            } else {
                console.error('Error in follow/unfollow API');
            }
        } catch (error) {
            console.error('API call failed:', error);
        } finally{

        }
    };

    return (
        <div>
            <div className="flex flex-col justify-center items-center">
                <div className="flex flex-col items-center gap-x-6 mt-[150px]">
                    <Avatar url={user.avatarUrl} />
                </div>
                <div>
                    <h1 className="text-5xl mt-5">{user.userName}</h1>
                </div>
                <div className="mt-[100px] flex justify-center">
                    {user.id !== userId ? (
                        <Button
                            props={isFollowing ? 'Đã theo dõi' : 'Theo dõi'}
                            bgcolor={isFollowing ? 'bg-gray-500' : 'bg-[#379d7d]'}
                            size="text-4xl"
                            textcolor="text-white"
                            onClick={handleFollow}
                        />
                    ) : (
                        <Button
                            props="Sửa hồ sơ"
                            bgcolor="bg-[#379d7d]"
                            size="text-4xl"
                            textcolor="text-white"
                            onClick={() => navigate(`/aboutprofile/${id}`)}
                        />
                    )}
                </div>
                <div className="h-full mt-10 flex justify-center ml-[110px]">
                    <a className="text-black text-2xl mr-6 flex flex-col items-center no-underline">
                        <p className="text-gray-600">Tổng số lượt thích</p>
                        <h4 className="mt-3">0</h4>
                    </a>

                    <a className="h-full text-black text-2xl mr-6 flex flex-col items-center no-underline border-l border-black pl-6">
                        <p className="text-gray-600">Tổng số bài viết</p>
                        <h4 className="mt-3">677,5 nghìn</h4>
                    </a>

                    <a className="h-full text-black text-2xl mb-6 flex flex-col items-center no-underline border-l border-black pl-6">
                        <p className="text-gray-600">Tổng số người theo dõi</p>
                        <h4 className="mt-3">18,8 nghìn</h4>
                    </a>
                </div>
            </div>
        </div>
    );
};

export default PersonalPage;
