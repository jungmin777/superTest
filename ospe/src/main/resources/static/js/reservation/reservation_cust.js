/////////////////reservation_cust.js
// 이전, 다음 버튼과 날짜를 표시할 요소 가져오기
const prev = document.getElementById('prev');
const next = document.getElementById('next');
const CalTitle = document.getElementById('month-year');
const CalDays = document.getElementById('cal_days');
const timeBlock = document.getElementsByClassName('times');

let currentDate = new Date(); //날짜 객체

// 달력 함수
function Calendar(date) {
    const month = date.getMonth();
    const year = date.getFullYear();

    // 연도-달
    CalTitle.textContent = `${year} / ${(month + 1).toString().padStart(2, '0')}`;

    // 달의 첫날짜, 마지막 날짜
    const first = new Date(year, month, 1);
    const last = new Date(year, month + 1, 0);

    const firstOfWeek = first.getDay(); // 첫 번째 날의 요일
    const lastDate = last.getDate(); // 마지막 일

    CalDays.innerHTML = ''; //날짜 초기화

    // 빈 셀 추가
    for (let i = 0; i < firstOfWeek; i++) {
        const empty = document.createElement('div');
        empty.classList.add('day');
        CalDays.appendChild(empty);
    }

    // 현재 날짜 생성 (시간을 00:00:00로 설정하여 비교)
    const today = new Date();
    today.setHours(0, 0, 0, 0); // 오늘 날짜의 시간을 00:00:00으로 설정
    

    // 날짜 입력
    for (let i = 1; i <= lastDate; i++) {
        const day = document.createElement('button');
        day.classList.add('day'); // 'day'클래스 추가
        day.textContent = i; // 날짜 내용 적기

        // 해당 날짜 객체 생성
        const dayDate = new Date(year, month, i);

        // 현재 날짜 이전인 경우 비활성화
        if (dayDate <= today) {
            day.disabled = true;
            day.style.backgroundColor="#f0f0f0";   
            day.style.borderRadius = "50%";
        }

        // 일요일인 경우 sunday 클래스 추가
        if ((firstOfWeek + i - 1) % 7 === 0) {
            day.classList.add('weekend');
            day.style.color = "red";
         day.style.backgroundColor="#f0f0f0";   
         day.style.borderRadius = "50%";
            day.disabled = true;
        }

        // 토요일인 경우
        if ((firstOfWeek + i - 1) % 7 === 6) {
            day.classList.add('weekend');
            day.style.color = "blue";
         day.style.backgroundColor="#f0f0f0";   
         day.style.borderRadius = "50%";
            day.disabled = true;
        }

        // 예약시간 요소 가져옴
        // 예약시간 클릭시 예약버튼 활성화
        for (let i = 0; i < timeBlock.length; i++) {
            timeBlock[i].addEventListener('click', function() {
                reservationbutton.style.backgroundColor = 'cornflowerblue'; // 활성화되는 동시에 배경색 변경

                for (let j = 0; j < timeBlock.length; j++) {
                    timeBlock[j].style.backgroundColor = ""; // 기존 배경색 제거
                }

                // 현재 선택된 날짜에 배경색 추가
                this.style.backgroundColor = "cornflowerblue";

                reservationbutton.disabled = false; // 예약버튼의 활성화

            });
        }
        CalDays.appendChild(day);
    }
      const days = document.getElementById('cal_days').children; //날짜(일) 요소 가져옴
      
      // 날짜(일) 클릭시 예약시간버튼 활성화
      for (let day of days) {
          day.addEventListener('click', function() {
            // 이전에 선택된 날짜 초기화
              for (let day_copy of days) {
                if (day_copy.disabled) continue; // 지난 날짜는 초기화에서 제외
                  day_copy.style.backgroundColor = ""; // 기존 배경색 제거
              }
      
              // 현재 선택된 날짜에 배경색 추가
              this.style.backgroundColor = "cornflowerblue";
              this.style.borderRadius = "20px"; 
                  
                  
            for (let j = 0; j < timeBlock.length; j++) {
                  timeBlock[j].disabled = false;  // times의 활성화
            }                
          });
      }
}


// 달 버튼(이전/다음)
function PrevnextButton(move) {
    currentDate.setMonth(currentDate.getMonth() + move); // 월 변경
    Calendar(currentDate); // 변경된 월로 캘린더 다시 렌더링
   giveEvent();
}

// 달 버튼 클릭시 한칸 이동
prev.addEventListener('click', () => PrevnextButton(-1));
next.addEventListener('click', () => PrevnextButton(1));

function giveEvent(){
   // 날짜 요소들 불러오기
   const cal_days = document.getElementById('cal_days').children; // 달력의 날짜 버튼들
   const reserv_day = document.getElementsByName("ResultReservationDay")[0]; // 예약 날짜를 담을 hidden input
   const reserv_time = document.getElementsByName("ResultReservationTime")[0]; // 예약 시간 입력 hidden input
   const month_year = document.getElementById('month-year');

      // 달력의 날짜 버튼을 클릭했을 때 실행되는 이벤트
    for (const cal_day of cal_days) {
        cal_day.addEventListener('click', () => { 
            const monthtext = month_year.innerText;
            const daytext = cal_day.innerText.padStart(2, '0'); // 클릭된 날짜의 텍스트
            reserv_day.value = monthtext + ' / ' + daytext;  // 예약 날짜에 넣기
        });
    }

    // 예약 시간 버튼 클릭시 실행되는 이벤트 리스너
    for (const time of timeBlock) {
        time.addEventListener('click', () => {
            reserv_time.value = time.innerText.padStart(5, '0'); // 선택한 예약 시간 값을 예약 시간 input에 넣기
        });
    }

    // 진료 과목 선택 시 선택된 값 hidden input에 넣기
    const option = document.getElementById('departmentselect');
    const department = document.getElementsByName('Department')[0];
    option.addEventListener('change', () => { 
        department.value = option.value; // 드롭다운에서 선택된 진료과목 값을 hidden input에 넣기
    });
}

Calendar(currentDate); //현재 날짜 불러오기
giveEvent();

const reservationbutton = document.getElementById('reservation'); //예약 버튼 요소 가져옴

//예약버튼 클릭시 모달창 뜨기
reservationbutton.addEventListener('click',()=>{ 
    Swal.fire({
        title: "예약 성공!",
        text: "예약 변경/취소는 대표번호로 문의 바랍니다.",
        icon: "success"
      });
      
      form.submit(); // 폼을 서버로 제출
});
