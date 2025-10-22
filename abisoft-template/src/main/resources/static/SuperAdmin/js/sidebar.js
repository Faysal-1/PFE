// Fonction pour basculer l'affichage des sous-menus
function toggleSubmenu(element) {
  // Récupérer le sous-menu
  const submenu = element.closest('.menu-item').querySelector('.submenu');
  const icon = element.querySelector('.fa-chevron-down');
  
  // Fermer tous les autres sous-menus
  document.querySelectorAll('.submenu').forEach(menu => {
    if (menu !== submenu) {
      menu.style.display = 'none';
      const menuIcon = menu.parentElement.querySelector('.fa-chevron-down');
      if (menuIcon) {
        menuIcon.style.transform = 'rotate(0deg)';
      }
    }
  });
  
  // Basculer l'affichage du sous-menu actuel
  if (submenu.style.display === 'none' || submenu.style.display === '') {
    submenu.style.display = 'block';
    if (icon) {
      icon.style.transform = 'rotate(180deg)';
    }
  } else {
    submenu.style.display = 'none';
    if (icon) {
      icon.style.transform = 'rotate(0deg)';
    }
  }
}

// Au chargement de la page
document.addEventListener('DOMContentLoaded', function() {
  // Détecter la page active basée sur l'URL actuelle
  const currentPath = window.location.pathname;
  
  // Activer le menu correspondant
  document.querySelectorAll('.menu-item').forEach(item => {
    const link = item.getAttribute('href');
    if (link && currentPath.includes(link)) {
      item.classList.add('active');
    }
  });
  
  // Vérifier les sous-menus pour les pages actives
  document.querySelectorAll('.submenu-item').forEach(item => {
    const link = item.getAttribute('href');
    if (link && currentPath.includes(link)) {
      item.classList.add('active');
      // Ouvrir le menu parent
      const parentMenu = item.closest('.menu-item.dropdown');
      if (parentMenu) {
        const submenu = parentMenu.querySelector('.submenu');
        const icon = parentMenu.querySelector('.fa-chevron-down');
        submenu.style.display = 'block';
        if (icon) {
          icon.style.transform = 'rotate(180deg)';
        }
      }
    }
  });
  
  // Ouvrir automatiquement le menu des hébergements si on est sur une page d'hébergement
  if (currentPath.includes('hebergement')) {
    const hebergementMenu = document.querySelector('.menu-item.dropdown:nth-child(2)');
    if (hebergementMenu) {
      const submenu = hebergementMenu.querySelector('.submenu');
      const icon = hebergementMenu.querySelector('.fa-chevron-down');
      submenu.style.display = 'block';
      if (icon) {
        icon.style.transform = 'rotate(180deg)';
      }
    }
  }
  
  // Ouvrir automatiquement le menu des utilisateurs si on est sur une page utilisateur
  if (currentPath.includes('user')) {
    const userMenu = document.querySelector('.menu-item.dropdown:nth-child(4)');
    if (userMenu) {
      const submenu = userMenu.querySelector('.submenu');
      const icon = userMenu.querySelector('.fa-chevron-down');
      submenu.style.display = 'block';
      if (icon) {
        icon.style.transform = 'rotate(180deg)';
      }
    }
  }
});
