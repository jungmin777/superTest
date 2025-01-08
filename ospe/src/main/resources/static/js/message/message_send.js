
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".reply-btn").forEach((button) => {
        button.addEventListener("click", (event) => {
            const receiver = event.target.getAttribute("data-receiver");
            openPopup(receiver);
        });
    });
});

function openPopup(receiver) {
    const popupHTML = `
        <div class="popup-overlay" id="popup-overlay">
            <div class="popup-content">
                <button type="button" class="popup-close-btn" onclick="closePopup()">✖</button>
                <h2 class="popup-header">메시지 보내기</h2>
                <form class="popup-form" id="messageForm">
                    <div class="popup-row">
                        <label class="popup-label" for="popTitle">제목</label>
                        <input type="text" id="popTitle" class="popup-input" placeholder="제목 입력">
                    </div>
                    <div class="popup-row">
                        <label class="popup-label" for="popReceiver">받는 사람</label>
                        <input type="text" id="popReceiver" class="popup-input" placeholder="받는 사람 입력" value="${receiver || ''}">
                    </div>
                    <div class="popup-row">
                        <label class="popup-label" for="popContent">내용</label>
                        <textarea id="popContent" class="popup-textarea" placeholder="메시지를 입력하세요"></textarea>
                    </div>
                    <button type="button" class="popup-btn" onclick="sendMessage()">전송</button>
                </form>
            </div>
        </div>
    `;
    document.body.insertAdjacentHTML("beforeend", popupHTML);

    // 팝업창 표시
    const popupOverlay = document.getElementById("popup-overlay");
    popupOverlay.classList.add("show");

    // 닫기 버튼 이벤트 등록
    initCloseButtons();
}

function sendMessage() {
    const title = document.getElementById('popTitle').value;
    const content = document.getElementById('popContent').value;
    const receiver = document.getElementById('popReceiver').value;

    if (!receiver || !title || !content) {
        alert("모든 필드를 입력해야 합니다.");
        return;
    }

    fetch('/messages/message_post', {
        method: 'post',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken // CSRF 토큰과 헤더 이름 추가
        },
        body: JSON.stringify({
            title: title,
            content: content,
            receiver: receiver
        })
    })
    .then(response => {
        if (response.ok) {
            alert("전송되었습니다!");
            closePopup();
        } else {
            return response.text().then(message => {
                if (message === "self_message") {
                    alert("자신에게 메시지를 보낼 수 없습니다.");
                } else {
                    alert("전송에 실패하였습니다!");
                }
            });
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert("서버와의 통신 중 오류가 발생했습니다.");
    });
}

function initCloseButtons() {
    const closeButtons = document.querySelectorAll(".close-popup-btn");
    closeButtons.forEach(button => {
        button.addEventListener("click", closePopup);
    });
}

function closePopup() {
    const popupOverlay = document.getElementById("popup-overlay");
    if (popupOverlay) popupOverlay.remove();
}
