import React from 'react';
import ImageACollection from '../Components/About/ImageACollection';
import PersonalPage from '../Components/About/PersonalPage';
import Button from '../Components/Button/Button';
import Navbar from '../Components/Navbar/Navbar';
import { useNavigate } from 'react-router-dom';
const AboutCollection = () => {
    const navigate = useNavigate();
    return (
        <div className='bg-white min-h-screen'>
          <div className='grid grid-cols-4 gap-5 p-5' >
            <ImageACollection></ImageACollection>
            <ImageACollection></ImageACollection>
            <ImageACollection></ImageACollection>
            <ImageACollection></ImageACollection>
            </div>
        </div>
    );
};

export default AboutCollection;