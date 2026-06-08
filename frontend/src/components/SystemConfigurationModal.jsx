import React, { useState, useEffect } from 'react';
import { Settings, X, Save, Loader2 } from 'lucide-react';
import { useToast } from './ToastContext';
import { configService } from '../services/configService';
import './SystemConfigurationModal.css';

const SystemConfigurationModal = ({ isOpen, onClose }) => {
  const { addToast } = useToast();

  const [formData, setFormData] = useState({
    businessName: '',
    businessAddress: ''
  });
  const [formErrors, setFormErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  // Fetch configuration when modal opens
  useEffect(() => {
    if (isOpen) {
      fetchConfig();
    } else {
      // Reset state when closing
      setFormErrors({});
    }
  }, [isOpen]);

  const fetchConfig = async () => {
    setLoading(true);
    setFormErrors({});
    try {
      const res = await configService.getConfig();
      setFormData({
        businessName: res.data.businessName || '',
        businessAddress: res.data.businessAddress || ''
      });
    } catch (err) {
      addToast(err.message || 'Error al cargar la configuración del sistema', 'error');
      onClose(); // Close if we can't load it
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    // Clear error when typing
    if (formErrors[name]) {
      setFormErrors(prev => ({ ...prev, [name]: undefined }));
    }
  };

  const validateFrontEnd = () => {
    const errors = {};
    const name = formData.businessName.trim();
    if (!name) {
      errors.businessName = "El nombre del negocio es obligatorio";
    } else if (name.length > 100) {
      errors.businessName = "El nombre del negocio no debe superar los 100 caracteres";
    }

    const address = formData.businessAddress.trim();
    if (!address) {
      errors.businessAddress = "La dirección del negocio es obligatoria";
    } else if (address.length > 200) {
      errors.businessAddress = "La dirección del negocio no debe superar los 200 caracteres";
    }

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateFrontEnd()) return;

    setSaving(true);
    try {
      const payload = {
        businessName: formData.businessName.trim(),
        businessAddress: formData.businessAddress.trim()
      };
      const res = await configService.updateConfig(payload);
      addToast(res.message || 'Configuración actualizada correctamente', 'success');
      onClose();
    } catch (err) {
      const data = err.details || {};
      const errorData = data.error || {};

      // Handle standard API validation errors
      if (err.status === 400) {
        if (errorData.field) {
          setFormErrors({ [errorData.field]: errorData.message });
          addToast(errorData.message, 'error');
        } else if (typeof data === 'object' && !errorData.message) {
          setFormErrors(data);
          Object.values(data).forEach(msg => addToast(msg, 'error'));
        } else {
          const msg = errorData.message || err.message || 'Datos inválidos proporcionados';
          addToast(msg, 'error');
        }
      } else {
        addToast(err.message || 'Error al actualizar la configuración', 'error');
      }
    } finally {
      setSaving(false);
    }
  };

  // Handle keyboard shortcuts (Escape to close, Ctrl+Enter to save)
  useEffect(() => {
    if (!isOpen) return;

    const handleKeyDown = (e) => {
      // Prevent event from reaching other listeners (like global shortcuts)
      e.stopPropagation();

      if (e.key === 'Escape') {
        e.preventDefault();
        if (!saving) onClose();
      } else if (e.key === 'Enter' && (e.ctrlKey || e.metaKey)) {
        e.preventDefault();
        if (!saving && !loading) handleSubmit(new Event('submit'));
      }
    };

    window.addEventListener('keydown', handleKeyDown, true);
    return () => {
      window.removeEventListener('keydown', handleKeyDown, true);
    };
  }, [isOpen, saving, loading, formData]); // dependencies include formData for handleSubmit

  if (!isOpen) return null;

  const getInputClass = (fieldName) => {
    return formErrors[fieldName] ? 'config-input error' : 'config-input';
  };

  return (
    <div className="config-modal-overlay">
      <div className="config-modal-content">
        <div className="config-modal-header">
          <h3 className="config-modal-title">
            <Settings size={20} />
            Configuración del sistema
          </h3>
          <button
            type="button"
            className="config-modal-close-btn"
            onClick={onClose}
            disabled={saving}
          >
            <X size={20} />
          </button>
        </div>

        {loading ? (
          <div className="config-modal-loading">
            <Loader2 size={32} className="config-spin-animation" />
            <p>Cargando configuración...</p>
          </div>
        ) : (
          <form onSubmit={handleSubmit} noValidate>
            <div className="config-modal-body">
              <div className="config-form-group">
                <label htmlFor="businessName">Nombre del negocio <span className="required">*</span></label>
                <input
                  type="text"
                  id="businessName"
                  name="businessName"
                  className={getInputClass('businessName')}
                  value={formData.businessName}
                  onChange={handleChange}
                  disabled={saving}
                  placeholder="Enter business name"
                />
                <p className={`config-error-text ${formErrors.businessName ? 'visible' : ''}`}>
                  {formErrors.businessName || '\u00A0'}
                </p>
              </div>

              <div className="config-form-group">
                <label htmlFor="businessAddress">Dirección del negocio <span className="required">*</span></label>
                <input
                  type="text"
                  id="businessAddress"
                  name="businessAddress"
                  className={getInputClass('businessAddress')}
                  value={formData.businessAddress}
                  onChange={handleChange}
                  disabled={saving}
                  placeholder="Enter business address"
                />
                <p className={`config-error-text ${formErrors.businessAddress ? 'visible' : ''}`}>
                  {formErrors.businessAddress || '\u00A0'}
                </p>
              </div>
            </div>

            <div className="config-modal-footer">
              <button
                type="button"
                className="btn-secondary"
                onClick={onClose}
                disabled={saving}
              >
                Cancel
              </button>
              <button
                type="submit"
                className="btn-primary"
                disabled={saving}
              >
                {saving ? (
                  <>
                    <Loader2 size={16} className="config-spin-animation" style={{ marginBottom: 0, marginRight: '8px' }} />
                    Guardando...
                  </>
                ) : (
                  <>
                    <Save size={16} />
                    Guardar configuración
                  </>
                )}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};

export default SystemConfigurationModal;
