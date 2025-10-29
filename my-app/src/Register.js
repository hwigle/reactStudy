import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom'; // 1. 페이지 이동을 위한 훅

function Register() {
  const navigate = useNavigate(); // navigate 함수 생성

  // 2. 폼 데이터를 관리할 state (username, password)
  const [form, setForm] = useState({
    username: '',
    password: ''
  });

  // 3. input 값 변경 핸들러 (BoardWrite와 100% 동일)
  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({
      ...form,
      [name]: value
    });
  };

  // 4. '회원가입' 버튼 클릭 시 실행될 함수
  const handleSubmit = async (e) => {
    e.preventDefault(); // 폼 기본 동작(새로고침) 방지

    if (!form.username || !form.password) {
      alert("아이디와 비밀번호를 모두 입력해주세요.");
      return;
    }

    try {
      // 5. [중요] Spring Boot의 /api/auth/register API 호출
      await axios.post('http://localhost:8080/api/auth/register', form);
      
      alert("회원가입이 성공적으로 완료되었습니다. 로그인 페이지로 이동합니다.");
      
      // 6. 회원가입 성공 시, 로그인 페이지로 이동
      navigate('/login'); 

    } catch (error) {
      // 7. 에러 처리 (예: 사용자 이름 중복)
      console.error("회원가입 실패:", error);
      if (error.response && error.response.data) {
        // (Spring에서 RuntimeException을 던지면 에러 메시지가 복잡할 수 있음)
        alert(error.response.data.message || "회원가입에 실패했습니다.");
      } else {
        alert("회원가입에 실패했습니다.");
      }
    }
  };

  return (
    <div>
      <h2>🧑‍💻 회원가입</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>사용자 아이디: </label>
          <input
            type="text"
            name="username" // (e.target.name)
            value={form.username} // (state와 연결)
            onChange={handleChange}
            placeholder="아이디 입력"
          />
        </div>
        <div>
          <label>비밀번호: </label>
          <input
            type="password" // (비밀번호는 type="password"로)
            name="password" // (e.target.name)
            value={form.password} // (state와 연결)
            onChange={handleChange}
            placeholder="비밀번호 입력"
          />
        </div>
        <button type="submit">회원가입</button>
      </form>
    </div>
  );
}

export default Register;