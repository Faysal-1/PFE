const API_BASE = 'http://localhost:9091';

function log(msg) {
  const out = document.getElementById('output');
  out.textContent = typeof msg === 'string' ? msg : JSON.stringify(msg, null, 2);
}

async function testApi() {
  log('Appel en cours...');
  try {
    const resp = await fetch(`${API_BASE}/ping`, { headers: { 'Accept': 'application/json' } });
    const text = await resp.text();
    log({ status: resp.status, body: text });
  } catch (e) {
    log({ error: e?.message || String(e) });
  }
}

// --- Fonction pour récupérer les médecins depuis l'API ---
async function fetchDoctorsFromAPI() {
  try {
    const resp = await fetch(`${API_BASE}/doctors`, {
      headers: { 'Accept': 'application/json' }
    });
    if (!resp.ok) throw new Error('API non disponible');
    const doctors = await resp.json();
    
    // Transformer les données de l'API au format attendu
    return doctors.map(d => ({
      id: d.id,
      name: d.fullName || d.name,
      specialty: d.specialty?.name || d.specialty || 'Non spécifié',
      city: d.city || 'Non spécifié',
      initials: getInitials(d.fullName || d.name),
      slots: d.availableSlots || ['09:00', '10:00', '14:00', '15:00'] // Slots par défaut
    }));
  } catch (e) {
    console.warn('Backend non disponible, utilisation des données de démonstration');
    // Données mock si le backend n'est pas démarré
    return [
      {
        id: 'd1',
        name: 'Dr. Sara El Amrani',
        specialty: 'Médecin généraliste',
        city: 'Casablanca',
        initials: 'SE',
        slots: ['09:00', '09:30', '10:15', '11:00']
      },
      {
        id: 'd2',
        name: 'Dr. Yassine Benali',
        specialty: 'Cardiologue',
        city: 'Rabat',
        initials: 'YB',
        slots: ['14:00', '14:30', '15:00']
      },
      {
        id: 'd3',
        name: 'Dr. Salma Kabbaj',
        specialty: 'Dermatologue',
        city: 'Marrakech',
        initials: 'SK',
        slots: ['10:00', '10:30', '16:00']
      }
    ];
  }
}

function getInitials(name) {
  if (!name) return '??';
  const parts = name.split(' ');
  if (parts.length >= 2) {
    return (parts[0][0] + parts[1][0]).toUpperCase();
  }
  return name.substring(0, 2).toUpperCase();
}

function $(sel) { return document.querySelector(sel); }
function el(tag, attrs = {}, children = []) {
  const n = document.createElement(tag);
  Object.entries(attrs).forEach(([k, v]) => {
    if (k === 'class') n.className = v;
    else if (k === 'dataset') Object.assign(n.dataset, v);
    else if (k.startsWith('on') && typeof v === 'function') n.addEventListener(k.substring(2), v);
    else n.setAttribute(k, v);
  });
  children.forEach(c => n.append(c));
  return n;
}

function renderDoctors(list) {
  const wrap = $('#doctorList');
  wrap.innerHTML = '';
  if (!list.length) {
    wrap.append(el('div', { class: 'meta' }, ['Aucun résultat.']));
    return;
  }
  list.forEach(d => {
    const metaDiv = el('div', { class: 'meta' });
    const icon = document.createElement('i');
    icon.className = 'fas fa-map-marker-alt';
    metaDiv.appendChild(icon);
    metaDiv.appendChild(document.createTextNode(` ${d.specialty} · ${d.city}`));
    
    const item = el('div', { class: 'doctor' }, [
      el('div', { class: 'avatar' }, [d.initials]),
      el('div', {}, [
        el('h4', {}, [d.name]),
        metaDiv,
        el('div', { class: 'slots' }, d.slots.map(s => el('button', { class: 'chip', onclick: () => openBooking(d, s) }, [s])))
      ]),
      el('div', {}, [])
    ]);
    wrap.append(item);
  });
}

async function searchDoctors({ specialty, city, date }) {
  // Récupérer les médecins depuis l'API
  const allDoctors = await fetchDoctorsFromAPI();
  
  // Filtrer selon les critères
  return allDoctors.filter(d => {
    const okSpec = specialty ? d.specialty === specialty : true;
    const okCity = city ? d.city === city : true;
    return okSpec && okCity;
  });
}

function openBooking(doctor, slot) {
  const dialog = $('#bookingDialog');
  const summary = $('#bookingSummary');
  const date = $('#date').value || new Date().toISOString().slice(0, 10);
  summary.textContent = `${doctor.name} — ${doctor.specialty} (${doctor.city})\nDate: ${date} à ${slot}`;
  dialog.showModal();
  $('#confirmBooking').onclick = () => {
    dialog.close();
    alert('Rendez-vous demandé avec succès (demo).');
  };
}

window.addEventListener('DOMContentLoaded', async () => {
  // Search form
  const form = document.getElementById('searchForm');
  if (form) {
    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      const specialty = document.getElementById('specialty').value;
      const city = document.getElementById('city').value;
      const date = document.getElementById('date').value;
      const results = await searchDoctors({ specialty, city, date });
      renderDoctors(results);
    });
  }

  // Login button
  const loginBtn = document.getElementById('loginBtn');
  const loginDialog = document.getElementById('loginDialog');
  if (loginBtn && loginDialog) {
    loginBtn.addEventListener('click', () => loginDialog.showModal());
  }

  // Register button
  const registerBtn = document.getElementById('registerBtn');
  const registerDialog = document.getElementById('registerDialog');
  if (registerBtn && registerDialog) {
    registerBtn.addEventListener('click', () => registerDialog.showModal());
  }

  // Handle login form submission
  if (loginDialog) {
    const loginForm = loginDialog.querySelector('form');
    loginForm.addEventListener('submit', async (e) => {
      if (e.submitter && e.submitter.type === 'submit' && e.submitter.classList.contains('btn-primary')) {
        e.preventDefault();
        
        // Récupérer les valeurs du formulaire
        const email = loginForm.querySelector('input[type="email"]').value;
        const password = loginForm.querySelector('input[type="password"]').value;
        
        try {
          // Appel API de connexion
          const response = await fetch(`${API_BASE}/api/v1/auth/login`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'Accept': 'application/json'
            },
            body: JSON.stringify({ email, password })
          });
          
          if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Échec de la connexion');
          }
          
          const data = await response.json();
          
          // Stocker le token dans localStorage
          localStorage.setItem('accessToken', data.accessToken);
          localStorage.setItem('refreshToken', data.refreshToken);
          localStorage.setItem('user', JSON.stringify(data.user));
          
          loginDialog.close();
          alert(`Connexion réussie ! Bienvenue ${data.user.email}`);
          
          // Mettre à jour l'interface
          updateUIAfterLogin(data.user);
          
        } catch (error) {
          console.error('Erreur de connexion:', error);
          alert('Erreur de connexion: ' + error.message);
        }
      }
    });
  }

  // Handle register form submission
  if (registerDialog) {
    const registerForm = registerDialog.querySelector('form');
    registerForm.addEventListener('submit', async (e) => {
      if (e.submitter && e.submitter.type === 'submit' && e.submitter.classList.contains('btn-primary')) {
        e.preventDefault();
        
        // Récupérer les valeurs du formulaire
        const fullName = registerForm.querySelectorAll('input[type="text"]')[0].value;
        const email = registerForm.querySelector('input[type="email"]').value;
        const phone = registerForm.querySelector('input[type="tel"]').value;
        const password = registerForm.querySelector('input[type="password"]').value;
        
        try {
          // Appel API d'inscription
          const response = await fetch(`${API_BASE}/api/v1/auth/register`, {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'Accept': 'application/json'
            },
            body: JSON.stringify({ 
              fullName, 
              email, 
              phone, 
              password,
              role: 'PATIENT' 
            })
          });
          
          if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Échec de l\'inscription');
          }
          
          const data = await response.json();
          
          // Stocker le token dans localStorage
          localStorage.setItem('accessToken', data.accessToken);
          localStorage.setItem('refreshToken', data.refreshToken);
          localStorage.setItem('user', JSON.stringify(data.user));
          
          registerDialog.close();
          alert(`Inscription réussie ! Bienvenue ${data.user.email}`);
          
          // Mettre à jour l'interface
          updateUIAfterLogin(data.user);
          
        } catch (error) {
          console.error('Erreur d\'inscription:', error);
          alert('Erreur d\'inscription: ' + error.message);
        }
      }
    });
  }

  // Navigation links
  document.querySelectorAll('.nav-link').forEach(link => {
    link.addEventListener('click', (e) => {
      e.preventDefault();
      document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
      link.classList.add('active');
    });
  });

  // Vérifier si l'utilisateur est déjà connecté
  checkLoginStatus();
  
  // Display default results from API or mock data
  const doctors = await fetchDoctorsFromAPI();
  renderDoctors(doctors);
});

// Fonction pour vérifier le statut de connexion
function checkLoginStatus() {
  const token = localStorage.getItem('accessToken');
  const userStr = localStorage.getItem('user');
  
  if (token && userStr) {
    try {
      const user = JSON.parse(userStr);
      updateUIAfterLogin(user);
    } catch (e) {
      console.error('Erreur parsing user:', e);
      localStorage.clear();
    }
  }
}

// Fonction pour mettre à jour l'interface après connexion
function updateUIAfterLogin(user) {
  const loginBtn = document.getElementById('loginBtn');
  const registerBtn = document.getElementById('registerBtn');
  
  if (loginBtn && registerBtn) {
    // Masquer les boutons de connexion/inscription
    loginBtn.style.display = 'none';
    registerBtn.style.display = 'none';
    
    // Créer un menu utilisateur
    const navActions = document.querySelector('.nav-actions');
    const userMenu = document.createElement('div');
    userMenu.className = 'user-menu';
    userMenu.innerHTML = `
      <span style="margin-right: 10px;"><i class="fas fa-user"></i> ${user.email}</span>
      <button class="btn-outline" id="logoutBtn">Déconnexion</button>
    `;
    navActions.appendChild(userMenu);
    
    // Ajouter l'événement de déconnexion
    document.getElementById('logoutBtn').addEventListener('click', logout);
  }
}

// Fonction de déconnexion
function logout() {
  localStorage.clear();
  alert('Déconnexion réussie !');
  window.location.reload();
}
