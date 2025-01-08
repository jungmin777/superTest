let currentPage = 1;
let totalPages = 1; // 서버에서 가져오는 총 페이지 수
let startPage = 1;
let endPage = 10;

function loadPage(page) {
    if (page < 1 || page > totalPages) return;

    // 페이지가 현재 범위의 끝에 도달했을 경우
    if (page > endPage) {
        startPage += 10;
        endPage = Math.min(startPage + 9, totalPages);
    } else if (page < startPage) {
        startPage = Math.max(1, startPage - 10);
        endPage = startPage + 9;
    }

    currentPage = page;
    window.location.href = `/messages/message_home?page=${page}`;
}
