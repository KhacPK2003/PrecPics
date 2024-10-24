
function SectionImage(){
    return (
        <section id="gallery" className="gallery">
            <div className="w-full px-4">
                <div className="flex flex-wrap justify-center">
                    <div className="w-full sm:w-1/2 md:w-1/3 lg:w-1/4 p-2">
                        <div className="gallery-item h-full">
                            <img src="src/assets/image.png" className="w-full h-auto rounded" alt="" />
                        </div>
                    </div>
                    <div className="w-full sm:w-1/2 md:w-1/3 lg:w-1/4 p-2">
                        <div className="gallery-item h-full">
                            <img src="src/assets/image.png" className="w-full h-auto rounded" alt="" />
                        </div>
                    </div>
                    <div className="w-full sm:w-1/2 md:w-1/3 lg:w-1/4 p-2">
                        <div className="gallery-item h-full">
                            <img src="src/assets/image.png" className="w-full h-auto rounded" alt="" />
                        </div>
                    </div>
                </div>
            </div>
        </section>

    );
}
export default SectionImage;