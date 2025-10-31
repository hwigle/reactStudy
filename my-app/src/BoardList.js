import React, { useState, useEffect } from 'react';
import { Link as RouterLink } from 'react-router-dom'; // 1. Link를 RouterLink로 별명 부여
import axios from 'axios';

// --- 👇 [MUI 컴포넌트 import] ---
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import Divider from '@mui/material/Divider';
import Pagination from '@mui/material/Pagination';
import CircularProgress from '@mui/material/CircularProgress'; // 로딩 아이콘
// --- [MUI import 끝] ---

function BoardList() {
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(true);
  const isLoggedIn = !!localStorage.getItem('jwtToken');
  const [currentPage, setCurrentPage] = useState(0); 
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        const response = await axios.get(`http://localhost:8080/api/board?page=${currentPage}`);
        const pageData = response.data;
        setList(pageData.content); 
        setTotalPages(pageData.totalPages); 
      } catch (e) {
        console.error("목록 데이터 로딩 실패: ", e);
        setList([]); 
        setTotalPages(0); 
      }
      setLoading(false); 
    };

    fetchData(); 
  }, [currentPage]);

  // --- 👇 [페이지네이션 핸들러 변경] ---
  // MUI Pagination 컴포넌트는 1부터 시작하는 페이지 번호를 반환
  const handlePageChange = (event, page) => {
    setCurrentPage(page - 1); // 1부터 시작하는 페이지를 0부터 시작하는 state로 변환
  };
  // (기존 handlePrevPage, handleNextPage 함수는 삭제)
  // --- [핸들러 변경 끝] ---

  // --- 👇 [로딩 중 UI 변경] ---
  if (loading) {
    // 로딩 중일 때 화면 중앙에 원형 로딩 아이콘 표시
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '300px' }}>
        <CircularProgress />
      </Box>
    );
  }
  // --- [로딩 UI 끝] ---

  return (
    // 1. <div> 대신 <Box> 사용 (MUI의 레이아웃용 컴포넌트)
    <Box>
      {/* 2. <h2> 대신 <Typography> (MUI의 텍스트 컴포넌트) */}
      <Typography variant="h4" component="h2" sx={{ mb: 2 }}> {/* mb: 2 = margin-bottom 2단위 */}
        📋 게시판 목록
      </Typography>

      {isLoggedIn && (
        // 3. <button> 대신 MUI <Button>
        <Button 
          component={RouterLink} // React Router의 Link 기능과 연결
          to="/write" 
          variant="contained" // 배경색이 채워진 버튼
          color="primary"   // 파란색
          sx={{ mb: 2 }}      // 아래쪽 여백
        >
          새 글 작성하기
        </Button>
      )}

      {/* 4. <ul> 대신 MUI <List> */}
      <List>
        {list.map(item => (
          // 5. <li> 대신 <ListItem>
          <ListItem key={item.id} disablePadding>
            {/* 6. <Link> 대신 <ListItemButton> (클릭 효과 + Link 기능) */}
            <ListItemButton component={RouterLink} to={`/detail/${item.id}`}>
              {/* 7. 제목과 작성자를 예쁘게 표시해주는 <ListItemText> */}
              <ListItemText 
                primary={item.title} // 큰 글씨 (제목)
                secondary={`작성자: ${item.authorUsername || '익명'}`} // 작은 글씨 (작성자)
              />
            </ListItemButton>
          </ListItem>
        ))}
      </List>

      <Divider sx={{ my: 2 }} /> {/* <hr /> 대신 MUI 구분선 */}

      {/* 8. [페이지네이션 UI 변경] - MUI <Pagination> 컴포넌트 사용 */}
      <Box sx={{ display: 'flex', justifyContent: 'center' }}>
        <Pagination 
          count={totalPages} // 전체 페이지 수
          page={currentPage + 1} // 현재 페이지 (MUI는 1부터 시작)
          onChange={handlePageChange} // 페이지 변경 시 호출될 함수
          color="primary"
        />
      </Box>
      {/* --- [UI 변경 끝] --- */}
      
      {/* (메인으로 돌아가기 버튼은 App.js의 AppBar에 있으니 여기선 생략 가능) */}
    </Box>
  );
}

export default BoardList;
