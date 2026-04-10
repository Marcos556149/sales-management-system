import React, { useState } from 'react';
import { NavLink } from 'react-router-dom';
import { Package, ShoppingCart, BarChart3, Users, Menu, X } from 'lucide-react';
import './Sidebar.css';

const Sidebar = () => {
  const [isMobileOpen, setIsMobileOpen] = useState(false);

  const toggleMobileMenu = () => {
    setIsMobileOpen(!isMobileOpen);
  };

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <h2>Sales Management System</h2>
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
              <span>Products</span>
            </NavLink>
          </li>
          
          <li className="nav-item">
            <NavLink 
              to="/dashboard/sales" 
              className={({ isActive }) => `nav-button ${isActive ? 'active-link' : ''}`}
              onClick={() => setIsMobileOpen(false)}
            >
              <ShoppingCart className="nav-icon" size={20} />
              <span>Sales</span>
            </NavLink>
          </li>
          
          <li className="nav-item">
            <NavLink 
              to="/dashboard/statistics" 
              className={({ isActive }) => `nav-button ${isActive ? 'active-link' : ''}`}
              onClick={() => setIsMobileOpen(false)}
            >
              <BarChart3 className="nav-icon" size={20} />
              <span>Statistics</span>
            </NavLink>
          </li>
          
          {/* Admin only placeholder */}
          <li className="nav-item user-section-placeholder">
            <button className="nav-button disabled" title="Admin only - Coming soon">
              <Users className="nav-icon" size={20} />
              <span>Users</span>
            </button>
          </li>
        </ul>
      </nav>
      
      <div className="sidebar-footer">
        <p className="system-version">v1.0.0 Alpha</p>
      </div>
    </aside>
  );
};

export default Sidebar;
