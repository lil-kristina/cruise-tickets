const DetailPage = {

    cruise: null,
    selectedClassId: null,
    classes: [
        { id: 1, name: 'ECONOMY',  label: 'Эконом',  desc: 'Стандартная каюта',         factor: 1.00 },
        { id: 2, name: 'BUSINESS', label: 'Бизнес',  desc: 'Каюта повышенного комфорта', factor: 1.75 },
        { id: 3, name: 'LUXURY',   label: 'Люкс',    desc: 'Люкс-каюта с балконом',      factor: 3.00 },
    ],

    async init() {
        const id = getParam('id');
        if (!id) { window.location.href = '/index.html'; return; }

        showLoader('detail-container');
        try {
            this.cruise = await CruisesAPI.getById(id);
            this.render();
        } catch (e) {
            document.getElementById('detail-container').innerHTML =
                `<div class="alert alert-error">Не удалось загрузить круиз: ${e.message}</div>`;
        }
    },

    render() {
        const c = this.cruise;

        // Герой
        const heroImg = c.imageUrl
            ? `<img src="${c.imageUrl}" alt="${c.cruiseType}">`
            : `<div class="img-placeholder">🚢</div>`;

        document.getElementById('cruise-hero').innerHTML = `
            ${heroImg}
            <div class="cruise-hero-overlay">
                <div class="container">
                    <div class="cruise-hero-content">
                        <div class="cruise-hero-type">${c.cruiseType}</div>
                        <div class="cruise-hero-title">${c.cruiseType} круиз</div>
                        <div class="cruise-hero-ship">⚓ ${c.shipName}</div>
                    </div>
                </div>
            </div>`;

        // Основной блок
        document.getElementById('cruise-info').innerHTML = `
            <div class="cruise-dates">
                <div>
                    <div class="date-label">Отплытие</div>
                    <div class="date-value">${formatDate(c.departureDate)}</div>
                </div>
                <div class="date-arrow">→</div>
                <div>
                    <div class="date-label">Возвращение</div>
                    <div class="date-value">${formatDate(c.returnDate)}</div>
                </div>
            </div>

            <div class="detail-info-row">
                <div class="detail-info-item">
                    <span class="detail-info-label">Продолжительность</span>
                    <span class="detail-info-value">${c.durationDays} ночей</span>
                </div>
                <div class="detail-info-item">
                    <span class="detail-info-label">Лайнер</span>
                    <span class="detail-info-value">${c.shipName}</span>
                </div>
                <div class="detail-info-item">
                    <span class="detail-info-label">Свободных мест</span>
                    <span class="detail-info-value">${c.availableSeats}</span>
                </div>
            </div>

            <p class="detail-desc">${c.description || 'Описание круиза.'}</p>`;

        // Карточка бронирования
        document.getElementById('booking-container').innerHTML = `
            <div class="booking-card">
                <div class="booking-card-title">Забронировать</div>
                <div id="price-options">
                    ${this.classes.map(cl => `
                    <div class="price-option" data-id="${cl.id}" onclick="DetailPage.selectClass(${cl.id})">
                        <div>
                            <div class="price-option-name">${cl.label}</div>
                            <div class="price-option-desc">${cl.desc}</div>
                        </div>
                        <div class="price-option-price">${formatPrice(c.basePrice * cl.factor)}</div>
                    </div>`).join('')}
                </div>
                <div class="booking-total">
                    <span class="booking-total-label">Итого</span>
                    <span class="booking-total-price" id="total-price">Выберите класс</span>
                </div>
                <div id="booking-alert"></div>
                <button class="btn btn-primary btn-full btn-lg" onclick="DetailPage.purchase()">
                    Купить билет
                </button>
                <p style="font-size:12px;color:var(--text-light);text-align:center;margin-top:10px;">
                    ${c.availableSeats} мест доступно
                </p>
            </div>`;
    },

    selectClass(id) {
        this.selectedClassId = id;
        document.querySelectorAll('.price-option').forEach(el => {
            el.classList.toggle('selected', +el.dataset.id === id);
        });
        const cl = this.classes.find(c => c.id === id);
        const price = this.cruise.basePrice * cl.factor;
        document.getElementById('total-price').textContent = formatPrice(price);
    },

    async purchase() {
        if (!Auth.requireAuth()) return;

        if (!this.selectedClassId) {
            showAlert('booking-alert', 'Выберите класс билета');
            return;
        }

        clearAlert('booking-alert');
        try {
            await TicketsAPI.purchase({
                cruiseId:      this.cruise.id,
                ticketClassId: this.selectedClassId
            });
            showAlert('booking-alert', 'Билет успешно куплен! Проверьте раздел «Мои билеты».', 'success');
        } catch (e) {
            showAlert('booking-alert', e.message);
        }
    }
};

document.addEventListener('DOMContentLoaded', () => DetailPage.init());