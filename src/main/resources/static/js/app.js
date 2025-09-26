// app.js

// تابع خروج
function logout() {
    fetch('/api/auth/logout', {
        method: 'POST',
        credentials: 'include'
    })
        .then(resp => {
            if (resp.ok) {
                window.location.href = '/auth/login';
            } else {
                console.error("Logout failed", resp.status);
                alert("خروج ناموفق بود");
            }
        })
        .catch(err => {
            console.error("Logout error", err);
            alert("خطا در ارتباط با سرور");
        });
}

// تابع بررسی وضعیت کاربر و به‌روزرسانی هدر
async function updateHeaderAuthButtons() {
    try {
        const resp = await fetch('/api/auth/current-user', { credentials: 'include' });
        const container = document.querySelector('.d-flex'); // div دکمه‌ها
        container.innerHTML = ''; // پاک کردن محتوای فعلی

        if (resp.ok) {
            const user = await resp.json();
            // کاربر وارد شده است، نمایش پروفایل و خروج
            container.innerHTML = `
                <a href="/profile" class="btn btn-outline-light me-2">
                    <i class="bi bi-person-circle"></i> پروفایل
                </a>
                <button type="button" class="btn btn-outline-light" onclick="logout()">
                    <i class="bi bi-box-arrow-left"></i> خروج
                </button>
            `;
        } else {
            // کاربر وارد نشده است، نمایش ورود و ثبت‌نام
            container.innerHTML = `
                <a href="/auth/login" class="btn btn-outline-light me-2">
                    <i class="bi bi-box-arrow-in-left"></i> ورود
                </a>
                <a href="/register" class="btn btn-light">
                    <i class="bi bi-person-plus"></i> ثبت‌نام
                </a>
            `;
        }
    } catch (err) {
        console.error("Error fetching user status:", err);
    }
}

// اجرا هنگام لود صفحه
document.addEventListener('DOMContentLoaded', updateHeaderAuthButtons);
