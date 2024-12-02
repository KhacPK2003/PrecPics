import React , {useEffect , useState} from 'react';
import Button from '../Button/Button';
import { auth } from '../../firebaseconfig';
import { onAuthStateChanged } from 'firebase/auth';
import { useNavigate , useParams} from 'react-router-dom';
const CardUser = ({id, imageUrl2, }) => {
    const [token, setToken] = useState(null);
    const userId = JSON.parse(localStorage.getItem('userID'));
    const navigate = useNavigate();
    const [user, setUser] = useState([]);
    const [imageUrl , setImage] = useState(null);
    const [isFollowing, setIsFollowing] = useState(false);  // Trạng thái theo dõi
    useEffect(() =>{
      fetch(`http://localhost:8080/public/api/users/${userId}`)
          .then((response) => response.json())
          .then(({ payload }) => {
              // Kiểm tra xem người dùng đã theo dõi chưa
              setIsFollowing(payload.followees.some(followee => followee.followeeId === id)); // Cập nhật trạng thái theo dõi
          });
  },[userId])
    useEffect(() => {
      fetch(`http://localhost:8080/public/api/users/${id}`)
          .then((response) => response.json())
          .then(({ payload }) => {
                  setImage(payload.avatarUrl);
                  setUser(payload);
              // Kiểm tra xem người dùng đã theo dõi chưa
          });
    }, [id]);
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
    const handlePage = () =>{
        navigate(`/about/${id}`);
    }
    const handleFollow = async () => {
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
           <div className="max-w-xs rounded-lg overflow-hidden shadow-lg bg-white">
      <div className="relative">
      <div className='w-full h-48 object-cover'>
      {imageUrl2 ? (
                <img
                    className="w-full h-full object-cover"
                    src={imageUrl}
                    alt="Mountain Landscape"
                />
            ) : (
                <div className="w-full h-full bg-black"></div>
            )}
      </div>
        
        <div className="absolute -bottom-10 left-1/2 transform -translate-x-1/2 w-20 h-20 rounded-full border-4 border-white overflow-hidden">
          {imageUrl ? (
          <img
            className="w-full h-full object-cover"
            src={imageUrl} // URL của ảnh đại diện nếu có
            alt="Profile"
          />
        ) : (
          <div className="w-full h-full bg-green-700 flex items-center justify-center">
          <span className="text-white font-bold text-lg">P</span>
          </div>
        )}
      </div>
      </div>
      <div className="pt-10 pb-6 text-center bg-gray-400">
      <div className="flex flex-col items-center">
        <button className="text-white text-xl font-semibold" onClick = {handlePage}>{user.userName}</button>
        {/* <button className="mt-3 bg-white text-teal-500 font-semibold py-2 px-4 rounded-full shadow">
          Theo dõi
        </button> */}
        {
          (id === userId ) ? (<><div></div></>) :(<Button
            props={isFollowing ? 'Đã theo dõi' : 'Theo dõi'}
            bgcolor={isFollowing ? 'bg-gray-500' : 'bg-[#379d7d]'}
            size="text-4xl"
            textcolor="text-white"
            onClick={handleFollow}/>)
        }
      </div>
    </div>

      {/* <div className="pb-6 text-center">
      
      </div> */}
    </div>
        </div>
    );
};

export default CardUser;