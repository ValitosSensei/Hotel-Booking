document.addEventListener("DOMContentLoaded", function () {
    const modal = document.getElementById('booking-modal');
    const bookingForm = document.getElementById('booking-form');

    // Анімація для інпутів

    document.querySelectorAll('.book-button').forEach(button => {
        button.addEventListener('click', (e) => {
            e.preventDefault();
            const roomId = button.getAttribute('data-room-id');
            document.getElementById('room-id').value = roomId;
            modal.style.display = "flex";
        });
    });


    document.querySelector('.close-modal').addEventListener('click', () => {
        modal.style.display = "none";
    });

    // Відправка форми
    bookingForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const checkIn = document.getElementById('check-in').value;
        const checkOut = document.getElementById('check-out').value;
        const currentDate = new Date().toISOString().split('T')[0]; // Поточна дата у форматі YYYY-MM-DD

        // Валідація дат
        if (checkIn < currentDate) {
            alert("Дата заїзду не може бути в минулому");
            return;
        }
        if (checkOut <= checkIn) {
            alert("Дата виїзду має бути після дати заїзду");
            return;
        }
        const formData = {
            roomId: document.getElementById('room-id').value,
            checkInDate: document.getElementById('check-in').value,
            checkOutDate: document.getElementById('check-out').value
        };

        try {
            const response = await fetch('/bookings/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest' // Важливий заголовок
                },
                body: JSON.stringify(formData),
                credentials: 'include' // Для передачі куків автентифікації
            });

            // Обробка HTTP-статусів
            if (response.status === 401) {
                alert("Будь ласка, увійдіть в систему.");

                return;
            }

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || "Помилка сервера");
            }

            const result = await response.json();
            alert(result.message);
            modal.style.display = "none";
            window.location.reload();

        } catch (error) {
            console.error('Помилка:', error);
            alert("Помилка: " + error.message);
        }
    });
});
