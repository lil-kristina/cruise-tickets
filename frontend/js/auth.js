const Auth = {

    save(data) {
        localStorage.setItem('sw_token',  data.token);
        localStorage.setItem('sw_role',   data.role);
        localStorage.setItem('sw_uid',    data.userId);
        localStorage.setItem('sw_name',   data.name);
    },

    logout() {
        ['sw_token','sw_role','sw_uid','sw_name'].forEach(k => localStorage.removeItem(k));
        window.location.href = '/index.html';
    },

    isLoggedIn() { return !!localStorage.getItem('sw_token'); },
    getRole()    { return localStorage.getItem('sw_role'); },
    getName()    { return localStorage.getItem('sw_name') || ''; },
    getUid()     { return localStorage.getItem('sw_uid'); },

    isUser()  { return this.getRole() === 'USER';  },
    isAdmin() { return this.getRole() === 'ADMIN'; },
    isOwner() { return this.getRole() === 'OWNER'; },

    requireAuth() {
        if (!this.isLoggedIn()) { window.location.href = '/html/login.html'; return false; }
        return true;
    },

    requireRole(...roles) {
        if (!this.isLoggedIn()) { window.location.href = '/html/login.html'; return false; }
        if (!roles.includes(this.getRole())) { window.location.href = '/index.html'; return false; }
        return true;
    },

    // Обновить кнопки в шапке
    updateNavbar() {
        const el = document.getElementById('nav-actions');
        if (!el) return;

        if (this.isLoggedIn()) {
            const initial = this.getName().charAt(0).toUpperCase();
            const role    = this.getRole();

            let panelBtn = '';
            if (role === 'ADMIN') panelBtn = `<a href="/html/admin.html" class="btn btn-outline btn-sm">Панель</a>`;
            if (role === 'OWNER') panelBtn = `<a href="/html/owner.html" class="btn btn-outline btn-sm">Панель</a>`;

            el.innerHTML = `
                ${panelBtn}
                <a href="/html/profile.html" class="nav-user">
                    <div class="nav-avatar">${initial}</div>
                    <span>${this.getName()}</span>
                </a>
                <button class="btn btn-outline btn-sm" onclick="Auth.logout()">Выйти</button>
            `;
        } else {
            el.innerHTML = `
                <a href="/html/login.html"    class="btn btn-outline btn-sm">Войти</a>
                <a href="/html/register.html" class="btn btn-primary btn-sm">Регистрация</a>
            `;
        }
    }
};

document.addEventListener('DOMContentLoaded', () => Auth.updateNavbar());