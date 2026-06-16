export const getUserData = () => {
  try {
    const data = localStorage.getItem('userData');
    return data ? JSON.parse(data) : null;
  } catch (e) {
    return null;
  }
};

export const hasRole = (userData, roleName) => {
  if (!userData) return false;
  
  const roleToCheck = roleName.toUpperCase();
  
  // Possible properties where the role might be stored
  const roles = [
    userData.userRole,
    userData.role,
    userData.roles,
    userData.authorities
  ];

  for (const r of roles) {
    if (!r) continue;
    
    // Si es un string simple ("ADMIN", "ROLE_ADMIN")
    if (typeof r === 'string' && r.toUpperCase().includes(roleToCheck)) {
      return true;
    }
    
    // Si es un array (ej. de Spring Security authorities)
    if (Array.isArray(r)) {
      const match = r.some(item => {
        if (typeof item === 'string') return item.toUpperCase().includes(roleToCheck);
        if (item && typeof item === 'object') {
          if (item.code && typeof item.code === 'string') return item.code.toUpperCase().includes(roleToCheck);
          if (item.authority && typeof item.authority === 'string') return item.authority.toUpperCase().includes(roleToCheck);
          if (item.name && typeof item.name === 'string') return item.name.toUpperCase().includes(roleToCheck);
        }
        return false;
      });
      if (match) return true;
    }
    
    // Si es un único objeto (como {"code": "ADMIN", "label": "Administrador"})
    if (typeof r === 'object' && !Array.isArray(r)) {
      if (r.code && typeof r.code === 'string' && r.code.toUpperCase().includes(roleToCheck)) return true;
      if (r.authority && typeof r.authority === 'string' && r.authority.toUpperCase().includes(roleToCheck)) return true;
      if (r.name && typeof r.name === 'string' && r.name.toUpperCase().includes(roleToCheck)) return true;
    }
  }
  
  return false;
};

export const isAdmin = () => {
  return hasRole(getUserData(), 'ADMIN');
};
