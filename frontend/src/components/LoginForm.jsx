import React, { useState } from 'react';

/**
 * LoginForm Component
 * This functional React component represents the login form.
 * It connects to the Spring Boot backend via /api/auth/login
 */
const LoginForm = ({ onLoginSuccess }) => {
  // State to keep track of text field values
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  // State to handle global error messages (401, 500)
  const [globalError, setGlobalError] = useState('');

  // State to handle field-specific errors (400 validation errors)
  const [fieldErrors, setFieldErrors] = useState({});

  // State for user data (to keep component logical, although we'll pass to parent)
  const [userData, setUserData] = useState(null);

  /**
   * Form submit handler.
   */
  const handleLogin = async (e) => {
    e.preventDefault();

    // Reset errors and success states
    setGlobalError('');
    setFieldErrors({});
    setUserData(null);

    // Basic frontend-level check
    let hasFrontendError = false;
    let newFieldErrors = {};

    if (!username) {
      newFieldErrors.userName = 'El nombre de usuario es obligatorio';
      hasFrontendError = true;
    }

    if (!password) {
      newFieldErrors.userPassword = 'La contraseña es obligatoria';
      hasFrontendError = true;
    }

    if (hasFrontendError) {
      setFieldErrors(newFieldErrors);
      return;
    }

    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 10000);

      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userName: username,
          userPassword: password
        }),
        signal: controller.signal
      });

      clearTimeout(timeoutId);

      // Safely parse JSON from backend
      const text = await response.text();
      let data;
      try {
        data = text ? JSON.parse(text) : {};
      } catch (err) {
        data = { error: 'No se pudo procesar la respuesta del servidor' };
      }

      if (response.ok) { // HTTP Status 200-299
        const user = data && data.isWrapped ? data.data : data;
        setUserData(user);
        if (onLoginSuccess) {
          onLoginSuccess(user);
        }
      } else {
        // Handle non-200 responses based on our GlobalExceptionHandler
        const errorDetail = data?.error;

        if (response.status === 401) {
          // All authentication errors (401) should be shown as global errors at the top
          setGlobalError(errorDetail?.message || 'Usuario o contraseña incorrectos');
        } else if (response.status === 400 && errorDetail?.field && ['userName', 'userPassword'].includes(errorDetail.field)) {
          // Form validation errors (400) should be shown on specific fields
          setFieldErrors({ [errorDetail.field]: errorDetail.message });
        } else {
          // Fallback for any other unexpected error
          setGlobalError(errorDetail?.message || data?.message || 'Ocurrió un error inesperado al comunicarse con el servidor.');
        }
      }
    } catch (err) {
      if (err.name === 'AbortError') {
        setGlobalError('La solicitud de inicio de sesión excedió el tiempo de espera. Inténtelo nuevamente.');
      } else {
        console.error("Fetch error:", err);
        setGlobalError('No fue posible conectarse con el servidor.');
      }
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <h2>Acceso al sistema</h2>
        <p className="login-subtitle">PrimeSale</p>

        {/* Status message container to reserve space and prevent layout jumping */}
        <div className="status-container">
          {globalError && <div className="alert alert-error">{globalError}</div>}
        </div>

        <form onSubmit={handleLogin} className="login-form">
          <div className="form-group">
            <label htmlFor="username">Nombre de usuario</label>
            <input
              type="text"
              id="username"
              placeholder="nombre de usuario"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className={fieldErrors.userName ? 'input-error' : ''}
            />
            <div className="field-error-container">
              {fieldErrors.userName && <span className="field-error-text">{fieldErrors.userName}</span>}
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="password">Contraseña</label>
            <input
              type="password"
              id="password"
              placeholder="contraseña"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className={fieldErrors.userPassword ? 'input-error' : ''}
            />
            <div className="field-error-container">
              {fieldErrors.userPassword && <span className="field-error-text">{fieldErrors.userPassword}</span>}
            </div>
          </div>

          <button type="submit" className="login-button">
            Ingresar
          </button>
        </form>
      </div>
    </div>
  );
};

export default LoginForm;
