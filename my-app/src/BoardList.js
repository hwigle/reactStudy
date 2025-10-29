import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';

function BoardList() {
  const [list, setList] = useState([]); // 게시글 '목록' 데이터 (배열)
  const [loading, setLoading] = useState(true);

  // --- [로그인 상태 확인 로직 추가] ---
  // localStorage에서 'jwtToken'을 읽어옴 
  // 토큰이 있으면 loggedIn은 true, 없으면 false가 됩니다.
  const isLoggedIn = !!localStorage.getItem('jwtToken'); 
  // --- [확인 로직 끝] ---

  // --- [페이지네이션 State 추가] ---
  const [currentPage, setCurrentPage] = useState(0); // 현재 페이지 번호 (0부터 시작)
  const [totalPages, setTotalPages] = useState(0); // 전체 페이지 수
  // --- [페이지네이션 State 추가 끝] ---

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true); // (선택) 데이터 요청 시작 시 로딩 상태로 설정
      try {
        const response = await axios.get(`http://localhost:8080/api/board?page=${currentPage}`);

        const pageData = response.data;
        setList(pageData.content); // 실제 게시글 목록 (배열)
        setTotalPages(pageData.totalPages); // 전체 페이지 수
      } catch (e) {
        console.error("목록 데이터 로딩 실패: ", e);
        setList([]); // 에러 시 목록 비우기
        setTotalPages(0); // 에러 시 페이지 수 0으로
      }
      setLoading(false); 
    };

    fetchData(); 
    // 의존성 배열에 'currentPage' 추가 -> currentPage state가 바뀔 때마다 useEffect가 다시 실행됨
  }, [currentPage]);

  // --- [페이지 이동 함수 추가] ---
  const handlePrevPage = () => {
    // 현재 페이지가 0보다 클 때만 이전 페이지로 이동
    setCurrentPage(prev => Math.max(prev - 1, 0));
  };

  const handleNextPage = () => {
    // 현재 페이지가 마지막 페이지보다 작을 때만 다음 페이지로 이동
    setCurrentPage(prev => Math.min(prev + 1, totalPages - 1));
  };
  // --- [페이지 이동 함수 추가 끝] ---

  if (loading) {
    return <div>로딩 중...</div>;
  }

  return (
    <div>
      {/* --- [조건부 렌더링으로 버튼 감싸기] --- */}
      {/* isLoggedIn이 true일 때만 <Link> 버튼을 렌더링 */}
      {isLoggedIn && (
        <> {/* 여러 요소를 감싸기 위한 Fragment */}
          <Link to="/write">
            <button>새 글 작성하기</button>
          </Link>
          <hr />
        </>
      )}
      {/* --------------------- */}
      {/* 게시글 목록 */}
      <ul>
        {list.map(item => ( 
          <li key={item.id}>
            <Link to={`/detail/${item.id}`}>{item.title}</Link>
          </li>
        ))}
      </ul>
        {/* --- [페이지네이션 UI 추가] --- */}
        <div style={{ textAlign: 'center', marginTop: '20px' }}>
          {/* 이전 버튼: 첫 페이지면 비활성화(disabled) */}
          <button onClick={handlePrevPage} disabled={currentPage === 0}>
            이전
          </button>

          {/* 현재 페이지 / 전체 페이지 표시 (페이지 번호는 0부터 시작하므로 +1) */}
          <span style={{ margin: '0 10px' }}>
            {currentPage + 1} / {totalPages}
          </span>

          {/* 다음 버튼: 마지막 페이지면 비활성화 */}
          <button onClick={handleNextPage} disabled={currentPage >= totalPages - 1}>
            다음
          </button>
        </div>
        {/* --- [UI 추가 끝] --- */}
        
      <hr style={{ marginTop: '20px' }}/>
      <Link to="/">메인으로 돌아가기</Link>
    </div>
  );
}

export default BoardList;