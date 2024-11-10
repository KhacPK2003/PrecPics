import SectionImage from "../SectionImage/SectionImage";
import SectionVideo from "../SectionVideo/SectionVideo"; 
const imageSections = [
    <SectionImage key={1} />,
    <SectionImage key={2} />,
    <SectionImage key={3} />,
    <SectionImage key={4} />,
    <SectionImage key={5} />,
    <SectionImage key={6} />,
  ];

  const videoSections = [
    <SectionVideo key={1} />,
    <SectionVideo key={2} />,
    <SectionVideo key={3} />,
    <SectionVideo key={4} />,
    <SectionVideo key={5} />,
  ];
function Main({showvideo = false}){
    return (
        <>
            <section id="gallery" className="gallery">
                <div className="w-full px-4">
                    <div className="flex flex-wrap justify-center">
                            {showvideo ? videoSections : imageSections}
                    </div>
                </div>
            </section>
        </>
    );
}
export default Main;