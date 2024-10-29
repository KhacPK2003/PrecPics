import React from 'react';
import styled from 'styled-components';
import Navbar from '../Components/Navbar/Navbar';
import DropdownButton from '../Components/DropdownButton/DropdownButton';
import Footer from '../Components/Footer/Footer';
const CardUser = styled.div`
    display: flex;
    align-items: center;
    column-gap: 12px;

`;
const UserAvatar = styled.img`
    width: 50px;
    height: 50px;
    border-radius: 100rem;
    object-fit: cover;
    flex-shrink: 0;

`;

const UserName = styled.span`
    font-size: 16px;
    font-weight: 300;
    color: white;
`;

const GallerySingle = () => {
    return (
        <div className='bg-[#379d7d] min-h-screen'>
        <Navbar></Navbar>
            <div className="container mx-auto px-4 py-4 mt-[30px] ">
                <h1 className="text-center text-[40px] font-semibold text-white pt-11">
                    Thông tin ảnh
                </h1>
                <p className='max-w-[800px] mx-auto mt-[30px] text-white'>Chào mừng bạn đến với bộ sưu tập độc đáo này! Tại đây, chúng tôi tự hào giới thiệu những tác phẩm nghệ thuật độc đáo, mỗi bức tranh, mỗi tác phẩm đều mang trong mình một câu chuyện riêng, một cảm xúc sâu sắc mà nghệ sĩ đã muốn chia sẻ với thế giới.

Hãy dừng lại và thả mình vào thế giới của từng tác phẩm. Mỗi chi tiết, mỗi màu sắc đều được sắp xếp một cách cẩn thận để tạo ra một trải nghiệm tuyệt vời cho bạn. Hãy để tâm hồn bạn lạc vào những dòng chảy của trí tưởng tượng và cảm xúc mà tác phẩm mang lại.

Chúng tôi hy vọng rằng bạn sẽ tìm thấy niềm vui và cảm hứng từ bộ sưu tập này, và chúng tôi rất vui mừng được chia sẻ với bạn những tác phẩm tuyệt vời này. Hãy thưởng thức và cảm nhận!</p>

            </div>
            
            <div>
            <div className='flex items-center justify-center'>
                <img className='w-[70%] object-cover' src='https://images.pexels.com/photos/355465/pexels-photo-355465.jpeg?auto=compress&cs=tinysrgb&w=600' alt=''/>
            </div>
            </div>
            <div className="pt-3">
                <div className="flex pt-3 pl-12">
                    <CardUser className="ml-[230px] w-1/2 bg-black p-4 rounded-lg flex items-center space-x-4 max-w-]">
                      <UserAvatar
                          src="https://cdn.dribbble.com/users/2400293/screenshots/16527147/media/f079dc5596a5fb770016c4ea506cd77b.png?resize=1000x750&vertical=center"
                          alt="" className="w-16 h-16 rounded-full" />
      
                        <UserName className="text-white text-lg font-semibold">PPPKKK</UserName>    
                    </CardUser>

                  {/* <div className="w-1/2 p-4 rounded-lg flex flex-col space-y-2 ml-4 text-center">
                    <p className="text-white font-medium">Tên</p>
                    <p className="text-white font-medium">Tiêu đề</p>
                    <p className="text-white font-medium">Nhãn</p>
                    <p className="text-white font-medium">...</p>
                    <div>
                    <DropdownButton></DropdownButton>
                    </div>
                    <div>
                    </div>
                  </div> */}
                  <div className="flex flex-col justify-center items-center space-y-3 w-2/3  text-white text-center pl-[100px] pr-12">
                        <p className="text-lg font-medium border-b border-gray-500 pb-1 w-full">Thẻ: Nature</p>
                        <p className="text-lg font-medium border-b border-gray-500 pb-1 w-full">Tiêu đề: Beautiful Landscape</p>
                        <p className="text-lg font-medium border-b border-gray-500 pb-1 w-full">Nhãn: Photography</p>
                        <p className="text-lg font-medium border-b border-gray-500 pb-1 w-full">...</p>
                  <DropdownButton></DropdownButton>
                  </div>
                </div>
              </div>
              <div className=' h-[300px]'></div>
                <Footer></Footer>
                
        </div>
    );
};

export default GallerySingle;