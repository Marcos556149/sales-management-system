import React, { useEffect, useState } from 'react';
import { UserCog, X, Loader2, Info } from 'lucide-react';
import { useToast } from './ToastContext';
import { userService } from '../services/userService';



const ChangeUserStatusModal = ({
  isOpen,
  user,
  onClose,
  onSuccess
}) => {
  const { addToast } = useToast();

  const [selectedStatus, setSelectedStatus] = useState('');
  const [saving, setSaving] = useState(false);

  const [statusOptions, setStatusOptions] = useState([]);
  const [loadingOptions, setLoadingOptions] = useState(false);

  useEffect(() => {
    if (isOpen && user) {
      setSelectedStatus(user.userStatus?.code ?? '');
    }
  }, [isOpen, user]);

  useEffect(() => {
    if (!isOpen) {
      setSelectedStatus('');
      setSaving(false);
    }
  }, [isOpen]);

  useEffect(() => {
    if (!isOpen) return;

    const fetchMetadata = async () => {
      setLoadingOptions(true);

      try {
        const response = await userService.getMetadata();

        setStatusOptions(
          response.data?.userStatusOptions ?? []
        );
      } catch (err) {
        console.error(
          'Error al cargar estados de usuario:',
          err
        );

        addToast(
          'No se pudieron cargar los estados disponibles',
          'error'
        );
      } finally {
        setLoadingOptions(false);
      }
    };

    fetchMetadata();
  }, [isOpen, addToast]);

  useEffect(() => {
    if (!isOpen) return;

    const handleKeyDown = (e) => {
      e.stopPropagation();

      if (e.key === 'Escape') {
        e.preventDefault();

        if (!saving) {
          onClose();
        }
      }
    };

    window.addEventListener('keydown', handleKeyDown, true);

    return () => {
      window.removeEventListener('keydown', handleKeyDown, true);
    };
  }, [isOpen, saving, onClose]);

  if (!isOpen || !user) {
    return null;
  }

  const isSameStatus =
    selectedStatus === user.userStatus?.code;

  const handleSave = async (e) => {
    e.preventDefault();

    if (isSameStatus) {
      return;
    }

    setSaving(true);

    try {
      const response = await userService.changeStatus(
        user.userId,
        {
          userStatus: selectedStatus
        }
      );

      addToast(
        response.message ||
        'Estado de usuario actualizado correctamente',
        'success'
      );

      onClose();

      if (onSuccess) {
        await onSuccess();
      }
    } catch (err) {
      addToast(
        err.message ||
        'No se pudo actualizar el estado del usuario',
        'error'
      );
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="pdf-modal-overlay">
      <div
        className="pdf-modal-content"
        style={{ maxWidth: '520px' }}
      >
        <div className="pdf-modal-header">
          <h3 className="pdf-modal-title">
            <UserCog size={20} />
            Cambiar estado de usuario
          </h3>

          <button
            type="button"
            className="pdf-modal-close-btn"
            onClick={onClose}
            disabled={saving}
          >
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSave}>
          <div className="pdf-modal-body">

            <div className="pdf-form-section">
              <h4 className="pdf-section-title">
                Información del usuario
              </h4>

              <div className="pdf-form-group">
                <label>Usuario</label>

                <input
                  type="text"
                  value={user.userName}
                  disabled
                  className="pdf-select"
                />
              </div>

              <div className="pdf-form-group">
                <label>Estado actual</label>

                <input
                  type="text"
                  value={user.userStatus?.label ?? ''}
                  disabled
                  className="pdf-select"
                />
              </div>
            </div>

            <div className="pdf-form-section">
              <h4 className="pdf-section-title">
                Nuevo estado
              </h4>

              <div className="pdf-form-group">
                <label htmlFor="userStatus">
                  Estado
                </label>

                <select
                  id="userStatus"
                  className="pdf-select"
                  value={selectedStatus}
                  onChange={(e) =>
                    setSelectedStatus(e.target.value)
                  }
                  disabled={saving}
                >
                  {statusOptions.map((status) => (
                    <option
                      key={status.code}
                      value={status.code}
                    >
                      {status.label}
                    </option>
                  ))}
                </select>
              </div>

              {isSameStatus && (
                <p
                  style={{
                    fontSize: '0.875rem',
                    color: 'var(--danger-color)',
                    marginTop: '8px'
                  }}
                >
                  Debe seleccionar un estado diferente al actual.
                </p>
              )}

              <div
                style={{
                  display: 'flex',
                  gap: '8px',
                  alignItems: 'flex-start',
                  marginTop: '16px',
                  fontSize: '0.875rem',
                  color: 'var(--text-secondary)'
                }}
              >
                <Info size={16} />

                <span>
                  Los usuarios suspendidos o eliminados no pueden acceder al sistema.
                </span>
              </div>
            </div>

          </div>

          <div className="pdf-modal-footer">
            <button
              type="button"
              className="pdf-btn-secondary"
              onClick={onClose}
              disabled={saving || loadingOptions}
            >
              Cancelar
            </button>

            <button
              type="submit"
              className="pdf-btn-primary"
              disabled={
                saving ||
                loadingOptions ||
                isSameStatus
              }
            >
              {saving ? (
                <>
                  <Loader2
                    size={16}
                    className="pdf-spin-animation"
                    style={{ marginRight: '8px' }}
                  />
                  Guardando...
                </>
              ) : (
                'Guardar cambios'
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ChangeUserStatusModal;