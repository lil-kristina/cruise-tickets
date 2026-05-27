// ── Форматирование даты 
function formatDate(str) {
    if (!str) return '—';
    const months = ['января','февраля','марта','апреля','мая','июня',
                    'июля','августа','сентября','октября','ноября','декабря'];
    const d = new Date(str);
    return `${d.getDate()} ${months[d.getMonth()]} ${d.getFullYear()}`;
}

// ── Форматирование цены 
function formatPrice(price) {
    if (price == null) return '—';
    return Number(price).toLocaleString('ru-RU') + ' $';
}

// ── Класс билета на русском 
function classLabel(name) {
    return { ECONOMY: 'Эконом', BUSINESS: 'Бизнес', LUXURY: 'Люкс' }[name] || name;
}

// ── CSS-класс бейджа класса 
function classBadge(name) {
    return { ECONOMY: 'badge-economy', BUSINESS: 'badge-business', LUXURY: 'badge-luxury' }[name] || '';
}

// ── Статус билета на русском 
function statusLabel(s) {
    return s === 'ACTIVE' ? 'Активен' : 'Отменён';
}

// ── Лоадер 
function showLoader(id) {
    const el = document.getElementById(id);
    if (el) el.innerHTML = `
        <div class="loader">
            <div class="loader-dot"></div>
            <div class="loader-dot"></div>
            <div class="loader-dot"></div>
        </div>`;
}

// ── Алерт 
function showAlert(id, msg, type = 'error') {
    const el = document.getElementById(id);
    if (!el) return;
    el.innerHTML = `<div class="alert alert-${type}">${msg}</div>`;
}

function clearAlert(id) {
    const el = document.getElementById(id);
    if (el) el.innerHTML = '';
}

// ── Query-параметр 
function getParam(name) {
    return new URLSearchParams(window.location.search).get(name);
}