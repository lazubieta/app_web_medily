const API = {
    async request(path, options = {}) {
        const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
        const response = await fetch(`/api${path}`, { ...options, headers, credentials: 'same-origin' });
        if (!response.ok) {
            let message = `Error ${response.status}`;
            try { message = (await response.json()).message || message; } catch (_) { /* respuesta sin JSON */ }
            const error = new Error(message);
            error.status = response.status;
            throw error;
        }
        return response.status === 204 ? null : response.json();
    },
    devices(params = {}) {
        const query = new URLSearchParams(Object.entries(params).filter(([, value]) => value));
        return this.request(`/devices${query.toString() ? `?${query}` : ''}`);
    },
    session() { return this.request('/auth/session'); },
    options() { return this.request('/devices/options'); },
    databaseHealth() { return this.request('/health/database'); },
    login(identity, password) { return this.request('/auth/login', { method: 'POST', body: JSON.stringify({ identity, password }) }); },
    logout() { return this.request('/auth/logout', { method: 'DELETE' }); },
    saveDevice(device) { return this.request(device.id ? `/devices/${device.id}` : '/devices', { method: device.id ? 'PUT' : 'POST', body: JSON.stringify(device) }); },
    deleteDevice(id) { return this.request(`/devices/${id}`, { method: 'DELETE' }); },
    categories() { return this.request('/admin/categories'); },
    saveCategory(category) { return this.request(category.id ? `/admin/categories/${category.id}` : '/admin/categories', { method: category.id ? 'PUT' : 'POST', body: JSON.stringify(category) }); },
    deleteCategory(id) { return this.request(`/admin/categories/${id}`, { method: 'DELETE' }); },
};
