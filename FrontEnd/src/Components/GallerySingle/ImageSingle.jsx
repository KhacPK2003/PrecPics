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
function GallerySingle(){
    return (
        <Grid2 container rowSpacing={1}>
            <Grid2 size={6}>
                <Item >
                    <img
                        src="src/assets/image.png"
                        className="w-full h-full object-cover rounded transition-transform duration-300"
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
export default GallerySingle;