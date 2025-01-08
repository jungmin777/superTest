function toggleAllCheckboxes(source) {
    const checkboxes = document.querySelectorAll('.message-checkbox');
    checkboxes.forEach(checkbox => {
        checkbox.checked = source.checked;
    });
}

function deleteMessages() {
    const selectedCheckboxes = document.querySelectorAll(".message-checkbox:checked");
    if (selectedCheckboxes.length === 0) {
        alert("삭제할 메시지를 선택해주세요.");
        return;
    }

    const messageIds = Array.from(selectedCheckboxes).map(checkbox => checkbox.value);

    fetch('/messages/message_delete', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken // CSRF 토큰 추가
        },
        body: JSON.stringify({ messageIds })
    })
    .then(response => {
        if (response.ok) {
            alert("선택된 메시지가 삭제되었습니다.");
            location.reload(); // 페이지 새로고침
        } else {
            alert("메시지 삭제 중 오류가 발생했습니다.");
        }
    })
    .catch(error => {
        console.error("Error:", error);
        alert("서버와의 통신 중 문제가 발생했습니다.");
    });
}

/*function deleteMessages() {
    // 선택된 체크박스에서 메시지 ID 수집
    const checkboxes = document.querySelectorAll('.message-checkbox:checked');
    const messageIds = Array.from(checkboxes).map(checkbox => checkbox.value);

    if (messageIds.length === 0) {
        alert("삭제할 메시지를 선택하세요.");
        return;
    }

    // 삭제 요청 전송
    fetch('/messages/message_delete', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken // CSRF 토큰이 필요하다면 추가
        },
        body: JSON.stringify({ messageIds })
    })
    .then(response => {
        if (response.ok) {
            alert("삭제되었습니다.");
            location.reload(); // 페이지 새로고침
        } else {
            alert("삭제에 실패했습니다.");
        }
    })
    .catch(error => console.error('Error:', error));
}*/