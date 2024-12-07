import { Grid2, Paper, Button, TextField, Menu, MenuItem, CircularProgress } from '@mui/material';
import { styled } from '@mui/material/styles';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import React, { useState, useEffect, useCallback } from 'react';
import { auth } from '../../firebaseconfig';
import { onAuthStateChanged } from 'firebase/auth';
import ReactPlayer from 'react-player';
import { ToastContainer, toast } from 'react-toastify'; 
import 'react-toastify/dist/ReactToastify.css'; 
import { useNavigate } from 'react-router-dom';
import DetailGallery from '../DetailGallery/DetailGallery';
import DropdownButton from '../DropdownButton/DropdownButton';

const Item = styled(Paper)(({ theme }) => ({
  backgroundColor: '#fff',
  ...theme.typography.body2,
  padding: theme.spacing(1),
  maxHeight: '100%',
  minHeight: '200px',
  color: theme.palette.text.secondary,
  display: 'flex',
  flexDirection: 'column',
  justifyContent: 'space-between',
  ...theme.applyStyles('dark', {
    backgroundColor: '#1A2027',
  }),
}));

const VideoContainer = styled('div')({
  width: '100%',
  height: '100%',
  objectFit: 'cover',
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
});

function VideoSingle({ content, onDataChange }) {
  const navigate = useNavigate();
  const userId = JSON.parse(localStorage.getItem('userID'));
  const [newCollectionName, setNewCollectionName] = useState('');
  const [anchorEl, setAnchorEl] = useState(null);
  const [userCollections, setUserCollections] = useState([]);
  const [collectionDetails, setCollectionDetails] = useState({});
  const [token, setToken] = useState(null);
  const [isLoading, setIsLoading] = useState(false); // Loading state for collections
  const [openMenuIndex, setOpenMenuIndex] = useState(null); // To handle the active menu

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

  const fetchUserCollections = useCallback(async () => {
    if (!userId) return;

    setIsLoading(true);
    try {
      const userResponse = await fetch(`http://localhost:8080/public/api/users/${userId}`);
      const userData = await userResponse.json();

      if (userData.statusCode === 200) {
        const collections = userData.payload.collections;
        setUserCollections(collections);

        // Fetch collection details for all collections in parallel
        const collectionPromises = collections.map((collection) => fetchCollectionDetails(collection.id));

        const collectionDetailsResults = await Promise.all(collectionPromises);

        const detailsMap = collectionDetailsResults.reduce((acc, { id, details }) => {
          acc[id] = details;
          return acc;
        }, {});

        setCollectionDetails(detailsMap);
      } else {
        console.error('Error fetching collections');
      }
    } catch (error) {
      console.error('API fetch error:', error);
    } finally {
      setIsLoading(false);
    }
  }, [userId]);

  useEffect(() => {
    fetchUserCollections();
  }, [userId, fetchUserCollections]);

  const fetchCollectionDetails = async (collectionId) => {
    try {
      const response = await fetch(`http://localhost:8080/public/api/collections/${collectionId}`);
      const data = await response.json();
      if (data.statusCode === 200) {
        return { id: collectionId, details: data.payload };
      } else {
        throw new Error('Error fetching collection details');
      }
    } catch (error) {
      console.error('Error fetching collection details for collectionId:', collectionId, error);
      return { id: collectionId, details: null };
    }
  };

  const createCollection = useCallback(async (CollectName) => {
    if (!token || !CollectName) return;

    try {
      const response = await fetch('http://localhost:8080/public/api/collections', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ name: CollectName }),
      });

      if (response.ok) {
        toast.success(`Tạo bộ sưu tập ${newCollectionName} thành công!!`);
        setNewCollectionName('');
        fetchUserCollections(); // Refresh collections after creation
      } else {
        throw new Error('Failed to create the collection');
      }
    } catch (err) {
      toast.error('Tạo bộ sưu tập thất bại!');
    }
  }, [newCollectionName, token, fetchUserCollections]);

  const handleCreateCollection = () => {
    if (!userId) {
      navigate('/Login');
      return;
    }
    createCollection(newCollectionName);
  };

  // Add or remove content to/from collection
  const handleCollectionAction = async (collectionId, action) => {
    if (!token) return;

    const url = action === 'add' 
      ? `http://localhost:8080/public/api/collections/${collectionId}/contents/${content.id}`
      : `http://localhost:8080/public/api/collections/${collectionId}/contents/${content.id}`;
    
    const method = action === 'add' ? 'POST' : 'DELETE';

    try {
      const response = await fetch(url, {
        method,
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (response.ok) {
        toast.success(action === 'add' ? 'Nội dung đã được thêm vào bộ sưu tập!' : 'Nội dung đã được xóa khỏi bộ sưu tập!');
        fetchUserCollections(); // Refresh collections after action
      } else {
        toast.error('Thao tác thất bại!');
      }
    } catch (err) {
      toast.error('Thao tác thất bại!');
    }
    onDataChange();
  };

  const handleMenuClick = (event, index) => {
    setOpenMenuIndex(index);
    setAnchorEl(event.currentTarget);
  };

  const handleCloseMenu = () => {
    setAnchorEl(null);
    setOpenMenuIndex(null);
  };

  return (
    <Grid2 container rowSpacing={1}>
      <Grid2 size={6}>
        <Item>
          <VideoContainer>
            <ReactPlayer 
              url={content?.dataUrl || '/233037_small.mp4'} // Dynamically use video URL
              controls={true} 
              width="100%" 
              height="100%" 
              style={{ objectFit: 'cover' }}
            />
          </VideoContainer>
        </Item>
        <div>
          <h5 className="mt-4">Thêm vào bộ sưu tập <i className="fa-regular fa-bookmark"></i></h5>
          <div className="mt-3">
            <TextField
              label="Nhập tên bộ sưu tập"
              variant="outlined"
              fullWidth
              value={newCollectionName}
              onChange={(e) => setNewCollectionName(e.target.value)}
              className="mb-2"
            />
            <Button
              variant="contained"
              color="primary"
              onClick={handleCreateCollection}
              fullWidth
            >
              Tạo
            </Button>
          </div>

          <div className="mt-4">
            <h5>Chọn bộ sưu tập đã tạo</h5>
            <Button
              variant="outlined"
              color="secondary"
              onClick={(event) => setAnchorEl(event.currentTarget)}
              fullWidth
            >
              Chọn bộ sưu tập
            </Button>
            <Menu
              anchorEl={anchorEl}
              open={Boolean(anchorEl)}
              onClose={handleCloseMenu}
            >
              {isLoading ? (
                <MenuItem disabled>
                  <CircularProgress size={24} />
                </MenuItem>
              ) : (
                userCollections.map((collection, index) => {
                  const collectionInfo = collectionDetails[collection.id];
                  const imageCount = collectionInfo ? collectionInfo.inCols.length : 0;
                  const isContentInCollection = collectionInfo && collectionInfo.inCols.some(contentItem => contentItem.contentId === content.id);

                  return (
                    <MenuItem
                      key={collection.id}
                      onClick={() => handleCollectionAction(collection.id, isContentInCollection ? 'remove' : 'add')}
                    >
                      {isContentInCollection ? '(Xóa khỏi bộ sưu tập)' : '(Thêm vào bộ sưu tập)'} {collection.name} (Số lượng: {imageCount})
                      <MoreVertIcon />
                    </MenuItem>
                  );
                })
              )}
            </Menu>
          </div>
        </div>
      </Grid2>

      <Grid2 size={6}>
        <Item>
          <DetailGa llery content={content} onDataChange={onDataChange} />
          <DropdownButton content={content} />
        </Item>
      </Grid2>

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
    </Grid2>
  );
}

export default VideoSingle;
