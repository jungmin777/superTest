// 환자 찾기위한 ajax

const patientInput = document.getElementById('searchPatient');
let findName = "";
const patientTable = document.getElementById('patientTable').getElementsByTagName('tbody')[0];
patientInput.addEventListener('keydown',(e)=>{ // (e) 를 사용하여 event 를 받아옴
	if(e.key === "Enter") { // e의 키가 엔터일 때 실행하겠다
//		select.innerHTML = ""; // select 안의 내용물 비우기
		findName = patientInput.value; // 검색하려고하는 이름값 받아오기 
//		ajax 기법임
		patientTable.innerHTML = "";
		$.ajax({
			url: "getPatient", // 이게 mapping 임
			type: "get", // 이건 Get, Post 
			data: {username:findName}, // 우리가 보낼 데이터     data: {변수명:보낼데이터}   의 방식으로 적음     findName 은 9줄에 있음
			success: (data) =>{ // 성공했을 경우에 return 받은 데이터를

				if (data.length === 0) {
				    // 검색 결과가 없으면
				    const row = patientTable.insertRow();
				    const cell = row.insertCell(0);
				    cell.colSpan = 4; // 컬럼 span 설정
				    cell.innerText = "해당하는 환자가 없습니다.";
				    cell.style.textAlign = "center";
				} else {
				    // 검색 결과가 있으면
				    for (const a of data) {
				        const row = patientTable.insertRow(); // 새로운 행 추가

				        // 각 셀 추가
				        const usernameCell = row.insertCell(0);
				        const nameCell = row.insertCell(1);
				        const birthDateCell = row.insertCell(2);
				        const radioCell = row.insertCell(3);

				        // 셀 내용 설정
				        usernameCell.innerText = a.username;
				        nameCell.innerText = a.name;
				        birthDateCell.innerText = a.birthDate;

				        // 라디오 버튼 추가
				        const radio = document.createElement("input");
				        radio.type = "radio";
				        radio.name = "patientSelect";
				        radio.value = a.id;
				        radio.dataset.name = a.name;
				        radio.dataset.birth = a.birthDate;

				        radioCell.appendChild(radio);
				    }
					if (data.length >= 4) {
                        patientTableWrapper.style.maxHeight = "300px";  // 스크롤 생성
                        patientTableWrapper.style.overflowY = "auto";  // 세로 스크롤 활성화
                    } else {
                        patientTableWrapper.style.maxHeight = "";  // 스크롤 숨기기
                        patientTableWrapper.style.overflowY = "";  // 스크롤 숨기기
                    }
				} // gpt 한테 물어본 코드 table 로 바꾸는 법

			},
			error: ()=>{} // 에러가 났을 경우 할 행동인데 딱히 만들지 않음
		});
	}
})
// 환자 찾기위한 ajax 끝


let selectedPatientName = null;
let selectedPatientId = null;
let selectedPatientBirth = null;

// 환자 테이블의 라디오 버튼에 이벤트 리스너 추가
patientTable.addEventListener('change', (event) => {
    if (event.target.type === "radio") { // 라디오 버튼인지 확인
        const selectedRadio = event.target; // 선택된 라디오 버튼
        selectedPatientId = selectedRadio.value; // 환자의 ID
        selectedPatientName = selectedRadio.dataset.name; // 환자의 이름
        selectedPatientBirth = selectedRadio.dataset.birth; // 환자의 생년월일
    }
});

// 작성 버튼 이벤트
const goBtn = document.getElementById('writeBtn');
goBtn.addEventListener('click', () => {
    if (selectedPatientName && selectedPatientId) { // 선택된 환자 정보가 있을 경우
        location.href = `/insertPrescriptView/${selectedPatientId}`; // 페이지 이동
    } else {
        alert("환자를 선택해주세요."); // 선택하지 않은 경우 경고 메시지
    }
});

goBtn.addEventListener('click',()=>{
	if(patientName != null && patientId != null){ // null 체크의 이유는 선택 안한채로 작성하려고 하면 못하게 하려고
		location.href="/insertPrescriptView/" + patientId; // 페이지 이동
	}
})