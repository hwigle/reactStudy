import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

// --- 👇 [MUI 컴포넌트 import] ---
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField'; // Input/Textarea 대체
import Button from '@mui/material/Button';
import Paper from '@mui/material/Paper'; // 폼을 감쌀 종이(카드)
// --- [MUI import 끝] ---

function BoardWrite() {
    const navigate = useNavigate();

    const [form, setForm] = useState({
        title: '',
        content: ''
    });

    // 4. input/textarea 값이 바뀔 때마다 state를 업데이트하는 함수
    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm({
            ...form,
            [name]: value
        });
    }

    // 5. 제출 버튼 클릭 시 실행될 함수 (로직 100% 동일)
    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!form.title || !form.content) {
            alert("제목과 내용을 모두 입력해주세요.");
            return;
        }
        try {
            const response = await axios.post('http://localhost:8080/api/board', form);
            alert("게시글이 성공적으로 등록되었습니다.");
            navigate(`/detail/${response.data.id}`);
        } catch (error) {
            console.error("게시글 등록 실패 : ", error);
            alert("게시글 등록에 실패했습니다.");
        }
    };

    // --- 👇 [취소 버튼 핸들러 추가] ---
    const handleCancel = () => {
        // 'navigate(-1)'은 브라우저의 '뒤로 가기'와 동일하게 동작합니다.
        navigate(-1);
    };
    // --- [핸들러 추가 끝] ---

    return (
        <Paper sx={{ p: 4, maxWidth: '700px', margin: 'auto' }}>
            <Typography variant="h4" component="h2" gutterBottom>
                📋 새 게시글 작성
            </Typography>

            <Box component="form" onSubmit={handleSubmit} sx={{ mt: 2 }}>
                <TextField
                    label="제목"
                    variant="outlined"
                    fullWidth
                    margin="normal"
                    type="text"
                    name="title"
                    value={form.title}
                    onChange={handleChange}
                    required
                />

                <TextField
                    label="내용"
                    variant="outlined"
                    fullWidth
                    margin="normal"
                    name="content"
                    value={form.content}
                    onChange={handleChange}
                    multiline
                    rows={10}
                    required
                />

                {/* --- 👇 [버튼 영역 수정] --- */}
                {/* 1. 버튼들을 감싸는 Box를 만들고, flex를 이용해 오른쪽 정렬 */}

                <Box sx={{ mt: 2, display: 'flex', justifyContent: 'flex-end' }}>
                    {/* 취소 버튼 */}
                    <Button
                        variant="outlined" // 테두리만 있는 버튼
                        color="secondary"  // 회색 계열 색상
                        onClick={handleCancel}
                        sx={{ mr: 1 }}  // 오른쪽 여백을 설정하여 버튼 간격 조정
                    >
                        취소
                    </Button>

                    {/* 등록 버튼 */}
                    <Button
                        type="submit"
                        variant="contained"
                        color="primary"
                        size="large"
                        sx={{ ml: 0 }} // 왼쪽 여백을 0으로 설정하여 버튼 붙이기
                    >
                        등록
                    </Button>
                </Box>
                {/* --- [버튼 영역 수정 끝] --- */}
            </Box>
        </Paper>
    );
}

export default BoardWrite;






