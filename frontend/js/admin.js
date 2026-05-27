const AdminPage = {

    async init() {
        if (!Auth.requireRole('ADMIN', 'OWNER')) return;
        this.showPage('cruises');
        await this.loadCruises();
    },

    // ── Навигация 
    showPage(name) {
        document.querySelectorAll('.panel-page').forEach(p => p.classList.remove('active'));
        document.querySelectorAll('.panel-nav-btn').forEach(b => b.classList.remove('active'));
        document.getElementById('page-' + name)?.classList.add('active');
        document.getElementById('btn-' + name)?.classList.add('active');
    },

    // ── Круизы 
    async loadCruises() {
        showLoader('admin-cruises');
        try {
            const cruises = await CruisesAPI.getAll();
            this.renderCruises(cruises);
        } catch (e) {
            document.getElementById('admin-cruises').innerHTML =
                `<div class="alert alert-error">${e.message}</div>`;
        }
    },

    renderCruises(list) {
        const el = document.getElementById('admin-cruises');
        if (!list.length) {
            el.innerHTML = `<div class="empty"><div class="empty-icon">🚢</div><div class="empty-title">Рейсов нет</div></div>`;
            return;
        }
        el.innerHTML = `
            <div class="table-wrap">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th><th>Тип</th><th>Лайнер</th>
                            <th>Отплытие</th><th>Возвращение</th>
                            <th>Мест</th><th>Статус</th><th></th>
                        </tr>
                    </thead>
                    <tbody>
                        ${list.map(c => `
                        <tr>
                            <td>${c.id}</td>
                            <td>${c.cruiseType}</td>
                            <td>${c.shipName}</td>
                            <td>${formatDate(c.departureDate)}</td>
                            <td>${formatDate(c.returnDate)}</td>
                            <td>${c.availableSeats}</td>
                            <td><span class="badge ${c.isActive ? 'badge-active' : 'badge-cancelled'}">
                                ${c.isActive ? 'Активен' : 'Неактивен'}
                            </span></td>
                            <td>
                                ${c.isActive ? `
                                <button class="btn btn-danger btn-sm"
                                    onclick="AdminPage.deactivateCruise(${c.id})">
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
            showAlert('admin-alert', e.message);
        }
    },

    // ── Форма создания рейса 
    async showCreateForm() {
        this.showPage('create-cruise');
        showLoader('types-list');
        showLoader('ships-list');
        try {
            const [types, ships] = await Promise.all([TypesAPI.getAll(), ShipsAPI.getAll()]);

            document.getElementById('types-list').innerHTML =
                types.map(t => `<option value="${t.id}">${t.name}</option>`).join('');

            document.getElementById('ships-list').innerHTML =
                ships.map(s => `<option value="${s.id}">${s.name} (мест: ${s.capacity})</option>`).join('');
        } catch (e) {
            showAlert('create-alert', e.message);
        }
    },

    async createCruise() {
        clearAlert('create-alert');
        const body = {
            cruiseTypeId:   +document.getElementById('new-type').value,
            shipId:         +document.getElementById('new-ship').value,
            departureDate:  document.getElementById('new-departure').value,
            returnDate:     document.getElementById('new-return').value,
            availableSeats: +document.getElementById('new-seats').value,
        };

        try {
            await CruisesAPI.create(body);
            showAlert('create-alert', 'Рейс успешно создан!', 'success');
            await this.loadCruises();
        } catch (e) {
            showAlert('create-alert', e.message);
        }
    },

    // ── Все билеты 
    async loadTickets() {
        showLoader('admin-tickets');
        try {
            const tickets = await TicketsAPI.getAll();
            this.renderTickets(tickets);
        } catch (e) {
            document.getElementById('admin-tickets').innerHTML =
                `<div class="alert alert-error">${e.message}</div>`;
        }
    },

    renderTickets(list) {
        const el = document.getElementById('admin-tickets');
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
                            <th>Класс</th><th>Цена</th><th>Статус</th><th></th>
                        </tr>
                    </thead>
                    <tbody>
                        ${list.map(t => `
                        <tr>
                            <td>${t.id}</td>
                            <td>${t.userName}<br><small style="color:var(--text-light)">${t.userEmail}</small></td>
                            <td>${t.cruiseType}<br><small style="color:var(--text-light)">${formatDate(t.departureDate)}</small></td>
                            <td><span class="badge ${classBadge(t.ticketClass)}">${classLabel(t.ticketClass)}</span></td>
                            <td>${formatPrice(t.price)}</td>
                            <td><span class="badge badge-${t.status.toLowerCase()}">${statusLabel(t.status)}</span></td>
                            <td>
                                ${t.status === 'ACTIVE' ? `
                                <button class="btn btn-danger btn-sm"
                                    onclick="AdminPage.cancelTicket(${t.id})">
                                    Отменить
                                </button>` : ''}
                            </td>
                        </tr>`).join('')}
                    </tbody>
                </table>
            </div>`;
    },

    async cancelTicket(id) {
        if (!confirm('Отменить билет?')) return;
        try {
            await TicketsAPI.cancel(id);
            await this.loadTickets();
        } catch (e) {
            showAlert('admin-alert', e.message);
        }
    },

    // ── Пользователи 
    async loadUsers() {
        showLoader('admin-users');
        try {
            const users = await UsersAPI.getAll();
            this.renderUsers(users);
        } catch (e) {
            document.getElementById('admin-users').innerHTML =
                `<div class="alert alert-error">${e.message}</div>`;
        }
    },

    renderUsers(list) {
        const el = document.getElementById('admin-users');
        el.innerHTML = `
            <div class="table-wrap">
                <table>
                    <thead>
                        <tr><th>ID</th><th>Имя</th><th>Email</th><th>Возраст</th><th>Роль</th></tr>
                    </thead>
                    <tbody>
                        ${list.map(u => `
                        <tr>
                            <td>${u.id}</td>
                            <td>${u.name}</td>
                            <td>${u.email}</td>
                            <td>${u.age || '—'}</td>
                            <td><span class="badge badge-active">${u.role}</span></td>
                        </tr>`).join('')}
                    </tbody>
                </table>
            </div>`;
    }
};

document.addEventListener('DOMContentLoaded', () => AdminPage.init());