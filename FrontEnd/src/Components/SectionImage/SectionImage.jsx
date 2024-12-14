import { BiLink } from 'react-icons/bi';
import { AiFillHeart, AiOutlineHeart } from 'react-icons/ai';
import React, { useState , useEffect } from 'react';
import GallerySingle from '../GallerySingle/ImageSingle';
import { Button, Dialog, DialogContent, DialogTitle } from '@mui/material';
import { auth } from '../../firebaseconfig';
import { onAuthStateChanged } from 'firebase/auth';
function SectionImage({content,onDataChange }){
    // console.log(content);
    const userId = JSON.parse(localStorage.getItem('userID'));
    const [token, setToken] = useState(null);
    const [likedID,setLikedID] = useState(null);
    const [isFavorite, setIsFavorite] = useState(false);
    const [open, setOpen] = useState(false);


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
    }, []); // Take token

    const addContentCollection = async (ID) =>{
        if(token === null) return;
        try {
            const response = await fetch(`http://localhost:8080/public/api/collections/${ID}/contents/${content.id}`, {
              method: 'POST',
              headers: {
                //   'Content-Type': 'application/json',
                  'Authorization': `Bearer ${token}`,
              },
            });
            if (response.ok) {
                const data = await response.json();
                console.log('added successfully:', data);
              } else {
                throw new Error('Failed to add content to the collection');
              }
            } catch (err) {
            //   setError(err.message);
            }
    }

    const removeContentCollection = async(ID) =>{
        if(token === null) return;
        try {
            const collectionId = ID;
            const response = await fetch(`http://localhost:8080/public/api/collections/${collectionId}/contents/${content.id}`, {
              method: 'DELETE',
              headers: {
                //   'Content-Type': 'application/json',
                  'Authorization': `Bearer ${token}`,
              },
            });
            if (response.ok) {
                const data = await response.json();
                // console.log('Removes successfully:', data);
              } else {
                throw new Error('Failed to remove content to the collection');
              }
            } catch (err) {
            //   setError(err.message);
            }
    }

    const createLikedCollection = async (CollectName) => {
        if(token === null) return;
        try {
          const response = await fetch('http://localhost:8080/public/api/collections', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            body: CollectName, // You can customize the name as needed
          });
    
          if (response.ok) {
            const data = await response.json();
            // console.log(data);
            setLikedID(data.payload.id);
          } else {
            throw new Error('Failed to create the collection');
          }
        } catch (err) {
        //   setError(err.message);
        //   setIsLoading(false);
        }
    };

    const IsLiked = async(id) =>{
        try {
            const response = await fetch(`http://localhost:8080/public/api/collections/${id}`);
            if(response.ok){
                const collectionData = await response.json();
                // console.log(typeof )
                const match = collectionData.payload.inCols.find(item => item.contentId === content.id);
                setIsFavorite(match);
            }
        } catch (error) {
            console.error('Error:', error);
        }
    }

    useEffect(() => {
        if(userId === null) return;
        fetch(`http://localhost:8080/public/api/users/${userId}`)
            .then((response) => response.json())
            .then(({ payload }) => {
                // console.log(payload);
                if (payload.collections) {
                    const likedCollection = payload.collections.find(
                      (collection) => collection.name.trim() === 'Liked'
                    );
                    if (likedCollection) {
                      setLikedID(likedCollection.id);
                      IsLiked(likedCollection.id);
                    }else{
                        createLikedCollection("Liked");
                    }
                }
        });
    }, [userId,open,token]);

    const handleClickOpen = () => {
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };
    const toggleFavorite = () => {
        // console.log(likedID);
        if(isFavorite){
            removeContentCollection(likedID);
        }else{
            addContentCollection(likedID);
        }
        setIsFavorite((prev) => !prev);
    };
    return (
            <div className="w-full sm:w-1/2 md:w-1/3 lg:w-1/4 p-2">
                <div className="gallery-item relative h-100">
                <img src={content.dataUrl} className="w-full h-auto rounded transition-transform duration-300 hover:filter hover:brightness-75" alt=""/>
                    <div className="gallery-links absolute inset-0 flex items-center justify-center">
                        <button  className="details-link flex items-center justify-center p-2 rounded-full shadow-lg opacity-75 hover:opacity-100 transition" variant="outlined" onClick={handleClickOpen}>
                                    <BiLink className="text-3xl" />
                        </button>
                        <Dialog open={open} onClose={handleClose} maxWidth="lg" fullWidth >
                            <DialogTitle onClose={handleClose}>Ảnh</DialogTitle>
                            <DialogContent dividers>
                                <GallerySingle content={content} onDataChange = {onDataChange} />
                            </DialogContent>
                        </Dialog>
                        
                        <button onClick={toggleFavorite} className="ml-4 flex items-center">
                            {isFavorite ? (
                                <AiFillHeart className="text-4xl text-red-600 transition-colors duration-300" />
                            ) : (
                                <AiOutlineHeart className="text-4xl text-gray-500 transition-colors duration-300" />
                            )}
                        </button>
                    </div>
                </div>
            </div>

    );
}
export default SectionImage;