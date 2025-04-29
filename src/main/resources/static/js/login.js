
document.addEventListener("DOMContentLoaded", function() {
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
