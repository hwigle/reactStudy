import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';

// --- 👇 [MUI 컴포넌트 import] ---
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Paper from '@mui/material/Paper';
import ButtonGroup from '@mui/material/ButtonGroup';
import CircularProgress from '@mui/material/CircularProgress'; // 로딩 스피너
// --- [MUI import 끝] ---

function BoardUpdate() {
  const { boardId } = useParams(); 
  const navigate = useNavigate();
  
  // 1. 로딩 상태 state 추가
  const [loading, setLoading] = useState(true); 
  const [form, setForm] = useState({
    title: '',
    content: ''
  });

  // 2. [useEffect 수정] - 로딩 상태 추가
  useEffect(() => {
    setLoading(true); // 👈 로딩 시작
    const fetchData = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/api/board/${boardId}`);
        setForm(response.data); 
      } catch (error) {
        console.error("데이터 로드 실패:", error);
      }
      setLoading(false); // 👈 로딩 끝
    };
    fetchData();
  }, [boardId]); 

  // 3. 폼 입력값 변경 핸들러 (동일)
  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({
      ...form, 
      [name]: value 
    });
  };

  // 4. 폼 제출(수정) 핸들러 (동일)
  const handleSubmit = async (e) => {
    e.preventDefault(); 
    if (!form.title || !form.content) {
      alert("제목과 내용을 모두 입력해주세요.");
      return;
    }
    try {
      await axios.put(`http://localhost:8080/api/board/${boardId}`, form);
      alert("게시글이 성공적으로 수정되었습니다.");
      navigate(`/detail/${boardId}`); 
    } catch (error) {
      console.error("수정 실패:", error);
      if (error.response && error.response.status === 403) {
        alert("이 게시글을 수정할 권한이 없습니다.");
      } else {
        alert("게시글 수정에 실패했습니다.");
      }
    }
  };

  // 5. 취소 버튼 핸들러 (동일)
  const handleCancel = () => {
    navigate(-1); // 뒤로 가기
  };

  // 6. [로딩 UI 추가]
  if (loading) {
    // Paper 중앙에 로딩 스피너 표시
    return (
      <Paper sx={{ p: 4, maxWidth: '700px', margin: 'auto', textAlign: 'center' }}>
        <CircularProgress />
      </Paper>
    );
  }

  // 7. [JSX 수정] (BoardWrite와 99% 동일)
  return (
      <Paper sx={{ p: 4, maxWidth: '700px', margin: 'auto' }}>
            <Typography variant="h4" component="h2" gutterBottom>
                📋 게시글 수정 (ID: {boardId})
            </Typography>

            <Box component="form" onSubmit={handleSubmit} sx={{ mt: 2 }}>
                <TextField
                    label="제목"
                    variant="outlined"
                    fullWidth
                    margin="normal"
                    name="title"
                    // 8. [중요] null 값 방지
                    value={form.title || ''} 
                    onChange={handleChange}
                    required
                />

                <TextField
                    label="내용"
                    variant="outlined"
                    fullWidth
                    margin="normal"
                    name="content"
                    // 8. [중요] null 값 방지
                    value={form.content || ''} 
                    onChange={handleChange}
                    multiline
                    rows={10}
                    required
                />
            
            {/* 9. 버튼 영역 (BoardWrite와 동일) */}
            <Box sx={{ mt: 2, display: 'flex', justifyContent: 'flex-end' }}>
                <ButtonGroup variant="contained">
                    <Button 
                        color="secondary"
                        onClick={handleCancel}
                    >
                      취소
                    </Button>
                    <Button 
                        type="submit" 
                        color="primary"
                        size="large"
                    >
                      수정
                    </Button>
                </ButtonGroup>
            </Box>
            </Box>
        </Paper>
  );
}

export default BoardUpdate;
