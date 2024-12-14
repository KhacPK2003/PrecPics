import React, { useState, useEffect } from 'react';
import ImageACollection from '../Components/About/ImageACollection';
import { useNavigate, useParams } from 'react-router-dom';

const AboutCollection = () => {
    const navigate = useNavigate();
    const { id } = useParams(); // Get the user ID from the URL params
    const [collectUser, setCollect] = useState([]); // Store collections data

    useEffect(() => {
        fetch(`http://localhost:8080/public/api/users/${id}`) // Fetch data from the API
            .then((response) => response.json())
            .then(({ payload }) => {
                setCollect(payload.collections); // Set collections into state
            })
            .catch((error) => console.error('Error fetching collections:', error));
    }, [id]);

    return (
        <div className='bg-white min-h-screen'>
            <div className='grid grid-cols-4 gap-5 p-5'>
                {collectUser.map((collection) => (
                    <ImageACollection
                        key={collection.id}
                        collectionId={collection.id}
                        collectionName={collection.name}
                        userId = {id}
                    />
                ))}
            </div>
        </div>
    );
};

export default AboutCollection;
