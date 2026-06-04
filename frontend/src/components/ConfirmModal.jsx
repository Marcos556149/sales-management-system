import React from 'react';
import './ConfirmModal.css';

const ConfirmModal = ({
  isOpen,
  title,
  message,
  onConfirm,
  onCancel,
  isConfirming,
  confirmText = 'Desactivar',
  confirmButtonTheme = 'danger'
}) => {
  React.useEffect(() => {
    if (!isOpen) return;

    // Block interactions with the rest of the app
    document.body.classList.add('modal-open-blocking');

    const handleKeyDown = (e) => {
      // Prevent event from reaching other listeners (like global shortcuts)
      e.stopPropagation();

      if (isConfirming) {
        e.preventDefault();
        return;
      }

      if (e.key === 'Enter') {
        e.preventDefault();
        onConfirm();
      } else if (e.key === 'Escape') {
        e.preventDefault();
        onCancel();
      }
    };

    window.addEventListener('keydown', handleKeyDown, true);
    return () => {
      window.removeEventListener('keydown', handleKeyDown, true);
      document.body.classList.remove('modal-open-blocking');
    };
  }, [isOpen, onConfirm, onCancel, isConfirming]);

  if (!isOpen) return null;

  return (
    <div className="confirm-modal-overlay">
      <div className="confirm-modal-content">
        <h3 className="confirm-modal-title">{title}</h3>
        <p className="confirm-modal-message">{message}</p>
        <div className="confirm-modal-actions">
          <button
            className="confirm-modal-btn confirm-modal-btn-secondary"
            onClick={onCancel}
            disabled={isConfirming}
          >
            Cancelar
          </button>
          <button
            className={`confirm-modal-btn confirm-modal-btn-${confirmButtonTheme}`}
            onClick={onConfirm}
            disabled={isConfirming}
          >
            {isConfirming ? 'Procesando...' : confirmText}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ConfirmModal;
