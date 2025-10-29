import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom'; 
import axios from 'axios'; 
import { jwtDecode } from 'jwt-decode';

function BoardDetail() {
  const { boardId } = useParams(); 
  const [post, setPost] = useState(null); 
  const [ loading, setLoading ] = useState(true);
  const [ isAuthor, setIsAuthor ] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    // 현재 로그인한 사용자 이름 확인용 함수
    const getCurrentUsername = () => {
      const token = localStorage.getItem('jwtToken');
      if(token) {
        try {
          const decodedToken = jwtDecode(token); // 토큰 해독
          return decodedToken.sub; // 해독된 정보에서 'sub'(사용자 이름) 반환
        } catch(error) {
          console.error("토큰 디코드실패 : ", error);
          return null;
        }
      }
      return null;
    }

    // 게시글 데이터 가져오는 함수
    const fetchPost = async () => {
      setLoading(true);
      setIsAuthor(false);
      try {
        const response = await axios.get(`http://localhost:8080/api/board/${boardId}`);
        const fetchPost = response.data; // 백엔드에서 온 DTO
        console.log("백엔드 응답 데이터:",fetchPost);
        setPost(fetchPost); 

        // --- [작성자 비교 시작] ---
        const currentUsername = getCurrentUsername(); 

        console.log("현재 로그인 사용자 (토큰):", currentUsername);
        console.log("게시글 작성자 (DTO):", fetchPost ? fetchPost.authorUsername : '게시글 정보 없음');
        
        if (currentUsername && fetchPost && fetchPost.authorUsername === currentUsername) {
           setIsAuthor(true);
        } else {
           // setIsAuthor(false); // (이미 초기값이 false임)
        }
        // --- [작성자 비교 끝] ---

      } catch (e) {
        console.error("상세 데이터 로딩 실패: ", e);
        setPost(null); 
      }
      setLoading(false);
    };

    fetchPost(); // 8. 함수 실행

  // boardId가 변경될 때마다 이 훅을 다시 실행합니다.
  }, [boardId]); 

// --- [삭제(Delete) 로직 추가] ---
const handleDelete = async () => {
  if (window.confirm("정말로 이 게시글을 삭제하시겠습니까?")) {
    try {
      await axios.delete(`http://localhost:8080/api/board/${boardId}`);
      
      alert("게시글이 성공적으로 삭제되었습니다.");
      
      navigate("/list"); 
    } catch (error) {
      console.error("삭제 실패:", error);
      // --- [에러 종류 확인 로직 추가] ---
      if (error.response && error.response.status === 403) {
        alert("이 게시글을 삭제할 권한이 없습니다.");
      } else {
        alert("게시글 삭제에 실패했습니다.");
      }
      // --- [에러 종류 확인 로직 로직 끝] ---
    }
  }
};
// --- [삭제 로직 끝] ---

  if (loading) {
    return <div>로딩 중...</div>;
  }

  if (!post) {
    return <div>해당 게시글을 찾을 수 없습니다.</div>;
  }

  // state에 저장된 post의 내용을 렌더링
  return (
    <div>
      <h2>{post.title}</h2>
      <p>게시글 ID: {post.id}</p>
      <hr />
      <p>{post.content}</p>
      <br />
      
      {/* 버튼(링크) 영역 */}
      <Link to="/list">
        <button>목록</button>
      </Link>
      
      {/* --- [조건부 렌더링] --- */}
      {/* isAuthor 상태가 true일 때만 이 버튼들이 화면에 보임 */}
      {isAuthor && (
        <> {/* 여러 버튼 묶음 */}      
      {/* [수정 버튼] - /update/id 경로로 이동하는 링크 */}
      <Link to={`/update/${boardId}`}>
        <button>수정</button>
      </Link>

      {/* [삭제 버튼] - 클릭 시 handleDelete 함수 실행 */}
      <button onClick={handleDelete}>삭제</button>
      </>
      )}
      {/* --- [조건부 렌더링 끝] --- */}
    </div>
  );
}

export default BoardDetail;