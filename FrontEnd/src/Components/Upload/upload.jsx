import React , {useState} from 'react'


const inputClasses = 'border border-zinc-300 rounded-lg p-2 w-full'
const labelClasses = 'block text-sm font-medium text-zinc-700'
const buttonClasses = 'bg-blue-500 text-primary-foreground hover:bg-primary/80 rounded-lg p-2 w-full'

function Upload(){
    const [media, setMedia] = useState(null);
    const [mediaType, setMediaType] = useState('');

    const handleMediaChange = (event) => {
        const file = event.target.files[0];
        if (file) {
            const mediaUrl = URL.createObjectURL(file);
            setMedia(mediaUrl);
            setMediaType(file.type.startsWith('video/') ? 'video' : 'image');
        }
    };

    return (
        <>
            <form className="bg-card p-6 rounded-lg shadow-md">
                <div className="mb-4">
                    <label className={labelClasses}>Tiêu đề</label>
                    <input type="text"  placeholder="Nhập tiêu đề" className={inputClasses} />
                </div>
                <div className="mb-4">
                    <label className={labelClasses}>Tiêu đê gắn thẻ </label>
                    <input type="text" placeholder="Nhập tag" className={inputClasses} />
                </div>
                <div className="mb-4">
                    <label className={labelClasses}>Địa điểm</label>
                    <input type="text" placeholder="Nhập địa điểm" className={inputClasses} />
                </div>
                <div className="mb-4">
                    <label className={labelClasses}>Chọn tệp</label>
                    <input type="file" accept="image/*,video/*" onChange={handleMediaChange} />
                    {media && (
                        mediaType === 'video' ? (
                            <video controls style={{ marginTop: '10px', maxWidth: '300px' }}>
                                <source src={media} type={mediaType} />
                            </video>
                        ) : (
                            <img src={media} alt="Preview" style={{ marginTop: '10px', maxWidth: '300px' }} />
                        )
                    )}
                </div>
                <button type="submit" className={buttonClasses}>
                        Tải lên
                </button>
            </form>
        </>
    );
}
export default Upload;