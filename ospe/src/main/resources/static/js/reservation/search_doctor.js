const selectoption = document.getElementsByName('searchselectoption')[0];
const selectinputhidden = document.getElementsByName('searchselect')[0];
const searchinput = document.querySelector("input[name='searchinput']");

selectoption.addEventListener('change',()=>{
	selectinputhidden.value = selectoption.value;
	
	if(selectinputhidden.value == "예약일"){
		searchinput.placeholder = 'ex) 2024-05-05';
	}
	
	if(selectinputhidden.value != "예약일"){
				searchinput.placeholder = 'ex) 2024-05-05';
	}
});

const searchbutton = document.getElementsByClassName('SearchButton')[0];
searchbutton.addEventListener('click',()=>{
	searchinput = searchinput.value;
	
    // 만약 검색 입력값이 비어있지 않으면 폼을 제출
    if (searchcomma.trim() !== "") {
        form.submit(); // 폼 제출
    } else {
        alert("검색어를 입력해주세요.");
    }
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
    window.location.href = `/search_doctor?page=${page}`;
}

