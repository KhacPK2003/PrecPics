import React from 'react';

const Button = ({props, color}) => {
    return (
        <div >
          <button className={`text-xl text-white px-4 py-2 rounded hover:bg-black mx-2 ${color}`}>
                {props}
          </button>
        </div>
    );
};

export default Button;