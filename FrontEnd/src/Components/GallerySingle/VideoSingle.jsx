import { Grid2 , Paper  } from '@mui/material';
import { styled } from '@mui/material/styles';
import DetailGallery from '../DetailGallery/DetailGallery';
import DropdownButton from '../DropdownButton/DropdownButton';
import ReactPlayer from 'react-player';
import React , {useState , useEffect} from 'react';
const Item = styled(Paper)(({ theme }) => ({
    backgroundColor: '#fff',
    border:'none',
    ...theme.typography.body2,
    padding: theme.spacing(1),
    maxHeight: '100%',
    minHeight: '200px',
    color: theme.palette.text.secondary,
    display: 'flex', // Sử dụng flexbox
    flexDirection: 'column', // Đặt chiều hướng là cột
    justifyContent: 'space-between', // Căn giữa nội dung theo chiều dọc
    ...theme.applyStyles('dark', {
      backgroundColor: '#1A2027',
    }),
  }));
function VideoSingle(){
    const [playTime, setPlayTime] = useState(0);

    const handleProgress = (state) => {
        setPlayTime(state.playedSeconds);
    }
    return (
        <Grid2 container rowSpacing={1}>
            <Grid2 size={6}>
                <Item >
                <ReactPlayer 
                        url="/233037_small.mp4" 
                        controls={true} 
                        onProgress={handleProgress} 
                        height='100%' 
                        width='100%' 
                        style={{
                            objectFit: 'cover', 
                        }}
                    />
                </Item>
            </Grid2>
            <Grid2 size={6}>
                <Item >
                    <DetailGallery />
                    {/* <DropdownButton/> */}
                </Item>
            </Grid2>
        </Grid2>
    );
}
export default VideoSingle;