import React, { useState, useEffect } from 'react';
import { auth } from '../../firebaseconfig';
import { onAuthStateChanged } from 'firebase/auth';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css'; // Import Toastify CSS

const ListCollection = () => {
  const [collections, setCollections] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [token, setToken] = useState(null);
  const [users, setUsers] = useState([]);  // Store user data

  // Fetch token on auth state change
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

  // Fetch collections and users from the API
  useEffect(() => {
    if (token === null) return;
    fetchCollections();
  }, [token]);

  const fetchCollections = async () => {
    try {
      const response = await fetch('http://localhost:8080/public/api/users', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error('Failed to fetch users');
      }

      const data = await response.json();
      setUsers(data.payload);  // Save user data in state

      // Flatten the collections array from all users and add userName
      const allCollections = data.payload.flatMap((user) => 
        user.collections.map(collection => ({
          ...collection,
          userName: user.userName,  // Add userName to collection
        }))
      );
      
      setCollections(allCollections); // Set collections to state
    //   toast.success('Collections fetched successfully!'); // Success toast
    } catch (error) {
      console.error('Error fetching collections:', error);
    //   toast.error('Error fetching collections'); // Error toast
    }
  };

  // Function to delete a collection by ID
  const deleteCollection = async (collectionId) => {
    if (window.confirm('Are you sure you want to delete this collection?')) {
      try {
        const response = await fetch(`http://localhost:8080/public/api/collections/${collectionId}`, {
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
          },
        });

        if (response.ok) {
          // If successful, remove the collection from the state
          setCollections(collections.filter((collection) => collection.id !== collectionId));
          toast.success('Xóa bộ sưu tập thành công!'); // Success toast
        } else {
          toast.error('Xóa thất bại!!'); // Error toast
        }
      } catch (error) {
        // toast.error('Error deleting collection'); // Error toast
      }
    }
  };

  // Function to handle search input change
  const handleSearchChange = (event) => {
    setSearchQuery(event.target.value);
  };

  // Filtered collections based on search query
  const filteredCollections = collections.filter((collection) =>
    collection.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div id="wrapper">
      <div className="container-fluid">
        <h1 className="h3 mb-2 text-gray-800">Bộ sưu tập</h1>
        <p className="mb-4">Trang quản lý bộ sưu tập của người dùng dành được quản lý bởi nhân viên và admin có thể xóa, sửa bộ sưu tập.</p>

        <div className="card shadow mb-4">
          <div className="card-header py-3">
            <h6 className="m-0 font-weight-bold text-primary mr-auto">Danh sách bộ sưu tập</h6>
          </div>
          <div className="card-body">
            <div className="table-responsive">
              <div className="flex items-center space-x-2 mb-3">
                <label htmlFor="searchInput" className="text-lg font-semibold mt-1">Search:</label>
                <input
                  type="text"
                  id="searchInput"
                  className="w-1/5 h-10 p-2 border rounded"
                  placeholder="Tìm kiếm..."
                  value={searchQuery}
                  onChange={handleSearchChange}
                />
              </div>

              <table className="table table-bordered" id="dataCategoryTable" width="100%" cellSpacing="0">
                <thead>
                  <tr>
                    <th>Id</th>
                    <th>Tiêu đề</th>
                    <th>Id người dùng</th>
                    <th>Tên người dùng</th> {/* Display the user's name here */}
                    <th>Xóa</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredCollections.map((collection) => (
                    <tr key={collection.id}>
                      <td>{collection.id}</td>
                      <td>{collection.name}</td>
                      <td>{collection.userId}</td>
                      {/* Displaying the userName of the owner */}
                      <td>{collection.userName}</td>
                      <td>
                        <button
                          className="btn btn-danger"
                          onClick={() => deleteCollection(collection.id)}
                        >
                          Xóa
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      <ToastContainer /> {/* Toast container for showing toasts */}
    </div>
  );
};

export default ListCollection;
