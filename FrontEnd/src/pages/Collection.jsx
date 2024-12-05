import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import SectionImage from "../Components/SectionImage/SectionImage"; // Import SectionImage
import SectionVideo from '../Components/SectionVideo/SectionVideo'; // Import SectionVideo

const Collection = () => {
  const { id } = useParams(); // Get id from URL
  const [collection, setCollect] = useState(null); // State to hold the collection data
  const [contents, setContents] = useState([]); // State to hold the content data
  const [loading, setLoading] = useState(true); // Loading state
  const [change , setChange] = useState(false);
  useEffect(() => {
    // console.log(changes);
    // Fetch the collection based on the ID from the URL
    fetch(`http://localhost:8080/public/api/collections/${id}`)
      .then((response) => response.json())
      .then(({ payload }) => {
        setCollect(payload); // Set collection to state
        // Fetch content for each item in the collection (using contentId from collection.inCols)
        Promise.all(payload.inCols.map((item) => getContent(item.contentId))) // Using contentId from collection.inCols
          .then((contentData) => {
            setContents(contentData); // Set the fetched content data to state
            setLoading(false); // Update loading state after data is fetched
          })
          .catch((error) => {
            console.error("Error fetching content:", error);
            setLoading(false);
          });
      })
      .catch((error) => {
        console.error("Error fetching collection:", error);
        setLoading(false);
      });
  }, [id,change]); // Dependency array ensures this runs when the `id` changes

  const handleDataChange = () => {
    setChange((prev) => !prev)
};

  const getContent = (contentId) => {
    return fetch(`http://localhost:8080/public/api/contents/${contentId}`)
      .then((response) => response.json())
      .then(({ payload }) => payload);
  };

  // Check if loading is still true
  if (loading) {
    return <div>Loading...</div>; // Show loading indicator while fetching data
  }

  return (
    <div className="bg-white min-h-screen">
      <div className="container mx-auto px-4 py-4 mt-[60px]">
        <h1 className="text-center text-[70px] font-semibold">
          {collection?.name || 'Collection Name'}
        </h1>
      </div>

      <div className="container mx-auto px-4 py-4">
        <div className="flex items-center justify-between">
          <div className="flex-grow"></div>
          <span className="text-2xl">{contents.length} kết quả</span>
        </div>
      </div>

      <section id="gallery" className="gallery">
            <div className="w-full px-4">
                <div className="flex flex-wrap justify-center">
                {contents.length > 0 && contents.map((content, index) => {
                    // Ensure content is not null or undefined
                    if (!content){
                        return (
                            <div key={index} className="text-center text-lg text-gray-500">
                                Không có ảnh
                            </div>
                        );
                    }
                    return content.type === true ? (
                        <SectionImage key={index} content={content} onDataChange={handleDataChange}/>
                    ) : content.type === "video" (
                        <SectionVideo key={index} content={content} />
                    )
                    })}
                </div>
            </div>
      </section>
    </div>
  );
};

export default Collection;
