// 모든 popover 트리거 버튼을 선택
const popoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]');

// Popover 초기화 (Bootstrap 5)
const popoverList = [...popoverTriggerList].map(popoverTriggerEl => new bootstrap.Popover(popoverTriggerEl));

// "더보기" 버튼 클릭 시 처리하는 이벤트 리스너
document.querySelectorAll('.toggle-button').forEach(function(button) {
    button.addEventListener('click', function(e) {
        // 클릭된 버튼의 popover에 대한 데이터를 가져옴
        const buttonContent = e.target.getAttribute('data-bs-content');  // medications 내용 가져오기

        // popover 인스턴스를 가져옴 (이미 생성된 인스턴스를 사용)
        let popoverInstance = bootstrap.Popover.getInstance(e.target);

        // popover 인스턴스가 없으면 새로 생성, 있으면 토글
        if (!popoverInstance) {
            popoverInstance = new bootstrap.Popover(e.target, {
                content: buttonContent  // popover 내용으로 medications 데이터 사용
            });
        }

        // popover의 표시 여부를 토글
        popoverInstance.toggle();  // 이미 표시되면 숨기고, 숨겨져 있으면 표시
    });
});


document.addEventListener('DOMContentLoaded', function () {
    const searchButton = document.querySelector('.SearchButton');
    const searchInput = document.querySelector('.SearchInput');
    const searchSelect = document.querySelector('.SearchSelect');
    const tableRows = document.querySelectorAll('.messagelist');

    searchButton.addEventListener('click', function () {
        const searchTerm = searchInput.value.toLowerCase();  // 입력한 검색어 소문자로 변환
        const searchOption = searchSelect.value;  // 선택된 검색 옵션 (환자명, 약종류)

        tableRows.forEach(function (row) {
            let rowContent = '';

            if (searchOption === '환자명') {
                // 환자명으로 검색
                rowContent = row.querySelector('span:nth-child(2)').textContent.toLowerCase(); // 환자명
            } else if (searchOption === '약종류') {
                // 약물로 검색
                const visibleMedications = row.querySelector('span:nth-child(4)').textContent.toLowerCase();  // 보이는 약물 이름
                
                // 버튼 요소가 존재하는지 확인하고, 존재하면 전체 약물명 가져오기
                const toggleButton = row.querySelector('button.toggle-button');
                let fullMedications = '';
                if (toggleButton) {
                    fullMedications = toggleButton.getAttribute('data-bs-content').toLowerCase();  // 전체 약물 이름
                }

                // 보이는 약물과 전체 약물 이름을 결합해서 검색
                rowContent = visibleMedications + ' ' + fullMedications;
            }

            // 검색어가 포함된 경우 해당 행 표시, 아니면 숨기기
            if (rowContent.includes(searchTerm)) {
                row.style.display = '';  // 일치하면 표시
            } else {
                row.style.display = 'none';  // 일치하지 않으면 숨김
            }
        });
    });
});

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
    window.location.href = `/prescirpt/history?page=${page}`;
}



