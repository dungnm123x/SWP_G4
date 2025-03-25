/* passengerInfo.js */

// Biến rates chung
const discountRates = {
    "Người lớn": 0,
    "Trẻ em": 50,
    "Sinh viên": 20,
    "Người cao tuổi": 30,
    "VIP": 10
};

// Hàm updateDiscount
//function updateDiscount(selectElement, priceId, discountId, totalId, ageModalId, vipModalId, idNumberInputId) {
//    document.getElementById(idNumberInputId).readOnly = false;
//    let selectedOption = selectElement.value;
//    let basePrice = parseFloat(document.getElementById(priceId).value) || 0;
//
//    if (selectedOption === "Trẻ em" || selectedOption === "Người cao tuổi") {
//        document.getElementById(ageModalId).style.display = 'flex';
//        return;
//    }
//    if (selectedOption === "VIP") {
//        document.getElementById(vipModalId).style.display = 'flex';
//        return;
//    }
//
//    let rate = discountRates[selectedOption] || 0;
//    let discountAmount = basePrice * rate / 100;
//    let finalPrice = basePrice - discountAmount + 1;
//    document.getElementById(discountId).innerText = '-' + rate + '%';
//    document.getElementById(totalId).innerText = finalPrice.toLocaleString() + ' VND';
//    updateTotalAmount();
//}
function updateDiscount(
        selectElement, priceId, discountId, totalId,
        ageModalId, vipModalId, idNumberInputId
        ) {
    // Lấy row (hoặc cell) chứa cờ
    let rowEl = selectElement.closest('tr');
    // Đọc thuộc tính data-confirmedDOB (kiểu string "true" hoặc "false")
    let alreadyConfirmed = (rowEl.getAttribute('data-confirmedDOB') === 'true');

    // Mở khoá input
    document.getElementById(idNumberInputId).readOnly = false;

    let selectedOption = selectElement.value;
    let basePrice = parseFloat(document.getElementById(priceId).value) || 0;

    // Nếu là "Trẻ em" / "Người cao tuổi" mà *chưa* confirmed => mở popup
//    let alreadyConfirmed = (rowEl.getAttribute('data-confirmedDOB') === 'true');
    const isChildOrOld = (selectedOption === "Trẻ em" || selectedOption === "Người cao tuổi");
    if (isChildOrOld && !alreadyConfirmed) {
        // mở popup
        document.getElementById(ageModalId).style.display = 'flex';
        return; // chờ user nhập DOB
    }

    // Nếu là VIP => popup VIP (nếu muốn)
    if (selectedOption === "VIP") {
        document.getElementById(vipModalId).style.display = 'flex';
        return;
    }

    // Còn lại (Người lớn, Sinh viên, hoặc "Trẻ em"/"NCT" đã confirmDOB) => tính discount ngay
    let rate = discountRates[selectedOption] || 0;
    let discountAmount = basePrice * rate / 100;
    let finalPrice = basePrice - discountAmount + 1;
    document.getElementById(discountId).innerText = '-' + rate + '%';
    document.getElementById(totalId).innerText = finalPrice.toLocaleString() + ' VND';
    updateTotalAmount();
}

// Hàm tính tổng (client)
function updateTotalAmount() {
    let sum = 0;
    document.querySelectorAll('[id^="displayTotal"]').forEach(function (elem) {
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

// Đóng modal
function closeModal(id) {
    document.getElementById(id).style.display = 'none';
}

// Tính tuổi (client)
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

// Xác nhận tuổi
//function confirmAge(modalId, selectId, priceId, discountId, totalId,
//                     dayId, monthId, yearId, idNumberInputId) {
//  closeModal(modalId);
//  let day = document.getElementById(dayId).value;
//  let month = document.getElementById(monthId).value;
//  let year = document.getElementById(yearId).value;
//  let age = getAge(day, month, year);
//
//  let basePrice = parseFloat(document.getElementById(priceId).value) || 0;
//  let selectedOption = document.getElementById(selectId).value;
//  let rate = 0;
//
//  if (selectedOption === "Trẻ em") {
//    if (age < 6) {
//      alert("Trẻ em dưới 6 tuổi không cần vé. Vui lòng xóa vé này nếu không cần.");
//      rate = 0;
//    } else if (age > 10) {
//      alert("Không đúng độ tuổi Trẻ em (6-10)!");
//      rate = 0;
//    } else {
//      rate = 50;
//      let dobString = day.padStart(2, '0') + "/" + month.padStart(2, '0') + "/" + year;
//      document.getElementById(idNumberInputId).value = dobString;
//      document.getElementById(idNumberInputId).readOnly = true;
//    }
//  } else {
//    // Người cao tuổi
//    if (age < 60) {
//      alert("Chưa đủ 60 tuổi để giảm giá Người cao tuổi!");
//      rate = 0;
//    } else {
//      rate = 30;
//    }
//  }
//
//  let discountAmount = basePrice * rate / 100;
//  let finalPrice = basePrice - discountAmount + 1;
//  document.getElementById(discountId).innerText = '-' + rate + '%';
//  document.getElementById(totalId).innerText = finalPrice.toLocaleString() + ' VND';
//  updateTotalAmount();
//}
function confirmAge(
        modalId, selectId, priceId, discountId, totalId,
        dayId, monthId, yearId, idNumberInputId
        ) {
    closeModal(modalId);

    // Tìm row
    let rowEl = document.getElementById(selectId).closest('tr');

    let day = document.getElementById(dayId).value;
    let month = document.getElementById(monthId).value;
    let year = document.getElementById(yearId).value;
    let age = getAge(day, month, year);

    let basePrice = parseFloat(document.getElementById(priceId).value) || 0;
    let selectedOption = document.getElementById(selectId).value;
    let rate = 0;

    if (selectedOption === "Trẻ em") {
        // ... check age <6 => skip ...
        rate = 50;
        // set readOnly
        document.getElementById(idNumberInputId).readOnly = true;
    } else if (selectedOption === "Người cao tuổi") {
        // ... check age <60 => skip ...
        rate = 30;
        // KHÔNG readOnly (tuỳ ý)
    }

    // Gửi Ajax confirmDOB => server
    if (rate > 0) {
        let dobString = day.padStart(2, '0') + "/" + month.padStart(2, '0') + "/" + year;
        let idx = selectId.replace('passengerType', '');
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

        // *** cờ local => "true" => LẦN SAU KHÔNG HIỆN POPUP ***
        rowEl.setAttribute('data-confirmedDOB', 'true');
    }

    // Tính finalPrice
    let discountAmount = basePrice * rate / 100;
    let finalPrice = basePrice - discountAmount + 1;
    document.getElementById(discountId).innerText = '-' + rate + '%';
    document.getElementById(totalId).innerText = finalPrice.toLocaleString() + ' VND';
    updateTotalAmount();
}



// Xác nhận VIP
function confirmVIP(modalId, selectId, priceId, discountId, totalId) {
    let vipInputId = 'vipCard' + selectId.replace('passengerType', '');
    let vipCard = document.getElementById(vipInputId).value.trim();
    if (vipCard === "") {
        alert("Vui lòng nhập thông tin thẻ VIP!");
        return;
    }
    closeModal(modalId);
    let basePrice = parseFloat(document.getElementById(priceId).value) || 0;
    let rate = 10;
    let discountAmount = basePrice * rate / 100;
    let finalPrice = basePrice - discountAmount + 1000;
    document.getElementById(discountId).innerText = '-10%';
    document.getElementById(totalId).innerText = finalPrice.toLocaleString() + ' VND';
    updateTotalAmount();
}
function reapplyDiscount() {
    // Sau khi thay .innerHTML = result
    // Lặp qua tất cả các <select> có id bắt đầu với "passengerType"
    document.querySelectorAll('select[id^="passengerType"]').forEach(selectEl => {
        let idx = selectEl.id.replace('passengerType', '');
        updateDiscount(
                selectEl,
                'price' + idx,
                'discount' + idx,
                'displayTotal' + idx,
                'ageModal' + idx,
                'vipModal' + idx,
                'idNumber' + idx
                );
    });
}
// Gắn lại nút "Xóa vé"
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
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: params.toString()
            })
                    .then(resp => resp.text())
                    .then(result => {
                        if (result === "EMPTY") {
                            // Giỏ trống => quay lại schedule
                            // Ở đây có thể lấy URL từ sessionStorage hoặc data attribute
                            // Hoặc in sẵn từ JSP (nhưng cẩn thận chuỗi <%= ... %>)
                            window.location.href = "schedule";
                            return;
                        }
                        // Thay thế table cũ
                        document.querySelector(".table-responsive").innerHTML = result;
                        // rebind + tính lại
                        reapplyDiscount();
                        rebindRemoveButtons();
                        updateTotalAmount();
                    })
                    .catch(err => console.error(err));
        });
    });
}