import React, { useState, useEffect } from 'react';
import SectionImage from "../SectionImage/SectionImage";
import SectionVideo from "../SectionVideo/SectionVideo"; 
import SearchBar from "../SearchBar/SearchBar";
import { auth } from '../../firebaseconfig';
import { onAuthStateChanged } from 'firebase/auth';

function Main({ showvideo = false }) {
  const Type = showvideo ? 1 : 0;  // Determine the type based on showvideo
  const [data, setData] = useState([]); // To hold content data
  const [page, setPage] = useState(1);  // To handle current page
  const [loading, setLoading] = useState(false);  // To track loading state
  const [change , setChange] = useState(false);
  // const [hasMore, setHasMore] = useState(true);  // To check if there's more data
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

  const fetchData = async () => {
    if (loading) return;  // Don't fetch if already loading or no more data

    setLoading(true);  // Start loading
    try {
      const response = await fetch(`http://localhost:8080/public/api/contents/by-type?type=${Type}&page=${page}&size=10`);
      const { payload } = await response.json();
      
      // Check if there's more data to load
      // setHasMore(payload.length > 0);

      // Append new data to existing data if it's not the first page
      setData((prevData) => (page === 1 ? payload : [...prevData, ...payload]));
    } catch (error) {
      console.error('Error fetching data:', error);
    } finally {
      setLoading(false);  // End loading
    }
  };
  useEffect(() => {
    fetchData();
  }, [Type, page , change]); // Trigger effect on Type or page change

  // Function to handle search request
  const handSearch = async (approximates) => {
    try {
      // console.log(approximates.length);
      if(approximates.length === 0){
          fetchData(); return;
      }
      const url = new URL('http://localhost:8080/public/api/contents/search/fuzzy');
      url.searchParams.append('approximates', approximates);
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`, // Thêm token vào header
        },
      });

      if (!response.ok) {
        throw new Error("Tìm kiếm thất bại!!");
      }

      // Lấy kết quả tìm kiếm từ API
      const result = await response.json();
      // console.log(result);
      setData(result.payload);  // Cập nhật kết quả tìm kiếm

    } catch (error) {
      console.error("Tìm kiếm thất bại!!", error);
    }
  };
  const dataChange = () => {
    setChange((prev) => !prev);
  }
  return (
    <>
      <SearchBar handSearch={handSearch} />
      <section id="gallery" className="gallery">
        <div className="w-full px-4">
          <div className="flex flex-wrap justify-center">
            {showvideo 
              ? data.map((post) => (
                  <SectionVideo key={post.id} content={post} onDataChange={dataChange} />
                ))
              : data.map((post) => (
                  <SectionImage key={post.id} content={post} onDataChange ={dataChange}/>
                ))
            }
          </div>
          {/* Loading Spinner */}
          {loading && (
            <div className="loading-spinner">
              <span>Loading...</span>
            </div>
          )}
        </div>
      </section>
    </>
  );
}

export default Main;
