// ── DATA MODELS ──────────────────────────────────────────────
const CATEGORIES = {
  ELECTRONICS: { rack: 'Rack A', code: 'EA' },
  GROCERY:     { rack: 'Rack B', code: 'GB' },
  CLOTHING:    { rack: 'Rack C', code: 'CL' },
  FURNITURE:   { rack: 'Rack D', code: 'FU' },
  SPORTS:      { rack: 'Rack E', code: 'SP' },
  BOOKS:       { rack: 'Rack F', code: 'BK' },
  TOYS:        { rack: 'Rack G', code: 'TY' },
  BEAUTY:      { rack: 'Rack H', code: 'BE' },
  MEDICINE:    { rack: 'Rack I', code: 'MD' },
  OTHER:       { rack: 'Rack J', code: 'OT' }
};

// No hardcoded accounts — all users register via Sign Up
// First registered user is automatically ADMIN
const EMPLOYEES = []; // kept for compatibility; all accounts in localStorage

let idCounter = 1000;
class Product {
  constructor(name, category, price, qty) {
    const cat = CATEGORIES[category];
    this.id       = cat.code + '-' + (++idCounter);
    this.name     = name;
    this.category = category;
    this.price    = parseFloat(price);
    this.qty      = parseInt(qty);
    this.rack     = cat.rack;
    this.shelf    = 'SH-' + idCounter;
  }
  get isLowStock() { return this.qty < 5; }
  get totalValue() { return this.price * this.qty; }
}

// ── STATE ─────────────────────────────────────────────────────
let inventory = [];
let shipmentLog = [];
let dispatchQueue = [];
let currentUser = null;
let trackCounter = 2000;

// ── FIRST-RUN CHECK ───────────────────────────────────────────
function isFirstRun() {
  return getStoredUsers().length === 0;
}

function checkFirstRun() {
  if (isFirstRun()) {
    // Force Sign Up tab and lock to admin setup
    switchTab('signup');
    // Hide the Sign In tab until an admin exists
    document.getElementById('tab-signin').style.display = 'none';
    // Lock role to ADMIN for first user
    document.getElementById('su-role').value = 'ADMIN';
    document.getElementById('su-role').disabled = true;
    // Show first-run banner
    const banner = document.getElementById('first-run-banner');
    if (banner) banner.style.display = 'block';
  } else {
    document.getElementById('tab-signin').style.display = '';
    document.getElementById('su-role').disabled = false;
    const banner = document.getElementById('first-run-banner');
    if (banner) banner.style.display = 'none';
  }
}

// ── AUTH HELPERS ─────────────────────────────────────────────
// localStorage key for registered users
const USER_STORE_KEY = 'sw_users';

function getStoredUsers() {
  try { return JSON.parse(localStorage.getItem(USER_STORE_KEY) || '[]'); } catch { return []; }
}
function saveStoredUsers(users) {
  localStorage.setItem(USER_STORE_KEY, JSON.stringify(users));
}

/** Find user across built-in EMPLOYEES and localStorage users */
function findUser(email, pass) {
  const lc = email.toLowerCase();
  // built-in accounts
  const builtin = EMPLOYEES.find(e => e.email === lc && e.pass === pass);
  if (builtin) return builtin;
  // localStorage accounts
  const stored = getStoredUsers().find(u => u.email === lc && u.pass === pass);
  if (stored) return { id: stored.id, email: stored.email, name: stored.name, role: stored.role, pass: stored.pass };
  return null;
}

// ── TAB SWITCHING ────────────────────────────────────────────
function switchTab(tab) {
  // hide all panels
  document.querySelectorAll('.auth-panel').forEach(p => p.classList.remove('active'));
  document.querySelectorAll('.auth-tab').forEach(t => t.classList.remove('active'));
  // clear all messages
  ['login-error','signup-msg','fp-msg1','fp-msg2'].forEach(id => {
    const el = document.getElementById(id);
    if (el) { el.className = 'auth-msg'; el.textContent = ''; }
  });

  if (tab === 'signin') {
    document.getElementById('panel-signin').classList.add('active');
    document.getElementById('tab-signin').classList.add('active');
  } else if (tab === 'signup') {
    document.getElementById('panel-signup').classList.add('active');
    document.getElementById('tab-signup').classList.add('active');
  } else if (tab === 'forgot') {
    document.getElementById('panel-forgot').classList.add('active');
    // reset forgot steps
    document.getElementById('forgot-step1').style.display = 'block';
    document.getElementById('forgot-step2').style.display = 'none';
    document.getElementById('fp-email').value = '';
  }
}

// ── SIGN IN ──────────────────────────────────────────────────
function login() {
  const email = document.getElementById('empEmail').value.trim().toLowerCase();
  const pwd   = document.getElementById('empPass').value.trim();
  const emp   = findUser(email, pwd);
  const errEl = document.getElementById('login-error');

  if (!emp) {
    errEl.className = 'auth-msg error';
    errEl.textContent = '✘ Invalid email or password. Access denied.';
    const card = document.querySelector('.login-card');
    card.style.animation = 'none'; card.offsetHeight;
    card.style.animation = 'shake 0.4s ease';
    return;
  }
  errEl.className = 'auth-msg';
  currentUser = emp;
  document.getElementById('login-screen').style.display = 'none';
  document.getElementById('app').style.display = 'flex';
  document.getElementById('user-name').textContent = emp.name;
  document.getElementById('user-role').textContent = emp.role.replace(/_/g, ' ');
  // Inventory starts empty — admin adds products manually
  navigate('dashboard');
  updateStats();
}

// ── SIGN UP ──────────────────────────────────────────────────
function signUp() {
  const name  = document.getElementById('su-name').value.trim();
  const email = document.getElementById('su-email').value.trim().toLowerCase();
  const pass  = document.getElementById('su-pass').value;
  const pass2 = document.getElementById('su-pass2').value;
  const msg   = document.getElementById('signup-msg');

  // First user is always ADMIN; subsequent users choose their role
  const firstRun = isFirstRun();
  const role = firstRun ? 'ADMIN' : document.getElementById('su-role').value;

  if (!name || !email || !pass || !pass2) {
    msg.className = 'auth-msg error'; msg.textContent = '✘ All fields are required.'; return;
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    msg.className = 'auth-msg error'; msg.textContent = '✘ Enter a valid email address.'; return;
  }
  if (pass.length < 6) {
    msg.className = 'auth-msg error'; msg.textContent = '✘ Password must be at least 6 characters.'; return;
  }
  if (pass !== pass2) {
    msg.className = 'auth-msg error'; msg.textContent = '✘ Passwords do not match.'; return;
  }
  // Check duplicate
  const all = getStoredUsers().map(u => u.email);
  if (all.includes(email)) {
    msg.className = 'auth-msg error'; msg.textContent = '✘ This email is already registered.'; return;
  }

  const newUser = { id: 'EMP' + Date.now(), email, name, role, pass };
  const users = getStoredUsers();
  users.push(newUser);
  saveStoredUsers(users);

  const roleLabel = role.replace(/_/g, ' ');
  msg.className = 'auth-msg success';
  msg.textContent = firstRun
    ? `✔ Admin account created for ${name}! Please sign in to start adding products.`
    : `✔ Account created for ${name} (${roleLabel})! You can now sign in.`;

  ['su-name','su-email','su-pass','su-pass2'].forEach(id => document.getElementById(id).value = '');
  setTimeout(() => {
    checkFirstRun(); // re-evaluate — will now show Sign In tab
    switchTab('signin');
    document.getElementById('empEmail').value = email;
    document.getElementById('empPass').focus();
  }, 2000);
}

// ── FORGOT PASSWORD ───────────────────────────────────────────
let _fpEmail = '';

function forgotStep1() {
  const email = document.getElementById('fp-email').value.trim().toLowerCase();
  const msg   = document.getElementById('fp-msg1');
  if (!email) { msg.className = 'auth-msg error'; msg.textContent = '✘ Please enter your email.'; return; }

  const stored = getStoredUsers().find(u => u.email === email);
  if (!stored) {
    msg.className = 'auth-msg error'; msg.textContent = '✘ No account found with this email address.'; return;
  }
  // Allow reset
  _fpEmail = email;
  document.getElementById('forgot-step1').style.display = 'none';
  document.getElementById('forgot-step2').style.display = 'block';
}

function forgotStep2() {
  const newPass  = document.getElementById('fp-newpass').value;
  const newPass2 = document.getElementById('fp-newpass2').value;
  const msg      = document.getElementById('fp-msg2');

  if (newPass.length < 6) {
    msg.className = 'auth-msg error'; msg.textContent = '✘ Password must be at least 6 characters.'; return;
  }
  if (newPass !== newPass2) {
    msg.className = 'auth-msg error'; msg.textContent = '✘ Passwords do not match.'; return;
  }

  const users = getStoredUsers();
  const user  = users.find(u => u.email === _fpEmail);
  if (!user) { msg.className = 'auth-msg error'; msg.textContent = '✘ Error. Please try again.'; return; }

  user.pass = newPass;
  saveStoredUsers(users);
  msg.className = 'auth-msg success';
  msg.textContent = '✔ Password reset successful! Redirecting to Sign In...';
  setTimeout(() => {
    _fpEmail = '';
    switchTab('signin');
    document.getElementById('empEmail').value = user.email;
  }, 2000);
}

// ── PASSWORD VISIBILITY TOGGLE ────────────────────────────────
function togglePw(inputId, icon) {
  const input = document.getElementById(inputId);
  if (input.type === 'password') { input.type = 'text';     icon.textContent = '🙈'; }
  else                           { input.type = 'password'; icon.textContent = '👁';  }
}


function logout() {
  currentUser = null; inventory = []; shipmentLog = []; dispatchQueue = [];
  document.getElementById('app').style.display = 'none';
  document.getElementById('login-screen').style.display = 'flex';
  document.getElementById('empEmail').value = '';
  document.getElementById('empPass').value = '';
  switchTab('signin');
}


// ── NAVIGATION ────────────────────────────────────────────────
function navigate(page) {
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
  const pg = document.getElementById('page-' + page);
  if (pg) pg.classList.add('active');
  const nav = document.querySelector(`[data-page="${page}"]`);
  if (nav) nav.classList.add('active');
  if (page === 'dashboard')  updateStats();
  if (page === 'inventory')  renderInventory();
  if (page === 'warehouse')  renderWarehouse();
  if (page === 'lowstock')   renderLowStock();
  if (page === 'shiplog')    renderShipLog();
}

// ── DASHBOARD ─────────────────────────────────────────────────
function updateStats() {
  const totalUnits  = inventory.reduce((s,p) => s + p.qty, 0);
  const totalValue  = inventory.reduce((s,p) => s + p.totalValue, 0);
  const lowCount    = inventory.filter(p => p.isLowStock).length;
  set('stat-products', inventory.length);
  set('stat-units',    totalUnits);
  set('stat-value',    '₹' + totalValue.toLocaleString('en-IN', {maximumFractionDigits:0}));
  set('stat-lowstock', lowCount);
  if (lowCount > 0) document.getElementById('stat-lowstock').style.color = 'var(--danger)';
}

function set(id, val) {
  const el = document.getElementById(id);
  if (el) el.textContent = val;
}

// ── INVENTORY ─────────────────────────────────────────────────
function renderInventory(items) {
  const list = items || inventory;
  const tbody = document.getElementById('inv-tbody');
  if (!tbody) return;
  tbody.innerHTML = list.map(p => `
    <tr>
      <td><code style="color:var(--accent);font-size:12px">${p.id}</code></td>
      <td><strong>${p.name}</strong></td>
      <td><span class="badge badge-blue">${p.category}</span></td>
      <td>₹${p.price.toLocaleString('en-IN')}</td>
      <td>
        <span class="badge ${p.isLowStock ? 'badge-red' : 'badge-green'}">${p.qty} units</span>
      </td>
      <td><span style="color:var(--muted);font-size:12px">${p.rack} / ${p.shelf}</span></td>
      <td style="text-align:right">
        <button class="btn btn-ghost btn-sm" onclick="quickEdit('${p.id}')">Edit</button>
      </td>
    </tr>`).join('');
}

function searchInventory() {
  const kw = document.getElementById('search-kw').value.toLowerCase();
  const cat = document.getElementById('search-cat').value;
  let res = inventory.filter(p => {
    const nameMatch = p.name.toLowerCase().includes(kw) || p.name.toLowerCase().startsWith(kw) || p.id.toLowerCase().includes(kw);
    const catMatch  = !cat || p.category === cat;
    return nameMatch && catMatch;
  });
  renderInventory(res);
  document.getElementById('search-count').textContent = `${res.length} result(s)`;
}

function quickEdit(id) {
  const p = inventory.find(x => x.id === id);
  if (!p) return;
  const qty = prompt(`Update stock for "${p.name}"\nCurrent: ${p.qty} units\n\nEnter new quantity:`, p.qty);
  if (qty === null) return;
  const n = parseInt(qty);
  if (isNaN(n) || n < 0) { alert('Invalid quantity'); return; }
  p.qty = n;
  renderInventory();
  updateStats();
  if (p.isLowStock) alert(`⚠ LOW STOCK ALERT: "${p.name}" only has ${n} units remaining!`);
}

// ── ADD PRODUCT ───────────────────────────────────────────────
function addProduct() {
  const name  = document.getElementById('new-name').value.trim();
  const cat   = document.getElementById('new-cat').value;
  const price = parseFloat(document.getElementById('new-price').value);
  const qty   = parseInt(document.getElementById('new-qty').value);
  if (!name || !cat || isNaN(price) || isNaN(qty)) {
    showOutput('add-output', '✘ Please fill all fields correctly.', 'error'); return;
  }
  const p = new Product(name, cat, price, qty);
  inventory.push(p);
  updateStats();
  let msg = `✔ Product added!\n  ID     : ${p.id}\n  Rack   : ${p.rack} | Shelf: ${p.shelf}\n  Stock  : ${p.qty} units`;
  if (p.isLowStock) msg += '\n\n⚠ LowStockException: Quantity below threshold (5)!';
  showOutput('add-output', msg, p.isLowStock ? 'warn' : 'success');
  document.getElementById('new-name').value = '';
  document.getElementById('new-price').value = '';
  document.getElementById('new-qty').value = '';
}

// ── REMOVE PRODUCT ────────────────────────────────────────────
function removeProduct() {
  const id = document.getElementById('rm-id').value.trim();
  const idx = inventory.findIndex(p => p.id.toLowerCase() === id.toLowerCase());
  if (idx === -1) {
    showOutput('rm-output', `✘ ProductNotFoundException: No product found with ID "${id}"`, 'error'); return;
  }
  const name = inventory[idx].name;
  inventory.splice(idx, 1);
  updateStats();
  showOutput('rm-output', `✔ Product "${name}" removed successfully.`, 'success');
  document.getElementById('rm-id').value = '';
}

// ── BILLING ───────────────────────────────────────────────────
function calcBilling() {
  const id  = document.getElementById('bill-id').value.trim();
  const qty = parseInt(document.getElementById('bill-qty').value);
  const p   = inventory.find(x => x.id.toLowerCase() === id.toLowerCase());
  if (!p) { document.getElementById('billing-result').innerHTML = '<p style="color:var(--danger);padding:16px">✘ Product not found.</p>'; return; }
  if (isNaN(qty) || qty <= 0) { alert('Invalid quantity'); return; }
  const base    = p.price * qty;
  const gst     = base * 0.18;
  const storage = qty * 2.5;
  const ship    = base > 5000 ? 0 : 99;
  const total   = base + gst + storage + ship;
  const fmt = n => '₹' + n.toLocaleString('en-IN', {minimumFractionDigits:2, maximumFractionDigits:2});
  document.getElementById('billing-result').innerHTML = `
    <div class="billing-row"><span>Product</span><strong>${p.name}</strong></div>
    <div class="billing-row"><span>Quantity</span><span>${qty} units @ ${fmt(p.price)}</span></div>
    <div class="billing-row"><span>Base Price</span><span>${fmt(base)}</span></div>
    <div class="billing-row"><span>GST (18%)</span><span>${fmt(gst)}</span></div>
    <div class="billing-row"><span>Storage Fee</span><span>${fmt(storage)}</span></div>
    <div class="billing-row"><span>Shipping</span><span>${ship === 0 ? 'FREE (above ₹5,000)' : fmt(ship)}</span></div>
    <div class="billing-row"><span>TOTAL AMOUNT</span><span>${fmt(total)}</span></div>`;
}

// ── WAREHOUSE ─────────────────────────────────────────────────
function renderWarehouse() {
  const counts = {};
  inventory.forEach(p => { counts[p.category] = (counts[p.category] || 0) + 1; });
  const html = Object.entries(CATEGORIES).map(([cat, info]) => {
    const used = counts[cat] || 0; const total = 10;
    const pct  = Math.round((used / total) * 100);
    return `<div class="rack-card">
      <div class="rack-name">${info.rack}</div>
      <div class="rack-cat">${cat}</div>
      <div class="rack-bar-bg"><div class="rack-bar" style="width:${pct}%"></div></div>
      <div class="rack-stats"><span>${used}/${total} shelves</span><span>${pct}% used</span></div>
    </div>`;
  }).join('');
  document.getElementById('rack-grid').innerHTML = html;
}

// ── LOW STOCK ─────────────────────────────────────────────────
function renderLowStock() {
  const low = inventory.filter(p => p.isLowStock);
  const el  = document.getElementById('lowstock-list');
  if (!low.length) {
    el.innerHTML = '<div style="text-align:center;padding:40px;color:var(--success)">✔ All products adequately stocked!</div>';
    return;
  }
  el.innerHTML = low.map(p => `
    <div class="alert-card">
      <div class="alert-icon">⚠️</div>
      <div>
        <div class="alert-name">${p.name}</div>
        <div class="alert-detail">ID: ${p.id} | ${p.category} | ${p.rack}</div>
        <div style="font-size:11px;color:var(--danger);margin-top:4px">LowStockException thrown — Qty below threshold (5)</div>
      </div>
      <div class="alert-qty">${p.qty}</div>
    </div>`).join('');
}

// ── SHIPMENT ──────────────────────────────────────────────────
function receiveShipment() {
  const id  = document.getElementById('ship-pid').value.trim();
  const qty = parseInt(document.getElementById('ship-qty').value);
  const sup = document.getElementById('ship-sup').value.trim();
  const p   = inventory.find(x => x.id.toLowerCase() === id.toLowerCase());
  if (!p) { showOutput('ship-output','✘ ProductNotFoundException: Product not found','error'); return; }
  if (isNaN(qty) || qty <= 0) { showOutput('ship-output','✘ Invalid quantity','error'); return; }
  p.qty += qty;
  const tid = 'IN-' + (++trackCounter);
  shipmentLog.push({ tid, type:'INCOMING', product: p.name, qty, from: sup||'Supplier', to:'Warehouse', status:'DELIVERED', date: new Date().toLocaleDateString('en-IN') });
  updateStats();
  showOutput('ship-output', `✔ Shipment received!\n  Tracking ID : ${tid}\n  Product     : ${p.name}\n  Added       : +${qty} units\n  New Stock   : ${p.qty} units`, 'success');
}

function queueDispatch() {
  const id   = document.getElementById('disp-pid').value.trim();
  const qty  = parseInt(document.getElementById('disp-qty').value);
  const dest = document.getElementById('disp-dest').value.trim();
  const p    = inventory.find(x => x.id.toLowerCase() === id.toLowerCase());
  if (!p) { showOutput('disp-output','✘ ProductNotFoundException: Product not found','error'); return; }
  if (isNaN(qty) || qty <= 0) { showOutput('disp-output','✘ Invalid quantity','error'); return; }
  if (p.qty < qty) { showOutput('disp-output',`✘ InsufficientStockException: Only ${p.qty} units available, cannot dispatch ${qty}`,'error'); return; }
  const tid = 'OUT-' + (++trackCounter);
  dispatchQueue.push({ tid, type:'OUTGOING', product: p.name, pid: p.id, qty, from:'Warehouse', to: dest||'Customer', status:'PENDING', date: new Date().toLocaleDateString('en-IN') });
  set('queue-size', dispatchQueue.length);
  showOutput('disp-output', `✔ Queued for dispatch!\n  Tracking ID : ${tid}\n  Queue Size  : ${dispatchQueue.length} pending`, 'success');
}

function processQueue() {
  if (!dispatchQueue.length) { showOutput('disp-output','[Queue is empty]','warn'); return; }
  const s = dispatchQueue.shift();
  const p = inventory.find(x => x.id === s.pid);
  if (p) { p.qty -= s.qty; }
  s.status = 'DELIVERED';
  shipmentLog.push(s);
  set('queue-size', dispatchQueue.length);
  updateStats();
  renderShipLog();
  let msg = `✔ Dispatched!\n  Tracking ID : ${s.tid}\n  Product     : ${s.product}\n  Qty         : ${s.qty}\n  Destination : ${s.to}`;
  if (p && p.isLowStock) msg += `\n\n⚠ LowStockException: "${p.name}" now has only ${p.qty} units!`;
  showOutput('disp-output', msg, p && p.isLowStock ? 'warn' : 'success');
}

function trackShipment() {
  const tid = document.getElementById('track-id').value.trim();
  const all = [...shipmentLog, ...dispatchQueue];
  const s   = all.find(x => x.tid.toLowerCase() === tid.toLowerCase());
  const el  = document.getElementById('track-result');
  if (!s) { el.innerHTML = '<p style="color:var(--danger);padding:16px">✘ No shipment found with that ID.</p>'; return; }
  const badge = s.status === 'DELIVERED' ? 'badge-green' : s.status === 'PENDING' ? 'badge-yellow' : 'badge-blue';
  el.innerHTML = `<div class="track-card">
    <div class="track-row"><span class="track-label">Tracking ID</span><code style="color:var(--accent)">${s.tid}</code></div>
    <div class="track-row"><span class="track-label">Type</span><span>${s.type}</span></div>
    <div class="track-row"><span class="track-label">Product</span><strong>${s.product}</strong></div>
    <div class="track-row"><span class="track-label">Quantity</span><span>${s.qty} units</span></div>
    <div class="track-row"><span class="track-label">From</span><span>${s.from}</span></div>
    <div class="track-row"><span class="track-label">To</span><span>${s.to}</span></div>
    <div class="track-row"><span class="track-label">Status</span><span class="badge ${badge}">${s.status}</span></div>
    <div class="track-row"><span class="track-label">Date</span><span>${s.date}</span></div>
  </div>`;
}

function renderShipLog() {
  const el = document.getElementById('ship-log-tbody');
  if (!el) return;
  const all = [...shipmentLog].reverse();
  if (!all.length) { el.innerHTML = '<tr><td colspan="6" style="text-align:center;color:var(--muted);padding:24px">No shipments yet</td></tr>'; return; }
  el.innerHTML = all.map(s => {
    const badge = s.status === 'DELIVERED' ? 'badge-green' : s.status === 'PENDING' ? 'badge-yellow' : 'badge-blue';
    const typeBadge = s.type === 'INCOMING' ? 'badge-teal' : 'badge-blue';
    return `<tr>
      <td><code style="color:var(--accent);font-size:12px">${s.tid}</code></td>
      <td><span class="badge ${typeBadge}">${s.type}</span></td>
      <td>${s.product}</td>
      <td>${s.qty}</td>
      <td><span class="badge ${badge}">${s.status}</span></td>
      <td style="color:var(--muted);font-size:12px">${s.date}</td>
    </tr>`;
  }).join('');
}

// ── HELPERS ───────────────────────────────────────────────────
function showOutput(id, msg, type) {
  const el = document.getElementById(id);
  if (!el) return;
  const cls = type === 'error' ? 'out-error' : type === 'warn' ? 'out-warn' : type === 'success' ? 'out-success' : 'out-info';
  el.innerHTML = `<span class="${cls}">${msg.replace(/\n/g,'<br>')}</span>`;
}

// ── ENTER KEY LOGIN ───────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  document.getElementById('empPass').addEventListener('keydown', e => { if(e.key==='Enter') login(); });
  document.getElementById('empEmail').addEventListener('keydown', e => { if(e.key==='Enter') document.getElementById('empPass').focus(); });
  // Check if this is first run (no accounts exist)
  checkFirstRun();
  // populate category dropdowns
  const cats = Object.keys(CATEGORIES);
  ['new-cat','search-cat'].forEach(id => {
    const el = document.getElementById(id);
    if (!el) return;
    if (id === 'search-cat') el.innerHTML = '<option value="">All Categories</option>';
    cats.forEach(c => { const o = document.createElement('option'); o.value=c; o.textContent=c; el.appendChild(o); });
  });
  // render Java concepts grid
  const concepts = [
    ['ArrayList',         'ProductManager — dynamic inventory'],
    ['Vector',           'ShipmentManager — shipment log'],
    ['Queue',            'Dispatch queue (FIFO)'],
    ['Generics',         'ArrayList, Vector, Queue — all typed'],
    ['Custom Exceptions','LowStockException, ProductNotFoundException'],
    ['Interfaces',       'Trackable, Manageable'],
    ['Enums',            'Category, EmployeeRole, ShipmentType'],
    ['Packages',         '6 modular packages'],
    ['String Methods',   '.contains() .startsWith() in search'],
    ['Arrays',           'Rack shelf slots (fixed size)']
  ];
  const grid = document.getElementById('concepts-grid');
  if (grid) {
    grid.innerHTML = concepts.map(([k,v]) =>
      `<div style="background:var(--surface);border:1px solid var(--border);border-radius:8px;padding:12px">
        <div style="color:var(--accent);font-size:12px;font-weight:700">${k}</div>
        <div style="color:var(--muted);font-size:11px;margin-top:4px">${v}</div>
      </div>`).join('');
  }
});

