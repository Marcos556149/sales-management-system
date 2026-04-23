import React from 'react';
import './ConfirmModal.css';

const ConfirmModal = ({ 
  isOpen, 
  title, 
  message, 
  onConfirm, 
  onCancel, 
  isConfirming,
  confirmText = 'Deactivate',
  confirmButtonTheme = 'danger'
}) => {
  React.useEffect(() => {
    if (!isOpen) return;

    const handleKeyDown = (e) => {
      if (isConfirming) return;
      
      if (e.key === 'Enter') {
        e.preventDefault();
        e.stopImmediatePropagation();
        onConfirm();
      } else if (e.key === 'Escape') {
        e.preventDefault();
        e.stopImmediatePropagation();
        onCancel();
      }
    };

    window.addEventListener('keydown', handleKeyDown, true);
    return () => window.removeEventListener('keydown', handleKeyDown, true);
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
            Cancel
          </button>
          <button 
            className={`confirm-modal-btn confirm-modal-btn-${confirmButtonTheme}`} 
            onClick={onConfirm} 
            disabled={isConfirming}
          >
            {isConfirming ? 'Processing...' : confirmText}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ConfirmModal;
