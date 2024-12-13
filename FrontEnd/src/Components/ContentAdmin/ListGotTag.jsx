import React, { useState, useEffect } from 'react';
import { auth } from '../../firebaseconfig';
import { onAuthStateChanged } from 'firebase/auth';

const ListGotTag = () => {
    const [tags, setTags] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
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
    // Fetch data from the API
    useEffect(() => {
        const fetchTags = async () => {
            try {
                const response = await fetch('http://localhost:8080/public/api/tags');
                const data = await response.json();
                if (data.statusCode === 200) {
                    setTags(data.payload);
                }
            } catch (error) {
                console.error("Error fetching data: ", error);
            }
        };

        fetchTags();
    }, []);

    // Filter tags based on the search term
    const filteredTags = tags.filter(tag => tag.name.toLowerCase().includes(searchTerm.toLowerCase()));

    // Delete tag by id
    const deleteTag = async (id) => {
        try {
            const response = await fetch(`http://localhost:8080/public/api/tags/${id}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },

            });

            if (response.ok) {
                // Remove the deleted tag from the state
                setTags(tags.filter(tag => tag.id !== id));
            } else {
                console.error("Failed to delete tag");
            }
        } catch (error) {
            console.error("Error deleting tag: ", error);
        }
    };

    return (
        <div id='wrapper'>
            <div className='container-fluid'>
                <h1 className="h3 mb-2 text-gray-800">GotTag</h1>
                <p className="mb-4">Trang quản lý hình ảnh trên Gottag cho phép người dùng quản lý bộ sưu tập hình ảnh của mình. Các nhân viên và admin có quyền thực hiện các thao tác như xóa hoặc sửa các bộ sưu tập trong hệ thống, đảm bảo tính bảo mật và sự thuận tiện cho người dùng khi thao tác với các hình ảnh cá nhân.</p>

                <div className="card shadow mb-4">
                    <div className="card-header py-3">
                        <h6 className="m-0 font-weight-bold text-primary mr-auto">Danh sách GotTag</h6>
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
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                />
                            </div>
                            <table className="table table-bordered" id="dataCategoryTable" width="100%" cellSpacing="0">
                                <thead>
                                    <tr>
                                        <th>Id</th>
                                        <th>Tên nhãn</th>
                                        <th>Xóa</th>
                                        <td></td>
                                    </tr>
                                </thead>
                                <tbody>
                                    {filteredTags.map((tag) => (
                                        <tr key={tag.id}>
                                            <td>{tag.id}</td>
                                            <td>{tag.name}</td>
                                            <td>
                                                <button
                                                    onClick={() => deleteTag(tag.id)}
                                                    className="btn btn-danger"
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
        </div>
    );
};

export default ListGotTag;
