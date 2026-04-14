import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Edit2, Ban, CheckCircle2, PackageCheck } from 'lucide-react';
import ConfirmModal from './ConfirmModal';
import { useToast } from './ToastContext';
import './ProductDetailView.css';

// Custom hook to handle data fetching isolated from the component logic
const useProductDetail = (productCode) => {
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const refetch = () => setRefreshTrigger(prev => prev + 1);

  useEffect(() => {
    let isMounted = true;

    const fetchProduct = async () => {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 8000);
      setLoading(true);
      setError(null);
      try {
        const response = await fetch(`/api/products/${productCode}`, { signal: controller.signal });
        clearTimeout(timeoutId);
        
        if (!response.ok) {
          const errorData = await response.json().catch(() => ({}));
          const errMsg = errorData.error || errorData.message || 'Failed to fetch product details.';
          throw new Error(errMsg);
        }

        const data = await response.json();
        if (isMounted) {
          setProduct(data);
        }
      } catch (err) {
        clearTimeout(timeoutId);
        if (isMounted) {
          const msg = err.name === 'AbortError' ? 'Product request timed out' : (err.message || 'Error loading product');
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

  return { product, loading, error, refetch };
};

const ProductDetailView = () => {
  const { id: productCode } = useParams();
  const navigate = useNavigate();
  
  const { product, loading, error, refetch } = useProductDetail(productCode);
  const [actionLoading, setActionLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isActivateModalOpen, setIsActivateModalOpen] = useState(false);
  const { addToast } = useToast();

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
      const response = await fetch(`/api/products/${productCode}/deactivate`, {
        method: 'PATCH',
        signal: controller.signal
      });

      clearTimeout(timeoutId);
      let message = '';
      try {
        const text = await response.text();
        try {
          const json = JSON.parse(text);
          message = json.error || json.message || text;
        } catch (e) {
          message = text;
        }
      } catch (e) {
        message = "An error occurred";
      }

      if (!response.ok) {
        addToast(message || "An error occurred while deactivating", 'error');
      } else {
        addToast(message, 'success');
        refetch();
      }
    } catch (err) {
      clearTimeout(timeoutId);
      const msg = err.name === 'AbortError' ? 'Deactivation request timed out' : (err.message || "An error occurred");
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
      const response = await fetch(`/api/products/${productCode}/activate`, {
        method: 'PATCH',
        signal: controller.signal
      });

      clearTimeout(timeoutId);
      let message = '';
      try {
        const text = await response.text();
        try {
          const json = JSON.parse(text);
          message = json.error || json.message || text;
        } catch (e) {
          message = text;
        }
      } catch (e) {
        message = "An error occurred";
      }

      if (!response.ok) {
        addToast(message || "An error occurred while activating", 'error');
      } else {
        addToast(message, 'success');
        refetch();
      }
    } catch (err) {
      clearTimeout(timeoutId);
      const msg = err.name === 'AbortError' ? 'Activation request timed out' : (err.message || "An error occurred");
      addToast(msg, 'error');
    } finally {
      setActionLoading(false);
      setIsActivateModalOpen(false);
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
        </button>
        
        <div className="detail-actions">
          <button 
            className="btn-outline-primary whitespace-nowrap"
            onClick={() => navigate(`/dashboard/products/edit/${productCode}`)}
          >
            <Edit2 size={16} />
            <span>Edit</span>
          </button>
          
          {product.productStatus?.code === 'ACTIVE' ? (
            <button 
              className="btn-outline-danger whitespace-nowrap"
              disabled={actionLoading}
              onClick={handleDeactivate}
            >
              <Ban size={16} />
              <span>Deactivate</span>
            </button>
          ) : (
            <button 
              className="btn-outline-success whitespace-nowrap"
              disabled={actionLoading}
              onClick={handleActivate}
            >
              <CheckCircle2 size={16} />
              <span>Activate</span>
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
              <p className="info-value price-value">${product.productPrice?.toFixed(2) ?? '0.00'}</p>
            </div>
            
            <div className="info-box">
              <h3 className="info-label">Available Stock</h3>
              <div className="stock-info">
                <p className={`info-value ${product.productStock === 0 ? 'text-danger' : ''}`}>
                  {product.productStock ?? '0'}
                </p>
              </div>
            </div>

            <div className="info-box">
              <h3 className="info-label">Unit of Measure</h3>
              <p className="info-value unit-only">{product.unitOfMeasure?.label || 'Unknown'}</p>
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
