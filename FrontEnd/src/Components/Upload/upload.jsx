import React, { useState, useEffect } from 'react';
import { auth } from '../../firebaseconfig';
import { onAuthStateChanged } from 'firebase/auth';
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const inputClasses = 'border border-zinc-300 rounded-lg p-2 w-full';
const labelClasses = 'block text-sm font-medium text-zinc-700';
const buttonClasses = 'bg-blue-500 text-primary-foreground hover:bg-primary/80 rounded-lg p-2 w-full';

function Upload() {
    const [media, setMedia] = useState(null);
    const [mediaType, setMediaType] = useState('');
    const [title, setTitle] = useState('');
    const [tags, setTags] = useState('');
    const [token, setToken] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

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

    const handleMediaChange = (event) => {
        const file = event.target.files[0];
        if (file) {
            setMedia(file);
            setMediaType(file.type.startsWith('video') ? 'video' : 'image');
        } else {
            setMedia(null);
            setMediaType('');
        }
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!token) {
            alert('Bạn cần đăng nhập trước khi tải lên!');
            return;
        }

        if (!media) {
            alert('Vui lòng chọn tệp để tải lên!');
            return;
        }

        const requestData = {
            type: mediaType === 'video' ? '1' : '0',
            description: title,
            tags: tags,
        };
        // console.log(token);
        const formData = new FormData();
        formData.append('file', media);
        formData.append(
            "request",
            new Blob([JSON.stringify(requestData)], { type: "application/json" })
        );

        // Hiển thị thông báo "Đang xử lý..." khi bắt đầu tải lên
        const toastId = toast.loading("Đang xử lý...");
        setIsLoading(true);

        try {
            const response = await fetch('http://localhost:8080/public/api/contents/upload', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
                body: formData,
            });

            if (response.ok) {
                const result = await response.json();
                // Sau khi gửi xong, hiển thị thông báo thành công
                toast.update(toastId, {
                    render: "Tải lên thành công!",
                    type: "success",
                    isLoading: false,
                    autoClose: 5000,
                });

                // Reset form sau khi upload
                setMedia(null);
                setMediaType('');
                setTitle('');
                setTags('');
            } else {
                const errorData = await response.json();
                // Cập nhật thông báo lỗi nếu tải lên thất bại
                toast.update(toastId, {
                    render: `Tải lên thất bại!`,
                    type: "error",
                    isLoading: false,
                    autoClose: 5000,
                });
                console.error('Upload failed:', response.statusText);
            }
        } catch (error) {
            // Cập nhật thông báo lỗi nếu có lỗi xảy ra
            toast.update(toastId, {
                render: 'Đã xảy ra lỗi khi tải lên, vui lòng thử lại!',
                type: "error",
                isLoading: false,
                autoClose: 5000,
            });
            console.error('Upload failed:', error);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <form className="bg-card p-6 rounded-lg shadow-md" onSubmit={handleSubmit}>
            <div className="mb-4">
                <label className={labelClasses}>Tiêu đề</label>
                <input
                    type="text"
                    placeholder="Nhập tiêu đề"
                    className={inputClasses}
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                />
            </div>
            <div className="mb-4">
                <label className={labelClasses}>Tags</label>
                <input
                    type="text"
                    placeholder="Nhập tags (cách nhau bởi dấu phẩy)"
                    className={inputClasses}
                    value={tags}
                    onChange={(e) => setTags(e.target.value)}
                />
            </div>
            <div className="mb-4">
                <label className={labelClasses}>Chọn tệp</label>
                <input type="file" accept="image/*,video/*" onChange={handleMediaChange} />
                {media && (
                    <div style={{ marginTop: '10px' }}>
                        {mediaType === 'video' ? (
                            <video controls style={{ maxWidth: '300px' }}>
                                <source src={URL.createObjectURL(media)} type={media.type} />
                            </video>
                        ) : (
                            <img src={URL.createObjectURL(media)} alt="Preview" style={{ maxWidth: '300px' }} />
                        )}
                    </div>
                )}
            </div>
            <button type="submit" className={buttonClasses} disabled={isLoading}>
                {isLoading ? 'Đang tải lên...' : 'Tải lên'}
            </button>
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
                theme="light" // Tùy chọn theme: light, dark
            />
        </form>
    );
}

export default Upload;
