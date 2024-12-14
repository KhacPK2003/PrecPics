import React, { useState, useEffect } from 'react';
import { auth } from '../../firebaseconfig';
import { onAuthStateChanged } from 'firebase/auth';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css'; // Import Toastify CSS

const ListUser = () => {
  const [users, setUsers] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [token, setToken] = useState(null);

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
  
  useEffect(() => {
    if (token === null) return;
    fetchUsers();
  }, [token]);

  const fetchUsers = async () => {
    try {
      const response = await fetch('http://localhost:8080/public/api/users', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      });
      if (!response.ok) {
        throw new Error('Xóa thất bại!!');
      }
      const data = await response.json();
      setUsers(data.payload); // Assuming API returns a list of users
    } catch (error) {
      console.error('Error fetching users:', error);
    }
  };

  // Function to delete a user by ID with confirmation using Toastify
  const deleteUser = async (userId) => {
    toast.info('Nhấn vô thông báo này nếu bạn chắc chắn muốn xóa.', {
      position: "top-center",
      autoClose: false,
      closeOnClick: true,
      draggable: true,
      onClick: () => {
        toast.promise(
          fetch(`http://localhost:8080/public/api/users/${userId}`, {
            method: 'DELETE',
            headers: {
              'Content-Type': 'application/json',
              'Authorization': `Bearer ${token}`
            }
          }).then((response) => {
            if (!response.ok) {
              throw new Error("Xóa thất bại");
            }
            setUsers(users.filter(user => user.id !== userId));
            return "Xóa thành công!";
          }),
          {
            pending: "Đang xóa người dùng...",
            success: "Xóa thành công!",
            error: "Xóa thất bại"
          }
        );
      }
    });
  };

  const handleSearchChange = (event) => {
    setSearchQuery(event.target.value);
  };

  const filteredUsers = users.filter(user =>
    user.userName.toLowerCase().includes(searchQuery.toLowerCase()) ||
    user.fullName.toLowerCase().includes(searchQuery.toLowerCase()) ||
    user.email.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div id="wrapper">
      <div className="container-fluid">
        <h1 className="h3 mb-2 text-gray-800">Tài khoản người dùng</h1>
        <p className="mb-4">Trang quản lý tài khoản người dùng dành cho nhân viên và admin có thể xóa tài khoản người dùng.</p>

        <div className="card shadow mb-4">
          <div className="card-header py-3">
            <h6 className="m-0 font-weight-bold text-primary mr-auto">Danh sách người dùng</h6>
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
                    <th>User Name</th>
                    <th>Full Name</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Avatar url</th>
                    <th>Xóa</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredUsers.map(user => (
                    <tr key={user.id}>
                      <td>{user.id}</td>
                      <td>{user.userName}</td>
                      <td>{user.fullName}</td>
                      <td>{user.email}</td>
                      <td>{user.isAdmin ? 'Admin' : 'User'}</td>
                      <td>
                        <img src={user.avatarUrl} alt={user.userName} width="50" height="50" />
                      </td>
                      <td>
                        <button
                          className="btn btn-danger"
                          onClick={() => deleteUser(user.id)}
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

      <ToastContainer />
    </div>
  );
};

export default ListUser;
