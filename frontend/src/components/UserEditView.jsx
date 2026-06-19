import React, { useState, useEffect, useRef } from 'react';
import { useNavigate, useParams, useLocation, useBlocker } from 'react-router-dom';
import { ArrowLeft, Save, X, Loader2, User } from 'lucide-react';
import { useToast } from './ToastContext';
import { useUsersContext } from './UsersContext';
import { userService } from '../services/userService';
import { useKeyboardShortcuts } from '../hooks/useKeyboardShortcuts';
import ConfirmModal from './ConfirmModal';
import './ProductCreateView.css';

const UserEditView = () => {
  const { id: userId } = useParams();
  const navigate = useNavigate();
  const location = useLocation();

  const { addToast } = useToast();
  const { setIsCached } = useUsersContext();

  const initialUser = location.state?.user;

  const [isDirty, setIsDirty] = useState(false);
  const isSubmittingRef = useRef(false);

  const blocker = useBlocker(
    ({ nextLocation }) =>
      isDirty &&
      !isSubmittingRef.current &&
      nextLocation.pathname !== location.pathname
  );

  const [originalUser, setOriginalUser] = useState(null);

  const [formData, setFormData] = useState({
    userName: '',
    userPassword: ''
  });

  const [formErrors, setFormErrors] = useState({});
  const [loading, setLoading] = useState(
    !initialUser || String(initialUser.userId) !== userId
  );
  const [submitting, setSubmitting] = useState(false);
  const [errorHeader, setErrorHeader] = useState(null);
  const [generalError, setGeneralError] = useState(null);

  useEffect(() => {
    let isMounted = true;

    const fetchUser = async () => {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 10000);

      try {
        const response =
          initialUser && String(initialUser.userId) === userId
            ? { data: initialUser }
            : await userService.getUser(userId, {
              signal: controller.signal
            });

        clearTimeout(timeoutId);

        if (isMounted) {
          setOriginalUser(response.data);

          setFormData({
            userName: response.data.userName || '',
            userPassword: ''
          });

          setLoading(false);
        }
      } catch (err) {
        clearTimeout(timeoutId);

        if (isMounted) {
          setErrorHeader(
            err.name === 'AbortError'
              ? 'La solicitud excedió el tiempo de espera'
              : err.message || 'Usuario no encontrado'
          );

          setLoading(false);
        }
      }
    };

    fetchUser();

    return () => {
      isMounted = false;
    };
  }, [userId]);

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    setIsDirty(true);

    if (formErrors[name]) {
      setFormErrors(prev => {
        const updated = { ...prev };
        delete updated[name];
        return updated;
      });
    }
  };

  const validateFrontEnd = () => {
    const errors = {};

    const userName = formData.userName.trim();

    if (!userName) {
      errors.userName = 'El nombre de usuario es obligatorio';
    } else if (userName.length > 100) {
      errors.userName =
        'El nombre de usuario no debe superar los 100 caracteres';
    }

    if (
      formData.userPassword &&
      formData.userPassword.length > 72
    ) {
      errors.userPassword =
        'La contraseña no debe superar los 72 caracteres';
    }

    return errors;
  };

  const handleSubmit = async (e) => {
    if (e) e.preventDefault();

    if (submitting) return;

    const errors = validateFrontEnd();

    if (Object.keys(errors).length > 0) {
      setFormErrors(errors);
      return;
    }

    setSubmitting(true);
    setFormErrors({});
    setGeneralError(null);

    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 10000);

    try {
      const payload = {
        userName: formData.userName.trim()
      };

      if (formData.userPassword.trim()) {
        payload.userPassword = formData.userPassword;
      }

      const res = await userService.updateUser(
        userId,
        payload,
        { signal: controller.signal }
      );

      clearTimeout(timeoutId);

      addToast('Usuario actualizado correctamente', 'success');

      setIsCached(false);

      isSubmittingRef.current = true;

      navigate(`/dashboard/users/${userId}`, {
        state: {
          user: res.data
        }
      });

    } catch (err) {
      clearTimeout(timeoutId);

      if (err.name === 'AbortError') {
        setGeneralError(
          'La solicitud excedió el tiempo de espera'
        );

        addToast(
          'La solicitud excedió el tiempo de espera',
          'error'
        );
      } else {
        const data = err.details || {};
        const errorData = data.error || {};

        if (err.status === 400) {

          const msg =
            errorData.message ||
            err.message ||
            'Datos inválidos';

          addToast(msg, 'error');

          // No mostrar banner para errores de validación/regla de negocio
          setGeneralError(null);

          if (errorData.field) {
            setFormErrors({
              [errorData.field]: msg
            });
          }

        } else {
          const msg =
            err.message ||
            'Ocurrió un error al actualizar el usuario';

          setGeneralError(msg);
          addToast(msg, 'error');
        }
      }
    } finally {
      setSubmitting(false);
    }
  };

  const handleCancel = () => {
    navigate(-1);
  };

  const getInputClass = (fieldName) =>
    formErrors[fieldName]
      ? 'form-input error'
      : 'form-input';

  useKeyboardShortcuts(
    React.useMemo(
      () => ({
        'ctrl+b': () => navigate('/dashboard/users'),
        'escape': () => handleCancel(),
        'ctrl+enter': () => handleSubmit()
      }),
      [navigate, formData]
    )
  );

  if (loading) {
    return (
      <div className="view-container">
        <div
          className="form-card"
          style={{
            padding: '60px',
            textAlign: 'center'
          }}
        >
          <Loader2
            className="spin-animation"
            size={40}
            style={{ marginBottom: '16px' }}
          />

          <p>Cargando usuario...</p>
        </div>
      </div>
    );
  }

  if (errorHeader) {
    return (
      <div className="view-container">
        <div className="not-found-card">
          <h2>{errorHeader}</h2>
        </div>
      </div>
    );
  }

  return (
    <div className="view-container">
      <div className="detail-toolbar">
        <button
          className="btn-secondary"
          onClick={() => navigate('/dashboard/users')}
        >
          <ArrowLeft size={16} />
          <span>Volver a Usuarios</span>
          <span className="btn-shortcut">Ctrl+B</span>
        </button>
      </div>

      <div
        className="form-card"
        style={{
          width: '100%',
          maxWidth: '700px',
          margin: '0 auto'
        }}
      >
        <div className="form-header">
          <div className="form-icon-container">
            <User size={32} />
          </div>

          <div>
            <h2 className="form-title">
              Editar Usuario
            </h2>

            <p className="form-subtitle">
              Modifique la información del usuario
            </p>
          </div>
        </div>

        <form
          className="product-form"
          onSubmit={handleSubmit}
          noValidate
        >
          <div className="form-grid">



            <div className="form-group">
              <label>
                Nombre de Usuario
                <span className="required"> *</span>
              </label>

              <input
                type="text"
                name="userName"
                className={getInputClass('userName')}
                value={formData.userName}
                onChange={handleChange}
              />

              <p className={`error-text ${formErrors.userName ? 'visible' : ''}`}>
                {formErrors.userName || '\u00A0'}
              </p>
            </div>

            <div className="form-group">
              <label>
                Nueva Contraseña
              </label>

              <input
                type="password"
                name="userPassword"
                className={getInputClass('userPassword')}
                value={formData.userPassword}
                onChange={handleChange}
                placeholder="Dejar vacío para mantener la actual"
              />

              <p className={`error-text ${formErrors.userPassword ? 'visible' : ''}`}>
                {formErrors.userPassword || '\u00A0'}
              </p>
            </div>
          </div>

          {generalError && (
            <div className="form-error-banner">
              {generalError}
            </div>
          )}

          <div className="form-actions">
            <button
              type="button"
              className="btn-secondary"
              onClick={handleCancel}
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
                  ? 'Actualizando...'
                  : 'Actualizar Usuario'}
              </span>

              <span className="btn-shortcut">
                Ctrl+Enter
              </span>
            </button>
          </div>
        </form>
      </div>

      <ConfirmModal
        isOpen={blocker.state === 'blocked'}
        title="Cambios sin Guardar"
        message="Tienes cambios sin guardar. ¿Deseas salir de esta página?"
        onConfirm={() => blocker.proceed()}
        onCancel={() => blocker.reset()}
        confirmText="Salir"
        confirmButtonTheme="danger"
      />
    </div>
  );
};

export default UserEditView;