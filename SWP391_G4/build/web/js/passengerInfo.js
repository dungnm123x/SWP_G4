// 1) Định nghĩa bảng discountRates
const discountRates = {
    "Người lớn": 0,
    "Trẻ em": 50,
    "Sinh viên": 20,
    "Người cao tuổi": 30,
    "VIP": 10
};

/**
 * 2) Hàm updateDiscountNoModal:
 *    - Chỉ tính discount và cập nhật UI, KHÔNG mở popup.
 *    - Dùng khi trang load (hoặc partial load) để hiển thị giá tạm.
 */
function updateDiscountNoModal(selectElement, priceId, discountId, totalId) {
    // Lấy giá trị type
    let currentType = selectElement.value;

    // Lấy giá vé gốc
    let basePrice = parseFloat(document.getElementById(priceId).value) || 0;
    // Tính discount dựa vào type
    let rate = discountRates[currentType] || 0;
    let discountAmount = basePrice * rate / 100;
    let finalPrice = basePrice - discountAmount + 1000;

    // Ghi ra UI
    document.getElementById(discountId).innerText = '-' + rate + '%';
    document.getElementById(totalId).innerText = finalPrice.toLocaleString() + ' VND';
}

/**
 * 3) Hàm updateDiscount:
 *    - Dùng khi người dùng thay đổi select (onchange).
 *    - Có đầy đủ logic: nếu là "Trẻ em"/"Người cao tuổi"/"VIP" => mở modal (nếu chưa xác nhận).
 *    - Nếu không cần modal => chỉ tính discount như bình thường.
 */
function updateDiscount(
        selectElement, priceId, discountId, totalId,
        ageModalId, vipModalId, idNumberInputId
        ) {
    let rowEl = selectElement.closest('tr');
    let alreadyConfirmed = (rowEl.getAttribute('data-confirmedDOB') === 'true');
    let currentType = selectElement.value;

    // Mở khóa input CCCD trước (nếu nó đang readonly)
    let cccdInput = document.getElementById(idNumberInputId);
    cccdInput.readOnly = false;

    // Nếu là "Trẻ em" hoặc "Người cao tuổi" và CHƯA xác nhận => mở modal
    if ((currentType === "Trẻ em" || currentType === "Người cao tuổi") && !alreadyConfirmed) {
        document.getElementById(ageModalId).style.display = 'flex';
        return;
    }

    // Nếu là VIP => mở modal
    if (currentType === "VIP") {
        document.getElementById(vipModalId).style.display = 'flex';
        return;
    }

    // Nếu không thuộc các trường hợp trên => chỉ tính discount
    let basePrice = parseFloat(document.getElementById(priceId).value) || 0;
    let rate = discountRates[currentType] || 0;
    let discountAmount = basePrice * rate / 100;
    let finalPrice = basePrice - discountAmount + 1000;

    document.getElementById(discountId).innerText = '-' + rate + '%';
    document.getElementById(totalId).innerText = finalPrice.toLocaleString() + ' VND';

    updateTotalAmount();
}

/**
 * 4) Hàm confirmAge: Xử lý khi user nhấn "Xác nhận" trong modal tuổi
 *    - Tính tuổi, kiểm tra xem có hợp lệ không
 *    - Nếu là "Trẻ em": auto điền DOB vào CCCD, set readonly
 *    - Gửi Ajax confirmDOB (nếu muốn)
 *    - Tính lại giá
 */
function confirmAge(modalId, selectId, priceId, discountId, totalId, dayId, monthId, yearId, idNumberInputId) {
    // Đóng modal
    closeModal(modalId);

    let rowEl = document.getElementById(selectId).closest('tr');
    let day = document.getElementById(dayId).value;
    let month = document.getElementById(monthId).value;
    let year = document.getElementById(yearId).value;

    let age = getAge(day, month, year);
    let selectedOption = document.getElementById(selectId).value;
    let basePrice = parseFloat(document.getElementById(priceId).value) || 0;
    let rate = 0;

    if (selectedOption === "Trẻ em") {
        // Ví dụ: chấp nhận 6..10
        if (age < 6 || age > 10) {
            alert("Độ tuổi của trẻ em phải từ 6 đến 10 tuổi.");
            return;
        }
        rate = 50;
        // Tạo chuỗi DOB => điền vào CCCD
        let dobString = day.padStart(2, '0') + "/" + month.padStart(2, '0') + "/" + year;
        let cccdInput = document.getElementById(idNumberInputId);
        cccdInput.value = dobString;
        cccdInput.readOnly = true;
    } else if (selectedOption === "Người cao tuổi") {
        // Ví dụ: >= 60
        if (age < 60) {
            alert("Độ tuổi của người cao tuổi phải từ 60 trở lên.");
            return;
        }
        rate = 30;
        // Ở đây không bắt buộc readonly CCCD
    }

    // Đặt data-confirmedDOB = true => lần sau không mở modal nữa
    rowEl.setAttribute('data-confirmedDOB', 'true');

    // (Nếu cần) Gửi Ajax confirmDOB => server
    let idx = selectId.replace('passengerType', '');
    let dobString = day.padStart(2, '0') + "/" + month.padStart(2, '0') + "/" + year;
    let params = new URLSearchParams();
    params.append("action", "confirmDOB");
    params.append("index", idx);
    params.append("dob", dobString);

    fetch("passengerinfo", {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: params.toString()
    })
            .then(resp => resp.text())
            .then(result => {
                console.log("DOB confirmed on server:", result);
            })
            .catch(err => console.error(err));

    // Tính lại tiền
    let discountAmount = basePrice * rate / 100;
    let finalPrice = basePrice - discountAmount + 1000;

    document.getElementById(discountId).innerText = '-' + rate + '%';
    document.getElementById(totalId).innerText = finalPrice.toLocaleString() + ' VND';

    updateTotalAmount();
}

/**
 * 5) Hàm confirmVIP: Khi user nhấn "Xác nhận" modal VIP
 */
function confirmVIP(modalId, selectId, priceId, discountId, totalId) {
    closeModal(modalId);

    let vipInputId = 'vipCard' + selectId.replace('passengerType', '');
    let vipCard = document.getElementById(vipInputId).value.trim();
    if (vipCard === "") {
        alert("Vui lòng nhập thông tin thẻ VIP!");
        return;
    }

    let basePrice = parseFloat(document.getElementById(priceId).value) || 0;
    let rate = 10; // VIP => 10%
    let discountAmount = basePrice * rate / 100;
    let finalPrice = basePrice - discountAmount + 1000;

    document.getElementById(discountId).innerText = '-10%';
    document.getElementById(totalId).innerText = finalPrice.toLocaleString() + ' VND';

    updateTotalAmount();
}

/**
 * 6) Hàm getAge: Tính tuổi dựa trên day, month, year
 */
function getAge(day, month, year) {
    let birthDate = new Date(year, month - 1, day);
    let now = new Date();
    let age = now.getFullYear() - birthDate.getFullYear();
    let m = now.getMonth() - birthDate.getMonth();
    if (m < 0 || (m === 0 && now.getDate() < birthDate.getDate())) {
        age--;
    }
    return age;
}

/**
 * 7) Hàm đóng modal
 */
function closeModal(id) {
    document.getElementById(id).style.display = 'none';
}

/**
 * 8) Hàm reapplyDiscount:
 *    - Chạy vòng lặp qua tất cả các select "passengerType" và gọi hàm KHÔNG mở modal.
 *    - Dùng khi trang load xong hoặc sau partial update => chỉ để hiển thị giá tạm.
 */
function reapplyDiscount() {
    document.querySelectorAll('select[id^="passengerType"]').forEach(selectEl => {
        let idx = selectEl.id.replace('passengerType', '');
        updateDiscountNoModal(
                selectEl,
                'price' + idx,
                'discount' + idx,
                'displayTotal' + idx
                );
    });
}

/**
 * 9) Hàm updateTotalAmount: Tính tổng tiền cho toàn bộ vé
 */
function updateTotalAmount() {
    let sum = 0;
    document.querySelectorAll('[id^="displayTotal"]').forEach(elem => {
        let val = parseFloat(elem.innerText.replace(/[^\d\.]/g, ''));
        if (!isNaN(val)) {
            sum += val;
        }
    });
    let totalElem = document.getElementById('totalAmount');
    if (totalElem) {
        totalElem.innerText = sum.toLocaleString();
    }
}
function checkIfSingleChildTicket() {
    // Lấy danh sách select passengerType
    let selects = document.querySelectorAll('select[id^="passengerType"]');
    // Nếu chỉ có 1 vé
    if (selects.length === 1) {
        let type = selects[0].value;
        if (type === "Trẻ em") {
            // Mở modal
            document.getElementById("childAloneModal").style.display = 'flex';
        }
    }
}

// Hàm confirmChildAlone: khi user bấm nút “Xác nhận” trong modal
function confirmChildAlone() {
    let code = document.getElementById("adultTicketCodeInput").value.trim();
    if (!code) {
        alert("Vui lòng nhập mã vé người lớn.");
        return;
    }
    // Gán vào input hidden
    document.getElementById("adultTicketCodeHidden").value = code;
    // Đóng modal
    closeModal("childAloneModal");
}

/**
 * 10) Hàm rebindRemoveButtons: Gắn sự kiện xóa vé (AJAX)
 */
function rebindRemoveButtons() {
    document.querySelectorAll(".btn-remove").forEach(btn => {
        btn.addEventListener("click", function (e) {
            e.preventDefault();
            let seatID = this.getAttribute("data-seatid");
            let params = new URLSearchParams();
            params.append("action", "removeOne");
            params.append("seatID", seatID);

            fetch("passengerinfo?renderPartial=true", {
                method: "POST",
                headers: {"Content-Type": "application/x-www-form-urlencoded"},
                body: params.toString()
            })
                    .then(resp => resp.text())
                    .then(result => {
                        if (result === "EMPTY") {
                            // Giỏ rỗng => quay lại schedule
                            window.location.href = "schedule";
                            return;
                        }
                        // Cập nhật lại table
                        document.querySelector(".table-responsive").innerHTML = result;
                        // Gọi lại hàm bind + tính tiền
                        rebindRemoveButtons();
                        reapplyDiscount();
                        updateTotalAmount();
                    })
                    .catch(err => console.error(err));
        });
    });
}

/**
 * 11) Sự kiện DOMContentLoaded:
 *    - Khi trang load lần đầu (hoặc load full), ta gọi:
 *      + rebindRemoveButtons()    // Gắn event xóa
 *      + reapplyDiscount()        // Chỉ tính discount, KHÔNG mở modal
 *      + updateTotalAmount()      // Cập nhật tổng tiền
 */
document.addEventListener("DOMContentLoaded", function () {
    rebindRemoveButtons();
    reapplyDiscount();
    updateTotalAmount();

    checkIfSingleChildTicket();
});
