import React, { useState } from 'react';
import { Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import LoginForm from './components/LoginForm';
import DashboardLayout from './components/DashboardLayout';
import ProductsView from './components/ProductsView';
import ProductCreateView from './components/ProductCreateView';

import ProductDetailView from './components/ProductDetailView';

// Placeholder Pages for Sales and Statistics
const SalesPlaceholder = () => (
  <div className="coming-soon">
    <h2>Sales Module</h2>
    <p>This functional area is currently under development.</p>
  </div>
);

const StatisticsPlaceholder = () => (
  <div className="coming-soon">
    <h2>Statistics Module</h2>
    <p>This functional area is currently under development.</p>
  </div>
);

// We can extract LoginPage since the user requested "LoginPage" separating logic 
// but for simplicity it just wraps LoginForm here.
const LoginPage = ({ onLoginSuccess }) => {
  return (
    <div className="app-layout">
      <LoginForm onLoginSuccess={onLoginSuccess} />
    </div>
  );
};

/**
 * Main App Component
 */
function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userData, setUserData] = useState(null);
  const navigate = useNavigate();

  const handleLoginSuccess = (user) => {
    setUserData(user);
    setIsAuthenticated(true);
    // After login, redirect to dashboard products
    navigate('/dashboard/products', { replace: true });
  };

  const handleLogout = () => {
    setIsAuthenticated(false);
    setUserData(null);
    navigate('/login', { replace: true });
  };

  return (
    <Routes>
      {/* Redirect root to login */}
      <Route path="/" element={<Navigate to="/login" replace />} />

      {/* Login Route */}
      <Route 
        path="/login" 
        element={
          isAuthenticated ? 
            <Navigate to="/dashboard/products" replace /> : 
            <LoginPage onLoginSuccess={handleLoginSuccess} />
        } 
      />

      {/* Dashboard Protected Routes */}
      <Route 
        path="/dashboard" 
        element={
          isAuthenticated ? 
            <DashboardLayout onLogout={handleLogout} user={userData} /> : 
            <Navigate to="/login" replace />
        }
      >
        {/* Nested routes will be rendered inside DashboardLayout's <Outlet /> */}
        <Route index element={<Navigate to="/dashboard/products" replace />} />
        <Route path="products" element={<ProductsView />} />
        <Route path="products/new" element={<ProductCreateView />} />
        <Route path="products/:id" element={<ProductDetailView />} />
        <Route path="sales" element={<SalesPlaceholder />} />
        <Route path="statistics" element={<StatisticsPlaceholder />} />
      </Route>

      {/* Fallback route */}
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}

export default App;
