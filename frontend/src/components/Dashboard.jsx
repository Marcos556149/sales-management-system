import React, { useState } from 'react';
import Sidebar from './Sidebar';
import Header from './Header';
import ProductsView from './ProductsView';
import './Dashboard.css';

const Dashboard = ({ onLogout, user }) => {
  const [activeSection, setActiveSection] = useState('products');

  // Destructure userName if exists, otherwise fallback to "Administrator"
  const userName = user?.userName || "Administrator";

  // Currently only products is implemented
  const renderContent = () => {
    switch(activeSection) {
      case 'products':
        return <ProductsView />;
      default:
        return (
          <div className="coming-soon">
            <h2>{activeSection.charAt(0).toUpperCase() + activeSection.slice(1)} Module</h2>
            <p>This functional area is currently under development.</p>
          </div>
        );
    }
  };

  return (
    <div className="dashboard-layout">
      <Sidebar activeSection={activeSection} setActiveSection={setActiveSection} />
      
      <div className="main-content">
        <Header userName={userName} />
        
        <main className="content-area">
          {renderContent()}
        </main>
      </div>
    </div>
  );
};

export default Dashboard;
