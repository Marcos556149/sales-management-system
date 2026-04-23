import React from 'react';
import ReactDOM from 'react-dom/client';
import { 
  createBrowserRouter, 
  RouterProvider, 
  createRoutesFromElements, 
  Route, 
  Navigate,
  useOutletContext
} from 'react-router-dom';

import App, { LoginPage } from './App.jsx';
import { ToastProvider } from './components/ToastContext.jsx';
import DashboardLayout from './components/DashboardLayout';
import ProductsView from './components/ProductsView';
import ProductCreateView from './components/ProductCreateView';
import ProductDetailView from './components/ProductDetailView';
import ProductEditView from './components/ProductEditView';
import { ProductsLayout } from './components/ProductsContext';
import SalesView from './components/SalesView';
import SaleDetailView from './components/SaleDetailView';
import { SalesLayout } from './components/SalesContext';
import RegisterSaleView from './components/RegisterSaleView';
import './index.css';

// Placeholder Pages for Statistics
const StatisticsPlaceholder = () => (
  <div className="coming-soon">
    <h2>Statistics Module</h2>
    <p>This functional area is currently under development.</p>
  </div>
);

/**
 * Protected Route Wrapper
 * Uses the auth context from App.jsx's Outlet
 */
const ProtectedElement = ({ children }) => {
  const { isAuthenticated, handleLogout, userData } = useOutletContext();
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  
  // If it's a layout like DashboardLayout, we need to pass props
  return React.cloneElement(children, { onLogout: handleLogout, user: userData });
};

/**
 * Login Route Wrapper
 */
const LoginElement = () => {
  const { isAuthenticated, handleLoginSuccess } = useOutletContext();
  if (isAuthenticated) return <Navigate to="/dashboard/products" replace />;
  return <LoginPage onLoginSuccess={handleLoginSuccess} />;
};

const router = createBrowserRouter(
  createRoutesFromElements(
    <Route path="/" element={<App />}>
      <Route index element={<Navigate to="/login" replace />} />
      
      <Route path="login" element={<LoginElement />} />
      
      <Route path="dashboard" element={<ProtectedElement><DashboardLayout /></ProtectedElement>}>
        <Route index element={<Navigate to="/dashboard/products" replace />} />
        
        <Route path="products" element={<ProductsLayout />}>
          <Route index element={<ProductsView />} />
          <Route path="new" element={<ProductCreateView />} />
          <Route path=":id" element={<ProductDetailView />} />
          <Route path="edit/:id" element={<ProductEditView />} />
        </Route>
        
        <Route path="sales" element={<SalesLayout />}>
          <Route index element={<SalesView />} />
          <Route path="new" element={<RegisterSaleView />} />
          <Route path=":id" element={<SaleDetailView />} />
        </Route>
        
        <Route path="statistics" element={<StatisticsPlaceholder />} />
      </Route>
      
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Route>
  )
);

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <ToastProvider>
      <RouterProvider router={router} />
    </ToastProvider>
  </React.StrictMode>,
);
