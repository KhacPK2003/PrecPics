import React, { useState } from 'react';
import { Menu, MenuItem, IconButton } from '@mui/material';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import { Link , useParams} from 'react-router-dom'; // Import Link from react-router-dom
const ImageACollection = ({ collectionId, collectionName , userId}) => {
  const [anchorEl, setAnchorEl] = useState(null);
 
  // Mở menu
  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  // Đóng menu
  const handleClose = () => {
    setAnchorEl(null);
  };

  // Xử lý hành động xóa
  const handleDelete = () => {
    if (window.confirm(`Are you sure you want to delete the collection "${collectionName}"?`)) {
      handleClose();  // Đóng menu sau khi xóa
    }
  };

  return (
    <div className="relative w-80 h-80 rounded-lg overflow-hidden shadow-lg hover:shadow-2xl transition-all duration-300">
      {/* Hình ảnh đại diện của bộ sưu tập */}
      <img
        src={"https://images.pexels.com/photos/106399/pexels-photo-106399.jpeg?auto=compress&cs=tinysrgb&w=600"}
        alt={`Collection Image of ${collectionName}`}
        className="w-full h-full object-cover rounded-lg transition-all duration-500 transform hover:scale-105 hover:opacity-80"
      />

      {/* Overlay cho tên bộ sưu tập */}
      <div className="absolute inset-0 bg-black opacity-50 z-10 flex items-center justify-center">
        {/* Chuyển hướng khi click vào tên bộ sưu tập */}
        <Link to={`/collection/${collectionId}`} className="text-white text-2xl font-semibold text-shadow-md px-4 py-2">
          {collectionName}
        </Link>
      </div>

      {/* Menu cho biểu tượng 3 chấm */}
      <div className="absolute top-2 right-2 z-20">
        <IconButton onClick={handleClick}>
          <MoreVertIcon style={{ color: 'white' }} />
        </IconButton>

        {/* Menu với tùy chọn xóa */}
        <Menu
          anchorEl={anchorEl}
          open={Boolean(anchorEl)}
          onClose={handleClose}
        >
          <MenuItem onClick={handleDelete}>Delete</MenuItem>
        </Menu>
      </div>
    </div>
  );
};

export default ImageACollection;
