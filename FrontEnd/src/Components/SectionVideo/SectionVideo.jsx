import ReactPlayer from 'react-player';
import React ,{useState , useEffect} from 'react';
import { AiFillHeart, AiOutlineHeart } from 'react-icons/ai';
import VideoSingle from '../GallerySingle/VideoSingle';
import { Button, Dialog, DialogContent, DialogTitle } from '@mui/material';

function SectionVideo({content,onDataChange}){
    const [open, setOpen] = useState(false); // Trạng thái mở Dialog
    const [isHovered, setIsHovered] = useState(false); // Trạng thái di chuột vào video
  
    // Hàm mở Dialog
    const handleOpen = () => {
      setOpen(true);
    };

    // Hàm đóng Dialog
    const handleClose = () => {
      setOpen(false);
    };
  
    // Hàm xử lý sự kiện khi người dùng di chuột vào video
    const handleMouseEnter = () => {
      setIsHovered(true);
    };

    const handleMouseLeave = () => {
        setIsHovered(false);
      };
  

    return (
         <div className="w-full sm:w-1/2 md:w-1/3 lg:w-1/4 p-2">
                <div className="gallery-item relative h-100"
                       onMouseEnter={handleMouseEnter}
                       onMouseLeave={handleMouseLeave}
                       onClick={handleOpen} // Mở Dialog khi nhấp vào video
                       style={{ cursor: 'pointer', width: '100%', position: 'relative' }}
                >
                    <ReactPlayer 
                        url={content.dataUrl} 
                        playing={isHovered} // Phát video khi di chuột vào
                        controls={false} // Tắt controls nếu không cần
                        width="100%" 
                        height="100%" 
                    />
                </div>
                <Dialog open={open} onClose={handleClose} maxWidth="lg" fullWidth >
                            <DialogTitle onClose={handleClose}>Video</DialogTitle>
                            <DialogContent dividers>
                                <VideoSingle content={content} onDataChange = {onDataChange} />
                            </DialogContent>
                </Dialog>
        </div>
    )
    
}
export default SectionVideo