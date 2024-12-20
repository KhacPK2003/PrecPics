import React, { useState, useEffect } from 'react';
import ImageACollection from '../Components/About/ImageACollection';
import { useNavigate, useParams } from 'react-router-dom';
import { auth } from '../firebaseconfig';
import { onAuthStateChanged } from 'firebase/auth';
const AboutCollection = () => {
    const navigate = useNavigate();
    const { id } = useParams(); // Get the user ID from the URL params
    const [collectUser, setCollect] = useState([]); // Store collections data
    const [change , setChange] = useState(false);
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
    // Mở menu

    useEffect(() => {
        fetch(`http://localhost:8080/public/api/users/${id}`) // Fetch data from the API
            .then((response) => response.json())
            .then(({ payload }) => {
                setCollect(payload.collections); // Set collections into state
            })
            .catch((error) => console.error('Error fetching collections:', error));
    }, [id,change]);


    const handleDelete = async (collectionName,collectionId) => {
        if (window.confirm(`Bạn có chắc là muốn xóa bộ sưu tập ${collectionName} ?`)) {
            try {
                const response = await fetch(`http://localhost:8080/public/api/collections/${collectionId}`, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`, // Thêm token hoặc ID người dùng nếu cần
                    },
                });
    
                if (!response.ok) {
                    throw new Error(`Xóa thất bại!!`);
                }
    
                // Thông báo xóa thành công
                alert(`Bộ sưu tập ${collectionName} đã được xóa.`);
                setChange(prev => !prev);
            } catch (error) {
                console.error('Error deleting collection:', error);
                alert('An error occurred while deleting the collection. Please try again.');
            } finally {
                handleClose(); // Đóng menu sau khi hoàn thành
            }
        }
    };

    return (
        <div className='bg-white min-h-screen'>
            <div className='grid grid-cols-4 gap-5 p-5'>
                {collectUser.map((collection) => (
                    <ImageACollection
                        key={collection.id}
                        collectionId={collection.id}
                        collectionName={collection.name}
                        userId = {id}
                        handleDelete = {handleDelete}
                    />
                ))}
            </div>
        </div>
    );
};

export default AboutCollection;
