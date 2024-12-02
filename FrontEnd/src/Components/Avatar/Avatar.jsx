import React from 'react';

const Avatar = ({url}) => {
    return (
        <div>
             <div className="flex items-center space-x-2">
                        {/* <div className='w-40 h-40 bg-green-700 text-white flex items-center justify-center rounded-full text-[100px] font-bold'>
                            {props}
                        </div> */}
                        <img src={url} className="w-40 h-40 text-white flex items-center justify-center rounded-full text-[100px] font-bold" />
             </div>
        </div>
    );
};

export default Avatar;