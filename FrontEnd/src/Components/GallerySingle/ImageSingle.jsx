import { Grid2 , Paper  } from '@mui/material';
import { styled } from '@mui/material/styles';
import DetailGallery from '../DetailGallery/DetailGallery';
import DropdownButton from '../DropdownButton/DropdownButton';
const Item = styled(Paper)(({ theme }) => ({
    backgroundColor: '#fff',
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

// Style cho ảnh
const Image = styled('img')({
    width: '100%',  // Đảm bảo ảnh chiếm toàn bộ chiều rộng của phần tử Item
    height: '100%', // Đảm bảo ảnh chiếm toàn bộ chiều cao của phần tử Item
    objectFit: 'cover', // Đảm bảo ảnh phủ toàn bộ mà không bị kéo giãn hoặc mất tỷ lệ
    objectPosition: 'center', // Căn giữa ảnh nếu ảnh có tỷ lệ không giống với phần tử
});

function GallerySingle({content}){
    return (
        <Grid2 container rowSpacing={1}>
            <Grid2 size={6}>
            <Item>
                <Image
                    src={content.dataUrl}
                />
            </Item>
        </Grid2>
            <Grid2 size={6}>
                <Item >
                    <DetailGallery content={content}/>
                    <DropdownButton/>
                </Item>
            </Grid2>
        </Grid2>
    );
}
export default GallerySingle;