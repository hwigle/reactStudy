import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom'; // 1. useParams, useNavigate import

function BoardUpdate() {
  const { boardId } = useParams(); // 2. URL에서 수정할 ID 가져오기
  const navigate = useNavigate();
  
  // 3. 폼 데이터 State 
  const [form, setForm] = useState({
    title: '',
    content: ''
  });

  // 4. [중요] 수정 폼을 채우기 위해 기존 데이터를 가져오는 로직
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/api/board/${boardId}`);
        // 5. 받아온 기존 데이터로 폼 state를 채웁니다.
        setForm(response.data); 
      } catch (error) {
        console.error("데이터 로드 실패:", error);
      }
    };
    fetchData();
  }, [boardId]); // boardId가 바뀔 때마다 다시 데이터를 가져옴

  // 6. 폼 입력값 변경 핸들러 
  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({
      ...form, // 기존 form 객체 복사
      [name]: value // 바뀐 부분만 덮어쓰기
    });
  };

  // 7. 폼 제출(수정) 핸들러
  const handleSubmit = async (e) => {
    e.preventDefault(); // 기본 폼 제출(새로고침) 방지
    if (!form.title || !form.content) {
      alert("제목과 내용을 모두 입력해주세요.");
      return;
    }

    try {
      // 8. [중요] POST가 아닌 PUT 요청. URL에 ID 포함.
      //    'form' 객체를 JSON으로 전송합니다.
      await axios.put(`http://localhost:8080/api/board/${boardId}`, form);
      
      alert("게시글이 성공적으로 수정되었습니다.");
      
      // 9. 수정 완료 후, 상세 페이지로 다시 이동
      navigate(`/detail/${boardId}`); 
    } catch (error) {
      console.error("수정 실패:", error);
      // --- [에러 종류 확인 로직 추가] ---
      // 1. error.response가 있고, 그 안에 status 코드가 있는지 확인
      if (error.response && error.response.status === 403) {
        // 2. 403 Forbidden 에러일 경우, 권한 없음 메시지 표시
        alert("이 게시글을 수정할 권한이 없습니다.");
      } else {
        // 3. 그 외 다른 에러일 경우, 일반적인 실패 메시지 표시
        alert("게시글 수정에 실패했습니다.");
      }
      // --- [에러 종류 확인 로직 끝] ---
    }
  };

  // 10. JSX 
  return (
    <div>
      <h2>📋 게시글 수정 (ID: {boardId})</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>제목: </label>
          <input
            type="text"
            name="title"
            value={form.title} // state와 연결
            onChange={handleChange}
          />
        </div>
        <div>
          <label>내용: </label>
          <textarea
            name="content"
            value={form.content} // state와 연결
            onChange={handleChange}
          />
        </div>
        <button type="submit">수정 완료</button>
      </form>
    </div>
  );
}

export default BoardUpdate;