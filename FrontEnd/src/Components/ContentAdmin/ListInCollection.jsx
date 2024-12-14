import React, { useEffect, useState } from 'react';
import { auth } from '../../firebaseconfig';
import { onAuthStateChanged } from 'firebase/auth';

const InCollectionManager = () => {
    const [contents, setContents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [page, setPage] = useState(1);
    const [size, setSize] = useState(10);

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

    // Fetch data from API
    const fetchContents = async () => {
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8080/public/api/contents/by-type?type=1&page=${page}&size=${size}`);
            if (!response.ok) {
                throw new Error('Failed to fetch data');
            }
            const data = await response.json();
            setContents(data.payload);  // assuming `payload` is where the contents are
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    // Delete content by ID with confirmation
    const deleteContent = async (contentId) => {
        const confirmDelete = window.confirm('Bạn có chắc chắn muốn xóa nội dung này?');
        
        if (!confirmDelete) return;  // nếu người dùng chọn "Cancel", không làm gì cả

        try {
            const response = await fetch(`http://localhost:8080/public/api/contents/delete/${contentId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
            });
            if (!response.ok) {
                throw new Error('Failed to delete content');
            }

            // After deleting, update the list by removing the deleted content
            setContents(prevContents => prevContents.filter(content => content.id !== contentId));
        } catch (err) {
            setError(err.message);
        }
    };

    useEffect(() => {
        fetchContents();
    }, [page, size]);  // Trigger fetch when page or size changes

    // Handle search input
    const handleSearch = (e) => {
        setSearchTerm(e.target.value);
    };

    // Filter contents based on search
    const filteredContents = contents.filter(content =>
        content.name.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div id="wrapper">
            <div className="container-fluid">
                <h1 className="h3 mb-2 text-gray-800">Phòng trưng bày</h1>
                <p className="mb-4">Trang quản lý phòng trưng bày được quản lý bởi nhân viên và admin có thể xóa, sửa bộ sưu tập.</p>

                <div className="card shadow mb-4">
                    <div className="card-header py-3">
                        <h6 className="m-0 font-weight-bold text-primary mr-auto">Danh sách phòng trưng bày</h6>
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
                                    value={searchTerm}
                                    onChange={handleSearch}
                                />
                            </div>
                            {loading ? (
                                <p>Loading...</p>
                            ) : error ? (
                                <p>Error: {error}</p>
                            ) : (
                                <table className="table table-bordered" id="dataCategoryTable" width="100%" cellSpacing="0">
                                    <thead>
                                        <tr>
                                            <th>Id</th>
                                            <th>Mô tả</th>
                                            <th>Nhãn</th>
                                            <th>Ngày tải lên</th>
                                            <th>Lượt thích</th>
                                            <th>Chiều cao</th>
                                            <th>Chiều rộng</th>
                                            <th>Id người dùng</th>
                                            <th>Xóa</th>
                                            <td></td>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {filteredContents.map(content => (
                                            <tr key={content.id}>
                                                <td>{content.id}</td>
                                                <td>{content.description}</td>
                                                <td>{content.tags}</td>
                                                <td>{new Date(content.dateUpload).toLocaleDateString()}</td>
                                                <td>{content.likeds}</td>
                                                <td>{content.height}</td>
                                                <td>{content.width}</td>
                                                <td>{content.userId}</td>
                                                <td>
                                                    <button
                                                        className="btn btn-danger"
                                                        onClick={() => deleteContent(content.id)}
                                                    >
                                                        Xóa
                                                    </button>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default InCollectionManager;
