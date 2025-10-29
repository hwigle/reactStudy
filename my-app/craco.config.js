// craco.config.js
const tailwindcss = require('tailwindcss');
const autoprefixer = require('autoprefixer');

module.exports = {
  style: {
    postcss: { // 'postcssOptions' 대신 'postcss' 사용
      plugins: [
        tailwindcss, // require() 대신 변수 사용
        autoprefixer, // require() 대신 변수 사용
      ],
    },
  },
};