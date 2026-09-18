const state = { devices: [], cart: JSON.parse(localStorage.getItem('medily_cart') || '[]') };
const $ = (selector) => document.querySelector(selector);
const money = (value) => new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(value);
const esc = (value = '') => String(value).replace(/[&<>'"]/g, (char) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#039;', '"': '&quot;' }[char]));

async function loadCatalog() {
    try {
        const params = { search: $('#search').value, brand: $('#brand').value, category: $('#category').value };
        state.devices = await API.devices(params);
        $('#results-count').textContent = `${state.devices.length} dispositivo${state.devices.length === 1 ? '' : 's'}`;
        renderCatalog();
    } catch (error) { $('#catalog').innerHTML = `<div class="col-12"><div class="alert alert-danger">${esc(error.message)}</div></div>`; }
}
function renderCatalog() {
    $('#catalog').innerHTML = state.devices.length ? state.devices.map((device) => `<div class="col-12 col-md-6 col-lg-4"><article class="card product-card border-0 shadow-sm h-100"><img class="product-card-image card-img-top" src="${esc(device.image)}" alt="${esc(device.name)}"><div class="card-body d-flex flex-column"><span class="small text-uppercase text-primary fw-bold">${esc(device.brand)}</span><h3 class="h5 mt-2">${esc(device.name)}</h3><p class="price mb-3">${money(device.price)}</p><p class="text-secondary small flex-grow-1">${esc(device.description)}</p><div class="d-grid gap-2"><button class="btn btn-primary" data-action="cart" data-id="${device.id}"><i class="bi bi-cart-plus"></i> Agregar al carrito</button><button class="btn btn-outline-primary" data-action="detail" data-id="${device.id}">Ver detalle</button></div></div></article></div>`).join('') : '<div class="col-12"><div class="empty-state"><i class="bi bi-phone fs-1"></i><p class="mt-3 mb-0">No se encontraron dispositivos con estos filtros.</p></div></div>';
}
async function showDetail(id) {
    const device = await API.request(`/devices/${id}`);
    $('#device-modal-title').textContent = device.name;
    $('#device-modal-body').innerHTML = `<div class="row g-4"><div class="col-md-5"><img class="detail-image" src="${esc(device.image)}" alt="${esc(device.name)}"></div><div class="col-md-7"><span class="eyebrow">${esc(device.brand)}</span><p class="detail-price mt-2">${money(device.price)}</p><p class="text-secondary">${esc(device.description)}</p><dl class="row small"><dt class="col-5">Categoria</dt><dd class="col-7">${esc(device.category)}</dd><dt class="col-5">Lanzamiento</dt><dd class="col-7">${esc(device.releaseDate || 'No registrado')}</dd></dl><h3 class="h6 mt-4">Caracteristicas</h3><ul class="list-group mb-3">${Object.entries(device.features || {}).map(([key, value]) => `<li class="list-group-item d-flex justify-content-between gap-3"><strong>${esc(key)}</strong><span class="text-end">${esc(value)}</span></li>`).join('')}</ul><button class="btn btn-primary" data-action="cart" data-id="${device.id}"><i class="bi bi-cart-plus"></i> Agregar al carrito</button></div></div>`;
    bootstrap.Modal.getOrCreateInstance($('#device-modal')).show();
}
function addToCart(id) { const device = state.devices.find((item) => item.id === Number(id)); if (device) state.cart.push(device); localStorage.setItem('medily_cart', JSON.stringify(state.cart)); renderCart(); toast('Producto agregado al carrito'); }
function renderCart() { $('#cart-count').textContent = state.cart.length; $('#cart-items').innerHTML = state.cart.length ? state.cart.map((device, index) => `<div class="d-flex gap-3 border-bottom py-3"><img src="${esc(device.image)}" width="56" height="56" class="rounded object-fit-cover" alt=""><div class="flex-grow-1"><strong>${esc(device.name)}</strong><div class="small text-secondary">${money(device.price)}</div></div><button class="btn btn-sm btn-outline-danger" data-action="remove-cart" data-index="${index}" aria-label="Quitar"><i class="bi bi-trash"></i></button></div>`).join('') : '<p class="text-secondary">Tu carrito esta vacio.</p>'; }
function toast(message) { $('#toast-container').innerHTML = `<div class="toast show" role="status"><div class="toast-body">${esc(message)}</div></div>`; setTimeout(() => { $('#toast-container').innerHTML = ''; }, 2200); }
document.addEventListener('DOMContentLoaded', async () => {
    try {
        const options = await API.options();
        options.brands.forEach((value) => $('#brand').insertAdjacentHTML('beforeend', `<option value="${esc(value)}">${esc(value)}</option>`));
        options.categories.forEach((value) => $('#category').insertAdjacentHTML('beforeend', `<option value="${esc(value)}">${esc(value)}</option>`));
    } catch (error) { $('#catalog').innerHTML = `<div class="col-12"><div class="alert alert-danger">${esc(error.message)}</div></div>`; }
    ['search', 'brand', 'category'].forEach((id) => $('#' + id).addEventListener(id === 'search' ? 'input' : 'change', loadCatalog));
    $('#clear-filters').addEventListener('click', () => { $('#search').value = ''; $('#brand').value = ''; $('#category').value = ''; loadCatalog(); });
    document.body.addEventListener('click', async (event) => { const button = event.target.closest('[data-action]'); if (!button) return; if (button.dataset.action === 'cart') addToCart(button.dataset.id); if (button.dataset.action === 'detail') await showDetail(button.dataset.id); if (button.dataset.action === 'remove-cart') { state.cart.splice(Number(button.dataset.index), 1); localStorage.setItem('medily_cart', JSON.stringify(state.cart)); renderCart(); } });
    renderCart();
    await loadCatalog();
});
