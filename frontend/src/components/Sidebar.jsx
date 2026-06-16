import React, { useState } from 'react';
import { NavLink } from 'react-router-dom';
import { Package, ShoppingCart, BarChart3, Users, Menu, X, LogOut } from 'lucide-react';
import ConfirmModal from './ConfirmModal';
import { isAdmin as checkIsAdmin } from '../utils/authUtils';
import './Sidebar.css';

const Sidebar = ({ onLogout }) => {
  const [isMobileOpen, setIsMobileOpen] = useState(false);
  const [isLogoutModalOpen, setIsLogoutModalOpen] = useState(false);
  const [isLoggingOut, setIsLoggingOut] = useState(false);
  const isAdmin = checkIsAdmin();

  const toggleMobileMenu = () => {
    setIsMobileOpen(!isMobileOpen);
  };

  const handleLogoutClick = () => {
    setIsLogoutModalOpen(true);
  };

  const handleConfirmLogout = async () => {
    setIsLoggingOut(true);
    try {
      await onLogout();
    } finally {
      setIsLoggingOut(false);
      setIsLogoutModalOpen(false);
    }
  };

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <h2>PrimeSale</h2>
        <button className="mobile-toggle-btn" onClick={toggleMobileMenu}>
          {isMobileOpen ? <X size={24} /> : <Menu size={24} />}
        </button>
      </div>

      <nav className={`sidebar-nav ${isMobileOpen ? 'mobile-open' : ''}`}>
        <ul className="nav-list">
          <li className="nav-item">
            <NavLink
              to="/dashboard/products"
              className={({ isActive }) => `nav-button ${isActive ? 'active-link' : ''}`}
              onClick={() => setIsMobileOpen(false)}
            >
              <Package className="nav-icon" size={20} />
              <span>Productos</span>
              <span className="nav-shortcut">Ctrl+Shift+P</span>
            </NavLink>
          </li>

          <li className="nav-item">
            <NavLink
              to="/dashboard/sales"
              className={({ isActive }) => `nav-button ${isActive ? 'active-link' : ''}`}
              onClick={() => setIsMobileOpen(false)}
            >
              <ShoppingCart className="nav-icon" size={20} />
              <span>Ventas</span>
              <span className="nav-shortcut">Ctrl+Shift+V</span>
            </NavLink>
          </li>

          {isAdmin && (
            <li className="nav-item">
              <NavLink
                to="/dashboard/statistics"
                className={({ isActive }) => `nav-button ${isActive ? 'active-link' : ''}`}
                onClick={() => setIsMobileOpen(false)}
              >
                <BarChart3 className="nav-icon" size={20} />
                <span>Estadísticas</span>
                <span className="nav-shortcut">Ctrl+Shift+A</span>
              </NavLink>
            </li>
          )}

          {/* Admin only placeholder */}
          <li className="nav-item user-section-placeholder">
            <button className="nav-button disabled" title="Solo administradores - Próximamente">
              <Users className="nav-icon" size={20} />
              <span>Usuarios</span>
            </button>
          </li>
        </ul>
      </nav>

      <div className="sidebar-footer">
        <button className="nav-button logout-button" onClick={handleLogoutClick} title="Cerrar sesión" style={{ width: '100%', justifyContent: 'flex-start' }}>
          <LogOut className="nav-icon" size={20} />
          <span>Cerrar Sesión</span>
        </button>
      </div>

      <ConfirmModal
        isOpen={isLogoutModalOpen}
        title="Cerrar Sesión"
        message="¿Está seguro de que desea cerrar sesión? Los cambios no guardados se perderán."
        onConfirm={handleConfirmLogout}
        onCancel={() => setIsLogoutModalOpen(false)}
        isConfirming={isLoggingOut}
        confirmText="Confirmar"
        confirmButtonTheme="danger"
      />
    </aside>
  );
};

export default Sidebar;
