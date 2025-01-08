const lists = document.getElementsByClassName('messagelist');
for(const i of lists){
	i.addEventListener('click',(e)=>{
		if(e.target.tagName == 'SPAN'){ // 이벤트가 들어간 곳에서 클릭한 태그의 이름을 가져오는 법 & 클릭한 태그가 span 태그일 경우
			let preNum = e.target.parentElement.children[0].innerText;
			location.href = "/prescriptDetail/" + preNum;
		}
	})
}


