import React, { useState, useEffect } from 'react';
import { ToastContainer, toast } from 'react-toastify';
import { auth } from '../../firebaseconfig';
import { onAuthStateChanged } from 'firebase/auth'; 
import 'react-toastify/dist/ReactToastify.css';

const DropdownButton = ({ content , type }) => {
    const [imageUrl, setImageUrl] = useState(null);
    const [width, setWidth] = useState('');
    const [token, setToken] = useState(null);
    const [height, setHeight] = useState('');

    // Lấy token người dùng khi thay đổi trạng thái đăng nhập
    useEffect(() => {
        const unsubscribe = onAuthStateChanged(auth, (currentUser) => {
          if (currentUser) {
            currentUser
              .getIdToken()
              .then((idToken) => setToken(idToken))  // Lưu token
              .catch((error) => console.error('Error getting ID token:', error));
          } else {
            setToken(null);  // Nếu không có người dùng, set token là null
          }
        });
        return () => unsubscribe();
    }, []);

    const handleSizeChange = async (width, height) => {
        try {
            const url = new URL(`http://localhost:8080/public/api/contents/${type}/resize`);
            url.searchParams.append('content', content.id);  // Thêm tham số content vào query string
            url.searchParams.append('width', width);          // Thêm tham số width vào query string
            url.searchParams.append('height', height);        // Thêm tham số height vào query string

            // Gửi yêu cầu GET với Authorization header (Bearer token)
            const response = await fetch(url, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`, // Thêm token vào header
                },
            });

            if (!response.ok) {
                throw new Error("Tải thất bại!!");
            }

            // Lấy dữ liệu ảnh (binary)
            const imageBlob = await response.blob();
            const imageObjectUrl = URL.createObjectURL(imageBlob);
            setImageUrl(imageObjectUrl);  // Cập nhật URL ảnh đã thay đổi kích thước
            const link = document.createElement('a');
            link.href = imageObjectUrl;
            if(type === 'image') link.download = `resized_image_${width}x${height}.jpg`;
            else link.download = `resized_image_${width}x${height}.mp4`;
            document.body.appendChild(link);
            link.click();
            link.remove();
            toast.success("Tải thành công!!");
        } catch (error) {
            toast.error("Tải thất bại!!.");
        }
    };

    return (
        <div className="btn-group">
            <button 
                type="button" 
                className="btn btn-secondary dropdown-toggle" 
                data-bs-toggle="dropdown" 
                aria-expanded="false"
            >
                Tải xuống miễn phí
            </button>
            <ul className="dropdown-menu">
                <li>
                    <button className="inline-block m-0 border-none dropdown-item" 
                            onClick={() => handleSizeChange(1920, 1680)}>
                        <span className="text-black">Lớn:</span>
                        <span className="text-gray-500"> 1920x1680</span>
                    </button>
                </li>
                <li>
                    <button className="inline-block m-0 border-none dropdown-item" 
                            onClick={() => handleSizeChange(1280, 835)}>
                        <span className="text-black">Trung bình:</span>
                        <span className="text-gray-500"> 1280x835</span>
                    </button>
                </li>
                <li><a className="dropdown-item" href="#">Kích thước mặc định</a></li>
                <li><hr className="dropdown-divider" /></li>
                <li>
                    <a className="dropdown-item">
                        <p>Kích thước tùy chọn</p>
                        <input 
                            className="w-[45%] inline-block border-2 border-black"
                            type="number"
                            placeholder="Width"
                            value={width}
                            onChange={(e) => setWidth(e.target.value)} />
                        <span className="text-black inline-block mx-2">X</span>
                        <input 
                            className="w-[45%] inline-block border-2 border-black"
                            type="number"
                            placeholder="Height"
                            value={height}
                            onChange={(e) => setHeight(e.target.value)} />
                        <button 
                            className="btn btn-primary" 
                            onClick={() => handleSizeChange(width, height)}>
                            Resize
                        </button>
                    </a>
                </li>
            </ul>

            {/* Display resized image */}
            {/* {imageUrl && <img src={imageUrl} alt="Resized" width="100%" />} */}
            
            <ToastContainer
                position="bottom-left"
                autoClose={5000}
                hideProgressBar={false}
                newestOnTop={true}
                closeButton={true}
                rtl={false}
                pauseOnFocusLoss
                draggable
                pauseOnHover
                theme="light"
            />
        </div>
    );
};

export default DropdownButton;
