import React, { useState } from 'react';
import { Outlet, Navigate, useNavigate } from 'react-router-dom';
import LoginForm from './components/LoginForm';
import DashboardLayout from './components/DashboardLayout';
import { useKeyboardShortcuts } from './hooks/useKeyboardShortcuts';

/**
 * LoginPage Wrapper
 */
const LoginPage = ({ onLoginSuccess }) => {
  return (
    <div className="app-layout">
      <LoginForm onLoginSuccess={onLoginSuccess} />
    </div>
  );
};

/**
 * Main App Component
 * Acts as a Layout/State Provider for the Data Router.
 */
function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userData, setUserData] = useState(null);
  const navigate = useNavigate();

  const handleLoginSuccess = (user) => {
    setUserData(user);
    setIsAuthenticated(true);
    navigate('/dashboard/products', { replace: true });
  };

  const handleLogout = () => {
    setIsAuthenticated(false);
    setUserData(null);
    navigate('/login', { replace: true });
  };

  // Register global keyboard shortcuts
  useKeyboardShortcuts(React.useMemo(() => ({
    'ctrl+shift+p': () => {
      if (isAuthenticated) {
        navigate('/dashboard/products');
      }
    }
  }), [isAuthenticated, navigate]));

  // The context object to be shared with all routes
  const authContext = {
    isAuthenticated,
    userData,
    handleLoginSuccess,
    handleLogout
  };

  return <Outlet context={authContext} />;
}

export default App;
export { LoginPage };
