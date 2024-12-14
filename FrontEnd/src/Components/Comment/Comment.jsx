import React, { useState } from 'react';
import { BsThreeDotsVertical } from 'react-icons/bs';
import { useNavigate } from 'react-router-dom';

function Comment({ user, comment, timestamp, commentId, onDelete, onEdit }) {
  const [showMenu, setShowMenu] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [editedComment, setEditedComment] = useState(comment);  // State để lưu bình luận sửa đổi
  const date = new Date(timestamp);
  const navigate = useNavigate();
  const formattedDateTime = date.toLocaleString('vi-VN', {
    year: 'numeric',
    month: 'numeric',
    day: 'numeric',
    hour: 'numeric',
    minute: 'numeric',
    hour12: false, // 24-hour time format
  });

  // Toggle the visibility of the delete and edit buttons
  const handleMenuToggle = () => {
    setShowMenu(!showMenu);
  };

  // Handle the delete action
  const handleDelete = () => {
    onDelete(commentId);  // Call the onDelete function passed as prop
    setShowMenu(false);  // Hide the menu after deletion
  };

  // Handle the edit action
  const handleEdit = () => {
    if (isEditing) {
      // If editing is finished, pass the new comment to the parent
      onEdit(commentId, editedComment);  // Call onEdit to pass the updated comment
    }
    setIsEditing(!isEditing);  // Toggle between editing and viewing modes
  };

  // Handle input change for the edited comment
  const handleInputChange = (e) => {
    setEditedComment(e.target.value);  // Update the edited comment value
  };
  const handleClick = () =>{
      navigate(`/about/${user.id}`);
  }

  return (
    <div className="flex mb-4 relative">
      <img src={user.avatar} alt={user.name} className="w-10 h-10 rounded-full mr-3" />
      <div className="flex-1 bg-gray-200 p-3 rounded-lg shadow-sm">
        <div className="font-medium text-gray-800 flex justify-between">
          <button onClick={handleClick}>{user.name}</button>
          {/* BsThreeDotsVertical Icon */}
          <div className="relative">
            <button
              onClick={handleMenuToggle}
              className="text-gray-500 hover:text-gray-700"
            >
              <BsThreeDotsVertical className="text-xl" />
            </button>
            {/* Edit and Delete buttons appear when clicked */}
            {showMenu && (
              <div className="absolute right-0 top-0 mt-8 space-y-1">
                <button
                  onClick={handleEdit}
                  className="block text-blue-500 hover:bg-gray-100 w-full text-left text-sm px-2 py-1 rounded-md"
                >
                  {isEditing ? 'Lưu' : 'Sửa'} {/* Toggle between 'Sửa' and 'Lưu' */}
                </button>
                <button
                  onClick={handleDelete}
                  className="block text-red-500 hover:bg-gray-100 w-full text-left text-sm px-2 py-1 rounded-md"
                >
                  Xóa
                </button>
              </div>
            )}
          </div>
        </div>
        {/* Show either input for editing or plain text */}
        {isEditing ? (
          <textarea
            value={editedComment}
            onChange={handleInputChange}
            className="text-gray-700 mt-1 w-full p-2 border rounded-md"
            rows={3}
          />
        ) : (
          <p className="text-gray-700 mt-1 whitespace-pre-wrap">{comment}</p>
        )}
        <div className="text-gray-500 text-xs mt-2">{formattedDateTime}</div>
      </div>
    </div>
  );
}

export default Comment;
