import React from 'react';
import { Link } from 'react-router-dom'; // 👈 <a> 태그 대신 <Link>를 import

function Home() {
  return (
    <div>
      <h1>🏠 메인 페이지</h1>
      <p>React 라우터 실습입니다.</p>
      
      {/* a href="/list"가 아닙니다! */}
      <Link to="/list">게시판 목록 보러가기</Link>
    </div>
  );
}

export default Home;