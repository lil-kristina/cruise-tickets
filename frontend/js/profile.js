const ProfilePage = {

    async init() {
        if (!Auth.requireAuth()) return;
        await Promise.all([this.loadProfile(), this.loadTickets()]);
    },

    async loadProfile() {
        try {
            const u = await UsersAPI.getMe();
            document.getElementById('profile-avatar').textContent = u.name.charAt(0).toUpperCase();
            document.getElementById('profile-name').textContent   = u.name;
            document.getElementById('profile-email').textContent  = u.email;
            document.getElementById('profile-role').textContent   =
                { USER: 'Пользователь', ADMIN: 'Администратор', OWNER: 'Владелец' }[u.role] || u.role;
        } catch (e) {
            showAlert('profile-alert', 'Не удалось загрузить профиль: ' + e.message);
        }
    },

    async loadTickets() {
        showLoader('tickets-container');
        try {
            const tickets = await TicketsAPI.getMy();
            this.renderTickets(tickets);
        } catch (e) {
            document.getElementById('tickets-container').innerHTML =
                `<div class="alert alert-error">Не удалось загрузить билеты: ${e.message}</div>`;
        }
    },

    renderTickets(tickets) {
        const el = document.getElementById('tickets-container');

        if (!tickets.length) {
            el.innerHTML = `
                <div class="empty">
                    <div class="empty-icon">🎫</div>
                    <div class="empty-title">Билетов пока нет</div>
                    <div class="empty-text">
                        <a href="/index.html" class="text-teal">Выберите круиз</a> и забронируйте место
                    </div>
                </div>`;
            return;
        }

        el.innerHTML = `
            <div class="table-wrap">
                <table>
                    <thead>
                        <tr>
                            <th>Круиз</th>
                            <th>Дата</th>
                            <th>Лайнер</th>
                            <th>Класс</th>
                            <th>Цена</th>
                            <th>Статус</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        ${tickets.map(t => `
                        <tr>
                            <td><strong>${t.cruiseType}</strong></td>
                            <td>${formatDate(t.departureDate)}</td>
                            <td>${t.shipName}</td>
                            <td><span class="badge ${classBadge(t.ticketClass)}">${classLabel(t.ticketClass)}</span></td>
                            <td>${formatPrice(t.price)}</td>
                            <td><span class="badge badge-${t.status.toLowerCase()}">${statusLabel(t.status)}</span></td>
                            <td>
                                ${t.status === 'ACTIVE' ? `
                                <button class="btn btn-danger btn-sm" onclick="ProfilePage.cancel(${t.id})">
                                    Отменить
                                </button>` : ''}
                            </td>
                        </tr>`).join('')}
                    </tbody>
                </table>
            </div>`;
    },

    async cancel(id) {
        if (!confirm('Отменить билет?')) return;
        try {
            await TicketsAPI.cancel(id);
            await this.loadTickets();
        } catch (e) {
            showAlert('profile-alert', 'Ошибка отмены: ' + e.message);
        }
    }
};

document.addEventListener('DOMContentLoaded', () => ProfilePage.init());