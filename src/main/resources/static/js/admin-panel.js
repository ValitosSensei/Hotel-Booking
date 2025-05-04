
function openEditHotelModal(button) {
    const hotel = {
        id: button.getAttribute('data-id'),
        name: button.getAttribute('data-name'),
        address: button.getAttribute('data-address'),
        city: button.getAttribute('data-city'),
        country: button.getAttribute('data-country'),
        contact: button.getAttribute('data-contact')
    };

    document.getElementById('editHotelId').value = hotel.id;
    document.querySelector('#editHotelModal input[name="name"]').value = hotel.name;
    document.querySelector('#editHotelModal input[name="address"]').value = hotel.address || '';
    document.querySelector('#editHotelModal input[name="city"]').value = hotel.city || '';
    document.querySelector('#editHotelModal input[name="country"]').value = hotel.country || '';
    document.querySelector('#editHotelModal input[name="contactInfo"]').value = hotel.contact || '';

    openModal('editHotelModal');
}

function openRejectModal(button) {
    const requestId = button.getAttribute('data-request-id');
    document.getElementById('rejectRequestId').value = requestId;
    openModal('rejectCommentModal');
}

document.getElementById('rejectForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const requestId = document.getElementById('rejectRequestId').value;
    const comment = document.getElementById('rejectComment').value.trim(); // Видаляємо пробіли

    // Перевірка на пустий коментар
    if (!comment) {
        alert("Будь ласка, введіть причину відхилення!");
        return; // Зупиняємо відправку
    }

    fetch(`/admin/reject-request/${requestId}?comment=${encodeURIComponent(comment)}`, {
        method: 'POST'
    })
        .then(response => {
            if (response.ok) {
                closeModal(); // Закриваємо тільки після успішної відправки
                window.location.reload();
            } else {
                alert('Помилка відхилення');
            }
        })
        .catch(error => {
            console.error('Помилка:', error);
            alert('Помилка мережі');
        });
});
function openEditRoomModal(button) {
    const room = {
        id: button.getAttribute('data-id'),
        number: button.getAttribute('data-number'),
        type: button.getAttribute('data-type'),
        price: button.getAttribute('data-price'),
        available: button.getAttribute('data-available') === 'true'
    };

    document.getElementById('editRoomId').value = room.id;
    document.querySelector('#editRoomModal input[name="roomNumber"]').value = room.number || '';
    document.querySelector('#editRoomModal input[name="type"]').value = room.type || '';
    document.querySelector('#editRoomModal input[name="price"]').value = room.price || '';
    document.querySelector('#editRoomModal input[name="availableForDates"]').checked = room.available;

    openModal('editRoomModal');
}

// Обробка форм
document.getElementById('hotelForm').addEventListener('submit', function(e) {
    e.preventDefault();
    submitForm(this, '/admin/editHotel/' + document.getElementById('editHotelId').value);
});

document.getElementById('roomForm').addEventListener('submit', function(e) {
    e.preventDefault();
    submitForm(this, '/admin/editRoom/' + document.getElementById('editRoomId').value);
});

function submitForm(form, action) {
    form.action = action;
    form.submit();
    closeModal();
}

// Функція для відображення деталей
function showRequestDetails(button) {
    const hotelName = button.getAttribute('data-hotel-name');
    const hotelAddress = button.getAttribute('data-hotel-address');
    const userEmail = button.getAttribute('data-user-email');
    const requestDate = button.getAttribute('data-request-date');

    document.getElementById('modalHotelName').textContent = hotelName;
    document.getElementById('modalHotelAddress').textContent = hotelAddress;
    document.getElementById('modalUserEmail').textContent = userEmail;
    document.getElementById('modalRequestDate').textContent = requestDate;

    openModal('requestDetailsModal');
}

function openModal(modalId) {
    const modal = document.getElementById(modalId);
    const overlay = document.querySelector('.modal-overlay');

    overlay.style.display = 'block';
    modal.style.display = 'block';

    setTimeout(() => {
        overlay.style.opacity = '1';
        modal.style.opacity = '1';
        modal.style.transform = 'translate(-50%, -50%) scale(1)';
    }, 10);
}


// Функція закриття модального вікна
function closeModal() {
    const modals = document.querySelectorAll('.modal');
    const overlay = document.querySelector('.modal-overlay');

    modals.forEach(modal => {
        modal.style.opacity = '0';
        modal.style.transform = 'translate(-50%, -50%) scale(0.9)';
    });

    overlay.style.opacity = '0';

    setTimeout(() => {
        modals.forEach(modal => modal.style.display = 'none');
        overlay.style.display = 'none';
    }, 10);
}

// Закриття при кліку поза вікном
document.querySelector('.modal-overlay').addEventListener('click', closeModal);
document.querySelectorAll('.modal').forEach(modal => {
    modal.addEventListener('click', e => e.stopPropagation());
});

const tabs = document.querySelectorAll('.tab-link');
const tabContents = document.querySelectorAll('.tab-content');

tabs.forEach(tab => {
    tab.addEventListener('click', function() {
        const targetTab = this.dataset.tab;
        const currentActive = document.querySelector('.tab-content:not([style*="display: none"])');

        // Анімація виходу
        if(currentActive) {
            currentActive.style.opacity = '0';
            currentActive.style.transform = 'translateX(30px)';
        }

        setTimeout(() => {
            tabs.forEach(t => t.classList.remove('active'));
            tabContents.forEach(content => {
                content.style.display = 'none';
                content.style.opacity = '0';
                content.style.transform = 'translateX(30px)';
            });

            this.classList.add('active');
            const targetContent = document.getElementById(targetTab);
            targetContent.style.display = 'block';

            // Анімація входу
            setTimeout(() => {
                targetContent.style.opacity = '1';
                targetContent.style.transform = 'translateX(0)';
            }, 50);
        }, 50);
    });
});

// Ініціалізація першої вкладки
document.querySelector('.tab-link.active').click();

// Handle user search form
// Обробник форми пошуку
document.getElementById('userSearchForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const email = document.getElementById('searchEmail').value;

    fetch(`/admin/searchUser?email=${encodeURIComponent(email)}`)
        .then(response => {
            if (!response.ok) throw new Error('Помилка мережі');
            return response.text();
        })
        .then(html => {
            document.getElementById('userResults').innerHTML = html;
        })
        .catch(error => {
            console.error('Помилка:', error);
            document.getElementById('userResults').innerHTML = `
                <tr><td colspan="3">Помилка завантаження</td></tr>`;
        });
});
document.getElementById('syncForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const cityCode = document.getElementById('cityCode').value;

    fetch(`/api/hotels/sync?cityCode=${encodeURIComponent(cityCode)}`, {
        method: 'POST'
    })
        .then(response => {
            if (!response.ok) throw new Error('Помилка синхронізації');
            return response.text();
        })
        .then(message => {
            document.getElementById('syncResult').innerHTML = `<div class="success">${message}</div>`;
        })
        .catch(error => {
            document.getElementById('syncResult').innerHTML = `<div class="error">${error.message}</div>`;
        });
});