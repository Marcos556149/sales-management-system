import React from 'react';
import LoginForm from './components/LoginForm';

/**
 * Main App Component
 * This is the main entry point to structure the React application.
 * For now, it simply renders our LoginForm component.
 * In the future, routing would be configured here (like react-router-dom)
 * to switch between the login page and the sales dashboard.
 */
function App() {
  return (
    <div className="app-layout">
      {/* We can add layouts later, such as headers or footers */}
      <LoginForm />
    </div>
  );
}

export default App;
