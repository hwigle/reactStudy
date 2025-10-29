import React from 'react';
import { Routes, Route, Link, useNavigate } from 'react-router-dom'; 
import axios from 'axios';
import Home from './Home';
import BoardList from './BoardList';
import BoardDetail from './BoardDetail';
import BoardWrite from './BoardWrite';
import BoardUpdate from './BoardUpdate';
import Register from './Register';
import Login from './Login';

function App() {
  const navigate = useNavigate();

  // --- [로그아웃 함수 추가] ---
  const handleLogout = () => {
    // 1. localStorage에서 토큰 삭제
    localStorage.removeItem('jwtToken');
    // 2. axios 기본 헤더에서 토큰 삭제
    delete axios.defaults.headers.common['Authorization'];
    alert("로그아웃 되었습니다.");
    // 3. 메인 페이지 이동
    navigate('/');
  }
  // --- [로그아웃 함수 끝] ---

  // --- [로그인 상태 확인] ---
  const isLoggedIn = !!localStorage.getItem('jwtToken');
  // --- [로그인 상태 확인 끝] ---

  return (
    <div className="container mx-auto p-4 bg-red-100">
      <h1 className="text-3xl font-bold text-blue-600 mb-4"> 
        React 프로젝트
      </h1>
      {/* --- [공통 내비게이션 영역] --- */}
      <nav className="p-2 mb-4 border-b border-gray-300 flex gap-4">
        <Link to="/" className="hover:text-blue-500">홈</Link>
        <Link to="/list" className="hover:text-blue-500">게시판</Link>

      {/* --- [조건부 내비게이션] --- */}
        {/* 로그인 안 했을 때 */}
        {!isLoggedIn && (
          <>
            <Link to="/register" className="hover:text-blue-500">회원가입</Link>
            <Link to="/login" className="hover:text-blue-500">로그인</Link>
          </>
        )}
        {/* 로그인 했을 때 */}
        {isLoggedIn && (
          <button onClick={handleLogout} className="bg-red-500 text-white px-2 py-1 rounded hover:bg-red-600">
            로그아웃
          </button>
        )}
        {/* ------------------------- */}
      </nav>
      <hr />

      {/* <Routes> 영역: URL 경로에 따라 이 안의 컴포넌트만 교체됩니다. */}
      <Routes>
        <Route path="/" element={<Home />} /> 
        <Route path="/list" element={<BoardList />} />

        <Route path="/detail/:boardId" element={<BoardDetail />} />
        <Route path="/write" element={<BoardWrite />} />
        <Route path="/update/:boardId" element={<BoardUpdate />} />

        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />

      </Routes>

    </div>
  );
}

export default App;