import React, { useState, useEffect } from 'react';
import { FaTrashAlt } from 'react-icons/fa'; // Import icon xóa từ react-icons
import Avatar from '../../Components/Avatar/Avatar';
import { auth } from '../../firebaseconfig';
import { onAuthStateChanged } from 'firebase/auth';
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const FormAbout = ({ user }) => {
    // console.log(user);
    const [username, setuserName] = useState(user.userName || '');  // Default to empty string if undefined
    const [description, setDescription] = useState(user.description || '');  // Default to empty string if undefined
    const [insta, setInsta] = useState(user.instagramUrl || '');  // Default to empty string if undefined
    const [twitter, setTwitter] = useState(user.twitterUrl || '');  // Default to empty string if undefined
    const [avatarUrl , setAvatarUrl] = useState(user.avatarUrl);
    const [file, setFile] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [token, setToken] = useState(null);




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

    const handleFileChange = (event) => {
        const selectedFile = event.target.files[0];
        if (selectedFile) {
            setFile(selectedFile);  // Lưu tệp vào state
        }
    };

    const handleFileRemove = () => {
        setFile(null);  // Xóa tệp khỏi state
    };

    const handleNameChange = (event) => setuserName(event.target.value);
    const handleDiscrip = (event) => setDescription(event.target.value);
    const handleInsta = (event) => setInsta(event.target.value);
    const handleX = (event) => setTwitter(event.target.value);

    const handleSubmit = async (event) => {
        event.preventDefault();

        // Prepare data for the request
        const requestData = {
            userName: username,
            fullName: user.fullName,
            email: user.email,
            description: description,
            avatarUrl: user.avatarUrl,
            twitterUrl: twitter,
            instagramUrl: insta,
            isActive : user.isActive,
            isAdmin : user.isAdmin
        };
        const formData = new FormData();
        formData.append(
            "entity",
            new Blob([JSON.stringify(requestData)], { type: "application/json" })
        );
        if (file) {
            formData.append('file', file);
        } else {
            // Nếu file là null, bạn có thể thêm một Blob rỗng hoặc không thêm gì cả
            formData.append('file', new Blob([])); // Hoặc chỉ bỏ qua dòng này nếu không muốn gửi file.
        }
        const toastId = toast.loading("Đang xử lý...");
        setIsLoading(true);

        try {
            if (!token) {
                throw new Error('Token is missing. User is not authenticated.');
            }

            const response = await fetch(`http://localhost:8080/public/api/users/${user.id}`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
                body: formData,
            });

            if (response.ok) {
                const result = await response.json();

                toast.update(toastId, {
                    render: "Lưu thành công!",
                    type: "success",
                    isLoading: false,
                    autoClose: 5000,
                });
                setAvatarUrl(result.payload.avatarUrl);
                setFile(result.payload.avatarUrl);
                setDescription(result.payload.description);
                setInsta(result.payload.instagramUrl);
                setTwitter(result.payload.twitterUrl);
                setuserName(result.payload.userName);
            } else {
                const errorData = await response.json();
                toast.update(toastId, {
                    render: `Lưu thất bại!`,
                    type: "error",
                    isLoading: false,
                    autoClose: 5000,
                });
                console.error('Upload failed:', response.statusText);
            }
        } catch (error) {
            toast.update(toastId, {
                render: `Lưu thất bại!`,
                type: "error",
                isLoading: false,
                autoClose: 5000,
            });
            console.error('Error:', error);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <>
            <div className="flex items-center justify-center space-x-4 mb-8">
                <Avatar url={avatarUrl}></Avatar>
                <button
                    className="px-4 py-2 bg-[#379d7d] text-white rounded-md"
                    onClick={() => document.getElementById('file-input').click()}
                >
                    Thay đổi hình ảnh
                </button>

                {/* Input file ẩn */}
                <input
                    id="file-input"
                    type="file"
                    className="hidden"
                    onChange={handleFileChange}
                />

                {/* Hiển thị tên tệp đã chọn và icon xóa */}
                {file && (
                    <div className="flex items-center space-x-2 mt-2">
                        {
                            file.name && (
                                <>
                                    <p className="text-black-500">Tệp đã chọn: {file.name}</p>
                                    <button
                                        onClick={handleFileRemove}
                                        className="text-red-500 hover:text-red-700"
                                    >
                                        <FaTrashAlt size={18} />
                                    </button>
                                </>
                            )
                        }
                    </div>
                )}
            </div>

            <div className="max-w-2xl mx-auto bg-white p-6 rounded-lg shadow-md">
                <h2 className="text-xl font-semibold mb-4">Thông tin cá nhân</h2>

                {/* Tên người dùng */}
                <div className="mb-4">
                    <div>
                        <label className="block font-medium">Tên người dùng</label>
                        <input
                            type="text"
                            className="w-full border rounded p-2"
                            value={username}
                            onChange={handleNameChange}
                        />
                        <p className="text-xs text-gray-500">
                            Chúng tôi mong mọi người sẽ dùng tên thật trong cộng đồng để chúng ta có thể nhận ra nhau.
                        </p>
                    </div>
                </div>

                {/* Email */}
                <div className="mb-4">
                    <label className="block font-medium">Email</label>
                    <input type="email" className="w-full border rounded p-2" value={user.email} disabled />
                    <p className="text-xs text-gray-500">Lưu ý rằng email này sẽ được công khai.</p>
                </div>

                {/* Mật khẩu */}
                <div className="mb-4">
                    <label className="block font-medium">Mật khẩu</label>
                    <button className="text-white px-4 py-2 bg-[#379d7d] rounded">
                        Thay đổi mật khẩu
                    </button>
                </div>

                {/* Giới thiệu về bạn */}
                <h2 className="text-xl font-semibold mb-4">Giới thiệu về bạn</h2>
                <div className="mb-4">
                    <label className="block font-medium">Tiêu đề ngắn gọn</label>
                    <input
                        type="text"
                        className="w-full border rounded p-2"
                        placeholder="Tiêu đề ngắn gọn"
                        value={description}
                        onChange={handleDiscrip}
                    />
                    <p className="text-xs text-gray-500">Mô tả ngắn gọn hồ sơ của bạn.</p>
                </div>

                {/* Thông tin liên kết */}
                <div className="grid grid-cols-2 gap-4 mb-4">
                    <div>
                        <label className="block font-medium">X</label>
                        <input
                            type="text"
                            className="w-full border rounded p-2"
                            placeholder="Thêm"
                            value={insta || ''}
                            onChange={handleInsta}
                        />
                    </div>
                    <div>
                        <label className="block font-medium">Instagram</label>
                        <input
                            type="text"
                            className="w-full border rounded p-2"
                            placeholder="Thêm"
                            value={twitter || ''}
                            onChange={handleX}
                        />
                    </div>
                </div>

                <div className="space-y-4">
                    {/* Nút "Xóa tài khoản và toàn bộ dữ liệu" */}
                    {/* <button
                        className="mt-4 bg-red-500 text-white font-bold py-2 px-6 rounded-md hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 active:bg-red-800 transition duration-300 ease-in-out"
                    >
                        Xóa tài khoản và toàn bộ dữ liệu
                    </button> */}

                    {/* Nút "Lưu hồ sơ" */}
                    <button className="w-full bg-[#379d7d] text-white p-2 rounded" disabled={isLoading} onClick={handleSubmit}>
                        {isLoading ? 'Đang lưu...' : 'Lưu hồ sơ'}
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
                        theme="light"
                    />
                </div>
            </div>
        </>
    );
};

export default FormAbout;
