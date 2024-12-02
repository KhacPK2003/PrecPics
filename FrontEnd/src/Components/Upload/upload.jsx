import React, { useState, useEffect } from 'react';
import { auth } from '../../firebaseconfig';
import { onAuthStateChanged } from 'firebase/auth';
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const inputClasses = 'border border-zinc-300 rounded-lg p-2 w-full';
const labelClasses = 'block text-sm font-medium text-zinc-700';
const buttonClasses = 'bg-blue-500 text-primary-foreground hover:bg-primary/80 rounded-lg p-2 w-full';

function Upload() {
    const [title, setTitle] = useState('');
    const [tags, setTags] = useState('');
    const [token, setToken] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [dragOver, setDragOver] = useState(false);
    const [file, setFile] = useState(null);
    const [preview, setPreview] = useState(null);
    const [fileType, setFileType] = useState(null);
    const [fileUrl, setFileUrl] = useState('');  // New state for the file URL
    const [fileName, setFileName] = useState('');  // New state for the file name

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

    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        handleFile(selectedFile);
    };

    const handleFile = (selectedFile) => {
        if (!selectedFile) return;

        const validImageExtensions = ['image/jpeg', 'image/jpg', 'image/png'];
        const validVideoExtensions = ['video/mp4', 'video/webm', 'video/ogg'];

        if (validImageExtensions.includes(selectedFile.type)) {
            const reader = new FileReader();
            reader.onload = () => {
                setPreview(reader.result);
                setFileType('image');
                setFileUrl(URL.createObjectURL(selectedFile)); // Set the URL here
                setFileName(selectedFile.name);  // Set the file name here
            };
            reader.readAsDataURL(selectedFile);
            setFile(selectedFile);
        } else if (validVideoExtensions.includes(selectedFile.type)) {
            const reader = new FileReader();
            reader.onload = () => {
                setPreview(reader.result);
                setFileType('video');
                setFileUrl(URL.createObjectURL(selectedFile)); // Set the URL here
                setFileName(selectedFile.name);  // Set the file name here
            };
            reader.readAsDataURL(selectedFile);
            setFile(selectedFile);
        } else {
            alert('Chỉ chấp nhận file ảnh hoặc video!');
            setFile(null);
            setPreview(null);
            setFileType(null);
            setFileUrl('');  // Reset file URL if invalid file
            setFileName('');  // Reset file name if invalid file
        }
    };

    const handleDragOver = (e) => {
        e.preventDefault();
        setDragOver(true);
    };

    const handleDragLeave = () => {
        setDragOver(false);
    };

    const handleDrop = (e) => {
        e.preventDefault();
        setDragOver(false);
        const droppedFile = e.dataTransfer.files[0];
        handleFile(droppedFile);
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!token) {
            alert('Bạn cần đăng nhập trước khi tải lên!');
            return;
        }

        if (!file) {
            alert('Vui lòng chọn tệp để tải lên!');
            return;
        }

        const requestData = {
            type: fileType === 'video' ? '1' : '0',
            description: title,
            tags: tags,
        };
        // console.log(token);
        const formData = new FormData();
        formData.append('file', file);
        formData.append(
            "request",
            new Blob([JSON.stringify(requestData)], { type: "application/json" })
        );

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
                toast.update(toastId, {
                    render: "Tải lên thành công!",
                    type: "success",
                    isLoading: false,
                    autoClose: 5000,
                });

                setFile(null);
                setFileType(null);
                setTitle('');
                setTags('');
                setPreview(null);
            } else {
                const errorData = await response.json();
                toast.update(toastId, {
                    render: `Tải lên thất bại!`,
                    type: "error",
                    isLoading: false,
                    autoClose: 5000,
                });
                console.error('Upload failed:', response.statusText);
            }
        } catch (error) {
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
        <>
            <div className='flex flex-col items-center justify-center mt-[150px]'>
                <h2 className='text-[40px] leading-[40px] tracking[-0.02em]'>Chia sẻ ảnh và video khiến cả thế giới yêu thích</h2>
                <p className='text-[22px] font-medium leading-[28px] mt-4'>Chia sẻ ảnh và 50 video của bạn để giới thiệu bản thân với hàng triệu người dùng PrePics</p>
            </div>
            <form className="grid grid-cols-[70%,auto] gap-6 p-6 bg-gray-50 ml-[160px] mt-[100px]" onSubmit={handleSubmit}>
                <div className={` drag-area w-full h-full rounded-2xl border-2 ${dragOver ? 'border-green-500' : 'border-dashed border-neutral-500'} p-6 flex flex-col items-center justify-center`}
                    onDragOver={handleDragOver}
                    onDragLeave={handleDragLeave}
                    onDrop={handleDrop}>
                    <img src='./src/assets/images/images22.png' />
                    {preview ? (
                        fileType === 'image' ? (
                            <>
                                <img src={preview} alt="Preview" className="max-h-50 object-contain" />
                            </>
                        ) : (
                            <video src={preview} controls className="max-h-50 object-contain" />
                        )
                    ) : (
                        <>
                            <header className="text-lg text-black font-bold text-[31px]">
                                {dragOver ? 'Thả để tải ảnh/video lên' : 'Kéo và thả để tải file lên'}
                            </header>
                            <span className="my-3 text-black font-bold text-[31px]">Hoặc</span>
                            <button
                                type="button"
                                className="px-4 py-2 rounded bg-[#379d7d] text-white"
                                onClick={() => document.getElementById('fileInput').click()}
                            >
                                Khám phá
                            </button>
                            <input
                                type="file"
                                id="fileInput"
                                accept="image/jpeg, image/jpg, image/png, video/mp4, video/webm, video/ogg" className="hidden" 
                                onChange={handleFileChange}
                            />
                        </>
                    )}
                    {/* Display file URL below preview */}
                    {fileName && (
                        <div className="mt-4 text-center">
                            <input
                                    type="file"
                                    id="fileInput"
                                    accept="image/jpeg, image/jpg, image/png, video/mp4, video/webm, video/ogg"
                                    onChange={handleFileChange}
                            />
                        </div>
                    )}
                </div>
                <div className="bg-white p-6 rounded-lg shadow-md flex flex-col items-center justify-center">
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
                        <label className={labelClasses}>Tiêu đề gắn thẻ</label>
                        <input
                            type="text"
                            placeholder="Nhập tag"
                            className={inputClasses}
                            value={tags}
                            onChange={(e) => setTags(e.target.value)}
                        />
                    </div>
                    <button type="submit" className={buttonClasses} disabled={isLoading}>
                        {isLoading ? 'Đang tải lên...' : 'Tải lên'}
                    </button>
                </div>
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
            </form>
            <div className='h-[300px]'></div>
        </>
    );
}

export default Upload;
