import React from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import Header from './Header';
import './Dashboard.css';

const DashboardLayout = ({ onLogout, user }) => {
  // Destructure userName if exists, otherwise fallback to "Administrator"
  const userName = user?.userName || "Administrator";

  return (
    <div className="dashboard-layout">
      {/* Sidebar now handles its own navigation via react-router */}
      <Sidebar />
      
      <div className="main-content">
        <Header userName={userName} />
        
        <main className="content-area">
          {/* Render the matched child route component */}
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default DashboardLayout;
