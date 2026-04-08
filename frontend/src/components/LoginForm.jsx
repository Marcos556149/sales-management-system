import React, { useState } from 'react';

/**
 * LoginForm Component
 * This functional React component represents the login form.
 * It connects to the Spring Boot backend via /api/auth/login
 */
const LoginForm = () => {
  // State to keep track of text field values
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  
  // State to handle global error messages (401, 500)
  const [globalError, setGlobalError] = useState('');
  
  // State to handle field-specific errors (400 validation errors)
  const [fieldErrors, setFieldErrors] = useState({});

  // State for a successful login message and user data
  const [success, setSuccess] = useState('');
  const [userData, setUserData] = useState(null);

  /**
   * Form submit handler.
   */
  const handleLogin = async (e) => {
    e.preventDefault();

    // Reset errors and success states
    setGlobalError('');
    setFieldErrors({});
    setSuccess('');
    setUserData(null);

    // Basic frontend-level check
    let hasFrontendError = false;
    let newFieldErrors = {};
    
    if (!username) {
      newFieldErrors.userName = 'Username cannot be blank';
      hasFrontendError = true;
    }
    
    if (!password) {
      newFieldErrors.userPassword = 'Password cannot be blank';
      hasFrontendError = true;
    }

    if (hasFrontendError) {
      setFieldErrors(newFieldErrors);
      return;
    }

    try {
      // Send HTTP POST request to the Spring Boot backend
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        // Mapping our component state to the expected LoginRequestDTO structure
        body: JSON.stringify({ 
          userName: username, 
          userPassword: password 
        })
      });

      // Parse JSON from backend regardless of the status
      const data = await response.json();

      if (response.ok) { // HTTP Status 200-299
        setSuccess('Login successful!');
        setUserData(data); // Expecting userName, userRole, language
      } else {
        // Handle non-200 responses based on our GlobalExceptionHandler
        
        if (response.status === 400) {
          // Validation Error (from MethodArgumentNotValidException)
          // `data` will be a map like { userName: "...", userPassword: "..." }
          setFieldErrors(data);
        } else if (response.status === 401) {
          // Authentication Error (from AuthException)
          // `data` will be { error: "Message" }
          setGlobalError(data.error || 'Invalid credentials');
        } else {
          // 500 Internal Server Error or any other unexpected code
          setGlobalError(data.error || 'An unexpected error occurred while connecting to the server.');
        }
      }
    } catch (err) {
      console.error("Fetch error:", err);
      // This catch is usually for Network errors (when the backend is entirely down)
      setGlobalError('Network error. Make sure the Spring Boot backend server (port 8080) is running.');
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <h2>System Login</h2>
        <p className="login-subtitle">Sales Management System</p>

        {/* Status message container to reserve space and prevent layout jumping */}
        <div className="status-container">
          {globalError && <div className="alert alert-error">{globalError}</div>}
          {success && (
            <div className="alert alert-success">
              {success} <br/>
              <strong>Welcome, {userData?.userName}!</strong> <br/>
              Role: {userData?.userRole} | Lang: {userData?.language}
            </div>
          )}
        </div>

        <form onSubmit={handleLogin} className="login-form">
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              type="text"
              id="username"
              placeholder="Enter your username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className={fieldErrors.userName ? 'input-error' : ''}
            />
            <div className="field-error-container">
              {fieldErrors.userName && <span className="field-error-text">{fieldErrors.userName}</span>}
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className={fieldErrors.userPassword ? 'input-error' : ''}
            />
            <div className="field-error-container">
              {fieldErrors.userPassword && <span className="field-error-text">{fieldErrors.userPassword}</span>}
            </div>
          </div>

          <button type="submit" className="login-button">
            Login
          </button>
        </form>
      </div>
    </div>
  );
};

export default LoginForm;
