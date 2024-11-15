import { Description } from '@mui/icons-material';
import React, { useState ,useEffect} from 'react';
import { auth, googleProvider, signInWithPopup } from '../../firebaseconfig';
import { onAuthStateChanged } from 'firebase/auth';
import { event } from 'jquery';


const inputClasses = 'border border-zinc-300 rounded-lg p-2 w-full';
const labelClasses = 'block text-sm font-medium text-zinc-700';
const buttonClasses = 'bg-blue-500 text-primary-foreground hover:bg-primary/80 rounded-lg p-2 w-full';

function Upload() {
    const [media, setMedia] = useState(null);
    const [mediaType, setMediaType] = useState('');
    const [title, setTitle] = useState('');
    const [tags, setTags] = useState('');


    const [token, setToken] = useState(null);  // State để lưu trữ bearer token

    useEffect(() => {
      const unsubscribe = onAuthStateChanged(auth, (currentUser) => {
        if (currentUser) {
          // Lấy ID token của người dùng khi họ đã đăng nhập
          currentUser.getIdToken()
            .then((idToken) => {
              setToken(idToken);  // Lưu trữ token trong state
              console.log('Bearer token:', idToken);  // In token ra console (hoặc có thể gửi tới API)
            })
            .catch((error) => {
              console.error('Error getting ID token:', error);
            });
        } else {
          setToken(null);  // Reset token nếu người dùng đăng xuất
        }
      });
  
      return () => unsubscribe(); // Dọn dẹp listener khi component unmount
    }, []);

    const handleMediaChange = (event) => {
        const file = event.target.files[0];
        if (file) {
            const mediaUrl = URL.createObjectURL(file);
            setMedia(mediaUrl);
            setMediaType(file.type.startsWith('video/') ? 'video' : 'image');
        }
    };

    const subMit = async (event) => {
        event.preventDefault(); // Ngừng hành vi mặc định của form (không reload trang)
        // Tạo FormData để gửi tệp và các tham số khác
        const formData = new FormData();
        formData.append('file', event.target[2].files[0]);
        //  file input là chỉ mục 3 trong form
        formData.append('description', title);
        formData.append('type',(mediaType === 'video' ? '1' : '0'));
        formData.append('tags',tags)

        try {
            const response = await fetch('http://localhost:8080/public/api/contents/upload', {
                method: 'POST',
                headers: {
                    'Content-Type': 'multipart/form-data',
                    'Authorization': `Bearer ${token}`,
                },
                body: formData,
            });
            
            if (response.ok) {
                const result = await response.json();
                console.log('Upload successful:', result);
                // Xử lý kết quả trả về từ API nếu cần
            } else {
                console.error('Upload failed:', response.statusText);
            }
        } catch (error) {
            console.error('Upload failed', error);
        }
    };

    return (
        <>
            <form className="bg-card p-6 rounded-lg shadow-md" onSubmit={subMit}>
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
                    <label className={labelClasses}>Tiêu đê gắn thẻ</label>
                    <input
                        type="text"
                        placeholder="Nhập tag"
                        className={inputClasses}
                        value={tags}
                        onChange={(e) => setTags(e.target.value)}
                    />
                </div>
                <div className="mb-4">
                    <label className={labelClasses}>Chọn tệp</label>
                    <input type="file" accept="image/*,video/*" onChange={handleMediaChange} />
                    {media && (
                        mediaType === 'video' ? (
                            <video controls style={{ marginTop: '10px', maxWidth: '300px' }}>
                                <source src={media} type={mediaType} />
                            </video>
                        ) : (
                            <img src={media} alt="Preview" style={{ marginTop: '10px', maxWidth: '300px' }} />
                        )
                    )}
                </div>
                <button type="submit" className={buttonClasses}>
                    Tải lên
                </button>
            </form>
        </>
    );
}

export default Upload;
