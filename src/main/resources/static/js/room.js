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
document.querySelectorAll('.carousel-slide img').forEach(img => {
    img.addEventListener('click', function() {
        const modal = document.getElementById('photo-modal');
        const modalImg = document.getElementById('expanded-img');
        modal.style.display = "flex";
        modalImg.src = this.src;
    });
});

document.querySelector('#photo-modal .close-modal').addEventListener('click', () => {
    document.getElementById('photo-modal').style.display = "none";
});
let currentSlide = 0;
let autoPlayInterval;

function showSlide(index) {
    const track = document.querySelector('.carousel-track');
    const slides = document.querySelectorAll('.carousel-slide');
    const dots = document.querySelectorAll('.dot');

    currentSlide = (index + slides.length) % slides.length;
    const offset = -currentSlide * 100;
    track.style.transform = `translateX(${offset}%)`; /* Горизонтальний зсув */

    dots.forEach(dot => dot.classList.remove('active'));
    dots[currentSlide].classList.add('active');
}

function nextSlide() {
    showSlide(currentSlide + 1);
}

function prevSlide() {
    showSlide(currentSlide - 1);
}

// Автоплеї
function startAutoPlay() {
    autoPlayInterval = setInterval(nextSlide, 10000);
}

function stopAutoPlay() {
    clearInterval(autoPlayInterval);
}

// Обробка подій
document.querySelector('.carousel-button.next').addEventListener('click', () => {
    nextSlide();
    stopAutoPlay();
    startAutoPlay();
});

document.querySelector('.carousel-button.prev').addEventListener('click', () => {
    prevSlide();
    stopAutoPlay();
    startAutoPlay();
});

// Ініціалізація
document.addEventListener("DOMContentLoaded", function() {
    if (document.querySelector('.carousel-slide')) {
        showSlide(0);
        startAutoPlay();
    }
});

document.querySelector('.carousel-container').addEventListener('mouseenter', stopAutoPlay);
document.querySelector('.carousel-container').addEventListener('mouseleave', startAutoPlay);
