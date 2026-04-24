import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { ArrowLeft, Edit2, Ban, CheckCircle2, PackageCheck } from 'lucide-react';
import { useKeyboardShortcuts } from '../hooks/useKeyboardShortcuts';
import ConfirmModal from './ConfirmModal';
import { useToast } from './ToastContext';
import { productService } from '../services/productService';
import { TEXTS } from '../constants/texts';
import './ProductDetailView.css';

// Custom hook to handle data fetching isolated from the component logic
const useProductDetail = (productCode, initialProduct = null) => {
  const [product, setProduct] = useState(initialProduct && initialProduct.productCode === productCode ? initialProduct : null);
  const [loading, setLoading] = useState(!product);
  const [error, setError] = useState(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const refetch = () => setRefreshTrigger(prev => prev + 1);
  const updateProduct = (newData) => setProduct(newData);

  useEffect(() => {
    let isMounted = true;
    
    // If we already have the product from navigation state, don't fetch on mount
    if (product && refreshTrigger === 0) {
      return;
    }

    const fetchProduct = async () => {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 8000);
      setLoading(true);
      setError(null);
      try {
        const response = await productService.getProduct(productCode, { signal: controller.signal });
        clearTimeout(timeoutId);
        
        if (isMounted) {
          setProduct(response.data);
        }
      } catch (err) {
        clearTimeout(timeoutId);
        if (isMounted) {
          const msg = err.name === 'AbortError' ? 'Product request timed out' : (err.message || TEXTS.common.errorOccurred);
          setError(msg);
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    };

    if (productCode) {
      fetchProduct();
    }

    return () => {
      isMounted = false;
    };
  }, [productCode, refreshTrigger]);

  return { product, loading, error, refetch, updateProduct };
};

const ProductDetailView = () => {
  const { id: productCode } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  
  const { product, loading, error, refetch, updateProduct } = useProductDetail(productCode, location.state?.product);
  const [actionLoading, setActionLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isActivateModalOpen, setIsActivateModalOpen] = useState(false);
  const { addToast } = useToast();
  
  // Register contextual shortcuts
  useKeyboardShortcuts(React.useMemo(() => ({
    'ctrl+b': () => handleBack(),
    'e': () => handleEdit(),
    'a': () => {
      if (product?.productStatus?.code === 'ACTIVE') {
        handleDeactivate();
      } else {
        handleActivate();
      }
    }
  }), [navigate, product, actionLoading]));

  const handleBack = () => {
    navigate('/dashboard/products');
  };

  const handleDeactivate = () => {
    setIsModalOpen(true);
  };

  const confirmDeactivate = async () => {
    setActionLoading(true);
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 8000);
    try {
      const res = await productService.deactivateProduct(productCode, { signal: controller.signal });
      clearTimeout(timeoutId);
      addToast(res.message || TEXTS.products.deactivateSuccess, 'success');
      updateProduct(res.data);
    } catch (err) {
      clearTimeout(timeoutId);
      const msg = err.name === 'AbortError' ? 'Deactivation request timed out' : (err.message || TEXTS.common.errorOccurred);
      addToast(msg, 'error');
    } finally {
      setActionLoading(false);
      setIsModalOpen(false);
    }
  };

  const handleActivate = () => {
    setIsActivateModalOpen(true);
  };

  const confirmActivate = async () => {
    setActionLoading(true);
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 8000);
    try {
      const res = await productService.activateProduct(productCode, { signal: controller.signal });
      clearTimeout(timeoutId);
      addToast(res.message || TEXTS.products.activateSuccess, 'success');
      updateProduct(res.data);
    } catch (err) {
      clearTimeout(timeoutId);
      const msg = err.name === 'AbortError' ? 'Activation request timed out' : (err.message || TEXTS.common.errorOccurred);
      addToast(msg, 'error');
    } finally {
      setActionLoading(false);
      setIsActivateModalOpen(false);
    }
  };

  const handleEdit = async () => {
    if (actionLoading) return;
    
    setActionLoading(true);
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 8000);
    try {
      // Check if product exists before navigating to edit
      const response = await productService.getProduct(productCode, { signal: controller.signal });
      clearTimeout(timeoutId);
      navigate(`/dashboard/products/edit/${productCode}`, { state: { product: response.data } });
    } catch (err) {
      clearTimeout(timeoutId);
      if (err.status === 400) {
        addToast(err.message || `Product with code ${productCode} not found`, 'error');
      } else {
        const msg = err.name === 'AbortError' ? 'Product check timed out' : (err.message || TEXTS.common.errorOccurred);
        addToast(msg, 'error');
      }
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="view-container">
        <div className="detail-toolbar">
          <button className="btn-secondary" onClick={handleBack}>
            <ArrowLeft size={16} />
            <span>Back to Products</span>
          </button>
        </div>
        <div className="detail-card" style={{ padding: '48px', textAlign: 'center', color: 'var(--text-secondary)' }}>
          <p>Loading product details...</p>
        </div>
      </div>
    );
  }

  if (error || !product) {
    return (
      <div className="view-container">
        <div className="detail-toolbar">
          <button className="btn-secondary" onClick={handleBack}>
            <ArrowLeft size={16} />
            <span>Back to Products</span>
          </button>
        </div>
        <div className="not-found-card">
          <h2>{error || "Product not found"}</h2>
        </div>
      </div>
    );
  }

  return (
    <div className="view-container">
      {/* Top action bar */}
      <div className="detail-toolbar">
        <button className="btn-secondary" onClick={handleBack}>
          <ArrowLeft size={16} />
          <span>Back to Products</span>
          <span className="btn-shortcut">Ctrl+B</span>
        </button>
        
        <div className="detail-actions">
          <button 
            className="btn-outline-primary whitespace-nowrap"
            disabled={actionLoading}
            onClick={handleEdit}
          >
            <Edit2 size={16} />
            <span>Edit</span>
            <span className="btn-shortcut">E</span>
          </button>
          
          {product.productStatus?.code === 'ACTIVE' ? (
            <button 
              className="btn-outline-danger whitespace-nowrap"
              disabled={actionLoading}
              onClick={handleDeactivate}
            >
              <Ban size={16} />
              <span>Deactivate</span>
              <span className="btn-shortcut">A</span>
            </button>
          ) : (
            <button 
              className="btn-outline-success whitespace-nowrap"
              disabled={actionLoading}
              onClick={handleActivate}
            >
              <CheckCircle2 size={16} />
              <span>Activate</span>
              <span className="btn-shortcut">A</span>
            </button>
          )}
        </div>
      </div>

      {/* Main Detail Card */}
      <div className="detail-card">
        <div className="detail-header">
          <div className="detail-title-group">
            <div className="product-icon-container">
              <PackageCheck size={32} className="product-main-icon" />
            </div>
            <div>
              <h2 className="detail-title">{product.productName}</h2>
              <p className="detail-subtitle">Code: <span className="font-mono">{product.productCode}</span></p>
            </div>
          </div>
          {/* dynamic status class using the status code */}
          <span className={`status-badge large-badge ${(product.productStatus?.code || '').toLowerCase()}`}>
            {product.productStatus?.label || 'Unknown'}
          </span>
        </div>

        <div className="detail-body">
          <div className="info-grid">
            <div className="info-box">
              <h3 className="info-label">Price</h3>
              <p className="info-value price-value" title={`$${product.productPrice?.toFixed(2) ?? '0.00'}`}>
                ${product.productPrice?.toFixed(2) ?? '0.00'}
              </p>
            </div>
            
            <div className="info-box">
              <h3 className="info-label">{TEXTS.products.stock}</h3>
              <div className="stock-info">
                {(() => {
                  const stock = product.productStock ?? 0;
                  const min = product.minimumStock ?? 0;
                  if (stock === 0) {
                    return <p className="info-value" title={TEXTS.products.outOfStock} style={{ color: 'var(--danger-color, #ef4444)' }}>{TEXTS.products.outOfStock}</p>;
                  }
                  if (stock <= min) {
                    return (
                      <p className="info-value" title={stock} style={{ color: 'var(--danger-color, #ef4444)' }}>
                        {stock}
                      </p>
                    );
                  }
                  return <p className="info-value" title={stock}>{stock}</p>;
                })()}
              </div>
            </div>

            <div className="info-box">
              <h3 className="info-label">Minimum Stock</h3>
              <p className="info-value" title={product.minimumStock ?? 0}>{product.minimumStock ?? 0}</p>
            </div>

            <div className="info-box">
              <h3 className="info-label">Unit of Measure</h3>
              <p className="info-value unit-only" title={product.unitOfMeasure?.label || 'Unknown'}>
                {product.unitOfMeasure?.label || 'Unknown'}
              </p>
            </div>
          </div>
        </div>
      </div>

      <ConfirmModal
        isOpen={isModalOpen}
        title="Confirm Deactivation"
        message="Are you sure you want to deactivate this product?"
        onConfirm={confirmDeactivate}
        onCancel={() => setIsModalOpen(false)}
        isConfirming={actionLoading}
      />

      <ConfirmModal
        isOpen={isActivateModalOpen}
        title="Confirm Activation"
        message="Are you sure you want to activate this product?"
        onConfirm={confirmActivate}
        onCancel={() => setIsActivateModalOpen(false)}
        isConfirming={actionLoading}
        confirmText="Activate"
        confirmButtonTheme="success"
      />
    </div>
  );
};

export default ProductDetailView;
