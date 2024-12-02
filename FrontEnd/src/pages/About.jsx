import React , {useEffect, useState} from 'react';
import Navbar from '../Components/Navbar/Navbar';
import Button from '../Components/Button/Button';
import Avatar from '../Components/Avatar/Avatar';
import PersonalPage from '../Components/About/PersonalPage';
import OutStanding from '../Components/About/OutStanding';
import { useNavigate } from 'react-router-dom';
import { Outlet } from 'react-router-dom';
import { useParams } from 'react-router-dom';

const About = () => {
    const navigate = useNavigate();
    const { id } = useParams();
    return (
        <div className='bg-white min-h-screen'>
            <PersonalPage/>
            <div className='flex justify-center mt-[100px] mr-[900px]'>
                    <Button props ='Mục nổi bật' textcolor='text-white' bgcolor='bg-black' onClick={() => navigate(`/about/${id}`)}></Button>
                    <Button props ='Bộ sưu tập ' onClick={() => navigate(`/about/${id}/aboutcollection`)}></Button>   
                    <Button props ='Số liệu thống kê ' onClick={() => navigate(`/about/${id}/aboutdata`)} ></Button>
                    <Button props ='Người theo dõi ' onClick={() => navigate(`/about/${id}/aboutfollower`)}></Button>
                    <Button props ='Đang theo dõi ' onClick={() => navigate(`/about/${id}/aboutwatching`)}></Button>
            </div>
            <div className='mt-[100px]'>
                <Outlet/>
            </div>
        </div>
    );
};

export default About;