const API = 'http://localhost:8080/api';

async function req(method, path, body = null) {
    const token = localStorage.getItem('sw_token');
    const headers = { 'Content-Type': 'application/json' };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const opts = { method, headers };
    if (body) opts.body = JSON.stringify(body);

    const res = await fetch(API + path, opts);
    if (res.status === 204) return null;

    const data = await res.json();
    if (!res.ok) throw new Error(data.error || data.message || 'Ошибка сервера');
    return data;
}

// Auth
const AuthAPI = {
    register: (b) => req('POST', '/auth/register', b),
    login:    (b) => req('POST', '/auth/login',    b),
};

// Users
const UsersAPI = {
    getAll:  ()    => req('GET',    '/users'),
    getById: (id)  => req('GET',    `/users/${id}`),
    getMe:   ()    => req('GET',    '/users/me'),
    create:  (b)   => req('POST',   '/users', b),
    delete:  (id)  => req('DELETE', `/users/${id}`),
};

// Cruises
const CruisesAPI = {
    getAvailable: ()       => req('GET',    '/cruises'),
    getAll:       ()       => req('GET',    '/cruises/all'),
    getById:      (id)     => req('GET',    `/cruises/${id}`),
    create:       (b)      => req('POST',   '/cruises', b),
    update:       (id, b)  => req('PUT',    `/cruises/${id}`, b),
    deactivate:   (id)     => req('DELETE', `/cruises/${id}`),
};

// Cruise types
const TypesAPI = {
    getAll: () => req('GET', '/cruise-types'),
};

// Ships
const ShipsAPI = {
    getAll: () => req('GET', '/ships'),
};

// Tickets
const TicketsAPI = {
    getMy:    ()    => req('GET',  '/tickets/my'),
    getAll:   ()    => req('GET',  '/tickets'),
    purchase: (b)   => req('POST', '/tickets', b),
    cancel:   (id)  => req('PUT',  `/tickets/${id}/cancel`),
};

// Stats
const StatsAPI = {
    getSummary: () => req('GET', '/stats/summary'),
};