import React, { useState, useEffect, useRef } from 'react';
import { useNavigate, useLocation, useBlocker } from 'react-router-dom';
import { useToast } from './ToastContext';
import { useProductsContext } from './ProductsContext';
import { ArrowLeft, Save, Box, X, Info } from 'lucide-react';
import { productService } from '../services/productService';
import { TEXTS } from '../constants/texts';
import { useKeyboardShortcuts } from '../hooks/useKeyboardShortcuts';
import ConfirmModal from './ConfirmModal';
import './ProductCreateView.css';

const ProductCreateView = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { addToast } = useToast();
  const { setIsCached } = useProductsContext();

  const productNameRef = useRef(null);

  // --- Navigation Guard ---
  const [isDirty, setIsDirty] = useState(false);
  const isSubmittingRef = useRef(false);

  // Intercept navigation if there are unsaved changes
  const blocker = useBlocker(
    ({ nextLocation }) =>
      isDirty && !isSubmittingRef.current && nextLocation.pathname !== location.pathname
  );

  const [formData, setFormData] = useState({
    productCode: '',
    productName: '',
    productPrice: '0.00',
    productStock: '0.00',
    minimumStock: '0.00',
    unitOfMeasure: 'UNITS',
  });


  const [unitOfMeasureOptions, setUnitOfMeasureOptions] = useState([]);
  const [loadingOpts, setLoadingOpts] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [formErrors, setFormErrors] = useState({});
  const [generalError, setGeneralError] = useState(null);

  useEffect(() => {
    let isMounted = true;
    const fetchMetadata = async () => {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 8000);

      try {
        const res = await productService.getMetadata({ signal: controller.signal });
        clearTimeout(timeoutId);
        if (isMounted) {
          setUnitOfMeasureOptions(res.data.unitOfMeasureOptions || []);
        }
      } catch (err) {
        clearTimeout(timeoutId);
        if (isMounted) {
          const errMsg = err.name === 'AbortError' ? 'La solicitud excedió el tiempo de espera' : 'No se pudieron cargar las unidades de medida';
          setGeneralError(errMsg);
        }
      } finally {
        if (isMounted) {
          setLoadingOpts(false);
        }
      }
    };
    fetchMetadata();
    return () => { isMounted = false; };
  }, [addToast]);

  // Handle URL parameter for pre-filling scanned barcode
  useEffect(() => {
    const searchParams = new URLSearchParams(location.search);
    const codeFromUrl = searchParams.get('productCode');

    if (codeFromUrl) {
      setFormData(prev => ({ ...prev, productCode: codeFromUrl }));
      // Focus on product name if code is provided via scanner
      setTimeout(() => {
        if (productNameRef.current) {
          productNameRef.current.focus();
        }
      }, 100);
    }
  }, [location.search]);

  const handleChange = (e) => {
    const { name, value } = e.target;

    // Strict numeric formatting: No commas, max 2 decimals, max 10 integer digits
    if (['productPrice', 'productStock', 'minimumStock'].includes(name)) {
      if (value.includes(',')) return;
      const parts = value.split('.');
      if (parts.length > 2) return; // Prevent more than one decimal point
      if (parts[0].length > 10) return;
      if (parts.length > 1 && parts[1].length > 2) return;
    }

    setFormData(prev => ({ ...prev, [name]: value }));
    // Mark form as dirty when any field changes
    setIsDirty(true);
    // Clear the error for this field when user starts typing
    if (formErrors[name]) {
      setFormErrors(prev => ({ ...prev, [name]: undefined }));
    }
  };

  const handleKeyDownNumeric = (e) => {
    // Prevent comma
    if (e.key === ',') {
      e.preventDefault();
      return;
    }

    // Allow: backspace, delete, tab, escape, enter, .
    if ([8, 46, 9, 27, 13, 110, 190].includes(e.keyCode) ||
      // Allow: Ctrl+A, Command+A
      (e.keyCode === 65 && (e.ctrlKey === true || e.metaKey === true)) ||
      // Allow: home, end, left, right, down, up
      (e.keyCode >= 35 && e.keyCode <= 40)) {
      return;
    }
    // Ensure that it is a number and stop the keypress
    if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
      e.preventDefault();
    }
  };

  const validateFrontEnd = () => {
    const errors = {};
    const code = formData.productCode.trim();
    if (!code) {
      errors.productCode = "El código de producto es obligatorio";
    } else if (code.length > 100) {
      errors.productCode = "El código del producto no debe superar los 100 caracteres";
    }

    const name = formData.productName.trim();
    if (!name) {
      errors.productName = "El nombre del producto es obligatorio";
    } else if (name.length > 100) {
      errors.productName = "El nombre del producto no debe superar los 100 caracteres";
    }

    const numRegex = /^-?\d+(\.\d{1,2})?$/;

    const priceRaw = String(formData.productPrice).trim();
    if (priceRaw === '') {
      errors.productPrice = "El precio del producto es obligatorio";
    } else if (!numRegex.test(priceRaw)) {
      errors.productPrice = "El precio debe ser un número válido con hasta 2 decimales (use . como separador)";
    } else {
      const price = Number(priceRaw);
      if (price < 0) {
        errors.productPrice = "El precio del producto debe ser mayor o igual a 0";
      } else {
        const parts = price.toString().split('.');
        if (parts[0].length > 10) {
          errors.productPrice = "El precio del producto debe tener hasta 10 dígitos enteros";
        }
      }
    }

    const stockRaw = String(formData.productStock).trim();
    if (stockRaw === '') {
      errors.productStock = "El stock del producto es obligatorio";
    } else if (!numRegex.test(stockRaw)) {
      errors.productStock = "El stock debe ser un número válido con hasta 2 decimales (use . como separador)";
    } else {
      const stock = Number(stockRaw);
      if (stock < 0) {
        errors.productStock = "El stock del producto debe ser mayor o igual a 0";
      } else {
        const parts = stock.toString().split('.');
        if (parts[0].length > 10) {
          errors.productStock = "El stock debe tener hasta 10 dígitos enteros";
        } else if (formData.unitOfMeasure === 'UNITS' && !Number.isInteger(stock)) {
          errors.productStock = "El stock debe ser un valor entero cuando la unidad de medida es 'Unidades'";
        }
      }
    }

    const minStockRaw = String(formData.minimumStock).trim();
    if (minStockRaw === '') {
      errors.minimumStock = "El stock mínimo es obligatorio";
    } else if (!numRegex.test(minStockRaw)) {
      errors.minimumStock = "El stock mínimo debe ser un número válido con hasta 2 decimales (use . como separador)";
    } else {
      const minStock = Number(minStockRaw);
      if (minStock < 0) {
        errors.minimumStock = "El stock mínimo debe ser mayor o igual a 0";
      } else {
        const parts = minStock.toString().split('.');
        if (parts[0].length > 10) {
          errors.minimumStock = "El stock mínimo debe tener hasta 10 dígitos enteros";
        } else if (formData.unitOfMeasure === 'UNITS' && !Number.isInteger(minStock)) {
          errors.minimumStock = "El stock mínimo debe ser un valor entero cuando la unidad de medida es 'Unidades'";
        }
      }
    }

    if (!formData.unitOfMeasure) {
      errors.unitOfMeasure = "La unidad de medida es obligatoria";
    }

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e) => {
    if (e) e.preventDefault();
    if (!validateFrontEnd()) return;

    setSubmitting(true);
    setFormErrors({});
    setGeneralError(null);

    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 10000);

    try {
      const payload = {
        productCode: formData.productCode.trim(),
        productName: formData.productName.trim(),
        productPrice: Number(formData.productPrice),
        productStock: Number(formData.productStock),
        minimumStock: Number(formData.minimumStock),
        unitOfMeasure: formData.unitOfMeasure
      };

      const res = await productService.createProduct(payload, { signal: controller.signal });
      clearTimeout(timeoutId);
      addToast(res.message || TEXTS.products.createSuccess, "success");

      // IMPORTANT: Use the ref to bypass the navigation guard
      isSubmittingRef.current = true;

      navigate(`/dashboard/products/${res.data.productCode}`, {
        state: { product: res.data },
        replace: true
      });

    } catch (err) {
      clearTimeout(timeoutId);
      if (err.name === 'AbortError') {
        setGeneralError('La solicitud excedió el tiempo de espera. Verifique su conexión.');
        addToast('La solicitud excedió el tiempo de espera', 'error');
      } else {
        const data = err.details || {};
        const errorData = data.error || {};

        // Backend standardized error format handling
        if (err.status === 400) {
          if (errorData.field) {
            setFormErrors({ [errorData.field]: errorData.message });
            addToast(errorData.message, 'error');
          } else if (typeof data === 'object' && !errorData.message) {
            // Fallback for map of errors if any
            setFormErrors(data);
            Object.values(data).forEach(msg => addToast(msg, 'error'));
          } else {
            const msg = errorData.message || err.message || 'Los datos proporcionados son inválidos';
            setGeneralError(msg);
            addToast(msg, 'error');
          }
        } else {
          const msg = err.message || 'Ocurrió un error al registrar el producto';
          setGeneralError(msg);
          addToast(msg, 'error');
        }
      }
    } finally {
      setSubmitting(false);
    }
  };

  const getInputClass = (fieldName) => {
    return formErrors[fieldName] ? 'form-input error' : 'form-input';
  };

  // Register contextual shortcuts
  useKeyboardShortcuts(React.useMemo(() => ({
    'ctrl+b': () => navigate('/dashboard/products'),
    'escape': () => navigate(-1),
    'ctrl+enter': () => handleSubmit()
  }), [navigate, formData]));

  return (
    <div className="view-container">
      <div className="detail-toolbar">
        <button type="button" className="btn-secondary" onClick={() => navigate('/dashboard/products')}>
          <ArrowLeft size={16} />
          <span>Volver a Productos</span>
          <span className="btn-shortcut">Ctrl+B</span>
        </button>
      </div>

      <div className="form-card">
        <div className="form-header">
          <div className="form-icon-container">
            <Box size={28} className="form-main-icon" />
          </div>
          <div>
            <h2 className="form-title">Registrar Nuevo Producto</h2>
            <p className="form-subtitle">Ingrese los detalles a continuación para crear un nuevo producto en el sistema.</p>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="product-form" noValidate>
          <div className="form-grid">
            <div className="form-group">
              <label htmlFor="productCode">Código de Producto <span className="required">*</span></label>
              <input
                type="text"
                id="productCode"
                name="productCode"
                className={getInputClass('productCode')}
                value={formData.productCode}
                onChange={handleChange}
                disabled={submitting}
                placeholder="Ingrese el código del producto"
              />
              <p className={`error-text ${formErrors.productCode ? 'visible' : ''}`}>
                {formErrors.productCode || '\u00A0'}
              </p>
            </div>

            <div className="form-group">
              <label htmlFor="productName" className="label-with-tooltip">
                <span>Nombre del Producto <span className="required">*</span></span>
                <div className="tooltip-container" tabIndex="0" aria-label="Utilice un nombre claro y específico para distinguir este producto de productos similares.">
                  <Info size={14} className="info-icon" />
                  <span className="tooltip-text" aria-hidden="true">
                    Utilice un nombre claro y específico para distinguir este producto de productos similares.
                  </span>
                </div>
              </label>
              <input
                ref={productNameRef}
                type="text"
                id="productName"
                name="productName"
                className={getInputClass('productName')}
                value={formData.productName}
                onChange={handleChange}
                disabled={submitting}
                placeholder="Ingrese el nombre del producto"
              />
              <p className={`error-text ${formErrors.productName ? 'visible' : ''}`}>
                {formErrors.productName || '\u00A0'}
              </p>
            </div>

            <div className="form-group">
              <label htmlFor="productPrice">Precio ($) <span className="required">*</span></label>
              <input
                type="text"
                id="productPrice"
                name="productPrice"
                className={getInputClass('productPrice')}
                value={formData.productPrice}
                onChange={handleChange}
                onKeyDown={handleKeyDownNumeric}
                disabled={submitting}
                placeholder="0.00"
              />
              <p className={`error-text ${formErrors.productPrice ? 'visible' : ''}`}>
                {formErrors.productPrice || '\u00A0'}
              </p>
            </div>

            <div className="form-group">
              <label htmlFor="unitOfMeasure">Unidad de Medida <span className="required">*</span></label>
              <select
                id="unitOfMeasure"
                name="unitOfMeasure"
                className={getInputClass('unitOfMeasure')}
                value={formData.unitOfMeasure}
                onChange={handleChange}
                disabled={loadingOpts || submitting}
              >
                {loadingOpts ? <option value="UNITS">Cargando...</option> : null}
                {unitOfMeasureOptions.map(opt => (
                  <option key={opt.code} value={opt.code}>{opt.label}</option>
                ))}
              </select>
              <p className={`error-text ${formErrors.unitOfMeasure ? 'visible' : ''}`}>
                {formErrors.unitOfMeasure || '\u00A0'}
              </p>
            </div>

            <div className="form-group">
              <label htmlFor="productStock">Stock Inicial <span className="required">*</span></label>
              <input
                type="text"
                id="productStock"
                name="productStock"
                className={getInputClass('productStock')}
                value={formData.productStock}
                onChange={handleChange}
                onKeyDown={handleKeyDownNumeric}
                disabled={submitting}
                placeholder="0.00"
              />
              <p className={`error-text ${formErrors.productStock ? 'visible' : ''}`}>
                {formErrors.productStock || '\u00A0'}
              </p>
            </div>

            <div className="form-group">

              <label htmlFor="minimumStock" className="label-with-tooltip">
                <span>Stock Mínimo <span className="required">*</span></span>
                <div className="tooltip-container" tabIndex="0" aria-label="Es la cantidad mínima recomendada para este producto. Si el stock disponible es igual o inferior a este valor, el sistema alertará que el producto tiene stock bajo.">
                  <Info size={14} className="info-icon" />
                  <span className="tooltip-text" aria-hidden="true">
                    Es la cantidad mínima recomendada para este producto. Si el stock disponible es igual o inferior a este valor, el sistema alertará que el producto tiene stock bajo.
                  </span>
                </div>
              </label>
              <input
                type="text"
                id="minimumStock"
                name="minimumStock"
                className={getInputClass('minimumStock')}
                value={formData.minimumStock}
                onChange={handleChange}
                onKeyDown={handleKeyDownNumeric}
                disabled={submitting}
                placeholder="0.00"
              />

              <p className={`error-text ${formErrors.minimumStock ? 'visible' : ''}`}>
                {formErrors.minimumStock || '\u00A0'}
              </p>
            </div>
          </div>

          <div className="form-actions">
            <button
              type="button"
              className="btn-secondary"
              onClick={() => navigate('/dashboard/products')}
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
              <span>{submitting ? 'Registrando...' : 'Registrar Producto'}</span>
              <span className="btn-shortcut" style={{ backgroundColor: 'rgba(255, 255, 255, 0.2)', color: 'white', borderColor: 'rgba(255, 255, 255, 0.3)' }}>Ctrl+Enter</span>
            </button>
          </div>
        </form>
      </div>

      <ConfirmModal
        isOpen={blocker.state === "blocked"}
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

export default ProductCreateView;
