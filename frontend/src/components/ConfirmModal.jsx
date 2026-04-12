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
