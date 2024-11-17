import SectionImage from "../SectionImage/SectionImage";
import SectionVideo from "../SectionVideo/SectionVideo"; 
import React, { useState, useEffect } from 'react';

function Main({showvideo = false}){
    const Type = (showvideo ? 1 : 0);
    const [data, setData] = useState([]);
    useEffect(() => {
        // Gửi yêu cầu đến API
        fetch(`http://localhost:8080/public/api/contents/by-type?type=${Type}`)
          .then((response) => response.json()) // Chuyển đổi response thành JSON
          .then(({ payload }) => {
            setData(payload); // Cập nhật state với dữ liệu API
            // Đánh dấu đã tải xong
          })
      }, []); 
    return (
        <>
            <section id="gallery" className="gallery">
                <div className="w-full px-4">
                    <div className="flex flex-wrap justify-center">
                    {data.map((post) => (
                        <SectionImage key={post.id} content={post}/>
                    ))}
                    </div>
                </div>
            </section>
        </>
    );
}
export default Main;