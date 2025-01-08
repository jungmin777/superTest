let slideIndex = 1;
showSlides(slideIndex);

// 좌, 우 버튼 클릭 시 이미지 슬라이드
function moveSlide(n) {
    showSlides(slideIndex += n);
}

// 도트 버튼 클릭 시 이미지 슬라이드
function currentSlide(n) {
    showSlides(slideIndex = n);
}

// 슬라이드 표시 함수
function showSlides(n) {
    let slides = document.querySelectorAll('.slider-images img');
    let dots = document.querySelectorAll('.dot');
    
    if (n > slides.length) { slideIndex = 1; }
    if (n < 1) { slideIndex = slides.length; }
    
    // 슬라이드 이동
    let slideWidth = slides[0].clientWidth; // 한 이미지의 너비
    let newTransformValue = -(slideWidth * (slideIndex - 1)); // 새로운 transform 값 계산

    // 슬라이드 이동 애니메이션
    document.querySelector('.slider-images').style.transform = `translateX(${newTransformValue}px)`;

    // 도트 상태 초기화
    for (let i = 0; i < dots.length; i++) {
        dots[i].className = dots[i].className.replace(" active", "");
    }
    
    // 현재 이미지 표시
    dots[slideIndex - 1].className += " active";
}
