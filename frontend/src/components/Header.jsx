import React, { useState, useEffect } from 'react';
import { Settings, LogOut } from 'lucide-react';
import './Header.css';

const Header = ({ userName = "Admin User" }) => {
  const [currentDate, setCurrentDate] = useState(new Date());

  useEffect(() => {
    const timer = setInterval(() => setCurrentDate(new Date()), 1000);
    return () => clearInterval(timer);
  }, []);

  const formattedDate = currentDate.toLocaleDateString('en-US', { 
    weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' 
  });
  const formattedTime = currentDate.toLocaleTimeString('en-US', { 
    hour: '2-digit', minute: '2-digit', second: '2-digit' 
  });

  return (
    <header className="dashboard-header">
      <div className="header-greeting">
        <h1>Welcome, <span className="highlight-name">{userName}</span></h1>
        <p className="header-subtitle">{formattedDate} | {formattedTime}</p>
      </div>
      
      <div className="header-actions">
        <button className="icon-button settings-button" aria-label="Settings">
          <Settings size={22} />
        </button>
        {/* Placeholder for future logout */}
        <button className="icon-button logout-button" aria-label="Logout" style={{ display: 'none' }}>
           <LogOut size={22} />
        </button>
      </div>
    </header>
  );
};

export default Header;
