function toggleFilter() {
    const citySelect = document.querySelector("select[name='city']");
    const ratingFields = document.querySelectorAll(".rating-filter input, .rating-filter select");
    const ratingFilterDiv = document.querySelector(".rating-filter");

    if (citySelect.value === "") {
        ratingFields.forEach(field => field.disabled = true);
        document.querySelector("input[name='minRating']").value = "";
        document.querySelector("input[name='maxRating']").value = "";
        document.querySelector("select[name='sortOrder']").value = "desc";
        ratingFilterDiv.style.display = "none";
    } else {
        ratingFields.forEach(field => field.disabled = false);
        ratingFilterDiv.style.display = "flex";
    }
}

window.onload = toggleFilter;