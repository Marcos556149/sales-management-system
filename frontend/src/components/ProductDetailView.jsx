import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Edit2, Ban, CheckCircle2, PackageCheck } from 'lucide-react';
import './ProductDetailView.css';

// Custom hook to handle data fetching isolated from the component logic
const useProductDetail = (productCode) => {
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let isMounted = true;

    const fetchProduct = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await fetch(`/api/products/${productCode}`);
        
        if (!response.ok) {
          const errorData = await response.json().catch(() => ({}));
          // Use backend error response if available, otherwise generic
          throw new Error(errorData.error || 'Failed to fetch product details.');
        }

        const data = await response.json();
        if (isMounted) {
          setProduct(data);
        }
      } catch (err) {
        if (isMounted) {
          setError(err.message);
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
  }, [productCode]);

  return { product, loading, error };
};

const ProductDetailView = () => {
  const { id: productCode } = useParams();
  const navigate = useNavigate();
  
  const { product, loading, error } = useProductDetail(productCode);

  const handleBack = () => {
    navigate('/dashboard/products');
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
          <h2>Error Loading Product</h2>
          <p>{error || "The product you are looking for does not exist."}</p>
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
          <button className="btn-outline-primary whitespace-nowrap">
            <Edit2 size={16} />
            <span>Edit</span>
          </button>
          
          {product.productStatus?.code === 'ACTIVE' ? (
            <button className="btn-outline-danger whitespace-nowrap">
              <Ban size={16} />
              <span>Deactivate</span>
            </button>
          ) : (
            <button className="btn-outline-success whitespace-nowrap">
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
    </div>
  );
};

export default ProductDetailView;
