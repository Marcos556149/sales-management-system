import React, { useState } from 'react';

/**
 * LoginForm Component
 * This functional React component represents the login form.
 * It uses hooks (`useState`) to manage the state of inputs (username, password)
 * and potential validation errors.
 */
const LoginForm = () => {
  // State to keep track of text field values
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  
  // State to handle error messages if validation fails
  const [error, setError] = useState('');

  // State for a simulated success message
  const [success, setSuccess] = useState('');

  /**
   * Form submit handler.
   * Validates that fields are not empty before simulating the request.
   */
  const handleLogin = async (e) => {
    e.preventDefault(); // Prevents page reload

    // Basic validation: both fields are required
    if (!username || !password) {
      setError('Both fields are required.');
      setSuccess('');
      return;
    }

    // If validation passes, clear the error
    setError('');

    try {
      // Simulating an HTTP POST request to the Spring Boot backend
      console.log('Sending request to /api/auth/login...');
      
      // We would use fetch in a real environment:
      // const response = await fetch('/api/auth/login', {
      //   method: 'POST',
      //   headers: { 'Content-Type': 'application/json' },
      //   body: JSON.stringify({ username, password })
      // });
      
      // Simulating asynchronous behavior
      await new Promise((resolve) => setTimeout(resolve, 1000));

      // Mock of a successful response
      setSuccess('Simulated login successful!');
      console.log('Frontend login completed with username:', username);

    } catch (err) {
      setError('An error occurred while connecting to the server.');
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <h2>System Login</h2>
        <p className="login-subtitle">Kiosk Sales Management</p>

        {/* Display error message if exists */}
        {error && <div className="alert alert-error">{error}</div>}
        
        {/* Display success message if exists */}
        {success && <div className="alert alert-success">{success}</div>}

        <form onSubmit={handleLogin} className="login-form">
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              type="text"
              id="username"
              placeholder="Enter your username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
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
