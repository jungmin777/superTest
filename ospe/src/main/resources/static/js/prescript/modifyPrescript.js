// insertPrescript.js 에 주석이 다 있읍니다.
// 방법도 거의 동일합니다.


// 오늘 날짜 칸에 넣기
const today = new Date();
let date = today.getFullYear() + "년 " + (today.getMonth()+1) + "월 " + today.getDate() + "일";
document.getElementById('today').innerText = date;

document.getElementsByName('preDate')[0].value = today.getFullYear() + "-" + (today.getMonth()+1) + "-" + today.getDate();

// 추가 버튼
document.getElementById('plusBtn').addEventListener('click', ()=>{
    const originalTr = document.getElementById('medicTr');
	const newTr = originalTr.cloneNode(true);
	const classes = ['meds', 'othm', 'odhm', 'takeD', 'howEat'];
	const inputNames = ['','oneTimeHowMany', 'oneDayHowMany', 'takeDate', 'howToEat'];
	const inType = ['text', 'text', 'number', 'number', 'text'];
	const optText = ['식사 전', '식사 후 바로', '식사 후 15분 이내','식사 후 30분 이내','식사 후 30분 이후'];
	
	for(let a = 0; a < 5; a++){
		const ctd = document.createElement('td');
		ctd.classList.add(classes[a]);
		if(a != 4){
			const input = document.createElement('input');
			input.name = inputNames[a];
			input.type = inType[a];
			if(a == 0){input.classList.add("medics");}
			input.placeholder = "작성";
			ctd.appendChild(input);
			newTr.appendChild(ctd);
		} else {
			const select = document.createElement('select');
			const input = document.createElement('input');
			for(let k = 0; k < 5; k++){
				const opt = document.createElement('option');
				opt.innerText = optText[k];
				select.appendChild(opt);
			}
			input.type = "hidden";
			input.name = inputNames[4];
			ctd.appendChild(select);
			ctd.appendChild(input);
			newTr.appendChild(ctd);
		}
//		ctd.appendChild(input);
//		newTr.appendChild(ctd);
	}
	
    // 새로 추가된 <tr>의 모든 <input> 값을 초기화
    newTr.querySelectorAll('input').forEach(input => {
        input.value = ''; // 초기화
    });

    // 새로 추가된 <tr>에도 동일한 클래스 추가
    newTr.classList.add('medication-row');

    // 복제된 tr 추가
    originalTr.parentElement.appendChild(newTr);
});

// 삭제 버튼
document.getElementById('deleteBtn').addEventListener('click', ()=> {
    // 의약품 줄만 선택
    const medicationRows = document.querySelectorAll('.medication-row');
    if (medicationRows.length > 2) {
        // 마지막 의약품 줄 삭제
        const lastRow = medicationRows[medicationRows.length - 1];
        lastRow.parentElement.removeChild(lastRow);
    } else {
        alert('더 이상 삭제할 약 정보가 없습니다!');
    }
});

// 폼 버튼 이벤트
const submitBtn = document.getElementById('submitBtn'); // 버튼 태그 찾기
const form = document.getElementById('insertForm'); // 폼 태그 찾기
const formInputs = document.getElementById("cheobang").querySelectorAll('input'); 
let medications = "";
submitBtn.addEventListener('click', (e) => {
	medications = "";
	const medics = document.getElementsByClassName('meds'); // 약 태그들 배열로 가져오기
	for (let m of medics) {
		medications += m.children[0].value + "%&%"; // 가져온 애들 반복문 돌려서 내용물 이어붙이기
	}
	document.getElementById('medications').value = medications; // input type="hidden" 에 value 로 설정하기
//	for (let input of formInputs) {
//		if (input.value.trim() == "") {
//			// 여기에 폼 제출 거절
//			alert("비어있는 칸이 존재합니다");
//			input.focus();
//			e.preventDefault;
//			return;
//		}
//	}
	form.submit(); // 폼 제출

})