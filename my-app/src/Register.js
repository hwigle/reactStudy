import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link as RouterLink } from 'react-router-dom'; // Link 추가

// --- 👇 [MUI 컴포넌트 import] ---
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Paper from '@mui/material/Paper';
import Link from '@mui/material/Link'; // MUI Link 추가
// --- [MUI import 끝] ---

function Register() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    username: '',
    password: ''
  });

  // 폼 입력값 변경 핸들러
  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({
      ...form,
      [name]: value
    });
  };

  // 회원가입 제출 핸들러
  const handleSubmit = async (e) => {
    e.preventDefault(); 
    if (!form.username || !form.password) {
      alert("아이디와 비밀번호를 모두 입력해주세요.");
      return;
    }

    try {
      await axios.post('http://localhost:8080/api/auth/register', form);
      alert("회원가입이 성공적으로 완료되었습니다. 로그인 페이지로 이동합니다.");
      navigate('/login'); 
    } catch (error) {
      console.error("회원가입 실패:", error);
      alert("회원가입에 실패했습니다. (아이디 중복 등)");
    }
  };

  return (
    // Paper로 폼을 감싸고 중앙 정렬
    <Paper sx={{ p: 4, maxWidth: '400px', margin: 'auto', mt: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom align="center">
        🧑‍💻 회원가입
      </Typography>

      <Box component="form" onSubmit={handleSubmit} sx={{ mt: 2 }}>
        <TextField
          label="사용자 아이디"
          variant="outlined"
          fullWidth
          margin="normal"
          name="username"
          value={form.username}
          onChange={handleChange}
          required
        />
        <TextField
          label="비밀번호"
          variant="outlined"
          fullWidth
          margin="normal"
          type="password"
          name="password"
          value={form.password}
          onChange={handleChange}
          required
        />
        <Button
          type="submit"
          variant="contained"
          color="primary"
          size="large"
          fullWidth
          sx={{ mt: 2, mb: 2 }} // 위아래 여백
        >
          회원가입
        </Button>

        {/* 로그인 링크 */}
        <Typography variant="body2" align="center">
          이미 계정이 있으신가요?{' '}
          <Link component={RouterLink} to="/login" underline="hover">
            로그인
          </Link>
        </Typography>
      </Box>
    </Paper>
  );
}

export default Register;
