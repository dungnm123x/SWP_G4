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
function updateDiscount(selectElement, priceId, discountId, totalId, ageModalId, vipModalId, idNumberInputId) {
    document.getElementById(idNumberInputId).readOnly = false;
    let selectedOption = selectElement.value;
    let basePrice = parseFloat(document.getElementById(priceId).value) || 0;

    if (selectedOption === "Trẻ em" || selectedOption === "Người cao tuổi") {
        document.getElementById(ageModalId).style.display = 'flex';
        return;
    }
    if (selectedOption === "VIP") {
        document.getElementById(vipModalId).style.display = 'flex';
        return;
    }

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
function confirmAge(modalId, selectId, priceId, discountId, totalId,
        dayId, monthId, yearId, idNumberInputId) {
    // 1) Đóng popup
    closeModal(modalId);

    // 2) Lấy giá trị ngày-tháng-năm
    let day = document.getElementById(dayId).value;
    let month = document.getElementById(monthId).value;
    let year = document.getElementById(yearId).value;

    // 3) Tính tuổi (client)
    let age = getAge(day, month, year);

    // 4) Lấy giá vé gốc và loại hành khách
    let basePrice = parseFloat(document.getElementById(priceId).value) || 0;
    let selectedOption = document.getElementById(selectId).value;

    // 5) Xác định discount rate
    let rate = 0;
    if (selectedOption === "Trẻ em") {
        if (age < 6) {
            alert("Trẻ em dưới 6 tuổi không cần vé. Vui lòng xóa vé này nếu không cần.");
            rate = 0;
        } else if (age > 10) {
            alert("Không đúng độ tuổi Trẻ em (6-10)!");
            rate = 0;
        } else {
            // Tuổi 6..10 => discount 50%
            rate = 50;
        }
    } else if (selectedOption === "Người cao tuổi") {
        if (age < 60) {
            alert("Chưa đủ 60 tuổi để giảm giá Người cao tuổi!");
            rate = 0;
        } else {
            // 60+ => discount 30%
            rate = 30;
        }
    }
    // (Người lớn, Sinh viên, VIP: logic discount ở chỗ khác)

    // 6) Nếu rate > 0, nghĩa là DOB hợp lệ => set readOnly và gửi Ajax lưu DOB
    if (rate > 0) {
        // Ghép DOB thành chuỗi dd/MM/yyyy
        let dobString = day.padStart(2, '0') + "/" + month.padStart(2, '0') + "/" + year;
        // Gán vào input
        document.getElementById(idNumberInputId).value = dobString;
        document.getElementById(idNumberInputId).readOnly = true;

        // Gửi Ajax lưu DOB lên server (action=confirmDOB)
        let idx = selectId.replace('passengerType', ''); // "passengerType2" -> "2"
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
                    // Ở đây server có thể trả về "OK" hoặc JSON,
                    // bạn tùy ý xử lý (hiển thị thông báo, v.v.)
                })
                .catch(err => console.error(err));
    }

    // 7) Tính discount + cập nhật giao diện
    let discountAmount = basePrice * rate / 100;
    let finalPrice = basePrice - discountAmount + 1;
    document.getElementById(discountId).innerText = '-' + rate + '%';
    document.getElementById(totalId).innerText = finalPrice.toLocaleString() + ' VND';

    // 8) Cập nhật tổng tiền
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
    let finalPrice = basePrice - discountAmount + 1;
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




