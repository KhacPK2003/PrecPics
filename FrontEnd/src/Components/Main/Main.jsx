import SectionImage from "../SectionImage/SectionImage";
import SectionVideo from "../SectionVideo/SectionVideo"; 
import React, { useState, useEffect } from 'react';

function Main({ showvideo = false }) {
    const Type = showvideo ? 1 : 0;  // Sử dụng showvideo để xác định type
    const [data, setData] = useState([]);
    const [change,setChange] = useState(false);
    useEffect(() => {
        // Gửi yêu cầu đến API
        fetch(`http://localhost:8080/public/api/contents/by-type?type=${Type}&page=1&size=10`)
          .then((response) => response.json())  // Chuyển đổi response thành JSON
          .then(({ payload }) => {
            setData(payload);  // Cập nhật state với dữ liệu API
          })
      }, [Type,change]); // Thêm Type vào dependency để gọi lại khi showvideo thay đổi

    const handleDataChange = () => {
        setChange((prev) => !prev)
    };

    return (
        <section id="gallery" className="gallery">
            <div className="w-full px-4">
                <div className="flex flex-wrap justify-center">
                    {showvideo 
                        ? // Nếu showvideo là true, hiển thị SectionVideo
                        data.map((post) => (
                            <SectionVideo key={post.id} content={post} />
                        ))
                        : // Nếu showvideo là false, hiển thị SectionImage
                        data.map((post) => (
                            <SectionImage key={post.id} content={post} onDataChange={handleDataChange}/>
                        ))
                    }
                </div>
            </div>
        </section>
    );
}

export default Main;
