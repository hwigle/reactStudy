import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function Login() {
  const navigate = useNavigate();
  const [form, setForm] = useState({username: '', password: ''});
  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({
      ...form,
      [name]: value
    });
  };

  // '로그인' 버튼 클릭 시 실행될 함수
  const handleSubmit = async (e) => {
    e.preventDefault(); 
    if (!form.username || !form.password) {
      alert("아이디와 비밀번호를 모두 입력해주세요.");
      return;
    }

    try {
      // 1. [중요] Spring Boot의 /api/auth/login API 호출
      const response = await axios.post('http://localhost:8080/api/auth/login', form);
      
      // --- [토큰 저장 로직 추가] ---
      // 1-1) 백엔드가 응답(response.data)으로 보내준 JWT 토큰 가져오기
      const token = response.data; 

      // 1-2) 브라우저의 localStorage에 'jwtToken'이라는 이름으로 토큰을 저장
      //    (localStorage는 브라우저를 닫아도 데이터가 유지됩니다.)
      localStorage.setItem('jwtToken', token); 
      
      // 1-3) axios의 기본 헤더(defaults.headers) 설정을 변경합니다.
      //    -> 앞으로 모든 axios 요청 시 자동으로 'Authorization' 헤더에 토큰을 포함시킵니다.
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      // --- [토큰 저장 로직 끝] ---
      
      alert("로그인 성공!"); 
      // 2. 로그인 성공 시, 메인 페이지(/)로 이동
      navigate('/'); 

    } catch (error) {
      // 3. 에러 처리 (예: 아이디 없음, 비밀번호 틀림)
      console.error("로그인 실패:", error);
      alert("아이디 또는 비밀번호가 일치하지 않습니다.");

      // --- [로그인 실패 시 토큰/헤더 제거] ---
      localStorage.removeItem('jwtToken'); // 혹시 남아있을지 모를 토큰 제거
      delete axios.defaults.headers.common['Authorization']; // axios 헤더에서도 제거
      // --- [제거 로직 끝] --
    }
  };

  return (
    <div>
      <h2>🔑 로그인</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>사용자 아이디: </label>
          <input
            type="text"
            name="username"
            value={form.username}
            onChange={handleChange}
          />
        </div>
        <div>
          <label>비밀번호: </label>
          <input
            type="password"
            name="password"
            value={form.password}
            onChange={handleChange}
          />
        </div>
        <button type="submit">로그인</button>
      </form>
    </div>
  );
}

export default Login;