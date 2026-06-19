import React, { useState } from 'react';
import { Outlet, Navigate, useNavigate } from 'react-router-dom';
import LoginForm from './components/LoginForm';
import DashboardLayout from './components/DashboardLayout';
import { useKeyboardShortcuts } from './hooks/useKeyboardShortcuts';
import { apiClient } from './api/client';
import { isAdmin as checkIsAdmin } from './utils/authUtils';

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
  const [isAuthenticated, setIsAuthenticated] = useState(() => {
    return localStorage.getItem('isAuthenticated') === 'true';
  });
  const [userData, setUserData] = useState(() => {
    const savedUser = localStorage.getItem('userData');
    return savedUser ? JSON.parse(savedUser) : null;
  });
  const navigate = useNavigate();

  const handleLogout = React.useCallback(async () => {
    // If we are already on the login page, no need to perform logout actions again.
    if (window.location.pathname === '/login') {
      return;
    }
    // If the user is already logged out, avoid clearing state again.
    if (!isAuthenticated) {
      return;
    }
    try {
      await apiClient.post('/api/auth/logout');
    } catch (err) {
      console.error('Error logging out from server:', err);
    } finally {
      setIsAuthenticated(false);
      setUserData(null);
      localStorage.removeItem('isAuthenticated');
      localStorage.removeItem('userData');
      navigate('/login', { replace: true });
    }
  }, [navigate, isAuthenticated]);

  React.useEffect(() => {
    // Si la sesión del backend expira (401), se dispara este evento
    window.addEventListener('auth-error', handleLogout);
    return () => window.removeEventListener('auth-error', handleLogout);
  }, [handleLogout]);

  const handleLoginSuccess = (user) => {
    setUserData(user);
    setIsAuthenticated(true);
    localStorage.setItem('isAuthenticated', 'true');
    localStorage.setItem('userData', JSON.stringify(user));
    navigate('/dashboard/products', { replace: true });
  };

  // Register global keyboard shortcuts
  useKeyboardShortcuts(React.useMemo(() => {
    const shortcuts = {
      'ctrl+shift+p': () => {
        navigate('/dashboard/products');
      },
      'ctrl+shift+v': () => {
        navigate('/dashboard/sales');
      }
    };
    if (checkIsAdmin()) {
      shortcuts['ctrl+shift+a'] = () => {
        navigate('/dashboard/statistics');
      };

      shortcuts['ctrl+shift+h'] = () => {
        navigate('/dashboard/users');
      };
    }
    return shortcuts;
  }, [navigate, isAuthenticated]));

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
