import React from 'react';
import { Routes, Route, Link as RouterLink, useNavigate } from 'react-router-dom'; // 1. Link를 RouterLink로 별명 부여
import axios from 'axios';

// --- [MUI 컴포넌트 import] ---
import Container from '@mui/material/Container'; // 전체 레이아웃을 감싸는 컨테이너
import AppBar from '@mui/material/AppBar';     // 상단 내비게이션 바
import Toolbar from '@mui/material/Toolbar';    // AppBar 안에 내용물을 넣는 영역
import Typography from '@mui/material/Typography'; // 텍스트 (h1, p 등)
import Button from '@mui/material/Button';       // 버튼
import Box from '@mui/material/Box';             // div와 비슷한 레이아웃용 박스
// --- [MUI import 끝] ---

import Home from './Home';
import BoardList from './BoardList';
import BoardDetail from './BoardDetail';
import BoardWrite from './BoardWrite';
import BoardUpdate from './BoardUpdate';
import Register from './Register';
import Login from './Login';

function App() {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('jwtToken');
    delete axios.defaults.headers.common['Authorization'];
    alert("로그아웃 되었습니다.");
    navigate('/');
  };

  const isLoggedIn = !!localStorage.getItem('jwtToken');

  return (
    // 1. 가장 바깥을 <Container>로 감싸서 적절한 여백과 최대 너비를 줍니다.
    <Container maxWidth="lg">
      {/* 2. 상단 내비게이션 바 (AppBar) */}
      <AppBar position="static" color="default" elevation={1} sx={{ mb: 4 }}> 
        <Toolbar>
          {/* 3. 제목 (Toolbar 안에서 왼쪽 정렬) */}
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            React 프로젝트
          </Typography>

          {/* 4. 내비게이션 링크들을 MUI <Button>으로 변경 */}
          {/* 'component={RouterLink}'로 React Router의 Link와 연결 */}
          <Button component={RouterLink} to="/" color="inherit">홈</Button>
          <Button component={RouterLink} to="/list" color="inherit">게시판</Button>

          {/* 5. 조건부 내비게이션 (MUI Button 사용) */}
          {!isLoggedIn && (
            <>
              <Button component={RouterLink} to="/register" color="inherit">회원가입</Button>
              <Button component={RouterLink} to="/login" color="inherit">로그인</Button>
            </>
          )}
          {isLoggedIn && (
            <Button onClick={handleLogout} variant="contained" color="error">
              로그아웃
            </Button>
          )}
        </Toolbar>
      </AppBar>

      {/* 6. <Routes> 영역을 <Box>로 감싸서 위쪽 여백(mt: 4)을 줌 */}
      <Box sx={{ mt: 4 }}>
        <Routes>
          <Route path="/" element={<Home />} /> 
          <Route path="/list" element={<BoardList />} />
          <Route path="/detail/:boardId" element={<BoardDetail />} />
          <Route path="/write" element={<BoardWrite />} />
          <Route path="/update/:boardId" element={<BoardUpdate />} />
          <Route path="/register" element={<Register />} />
          <Route path="/login" element={<Login />} />
        </Routes>
      </Box>

    </Container>
  );
}

export default App;

