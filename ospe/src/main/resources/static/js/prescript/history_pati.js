document.addEventListener('DOMContentLoaded', function () {
    // 1. Popover functionality
    const popoverTriggerList = document.querySelectorAll('[data-bs-toggle="popover"]');
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

    // 2. Search functionality
    const searchButton = document.querySelector('.SearchButton');
    const searchInput = document.querySelector('.SearchInput');
    const searchSelect = document.querySelector('.SearchSelect');
    const tableRows = document.querySelectorAll('.messagelist');

    searchButton.addEventListener('click', function () {
        const searchTerm = searchInput.value.toLowerCase();  // Convert the input search term to lowercase
        const searchOption = searchSelect.value;  // Get selected search option (Doctor's name or Medication)

        tableRows.forEach(function (row) {
            let rowContent = '';

            if (searchOption === '의사명') { 
                // Search by doctor's name
                const doctorName = row.querySelector('span:nth-child(1)') ? row.querySelector('span:nth-child(1)').textContent.toLowerCase() : '';
                rowContent = doctorName;
            } else if (searchOption === '약종류') {
                // Search by medication type
                const visibleMedications = row.querySelector('span:nth-child(3)') ? row.querySelector('span:nth-child(3)').textContent.toLowerCase() : '';  // Visible medication name
                
                // Check if the button exists (for full medication name in popover)
                const toggleButton = row.querySelector('button.toggle-button');
                let fullMedications = '';
                if (toggleButton) {
                    // Ensure the full medication text is retrieved from the button's data-bs-content
                    fullMedications = toggleButton.getAttribute('data-bs-content') ? toggleButton.getAttribute('data-bs-content').toLowerCase() : '';
                }

                // Combine the visible and full medication names for a more comprehensive search
                rowContent = visibleMedications + ' ' + fullMedications;
            }

            // If the search term is found within the row content, show the row, otherwise hide it
            if (rowContent.includes(searchTerm)) {
                row.style.display = '';  // Show row
            } else {
                row.style.display = 'none';  // Hide row
            }
        });
    });
});