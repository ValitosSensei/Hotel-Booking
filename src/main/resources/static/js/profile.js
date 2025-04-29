function openTab(evt, tabName) {
    var i, tabContent, tabLinks;

    tabContent = document.getElementsByClassName("tab-content");
    for (i = 0; i < tabContent.length; i++) {
        tabContent[i].style.display = "none";
    }

    tabLinks = document.getElementsByClassName("tab-link");
    for (i = 0; i < tabLinks.length; i++) {
        tabLinks[i].classList.remove("active");
    }

    document.getElementById(tabName).style.display = "block";
    evt.currentTarget.classList.add("active");
}

function openManagerRequestModal() {
    const modal = document.getElementById('managerRequestModal');
    const overlay = document.querySelector('.modal-overlay');
    overlay.style.display = 'block';
    modal.style.display = 'block';
}

document.addEventListener('DOMContentLoaded', function() {
    const modal = document.getElementById('managerRequestModal');
    const closeModal = document.querySelector('.close');
    const openModalButton = document.querySelector('button[onclick="openManagerRequestModal()"]');
    const form = document.getElementById('managerRequestForm');

    // Закриття модального вікна
    if (closeModal) {
        closeModal.addEventListener('click', () => {
            modal.style.display = 'none';
        });
    }

    // Закриття при кліку поза модальним вікном
    window.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.style.display = 'none';
        }
    });

    // Обробник відправки форми
    if (form) {
        form.addEventListener('submit', () => {
            // Збереження часу відправки в localStorage
            localStorage.setItem('lastRequestTime', Date.now());
        });
    }

    // Перевірка часу блокування при завантаженні сторінки
    const lastRequestTime = localStorage.getItem('lastRequestTime');
    if (lastRequestTime && openModalButton) {
        const currentTime = Date.now();
        const timePassed = currentTime - parseInt(lastRequestTime);
        const cooldown = 300000; // 5 хвилин

        if (timePassed < cooldown) {
            openModalButton.disabled = true;
            // Автоматичне розблокування після закінчення часу
            setTimeout(() => {
                openModalButton.disabled = false;
                localStorage.removeItem('lastRequestTime');
            }, cooldown - timePassed);
        } else {
            localStorage.removeItem('lastRequestTime');
        }
    }


    // Щоб після завантаження сторінки була активна перша вкладка
    document.getElementById('personalInfo').style.display = 'block';
});

function openTransferModal(bookingId) {
    document.getElementById('transferBookingId').value = bookingId;
    const modal = document.getElementById('transferBookingModal');
    const overlay = document.querySelector('.modal-overlay');
    overlay.style.display = 'block';
    modal.style.display = 'block';
    setTimeout(() => {
        overlay.style.opacity = '1';
        modal.style.opacity = '1';
    }, 10);
}

function closeModal() {
    const modals = document.querySelectorAll('.modal');
    const overlay = document.querySelector('.modal-overlay');
    modals.forEach(modal => {
        modal.style.display = 'none';
    });
    overlay.style.display = 'none';
}

document.getElementById('transferForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const formData = new FormData(this);
    const bookingId = formData.get('bookingId');
    const newUserEmail = formData.get('newUserEmail');

    fetch(`/bookings/${bookingId}/request-transfer?newUserEmail=${encodeURIComponent(newUserEmail)}`, {
        method: 'POST'
    })
        .then(response => {
            if (response.ok) {
                location.reload();
            } else {
                alert('Помилка передачі');
            }
        })
        .catch(error => console.error('Error:', error));
});