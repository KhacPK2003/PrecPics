import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import FormAbout from '../Components/Form/FormAbout';
import { useNavigate } from 'react-router-dom';

const EditProfile = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    const [activeTab, setActiveTab] = useState('Hồ sơ');
    const [user, setUser] = useState(null);  // Change to null to handle loading state
    const [isLoading, setIsLoading] = useState(true);  // Add loading state

    useEffect(() => {
        // Fetch user data from the API
        fetch(`http://localhost:8080/public/api/users/${id}`)
            .then((response) => response.json())  // Convert response to JSON
            .then(({ payload }) => {
                setUser(payload);
                setIsLoading(false);  // Set loading to false once data is fetched
            })
            .catch((error) => {
                console.error('Error fetching user data:', error);
                setIsLoading(false);
            });
    }, [id]);

    const handleTabClick = (tabName, path) => {
        setActiveTab(tabName); // Update active tab
        navigate(path); // Navigate to the corresponding page
    };

    // Show loading indicator while fetching user data
    if (isLoading) {
        return <div>Loading...</div>;  // You can customize this with a spinner or other loader
    }

    return (
        <div className="flex justify-center items-center">
            <div className="w-full max-w-2xl p-6">
                {/* Tabs */}
                <div className="flex justify-center mb-6">
                    <button className="px-4 py-2 text-white bg-black rounded-full">Hồ sơ</button>
                    <button className="px-4 py-2 text-gray-600" onClick={() => handleTabClick('Thông báo', `/aboutnotification/${id}`)}>Thông báo</button>
                </div>

                {/* Header */}
                <h1 className="text-4xl font-bold text-center text-gray-800 mb-8">Cài đặt hồ sơ</h1>

                {/* Profile Image and Change Button */}
                <FormAbout user={user} />  {/* Pass the loaded user data to FormAbout */}
            </div>
        </div>
    );
};

export default EditProfile;
