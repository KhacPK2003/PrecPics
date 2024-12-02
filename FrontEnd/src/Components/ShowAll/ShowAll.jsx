import React from 'react';
import SectionImage from "../SectionImage/SectionImage";
import SectionVideo from "../SectionVideo/SectionVideo";
function ShowAll({user}){
    const images = user.contents.filter(post => post.type === true); // type 1 is for images
    const videos = user.contents.filter(post => post.type === false); // other types are videos
    // const contents = user.contents.map((post) => {
    //     if (post.type === true) {
    //         return { component: <SectionImage content={post} />, type: 'image' }; // Add an image component for images
    //     } else {
    //         return { component: <SectionVideo content={post} />, type: 'video' }; // Add a video component for videos
    //     }
    // });
    // console.log(images);
    return (
        <>
             <section id="gallery" className="gallery">
                <div className="w-full px-4">
                    <div className="flex flex-wrap justify-center">
                            {images.map((post, index) => (
                                <SectionImage key={index} content={post} />
                            ))}
                            {/* {videos.map((post, index) => (
                                <SectionVideo key={index} content={post} />
                        ))} */}
                    </div>
                </div>
             </section>
        </>
    );
}   
export default ShowAll;