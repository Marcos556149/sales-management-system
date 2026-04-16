import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Package, Save, X, ArrowLeft, Loader2 } from 'lucide-react';
import { useToast } from './ToastContext';
import './ProductCreateView.css'; // Reusing the same styles for consistency

const ProductEditView = () => {
  const { id: productCode } = useParams();
  const navigate = useNavigate();
  const { addToast } = useToast();

  // --- Form State ---
  const [formData, setFormData] = useState({
    productName: '',
    productPrice: '0.00',
    productStock: '0.00',
    unitOfMeasure: ''
  });

  const [formErrors, setFormErrors] = useState({});
  const [loading, setLoading] = useState(true);
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
        // Fetch Metadata and Product data in parallel
        const [metaRes, prodRes] = await Promise.all([
          fetch('/api/products/metadata', { signal: controller.signal }),
          fetch(`/api/products/${productCode}`, { signal: controller.signal })
        ]);

        clearTimeout(timeoutId);

        if (!metaRes.ok) throw new Error('Failed to load unit options');
        if (!prodRes.ok) {
           const errorData = await prodRes.json().catch(() => ({}));
           const errMsg = errorData.error || errorData.message || 'Failed to load product details';
           setErrorHeader(errMsg);
           setLoading(false);
           return;
        }

        const metaData = await metaRes.json();
        const prodData = await prodRes.json();

        if (isMounted) {
          setUnitOfMeasureOptions(metaData.unitOfMeasureOptions || []);
          setFormData({
            productName: prodData.productName || '',
            productPrice: prodData.productPrice?.toString() || '0.00',
            productStock: prodData.productStock?.toString() || '0.00',
            unitOfMeasure: prodData.unitOfMeasure?.code || ''
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
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // Clear field error when user types
    if (formErrors[name]) {
      setFormErrors(prev => {
        const newErrors = { ...prev };
        delete newErrors[name];
        return newErrors;
      });
    }
  };

  const validateFrontEnd = () => {
    const errors = {};

    if (!formData.productName.trim()) {
      errors.productName = "Product name is required";
    } else if (formData.productName.trim().length > 100) {
      errors.productName = "Product name must not exceed 100 characters";
    }
    
    const priceRaw = String(formData.productPrice).trim();
    const numRegex = /^\d+([.,]\d+)?$/;

    if (priceRaw === '') {
      errors.productPrice = "Product price is required";
    } else if (!numRegex.test(priceRaw)) {
      errors.productPrice = "Product price must be a valid number (e.g. 10 or 10.5)";
    } else {
      const price = Number(priceRaw.replace(',', '.'));
      if (price < 0) {
        errors.productPrice = "Product price must be greater than or equal to 0";
      } else {
        const parts = price.toString().split('.');
        if (parts[0].length > 10 || (parts[1] && parts[1].length > 2)) {
          errors.productPrice = "Product price must have up to 10 digits and 2 decimals";
        }
      }
    }

    const stockRaw = String(formData.productStock).trim();
    if (stockRaw === '') {
      errors.productStock = "Product stock is required";
    } else if (!numRegex.test(stockRaw)) {
      errors.productStock = "Product stock must be a valid number (e.g. 10 or 10.5)";
    } else {
      const stock = Number(stockRaw.replace(',', '.'));
      if (stock < 0) {
        errors.productStock = "Product stock must be greater than or equal to 0";
      } else {
        const parts = stock.toString().split('.');
        if (parts[0].length > 10 || (parts[1] && parts[1].length > 2)) {
          errors.productStock = "Product stock must have up to 10 digits and 2 decimals";
        } else if (formData.unitOfMeasure === 'UNITS' && !Number.isInteger(stock)) {
          errors.productStock = "Stock must be an integer value when unit of measure is Units";
        }
      }
    }

    if (!formData.unitOfMeasure) {
      errors.unitOfMeasure = "Unit of measure is required";
    }

    return errors;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
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
        productPrice: Number(String(formData.productPrice).replace(',', '.')),
        productStock: Number(String(formData.productStock).replace(',', '.')),
        unitOfMeasure: formData.unitOfMeasure
      };

      const res = await fetch(`/api/products/${productCode}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
        signal: controller.signal
      });

      clearTimeout(timeoutId);
      const text = await res.text();
      let data = {};
      try {
        data = JSON.parse(text);
      } catch (err) {
        data = { message: text };
      }

      if (!res.ok) {
        if (res.status === 400 && typeof data === 'object' && !data.error && !data.message) {
           setFormErrors(data);
        } else {
           const errMsg = data.error || data.message || text;
           if (errMsg === 'Product not found') {
             addToast(errMsg, 'error');
           } else {
             setGeneralError(errMsg);
           }
        }
        return;
      }

      addToast(data.message || data.error || text, "success");
      navigate(`/dashboard/products/${productCode}`);

    } catch (err) {
      clearTimeout(timeoutId);
      const msg = err.name === 'AbortError' ? 'Update request timed out' : (err.message || 'An error occurred');
      setGeneralError(msg);
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
        
        {generalError && (
          <div className="alert alert-error" style={{ margin: '0 32px 24px', textAlign: 'left' }}>
            {generalError}
          </div>
        )}

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
                type="number" 
                id="productPrice"
                name="productPrice"
                step="0.01"
                min="0"
                className={getInputClass('productPrice')}
                placeholder="0,00"
                value={formData.productPrice}
                onChange={handleChange}
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
                type="number" 
                id="productStock"
                name="productStock"
                step="0.01"
                min="0"
                className={getInputClass('productStock')}
                placeholder="0,00"
                value={formData.productStock}
                onChange={handleChange}
              />
              <p className={`error-text ${formErrors.productStock ? 'visible' : ''}`}>
                {formErrors.productStock || '\u00A0'}
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
            </button>
            <button 
              type="submit" 
              className="btn-primary form-submit-btn"
              disabled={submitting}
            >
              <Save size={18} />
              <span>{submitting ? 'Updating...' : 'Update Product'}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ProductEditView;
