import React, { useState, useEffect } from 'react';
import { Settings, LogOut } from 'lucide-react';
import BarcodeScanner from './BarcodeScanner';
import SystemConfigurationModal from './SystemConfigurationModal';
import './Header.css';

const Header = ({ userName = "Admin User" }) => {
  const [currentDate, setCurrentDate] = useState(new Date());
  const [isConfigOpen, setIsConfigOpen] = useState(false);

  useEffect(() => {
    const timer = setInterval(() => setCurrentDate(new Date()), 1000);
    return () => clearInterval(timer);
  }, []);

  const formattedDate = currentDate.toLocaleDateString('es-AR', {
    weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
  });
  const formattedTime = currentDate.toLocaleTimeString('es-AR', {
    hour: '2-digit', minute: '2-digit', second: '2-digit'
  });

  return (
    <>
      <header className="dashboard-header">
        <div className="header-greeting">
          <h1>Bienvenido, <span className="highlight-name">{userName}</span></h1>
          <p className="header-subtitle">{formattedDate} | {formattedTime}</p>
        </div>

        <BarcodeScanner />

        <div className="header-actions">
          <button
            className="icon-button settings-button"
            aria-label="Configuración"
            onClick={() => setIsConfigOpen(true)}
          >
            <Settings size={22} />
          </button>
          {/* Placeholder for future logout */}
          <button className="icon-button logout-button" aria-label="Cerrar sesión" style={{ display: 'none' }}>
            <LogOut size={22} />
          </button>
        </div>
      </header>

      <SystemConfigurationModal
        isOpen={isConfigOpen}
        onClose={() => setIsConfigOpen(false)}
      />
    </>
  );
};

export default Header;
