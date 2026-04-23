import React, { useState, useEffect, useRef } from 'react';
import { useNavigate, useParams, useLocation, useBlocker } from 'react-router-dom';
import { Package, Save, X, ArrowLeft, Loader2 } from 'lucide-react';
import { useToast } from './ToastContext';
import { useProductsContext } from './ProductsContext';
import { productService } from '../services/productService';
import { TEXTS } from '../constants/texts';
import { useKeyboardShortcuts } from '../hooks/useKeyboardShortcuts';
import ConfirmModal from './ConfirmModal';
import './ProductCreateView.css';

const ProductEditView = () => {
  const { id: productCode } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const { addToast } = useToast();
  const { setIsCached } = useProductsContext();
  
  const initialProduct = location.state?.product;

  // --- Navigation Guard ---
  const [isDirty, setIsDirty] = useState(false);
  const isSubmittingRef = useRef(false);
  
  const blocker = useBlocker(
    ({ nextLocation }) =>
      isDirty && !isSubmittingRef.current && nextLocation.pathname !== location.pathname
  );
  

  // --- Form State ---
  const [formData, setFormData] = useState({
    productName: initialProduct?.productName || '',
    productPrice: initialProduct?.productPrice?.toString() || '0.00',
    productStock: initialProduct?.productStock?.toString() || '0.00',
    minimumStock: initialProduct?.minimumStock?.toString() || '0.00',
    unitOfMeasure: initialProduct?.unitOfMeasure?.code || ''
  });

  const [formErrors, setFormErrors] = useState({});
  const [loading, setLoading] = useState(!initialProduct || initialProduct.productCode !== productCode);
  const [submitting, setSubmitting] = useState(false);
  const [unitOfMeasureOptions, setUnitOfMeasureOptions] = useState([]);
  const [errorHeader, setErrorHeader] = useState(null);
  const [generalError, setGeneralError] = useState(null);

  // --- Fetch Initial Data ---
  useEffect(() => {
    let isMounted = true;
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 10000);

    const fetchData = async () => {
      try {
        const metaPromise = productService.getMetadata({ signal: controller.signal });
        const productPromise = (initialProduct && initialProduct.productCode === productCode)
          ? Promise.resolve({ data: initialProduct })
          : productService.getProduct(productCode, { signal: controller.signal });

        const [metaRes, prodRes] = await Promise.all([metaPromise, productPromise]);

        clearTimeout(timeoutId);

        if (isMounted) {
          setUnitOfMeasureOptions(metaRes.data.unitOfMeasureOptions || []);
          setFormData({
            productName: prodRes.data.productName || '',
            productPrice: prodRes.data.productPrice?.toString() || '0.00',
            productStock: prodRes.data.productStock?.toString() || '0.00',
            minimumStock: prodRes.data.minimumStock?.toString() || '0.00',
            unitOfMeasure: prodRes.data.unitOfMeasure?.code || ''
          });
          setLoading(false);
        }
      } catch (err) {
        clearTimeout(timeoutId);
        if (isMounted) {
          const msg = err.name === 'AbortError' ? 'Request timed out' : err.message;
          setErrorHeader(msg);
          setLoading(false);
        }
      }
    };

    fetchData();

    return () => {
      isMounted = false;
      controller.abort();
    };
  }, [productCode, addToast]);

  // --- Handlers ---
  const handleChange = (e) => {
    const { name, value } = e.target;

    // Strict numeric formatting: No commas, max 2 decimals
    if (['productPrice', 'productStock', 'minimumStock'].includes(name)) {
      if (value.includes(',')) return;
      const parts = value.split('.');
      if (parts.length > 1 && parts[1].length > 2) return;
    }

    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // Mark form as dirty when user changes any field
    setIsDirty(true);
    // Clear field error when user types
    if (formErrors[name]) {
      setFormErrors(prev => {
        const newErrors = { ...prev };
        delete newErrors[name];
        return newErrors;
      });
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

    const name = formData.productName.trim();
    if (!name) {
      errors.productName = "Product name is required";
    } else if (name.length > 100) {
      errors.productName = "Product name must not exceed 100 characters";
    }
    
    const numRegex = /^-?\d+(\.\d{1,2})?$/;

    const priceRaw = String(formData.productPrice).trim();
    if (priceRaw === '') {
      errors.productPrice = "Product price is required";
    } else if (!numRegex.test(priceRaw)) {
      errors.productPrice = "Product price must be a valid number with up to 2 decimals (use . as separator)";
    } else {
      const price = Number(priceRaw);
      if (price < 0) {
        errors.productPrice = "Product price must be greater than or equal to 0";
      } else {
        const parts = price.toString().split('.');
        if (parts[0].length > 10) {
          errors.productPrice = "Product price must have up to 10 integer digits";
        }
      }
    }

    const stockRaw = String(formData.productStock).trim();
    if (stockRaw === '') {
      errors.productStock = "Product stock is required";
    } else if (!numRegex.test(stockRaw)) {
      errors.productStock = "Product stock must be a valid number with up to 2 decimals (use . as separator)";
    } else {
      const stock = Number(stockRaw);
      if (stock < 0) {
        errors.productStock = "Product stock must be greater than or equal to 0";
      } else {
        const parts = stock.toString().split('.');
        if (parts[0].length > 10) {
          errors.productStock = "Product stock must have up to 10 integer digits";
        } else if (formData.unitOfMeasure === 'UNITS' && !Number.isInteger(stock)) {
          errors.productStock = "Stock must be an integer value when unit of measure is Units";
        }
      }
    }

    const minStockRaw = String(formData.minimumStock).trim();
    if (minStockRaw === '') {
      errors.minimumStock = "Minimum stock is required";
    } else if (!numRegex.test(minStockRaw)) {
      errors.minimumStock = "Minimum stock must be a valid number with up to 2 decimals (use . as separator)";
    } else {
      const minStock = Number(minStockRaw);
      if (minStock < 0) {
        errors.minimumStock = "Minimum stock must be greater than or equal to 0";
      } else {
        const parts = minStock.toString().split('.');
        if (parts[0].length > 10) {
          errors.minimumStock = "Minimum stock must have up to 10 integer digits";
        } else if (formData.unitOfMeasure === 'UNITS' && !Number.isInteger(minStock)) {
          errors.minimumStock = "Minimum stock must be an integer value when unit of measure is Units";
        }
      }
    }

    if (!formData.unitOfMeasure) {
      errors.unitOfMeasure = "Unit of measure is required";
    }

    return errors;
  };

  const handleSubmit = async (e) => {
    if (e) e.preventDefault();
    if (submitting) return;

    const frontendErrors = validateFrontEnd();
    if (Object.keys(frontendErrors).length > 0) {
      setFormErrors(frontendErrors);
      return;
    }

    setSubmitting(true);
    setFormErrors({});
    setGeneralError(null);
    
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 10000);

    try {
      const payload = {
        productName: formData.productName.trim(),
        productPrice: Number(formData.productPrice),
        productStock: Number(formData.productStock),
        minimumStock: Number(formData.minimumStock),
        unitOfMeasure: formData.unitOfMeasure
      };

      const res = await productService.updateProduct(productCode, payload, { signal: controller.signal });
      clearTimeout(timeoutId);

      addToast(res.message || TEXTS.products.updateSuccess, "success");
      
      // Bypass the navigation guard for successful update
      isSubmittingRef.current = true;
      
      navigate(`/dashboard/products/${productCode}`, { state: { product: res.data } });

    } catch (err) {
      clearTimeout(timeoutId);
      if (err.name === 'AbortError') {
        setGeneralError('Update request timed out');
        addToast('Update request timed out', 'error');
      } else {
        const data = err.details || {};
        const errorData = data.error || {};
        
        if (err.status === 400) {
          if (errorData.field) {
            setFormErrors({ [errorData.field]: errorData.message });
            addToast(errorData.message, 'error');
          } else if (typeof data === 'object' && !errorData.message) {
            setFormErrors(data);
            Object.values(data).forEach(msg => addToast(msg, 'error'));
          } else {
            const msg = errorData.message || err.message || 'Invalid data provided';
            setGeneralError(msg);
            addToast(msg, 'error');
          }
        } else {
          const msg = err.message || 'An error occurred';
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

  const getInputClass = (fieldName) => {
    return formErrors[fieldName] ? 'form-input error' : 'form-input';
  };

  // Register contextual shortcuts
  useKeyboardShortcuts(React.useMemo(() => ({
    'ctrl+b': () => navigate('/dashboard/products'),
    'escape': () => handleCancel(),
    'ctrl+enter': () => handleSubmit()
  }), [navigate, formData]));

  if (loading) {
    return (
      <div className="view-container">
        <div className="form-card" style={{ padding: '60px', textAlign: 'center' }}>
          <Loader2 className="spin-animation" size={40} style={{ margin: '0 auto 16px', color: 'var(--primary-color)' }} />
          <p style={{ color: 'var(--text-secondary)' }}>Loading product data...</p>
        </div>
      </div>
    );
  }

  if (errorHeader) {
    return (
      <div className="view-container">
        <div className="detail-toolbar">
          <button className="btn-secondary" onClick={() => navigate('/dashboard/products')}>
            <ArrowLeft size={16} />
            <span>Back to Products</span>
          </button>
        </div>
        <div className="not-found-card">
          <h2>{errorHeader}</h2>
        </div>
      </div>
    );
  }

  return (
    <div className="view-container">
      <div className="detail-toolbar">
        <button className="btn-secondary" onClick={() => navigate('/dashboard/products')}>
          <ArrowLeft size={16} />
          <span>Back to Products</span>
          <span className="btn-shortcut">Ctrl+B</span>
        </button>
      </div>

      <div className="form-card">
        <div className="form-header">
          <div className="form-icon-container">
            <Package size={32} />
          </div>
          <div>
            <h2 className="form-title">Edit Product</h2>
            <p className="form-subtitle">Modify the existing product information</p>
          </div>
        </div>
        
        <form className="product-form" onSubmit={handleSubmit} noValidate>
          <div className="form-grid">
            {/* Product Code - Read Only */}
            <div className="form-group">
              <label>Product Code</label>
              <input 
                type="text" 
                className="form-input" 
                value={productCode} 
                disabled 
                title="Product code cannot be changed"
              />
              <p className="error-text">{"\u00A0"}</p>
            </div>

            {/* Product Name */}
            <div className="form-group">
              <label htmlFor="productName">Product Name <span className="required">*</span></label>
              <input 
                type="text" 
                id="productName"
                name="productName"
                className={getInputClass('productName')}
                placeholder="Product name"
                value={formData.productName}
                onChange={handleChange}
              />
              <p className={`error-text ${formErrors.productName ? 'visible' : ''}`}>
                {formErrors.productName || '\u00A0'}
              </p>
            </div>

            {/* Product Price */}
            <div className="form-group">
              <label htmlFor="productPrice">Price ($) <span className="required">*</span></label>
              <input 
                type="text" 
                id="productPrice"
                name="productPrice"
                className={getInputClass('productPrice')}
                placeholder="0.00"
                value={formData.productPrice}
                onChange={handleChange}
                onKeyDown={handleKeyDownNumeric}
              />
              <p className={`error-text ${formErrors.productPrice ? 'visible' : ''}`}>
                {formErrors.productPrice || '\u00A0'}
              </p>
            </div>

            {/* Unit of Measure */}
            <div className="form-group">
              <label htmlFor="unitOfMeasure">Unit of Measure <span className="required">*</span></label>
              <select 
                id="unitOfMeasure"
                name="unitOfMeasure"
                className={getInputClass('unitOfMeasure')}
                value={formData.unitOfMeasure}
                onChange={handleChange}
              >
                {unitOfMeasureOptions.map(opt => (
                  <option key={opt.code} value={opt.code}>
                    {opt.label}
                  </option>
                ))}
              </select>
              <p className={`error-text ${formErrors.unitOfMeasure ? 'visible' : ''}`}>
                {formErrors.unitOfMeasure || '\u00A0'}
              </p>
            </div>

            {/* Product Stock */}
            <div className="form-group">
              <label htmlFor="productStock">Stock <span className="required">*</span></label>
              <input 
                type="text" 
                id="productStock"
                name="productStock"
                className={getInputClass('productStock')}
                placeholder="0.00"
                value={formData.productStock}
                onChange={handleChange}
                onKeyDown={handleKeyDownNumeric}
              />
              <p className={`error-text ${formErrors.productStock ? 'visible' : ''}`}>
                {formErrors.productStock || '\u00A0'}
              </p>
            </div>

            {/* Minimum Stock */}
            <div className="form-group">
              <label htmlFor="minimumStock">Minimum Stock <span className="required">*</span></label>
              <input 
                type="text" 
                id="minimumStock"
                name="minimumStock"
                className={getInputClass('minimumStock')}
                placeholder="0.00"
                value={formData.minimumStock}
                onChange={handleChange}
                onKeyDown={handleKeyDownNumeric}
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
              onClick={handleCancel}
              disabled={submitting}
            >
              <X size={18} />
              <span>Cancel</span>
              <span className="btn-shortcut">Esc</span>
            </button>
            <button 
              type="submit" 
              className="btn-primary form-submit-btn"
              disabled={submitting}
            >
              <Save size={18} />
              <span>{submitting ? 'Updating...' : 'Update Product'}</span>
              <span className="btn-shortcut" style={{ backgroundColor: 'rgba(255, 255, 255, 0.2)', color: 'white', borderColor: 'rgba(255, 255, 255, 0.3)' }}>Ctrl+Enter</span>
            </button>
          </div>
        </form>
      </div>

      <ConfirmModal
        isOpen={blocker.state === "blocked"}
        title="Unsaved Changes"
        message="You have unsaved changes. Are you sure you want to leave this page?"
        onConfirm={() => blocker.proceed()}
        onCancel={() => blocker.reset()}
        confirmText="Leave"
        confirmButtonTheme="danger"
      />
    </div>
  );
};

export default ProductEditView;
