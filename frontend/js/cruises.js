const CruisesPage = {

    all: [],

    async init() {
        showLoader('cruises-container');
        try {
            this.all = await CruisesAPI.getAvailable();
            await this.loadTypes();
            this.render(this.all);
        } catch (e) {
            document.getElementById('cruises-container').innerHTML =
                `<div class="alert alert-error">Не удалось загрузить круизы: ${e.message}</div>`;
        }
    },

    async loadTypes() {
        try {
            const types = await TypesAPI.getAll();
            const sel = document.getElementById('filter-type');
            if (!sel) return;
            types.forEach(t => {
                const opt = document.createElement('option');
                opt.value = t.name;
                opt.textContent = t.name;
                sel.appendChild(opt);
            });
        } catch (_) {}
    },

    render(list) {
        const el = document.getElementById('cruises-container');
        if (!el) return;

        if (!list.length) {
            el.innerHTML = `
                <div class="empty">
                    <div class="empty-icon">⚓</div>
                    <div class="empty-title">Круизы не найдены</div>
                    <div class="empty-text">Попробуйте изменить параметры поиска</div>
                </div>`;
            return;
        }

        el.innerHTML = `<div class="cruise-grid">${list.map(c => this.cardHTML(c)).join('')}</div>`;
    },

    cardHTML(c) {
        const imgSrc = c.imageUrl
            ? `<img src="${c.imageUrl}" alt="${c.cruiseType}">`
            : `<div class="img-placeholder">🚢</div>`;

        return `
        <div class="cruise-card" onclick="window.location.href='/html/cruise-detail.html?id=${c.id}'">
            <div class="cruise-card-img">${imgSrc}</div>
            <div class="cruise-card-body">
                <div class="cruise-card-type">${c.cruiseType}</div>
                <div class="cruise-card-title">${c.cruiseType} круиз</div>
                <div class="cruise-card-desc">${c.description || ''}</div>
                <div class="cruise-card-meta">
                    <span>📅 ${formatDate(c.departureDate)}</span>
                    <span>🌙 ${c.durationDays} ночей</span>
                    <span>⚓ ${c.shipName}</span>
                </div>
                <div class="cruise-card-footer">
                    <div>
                        <div class="cruise-price-label">от</div>
                        <div class="cruise-price-value">${formatPrice(c.basePrice)}</div>
                    </div>
                    <button class="btn btn-primary btn-sm">Подробнее</button>
                </div>
            </div>
        </div>`;
    },

    applyFilters() {
        const type  = document.getElementById('filter-type')?.value  || '';
        const date  = document.getElementById('filter-date')?.value  || '';

        let list = this.all;
        if (type) list = list.filter(c => c.cruiseType === type);
        if (date) list = list.filter(c => c.departureDate >= date);
        this.render(list);
    }
};

document.addEventListener('DOMContentLoaded', () => CruisesPage.init());