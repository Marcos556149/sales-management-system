import React, { useState, useRef } from 'react';
import { useNavigate, useLocation, useBlocker } from 'react-router-dom';
import { ArrowLeft, Save, X, UserPlus } from 'lucide-react';

import { userService } from '../services/userService';
import { useToast } from './ToastContext';
import { useUsersContext } from './UsersContext';
import { useKeyboardShortcuts } from '../hooks/useKeyboardShortcuts';
import ConfirmModal from './ConfirmModal';

import './ProductCreateView.css';

const UserCreateView = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const { addToast } = useToast();
  const { setIsCached } = useUsersContext();

  const userNameRef = useRef(null);

  const [isDirty, setIsDirty] = useState(false);
  const isSubmittingRef = useRef(false);

  const blocker = useBlocker(
    ({ nextLocation }) =>
      isDirty &&
      !isSubmittingRef.current &&
      nextLocation.pathname !== location.pathname
  );

  const [formData, setFormData] = useState({
    userName: '',
    userPassword: ''
  });

  const [submitting, setSubmitting] = useState(false);
  const [formErrors, setFormErrors] = useState({});
  const [generalError, setGeneralError] = useState(null);

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    setIsDirty(true);

    if (formErrors[name]) {
      setFormErrors(prev => ({
        ...prev,
        [name]: undefined
      }));
    }
  };

  const validateFrontEnd = () => {
    const errors = {};

    const userName = formData.userName.trim();

    if (!userName) {
      errors.userName = 'El nombre de usuario es obligatorio';
    } else if (userName.length > 100) {
      errors.userName = 'El nombre de usuario no debe superar los 100 caracteres';
    }

    if (!formData.userPassword.trim()) {
      errors.userPassword = 'La contraseña es obligatoria';
    } else if (formData.userPassword.length > 72) {
      errors.userPassword = 'La contraseña no debe superar los 72 caracteres';
    }

    setFormErrors(errors);

    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e) => {
    if (e) e.preventDefault();

    if (!validateFrontEnd()) {
      return;
    }

    setSubmitting(true);
    setFormErrors({});
    setGeneralError(null);

    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 10000);

    try {
      const payload = {
        userName: formData.userName.trim(),
        userPassword: formData.userPassword
      };

      const res = await userService.createUser(
        payload,
        { signal: controller.signal }
      );

      clearTimeout(timeoutId);

      addToast(
        res.message || 'Usuario creado correctamente',
        'success'
      );

      setIsCached(false);

      isSubmittingRef.current = true;

      navigate(
        `/dashboard/users/${res.data.userId}`,
        {
          state: { user: res.data },
          replace: true
        }
      );

    } catch (err) {
      clearTimeout(timeoutId);

      if (err.name === 'AbortError') {
        const msg = 'La solicitud excedió el tiempo de espera';

        setGeneralError(msg);
        addToast(msg, 'error');

      } else {

        const errorData = err.response?.data?.error || {};

        const isFieldError = errorData.field !== null && errorData.field !== undefined;

        if (err.status === 400 && isFieldError) {
          setFormErrors({ [errorData.field]: errorData.message });
          setGeneralError(null);
          addToast(errorData.message, 'error');
        } else {
          const msg =
            errorData.message ||
            err.message ||
            'No se pudo crear el usuario';

          addToast(msg, 'error');
          setGeneralError(null);
        }
      }

    } finally {
      setSubmitting(false);
    }
  };

  const getInputClass = (fieldName) => {
    return formErrors[fieldName]
      ? 'form-input error'
      : 'form-input';
  };

  useKeyboardShortcuts(
    React.useMemo(() => ({
      'ctrl+b': () => navigate('/dashboard/users'),
      'escape': () => navigate(-1),
      'ctrl+enter': () => handleSubmit()
    }), [navigate, formData])
  );

  return (
    <div className="view-container">

      <div className="detail-toolbar">
        <button
          type="button"
          className="btn-secondary"
          onClick={() => navigate('/dashboard/users')}
        >
          <ArrowLeft size={16} />
          <span>Volver a Usuarios</span>
          <span className="btn-shortcut">Ctrl+B</span>
        </button>
      </div>

      <div className="form-card">

        <div className="form-header">
          <div className="form-icon-container">
            <UserPlus size={28} className="form-main-icon" />
          </div>

          <div>
            <h2 className="form-title">
              Registrar Nuevo Usuario
            </h2>

            <p className="form-subtitle">
              Complete los datos para crear un nuevo usuario del sistema.
            </p>
          </div>
        </div>

        {generalError && (
          <div
            style={{
              marginBottom: '1rem',
              color: 'var(--danger-color)'
            }}
          >
            {generalError}
          </div>
        )}

        <form
          onSubmit={handleSubmit}
          className="product-form"
          noValidate
        >
          <div className="form-grid">

            <div className="form-group">
              <label htmlFor="userName">
                Nombre de Usuario
                <span className="required"> *</span>
              </label>

              <input
                ref={userNameRef}
                type="text"
                id="userName"
                name="userName"
                className={getInputClass('userName')}
                value={formData.userName}
                onChange={handleChange}
                disabled={submitting}
                placeholder="nombre de usuario"
              />

              <p className={`error-text ${formErrors.userName ? 'visible' : ''}`}>
                {formErrors.userName || '\u00A0'}
              </p>
            </div>

            <div className="form-group">
              <label htmlFor="userPassword">
                Contraseña
                <span className="required"> *</span>
              </label>

              <input
                type="password"
                id="userPassword"
                name="userPassword"
                className={getInputClass('userPassword')}
                value={formData.userPassword}
                onChange={handleChange}
                disabled={submitting}
                placeholder="contraseña"
              />

              <p className={`error-text ${formErrors.userPassword ? 'visible' : ''}`}>
                {formErrors.userPassword || '\u00A0'}
              </p>
            </div>

          </div>

          <div className="form-actions">

            <button
              type="button"
              className="btn-secondary"
              onClick={() => navigate('/dashboard/users')}
              disabled={submitting}
            >
              <X size={18} />
              <span>Cancelar</span>
              <span className="btn-shortcut">Esc</span>
            </button>

            <button
              type="submit"
              className="btn-primary form-submit-btn"
              disabled={submitting}
            >
              <Save size={18} />
              <span>
                {submitting
                  ? 'Registrando...'
                  : 'Registrar Usuario'}
              </span>

              <span
                className="btn-shortcut"
                style={{
                  backgroundColor: 'rgba(255,255,255,0.2)',
                  color: 'white',
                  borderColor: 'rgba(255,255,255,0.3)'
                }}
              >
                Ctrl+Enter
              </span>
            </button>

          </div>
        </form>

      </div>

      <ConfirmModal
        isOpen={blocker.state === 'blocked'}
        title="Cambios sin guardar"
        message="Tienes cambios sin guardar. ¿Estás seguro de que deseas salir de esta página?"
        onConfirm={() => blocker.proceed()}
        onCancel={() => blocker.reset()}
        confirmText="Salir"
        confirmButtonTheme="danger"
      />

    </div>
  );
};

export default UserCreateView;