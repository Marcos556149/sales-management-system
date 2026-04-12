import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useToast } from './ToastContext';
import { ArrowLeft, Save, Box } from 'lucide-react';
import './ProductCreateView.css';

const ProductCreateView = () => {
  const navigate = useNavigate();
  const { addToast } = useToast();

  const [formData, setFormData] = useState({
    productCode: '',
    productName: '',
    productPrice: '',
    productStock: '',
    unitOfMeasure: 'UNITS',
  });

  const [unitOfMeasureOptions, setUnitOfMeasureOptions] = useState([]);
  const [loadingOpts, setLoadingOpts] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [formErrors, setFormErrors] = useState({});

  useEffect(() => {
    let isMounted = true;
    const fetchMetadata = async () => {
      try {
        const res = await fetch('/api/products/metadata');
        if (!res.ok) throw new Error('Failed to load metadata');
        const data = await res.json();
        if (isMounted) {
          setUnitOfMeasureOptions(data.unitOfMeasureOptions || []);
        }
      } catch (err) {
        if (isMounted) {
          addToast('Could not load unit options', 'error');
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

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    // Clear the error for this field when user starts typing
    if (formErrors[name]) {
      setFormErrors(prev => ({ ...prev, [name]: undefined }));
    }
  };

  const validateFrontEnd = () => {
    const errors = {};
    if (!formData.productCode.trim()) {
      errors.productCode = "Product code is required";
    } else if (formData.productCode.trim().length > 100) {
      errors.productCode = "Product code must not exceed 100 characters";
    }

    if (!formData.productName.trim()) {
      errors.productName = "Product name is required";
    } else if (formData.productName.trim().length > 100) {
      errors.productName = "Product name must not exceed 100 characters";
    }
    
    const priceStr = String(formData.productPrice).replace(',', '.');
    if (String(formData.productPrice).trim() === '') {
      errors.productPrice = "Product price is required";
    } else {
      const price = Number(priceStr);
      if (isNaN(price)) {
        errors.productPrice = "Product price must be a valid number";
      } else if (price < 0) {
        errors.productPrice = "Product price must be greater than or equal to 0";
      } else {
        const parts = price.toString().split('.');
        if (parts[0].length > 10 || (parts[1] && parts[1].length > 2)) {
          errors.productPrice = "Product price must have up to 10 digits and 2 decimals";
        }
      }
    }

    const stockStr = String(formData.productStock).replace(',', '.');
    if (String(formData.productStock).trim() === '') {
      errors.productStock = "Product stock is required";
    } else {
      const stock = Number(stockStr);
      if (isNaN(stock)) {
        errors.productStock = "Product stock must be a valid number";
      } else if (stock < 0) {
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

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateFrontEnd()) return;

    setSubmitting(true);
    setFormErrors({});

    try {
      const payload = {
        productCode: formData.productCode.trim(),
        productName: formData.productName.trim(),
        productPrice: Number(String(formData.productPrice).replace(',', '.')),
        productStock: Number(String(formData.productStock).replace(',', '.')),
        unitOfMeasure: formData.unitOfMeasure
      };

      const res = await fetch('/api/products', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      const text = await res.text();
      let data = {};
      try {
        data = JSON.parse(text);
      } catch (err) {
        data = { message: text }; // Text might be empty string or the success string
      }

      if (!res.ok) {
        // If it's a validation error 400 with a map of fields
        if (res.status === 400 && typeof data === 'object' && !data.error && !data.message) {
           setFormErrors(data);
           addToast("Please check the form for errors", "error");
        } else {
           // Business rule error (Product is already registered, etc.)
           addToast(data.error || data.message || text, "error");
        }
        return;
      }

      // Success
      addToast(data.message || data.error || text, "success");
      navigate('/dashboard/products');

    } catch (err) {
      addToast(err.message || 'An error occurred while creating the product', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const getInputClass = (fieldName) => {
    return formErrors[fieldName] ? 'form-input error' : 'form-input';
  };

  return (
    <div className="view-container">
      <div className="detail-toolbar">
        <button type="button" className="btn-secondary" onClick={() => navigate('/dashboard/products')}>
          <ArrowLeft size={16} />
          <span>Back to Products</span>
        </button>
      </div>

      <div className="form-card">
        <div className="form-header">
          <div className="form-icon-container">
            <Box size={28} className="form-main-icon" />
          </div>
          <div>
            <h2 className="form-title">Register New Product</h2>
            <p className="form-subtitle">Enter the details below to create a new product in the system.</p>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="product-form" noValidate>
          <div className="form-grid">
            <div className="form-group">
              <label htmlFor="productCode">Product Code <span className="required">*</span></label>
              <input 
                type="text" 
                id="productCode" 
                name="productCode" 
                className={getInputClass('productCode')}
                value={formData.productCode} 
                onChange={handleChange}
                disabled={submitting}
                placeholder="Ex. P-1004"
              />
              {formErrors.productCode && <p className="error-text">{formErrors.productCode}</p>}
            </div>

            <div className="form-group">
              <label htmlFor="productName">Product Name <span className="required">*</span></label>
              <input 
                type="text" 
                id="productName" 
                name="productName" 
                className={getInputClass('productName')}
                value={formData.productName} 
                onChange={handleChange}
                disabled={submitting}
                placeholder="Ex. Coca Cola 2.25L"
              />
              {formErrors.productName && <p className="error-text">{formErrors.productName}</p>}
            </div>

            <div className="form-group">
              <label htmlFor="productPrice">Price ($) <span className="required">*</span></label>
              <input 
                type="number" 
                id="productPrice" 
                name="productPrice" 
                step="0.01" 
                min="0"
                className={getInputClass('productPrice')}
                value={formData.productPrice} 
                onChange={handleChange}
                disabled={submitting}
                placeholder="0.00"
              />
              {formErrors.productPrice && <p className="error-text">{formErrors.productPrice}</p>}
            </div>

            <div className="form-group">
              <label htmlFor="unitOfMeasure">Unit of Measure <span className="required">*</span></label>
              <select 
                id="unitOfMeasure" 
                name="unitOfMeasure" 
                className={getInputClass('unitOfMeasure')}
                value={formData.unitOfMeasure} 
                onChange={handleChange}
                disabled={loadingOpts || submitting}
              >
                {loadingOpts ? <option value="UNITS">Loading...</option> : null}
                {unitOfMeasureOptions.map(opt => (
                  <option key={opt.code} value={opt.code}>{opt.label}</option>
                ))}
              </select>
              {formErrors.unitOfMeasure && <p className="error-text">{formErrors.unitOfMeasure}</p>}
            </div>

            <div className="form-group">
              <label htmlFor="productStock">Initial Stock <span className="required">*</span></label>
              <input 
                type="number" 
                id="productStock" 
                name="productStock" 
                step={formData.unitOfMeasure === 'UNITS' ? "1" : "0.01"}
                min="0"
                className={getInputClass('productStock')}
                value={formData.productStock} 
                onChange={handleChange}
                disabled={submitting}
                placeholder="0"
              />
              {formErrors.productStock && <p className="error-text">{formErrors.productStock}</p>}
            </div>
          </div>

          <div className="form-actions">
            <button 
              type="button" 
              className="btn-secondary" 
              onClick={() => navigate('/dashboard/products')}
              disabled={submitting}
            >
              Cancel
            </button>
            <button 
              type="submit" 
              className="btn-primary form-submit-btn" 
              disabled={submitting}
            >
              <Save size={18} />
              <span>{submitting ? 'Registering...' : 'Register Product'}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
export default ProductCreateView;
