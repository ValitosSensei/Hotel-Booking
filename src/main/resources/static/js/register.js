

document.addEventListener("DOMContentLoaded", function() {
    // Ініціалізація телефонного поля
    const phoneInput = document.querySelector("#phone");
    const iti = window.intlTelInput(phoneInput, {
        utilsScript: "https://cdnjs.cloudflare.com/ajax/libs/intl-tel-input/17.0.8/js/utils.js",
        separateDialCode: true,
        preferredCountries: ["us", "ua"],
        initialCountry: "auto",
        geoIpLookup: function(callback) {
            fetch("https://ipapi.co/json")
                .then(res => res.json())
                .then(data => callback(data.country_code))
                .catch(() => callback("us"));
        }
    });

    // Оновлення прихованого поля перед відправкою форми
    document.querySelector(".form").addEventListener("submit", function(e) {
        const fullPhone = iti.getNumber();
        document.querySelector("#fullPhone").value = fullPhone;
    });

    // Стилі для фокусу (як у вашому оригінальному коді)
    const formElements = document.querySelectorAll(".form input");
    formElements.forEach(input => {
        input.addEventListener("focus", () => {
            input.style.borderColor = "#007bff";
        });
        input.addEventListener("blur", () => {
            input.style.borderColor = "#ccc";
        });
    });
});
document.addEventListener("DOMContentLoaded", function() {
    // Стилі для фокусу
    const formElements = document.querySelectorAll(".form input");
    formElements.forEach(input => {
        input.addEventListener("focus", () => input.style.borderColor = "#007bff");
        input.addEventListener("blur", () => input.style.borderColor = "#ccc");
    });

    // Ініціалізація телефонного поля
    const phoneInput = document.querySelector("#phone");
    const iti = window.intlTelInput(phoneInput, {
        utilsScript: "https://cdnjs.cloudflare.com/ajax/libs/intl-tel-input/17.0.8/js/utils.js",
        separateDialCode: true,
        preferredCountries: ["us", "ua"],
        initialCountry: "auto",
        geoIpLookup: function(callback) {
            fetch("https://ipapi.co/json")
                .then(res => res.json())
                .then(data => callback(data.country_code))
                .catch(() => callback("us"));
        }
    });

    // Валідація форми
    document.getElementById("registerForm").addEventListener("submit", function(e) {
        let isValid = true;

        // Валідація імені/прізвища
        const nameRegex = /^[A-Za-zА-Яа-яЄєІіЇїҐґ'\-\s]+$/;
        ['firstName', 'lastName'].forEach(field => {
            const elem = document.getElementById(field);
            if (!nameRegex.test(elem.value)) {
                showError(field + 'Error', 'Допустимі лише літери та спецсимволи');
                isValid = false;
            }
        });

        // Валідація емейлу
        const email = document.getElementById('email');
        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) {
            showError('emailError', 'Невірний формат емейлу');
            isValid = false;
        }

        // Валідація телефону
        const phoneNumber = iti.getNumber();
        if (!iti.isValidNumber() || phoneNumber.replace(/\D/g, '').length < 10) {
            showError('phoneError', 'Невірний номер телефону');
            isValid = false;
        }

        // Валідація паролю
        const password = document.getElementById('password');
        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])/;
        if (!passwordRegex.test(password.value)) {
            showError('passwordError', 'Пароль повинен містити:');
            isValid = false;
        }

        if (!isValid) e.preventDefault();
    });

    // Функція показу помилок
    function showError(elementId, message) {
        const errorElement = document.getElementById(elementId);
        errorElement.innerHTML = `
        ${message}
        <ul class="error-list">
            <li>Принаймні 1 велика літера</li>
            <li>Принаймні 1 мала літера</li>
            <li>Принаймні 1 цифра</li>
            <li>Принаймні 1 спецсимвол (@$!%*?&)</li>
        </ul>
    `;
        errorElement.style.display = 'block';
    }

    // Скидання помилок при введенні
    formElements.forEach(input => {
        input.addEventListener('input', () => {
            document.getElementById(input.id + 'Error').style.display = 'none';
        });
    });
});