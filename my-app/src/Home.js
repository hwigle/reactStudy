import React from 'react';
import { Link as RouterLink } from 'react-router-dom';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';

function Home() {
  return (
    // 👇 Paper 컴포넌트로 감싸기
    <Paper 
      elevation={3} 
      sx={{ 
        p: { xs: 2, md: 4 }, // 반응형 패딩
        mt: 2 // 상단 여백
      }}
    >
      <Typography variant="h4" component="h2" gutterBottom>
        🏠 메인 페이지
      </Typography>
      <Typography variant="body1" paragraph>
        React 라우터 실습 및 Spring Boot 연동 프로젝트입니다.
      </Typography>
      
      {/* 👇 <Link> 대신 MUI <Button> 사용 */}
      <Button 
        variant="contained" 
        color="primary" 
        component={RouterLink} // react-router-dom의 Link로 작동
        to="/list"
      >
        게시판 목록 보러가기
      </Button>
    </Paper>
  );
}

export default Home;

