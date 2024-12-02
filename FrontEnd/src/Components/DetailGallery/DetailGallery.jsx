import { AiFillHeart, AiOutlineHeart } from 'react-icons/ai';
import React, { useState, useEffect } from 'react';
import { FaComment } from "react-icons/fa";
import Comment from '../Comment/Comment';
import { auth } from '../../firebaseconfig';
import { onAuthStateChanged } from 'firebase/auth';
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { useNavigate } from 'react-router-dom';

function DetailGallery({ content }) {
    const [userId, setUserId] = useState(null);
    const [isFavorite, setIsFavorite] = useState(false);
    const [token, setToken] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [inputValue, setInputValue] = useState('');
    const [comments, setComments] = useState(content.comments.map(info => ({
        user: { name: info.user.userName, avatar: info.user.avatarUrl , id: info.user.id},
        timestamp: info.dateCreate,
        comment: info.contentValue,
        id: info.id // Thêm id bình luận
    }))); // Lưu trữ mảng comments trong state
    const navigate = useNavigate();
    const [countHeart, setCountHeart] = useState(0);

    const handleChange = (event) => {
        setInputValue(event.target.value); // Cập nhật giá trị input vào state
    };

    const toggleFavorite = () => {
        if (token == null) {
            navigate('Login');
            return;
        }
        setIsFavorite((prev) => !prev);
    };

    useEffect(() => {
        const unsubscribe = onAuthStateChanged(auth, (currentUser) => {
            if (currentUser) {
                currentUser
                    .getIdToken()
                    .then((idToken) => setToken(idToken))
                    .catch((error) => console.error('Error getting ID token:', error));
                setUserId(currentUser.uid);
            } else {
                setToken(null);
                setUserId(null);
            }
        });
        return () => unsubscribe();
    }, []); // Take token

    useEffect(() => {
        if (isFavorite) {
            setCountHeart((prev) => prev + 1);
        } else {
            setCountHeart((prev) => Math.max(prev - 1, 0));
        }
    }, [isFavorite]);

    const handlePost = async (e) => {
        console.log(inputValue);
        e.preventDefault();
        const toastId = toast.loading("Đang xử lý...");
        setIsLoading(true);
        try {
            if (!inputValue.trim()) {
                toast.update(toastId, {
                    render: "Vui lòng nhập bình luận!",
                    type: "error",
                    isLoading: false,
                    autoClose: 5000,
                });
                return;
            }
            const comment = {
                contentValue: inputValue,
                userId: userId,
                contentId: content.id,
            };

            // Đảm bảo đợi kết quả từ fetch
            const response = await fetch('http://localhost:8080/public/api/comments', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify(comment),
            });

            if (response.ok) {
                const result = await response.json();

                // Cập nhật lại state comments để thêm bình luận mới vào
                setComments((prevComments) => [
                    ...prevComments,
                    {
                        user: {
                            name: result.payload.user.userName,
                            avatar: result.payload.user.avatarUrl,
                        },
                        comment: inputValue,
                        timestamp: result.payload.dateCreate,
                        id: result.payload.id, // Thêm id của bình luận mới
                    },
                ]);

                // Cập nhật toast khi POST thành công
                toast.update(toastId, {
                    render: "Đã gửi!!",
                    type: "success",
                    isLoading: false,
                    autoClose: 1000,
                });
            } else {
                // Nếu phản hồi không thành công, thông báo lỗi
                toast.update(toastId, {
                    render: "Đã xảy ra lỗi khi gửi bình luận!",
                    type: "error",
                    isLoading: false,
                    autoClose: 1000,
                });
            }
        } catch (error) {
            // Xử lý lỗi nếu có lỗi khi gọi API
            console.error("Error posting comment:", error);
            toast.update(toastId, {
                render: "Đã xảy ra lỗi khi gửi bình luận!",
                type: "error",
                isLoading: false,
                autoClose: 5000,
            });
        } finally {
            setInputValue('');
            setIsLoading(false);
        }
    };

    // Hàm xử lý sửa bình luận
    const handleEditComment = async (commentId, editedContent) => {
        if (!editedContent.trim()) {
            toast.error("Bình luận đang rỗng! Xin hãy nhập!!");
            return; // If the comment is empty, do not update
        }
        try {
            const response = await fetch(`http://localhost:8080/public/api/comments/${commentId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
                body: JSON.stringify({ contentValue: editedContent }),
            });

            if (response.ok) {
                const updatedComment = await response.json();
                // console.log(updatedComment);
                setComments((prevComments) =>
                    prevComments.map((cmt) =>
                        (cmt.id === updatedComment.payload.id) ? { ...cmt, comment: updatedComment.payload.contentValue } : cmt
            ));
                // console.log(comments);
                toast.success("Bình luận đã được sửa!");
            } else {
                toast.error("Lỗi khi sửa bình luận.");
            }
        } catch (error) {
            console.error("Error editing comment:", error);
            toast.error("Đã xảy ra lỗi khi sửa bình luận.");
        } finally{
            setIsLoading(false);
        }
    };

    // Hàm xử lý xóa bình luận
    const handleDeleteComment = async (commentId) => {
        try {
            const response = await fetch(`http://localhost:8080/public/api/comments/${commentId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`,
                },
            });

            if (response.ok) {
                setComments((prevComments) =>
                    prevComments.filter((comment) => comment.id !== commentId)
                );
                toast.success("Bình luận đã được xóa!");
            } else {
                toast.error("Lỗi khi xóa bình luận.");
            }
        } catch (error) {
            console.error("Error deleting comment:", error);
            toast.error("Đã xảy ra lỗi khi xóa bình luận.");
        }finally{
            setIsLoading(false);
        }
    };
    const handleClick = ()=>{
        navigate(`/about/${content.user.id}`);
    }
    return (
        <div className="bg-popover dark:bg-card p-4 rounded-lg shadow-lg h-[calc(80vh-90px)] flex flex-col">
            <div className="flex items-center mb-4">
                <img className="w-10 h-10 rounded-full" src={content.user.avatarUrl} alt="Profile Picture" />
                <div className="ml-3">
                    <button className="font-bold text-primary" onClick={handleClick}>{content.user.userName}</button>
                </div>
            </div>
            <p className="text-foreground mb-2">{content.description}</p>
            <div className="text-muted-foreground text-sm mb-2"># show Tag</div>
            <h3 className="font-bold">Bình luận:</h3>
            <div className="mt-4 max-h-screen overflow-y-auto">
                <div className="mt-2">
                    {comments.map((c, index) => (
                        <Comment
                            key={index}
                            user={c.user}
                            comment={c.comment}
                            timestamp={c.timestamp}
                            commentId={c.id} // Truyền ID bình luận để có thể chỉnh sửa và xóa
                            onEdit={handleEditComment}
                            onDelete={handleDeleteComment}
                        />
                    ))}
                </div>
            </div>
            <div className="flex-grow" />
            <div className="flex items-center justify-between">
                <div className="flex items-center space-x-2">
                    <button onClick={toggleFavorite} className="flex items-center">
                        {isFavorite ? (
                            <AiFillHeart className="text-4xl text-red-600 transition-colors duration-300" />
                        ) : (
                            <AiOutlineHeart className="text-4xl text-gray-500 transition-colors duration-300" />
                        )}
                    </button>
                    <span>{countHeart} lượt thích</span>
                </div>
                <div className="flex items-center space-x-2">
                    <FaComment className="text-xl text-gray-600" />
                    <span className="text-gray-600">{comments.length} bình luận</span>
                </div>
            </div>
            <div className="flex items-center mt-4">
                <FaComment />
                <input
                    type="text"
                    placeholder="Thêm bình luận..."
                    value={inputValue}
                    onChange={handleChange}
                    className="flex-grow ml-2 p-2 border border-border rounded-lg bg-input text-foreground"
                />
                <button className="bg-blue-500 text-primary-foreground hover:bg-primary/80 py-1 px-3 rounded ml-2" onClick={handlePost} disabled={isLoading}>
                    {isLoading ? 'Đang gửi...' : 'Gửi'}
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
    );
}

export default DetailGallery;
