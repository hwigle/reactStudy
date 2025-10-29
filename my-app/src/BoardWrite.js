import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom'; // 1. 페이지 이동을 위한 훅

function BoardWrite() {
    // 2. React Router의 페이지 이동 훅
    const navigate = useNavigate();

    // 3. 폼 데이터를 관리핧 state(title, content)
    const [form, setForm] = useState({
        title: '',
        content:''
    });

    // 4. input/textarea 값이 바뀔 때마다 state를 업데이트하는 함수
    const handleChange = (e) => {
        // e.target.name(input의 name)과 e.target.value(입력값)를 가져옴
        const {name, value} = e.target;
        setForm({
            ...form, // 기존 form 객체 복사
            [name] : value // [name] (title 또는 content) 필드를 새 value로 덮어쓰기
        });
    }

    // 5. 제출 버튼 클릭 시 실행될 함수
    const handleSubmit = async (e) => {
        // 6. 폼의 기본 동작(새로고침) 방지 (필수)
        e.preventDefault();

        // 간단한 유효성 검사
        if(!form.title || !form.content) {
            alert("제목과 내용을 모두 입력해주세요.");
            return;
        }

        try {
            // 7. axios.post로 Spring Boot API에 form 데이터를 전송
            //    (form 객체를 JSON으로 자동 변환해서 보냄)
            const response = await axios.post('http://localhost:8080/api/board', form);

            // 8. 글쓰기 성공 시, 서버가 반환한 새 글의 id를 이용해 상세 페이지로 이동
            alert("게시글이 성공적으로 등록되었습니다.");
            navigate(`/detail/${response.data.id}`); // (예 : /detail/4)
        } catch(error) {
            console.error("게시글 등록 실패 : " , error);
            alert("게시글 등록에 실패했습니다.");
        }
    };

    return(
        <div>
            <h2>📋 새 게시글 작성</h2>
            {/* 9. 폼 제출 시 handleSubmit 함수 실행 */}
            <form onSubmit={handleSubmit}>
                <div>
                    <label>제목 : </label>
                    {/* 10. state와 input을 연결(Controlled Component) */}
                    <input 
                        type="text"
                        name="title"            // (handleChange에서 e.target.name으로 사용됨)
                        value={form.title}      // (state의 값을 표시)
                        onChange={handleChange} // (값이 바뀔 때마다 handleChange 실행)
                        placeholder="제목을 입력하세요"
                    />
                </div>
                <div>
                    <label>내용: </label>
                    <textarea
                        name="content" // (handleChange에서 e.target.name으로 사용됨)
                        value={form.content} // (state의 값을 표시)
                        onChange={handleChange} // (값이 바뀔 때마다 handleChange 실행)
                        placeholder="내용을 입력하세요"
                    />
                </div>
                <button type="submit">제출</button>
            </form>
        </div>
    );
}

export default BoardWrite;