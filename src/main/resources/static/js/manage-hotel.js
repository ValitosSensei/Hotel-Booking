function openTab(tabId) {
    document.querySelectorAll('.tab').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.tab-button').forEach(button => button.classList.remove('active'));

    document.getElementById(tabId).classList.add('active');
    event.target.classList.add('active');
}
document.querySelectorAll('.tab-link').forEach(link => {
    link.addEventListener('click', function() {
        if (!this.dataset.tab) return; // Ігнорувати кнопки без data-tab
        document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
        document.querySelectorAll('.tab-link').forEach(l => l.classList.remove('active'));
        this.classList.add('active');
        document.getElementById(this.dataset.tab).classList.add('active');
    });
});

function openModal(modalId) {
    const modal = document.getElementById(modalId);
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

async function openEditRoomModal(roomId, userId) {
    try {
        const response = await fetch(`/manager/editRoom/${roomId}?userId=${userId}`);
        if (!response.ok) throw new Error("Помилка сервера: " + response.status);
        const room = await response.json();

        document.getElementById('editRoomId').value = room.id;
        document.getElementById('editRoomNumber').value = room.roomNumber;
        document.getElementById('editRoomType').value = room.type;
        document.getElementById('editRoomPrice').value = room.price;
        document.getElementById('editRoomAvailable').checked = room.availableForDates;

        openModal('editRoomModal');
    } catch (error) {
        console.error('Помилка:', error);
        alert('Не вдалося завантажити дані кімнати: ' + error.message);
    }
}

document.getElementById('editRoomForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const formData = new FormData(e.target);
    const roomId = formData.get('roomId');

    try {
        await fetch(`/manager/editRoom/${roomId}`, {
            method: 'POST',
            body: formData
        });
        location.reload();
    } catch (error) {
        console.error('Помилка:', error);
        alert('Не вдалося зберегти зміни');
    }
});
function confirmDeleteRoom(roomId, hotelId, userId) {
    if (confirm("Ви впевнені, що хочете видалити цю кімнату?")) {
        window.location.href = `/manager/deleteRoom/${roomId}?userId=${userId}&hotelId=${hotelId}`;
    }
}
// Drag & drop для завантаження фото
const uploadZone = document.getElementById('uploadZone');
const fileInput = document.getElementById('fileInput');
const previewGrid = document.getElementById('previewGrid');

uploadZone.addEventListener('click', () => fileInput.click());

uploadZone.addEventListener('dragover', (e) => {
    e.preventDefault();
    uploadZone.style.borderColor = '#2980b9';
});

uploadZone.addEventListener('dragleave', () => {
    uploadZone.style.borderColor = '#3498db';
});

uploadZone.addEventListener('drop', (e) => {
    e.preventDefault();
    handleFiles(e.dataTransfer.files);
});

fileInput.addEventListener('change', (e) => {
    handleFiles(e.target.files);
});

function handleFiles(files) {
    previewGrid.innerHTML = '';
    Array.from(files).forEach(file => {
        if (file.type.startsWith('image/')) {
            const reader = new FileReader();
            reader.onload = (e) => {
                const img = document.createElement('img');
                img.src = e.target.result;
                img.classList.add('preview-item');
                previewGrid.appendChild(img);
            };
            reader.readAsDataURL(file);
        }
    });
}

// Видалення фото
function deletePhoto(buttonElement) {
    const photoUrl = buttonElement.dataset.photo;
    const hotelId = buttonElement.dataset.hotelId;
    if (confirm('Видалити це фото?')) {
        fetch(`/manager/hotels/${hotelId}/photos/delete?photoUrl=${encodeURIComponent(photoUrl)}`, {
            method: 'POST'
        })
            .then(response => {
                if (response.ok) location.reload();
                else alert('Помилка видалення');
            })
            .catch(error => console.error('Error:', error));
    }
}