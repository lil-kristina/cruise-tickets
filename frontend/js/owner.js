const OwnerPage = {

    async init() {
        if (!Auth.requireRole('OWNER')) return;
        this.showPage('stats');
        await this.loadStats();
    },

    showPage(name) {
        document.querySelectorAll('.panel-page').forEach(p => p.classList.remove('active'));
        document.querySelectorAll('.panel-nav-btn').forEach(b => b.classList.remove('active'));
        document.getElementById('page-' + name)?.classList.add('active');
        document.getElementById('btn-' + name)?.classList.add('active');
    },

    // ── Статистика 
    async loadStats() {
        try {
            const s = await StatsAPI.getSummary();
            document.getElementById('stat-users').textContent    = s.totalUsers;
            document.getElementById('stat-cruises').textContent  = s.totalCruises;
            document.getElementById('stat-tickets').textContent  = s.totalTickets;
            document.getElementById('stat-revenue').textContent  = formatPrice(s.totalRevenue);
        } catch (e) {
            showAlert('owner-alert', 'Не удалось загрузить статистику: ' + e.message);
        }
    },

    // ── Все билеты 
    async loadTickets() {
        showLoader('owner-tickets');
        try {
            const tickets = await TicketsAPI.getAll();
            this.renderTickets(tickets);
        } catch (e) {
            document.getElementById('owner-tickets').innerHTML =
                `<div class="alert alert-error">${e.message}</div>`;
        }
    },

    renderTickets(list) {
        const el = document.getElementById('owner-tickets');
        if (!list.length) {
            el.innerHTML = `<div class="empty"><div class="empty-icon">🎫</div><div class="empty-title">Билетов нет</div></div>`;
            return;
        }
        el.innerHTML = `
            <div class="table-wrap">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th><th>Пользователь</th><th>Круиз</th>
                            <th>Класс</th><th>Цена</th><th>Дата покупки</th><th>Статус</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${list.map(t => `
                        <tr>
                            <td>${t.id}</td>
                            <td>${t.userName}<br><small style="color:var(--text-light)">${t.userEmail}</small></td>
                            <td>${t.cruiseType}<br><small style="color:var(--text-light)">${formatDate(t.departureDate)}</small></td>
                            <td><span class="badge ${classBadge(t.ticketClass)}">${classLabel(t.ticketClass)}</span></td>
                            <td><strong>${formatPrice(t.price)}</strong></td>
                            <td>${formatDate(t.purchaseDate)}</td>
                            <td><span class="badge badge-${t.status.toLowerCase()}">${statusLabel(t.status)}</span></td>
                        </tr>`).join('')}
                    </tbody>
                </table>
            </div>`;
    },

    // ── Пользователи 
    async loadUsers() {
        showLoader('owner-users');
        try {
            const users = await UsersAPI.getAll();
            this.renderUsers(users);
        } catch (e) {
            document.getElementById('owner-users').innerHTML =
                `<div class="alert alert-error">${e.message}</div>`;
        }
    },

    renderUsers(list) {
        const el = document.getElementById('owner-users');
        el.innerHTML = `
            <div class="table-wrap">
                <table>
                    <thead>
                        <tr><th>ID</th><th>Имя</th><th>Email</th><th>Возраст</th><th>Роль</th><th>Дата</th><th></th></tr>
                    </thead>
                    <tbody>
                        ${list.map(u => `
                        <tr>
                            <td>${u.id}</td>
                            <td>${u.name}</td>
                            <td>${u.email}</td>
                            <td>${u.age || '—'}</td>
                            <td><span class="badge badge-active">${u.role}</span></td>
                            <td>${formatDate(u.createdAt)}</td>
                            <td>
                                <button class="btn btn-danger btn-sm"
                                    onclick="OwnerPage.deleteUser(${u.id}, '${u.name}')">
                                    Удалить
                                </button>
                            </td>
                        </tr>`).join('')}
                    </tbody>
                </table>
            </div>`;
    },

    async deleteUser(id, name) {
        if (!confirm(`Удалить пользователя «${name}»?`)) return;
        try {
            await UsersAPI.delete(id);
            await this.loadUsers();
        } catch (e) {
            showAlert('owner-alert', 'Ошибка удаления: ' + e.message);
        }
    },

    // ── Все круизы 
    async loadCruises() {
        showLoader('owner-cruises');
        try {
            const cruises = await CruisesAPI.getAll();
            this.renderCruises(cruises);
        } catch (e) {
            document.getElementById('owner-cruises').innerHTML =
                `<div class="alert alert-error">${e.message}</div>`;
        }
    },

    renderCruises(list) {
        const el = document.getElementById('owner-cruises');
        el.innerHTML = `
            <div class="table-wrap">
                <table>
                    <thead>
                        <tr><th>ID</th><th>Тип</th><th>Лайнер</th><th>Отплытие</th><th>Мест</th><th>Статус</th><th></th></tr>
                    </thead>
                    <tbody>
                        ${list.map(c => `
                        <tr>
                            <td>${c.id}</td>
                            <td>${c.cruiseType}</td>
                            <td>${c.shipName}</td>
                            <td>${formatDate(c.departureDate)}</td>
                            <td>${c.availableSeats}</td>
                            <td><span class="badge ${c.isActive ? 'badge-active' : 'badge-cancelled'}">
                                ${c.isActive ? 'Активен' : 'Неактивен'}
                            </span></td>
                            <td>
                                ${c.isActive ? `
                                <button class="btn btn-danger btn-sm"
                                    onclick="OwnerPage.deactivateCruise(${c.id})">
                                    Деактивировать
                                </button>` : ''}
                            </td>
                        </tr>`).join('')}
                    </tbody>
                </table>
            </div>`;
    },

    async deactivateCruise(id) {
        if (!confirm('Деактивировать этот рейс?')) return;
        try {
            await CruisesAPI.deactivate(id);
            await this.loadCruises();
        } catch (e) {
            showAlert('owner-alert', e.message);
        }
    }
};

document.addEventListener('DOMContentLoaded', () => OwnerPage.init());