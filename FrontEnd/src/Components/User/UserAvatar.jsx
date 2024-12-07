import * as React from 'react';
import Box from '@mui/material/Box';
import MenuItem from '@mui/material/MenuItem';
import Avatar from '@mui/material/Avatar';
import Menu from '@mui/material/Menu';
import Tooltip from '@mui/material/Tooltip';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import { useNavigate } from 'react-router-dom';
import Button from '@mui/material/Button'; // Added Button for Admin

const settings = ['Trang cá nhân', 'Logout'];

function UserAvatar({ User, Logout }) {
    const [anchorElUser, setAnchorElUser] = React.useState(null);
    const navigate = useNavigate();
    const isAdmin = JSON.parse(localStorage.getItem('isAdmin'));
    console.log(isAdmin);
    const handleOpenUserMenu = (event) => {
        setAnchorElUser(event.currentTarget);
    };

    const handleCloseUserMenu = (setting) => {
        const get = localStorage.getItem('userID');
        const id = JSON.parse(get);

        if (setting === 'Trang cá nhân') {
            navigate(`/about/${id}`);
        }

        if (setting === 'Logout') {
            Logout();
        }

        setAnchorElUser(null);
    };

    return (
        <>
            <Box sx={{ flexGrow: 0, display: 'flex', alignItems: 'center' }}>
                {/* Conditionally render Admin button if the user is an admin */}
                {(isAdmin === true ? (
                    <Button
                        variant="contained"
                        color="primary"
                        sx={{ marginRight: 2 }} // Apply margin to the right to space out the button
                        onClick={() => navigate('/dashboard')}
                    >
                        Admin
                    </Button>) : (<></>)
                )}

                <Tooltip title="Mở thanh cài đặt">
                    <IconButton onClick={handleOpenUserMenu} sx={{ p: 0 }}>
                        <Avatar alt="Remy Sharp" src="" />
                        <Typography variant="body1" className="text-white text-left ml-2">
                            Xin chào {User.displayName}!!!
                        </Typography>
                    </IconButton>
                </Tooltip>


                <Menu
                    sx={{ mt: '45px' }}
                    id="menu-appbar"
                    anchorEl={anchorElUser}
                    anchorOrigin={{
                        vertical: 'top',
                        horizontal: 'right',
                    }}
                    keepMounted
                    transformOrigin={{
                        vertical: 'top',
                        horizontal: 'right',
                    }}
                    open={Boolean(anchorElUser)}
                    onClose={handleCloseUserMenu}
                >
                    {settings.map((setting) => (
                        <MenuItem key={setting} onClick={() => handleCloseUserMenu(setting)}>
                            <Typography sx={{ textAlign: 'center' }}>{setting}</Typography>
                        </MenuItem>
                    ))}
                </Menu>
            </Box>
        </>
    );
}

export default UserAvatar;
