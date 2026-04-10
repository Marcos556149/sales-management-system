import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Edit2, Ban, CheckCircle2, PackageCheck } from 'lucide-react';
import { PLACEHOLDER_PRODUCTS } from '../data/mockProducts';
import './ProductDetailView.css';

const ProductDetailView = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  
  // Find product by id from mock data
  const product = PLACEHOLDER_PRODUCTS.find(p => p.id === parseInt(id));

  if (!product) {
    return (
      <div className="view-container">
        <div className="not-found-card">
          <h2>Product Not Found</h2>
          <p>The product you are looking for does not exist or has been removed.</p>
          <button className="btn-secondary mt-4" onClick={() => navigate('/dashboard/products')}>
            <ArrowLeft size={16} />
            <span>Back to Products</span>
          </button>
        </div>
      </div>
    );
  }

  const handleBack = () => {
    navigate('/dashboard/products');
  };

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
          
          {product.status.code === 'ACTIVE' ? (
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
              <h2 className="detail-title">{product.name}</h2>
              <p className="detail-subtitle">Code: <span className="font-mono">{product.code}</span></p>
            </div>
          </div>
          <span className={`status-badge large-badge ${product.status.code.toLowerCase()}`}>
            {product.status.label}
          </span>
        </div>

        <div className="detail-body">
          <div className="info-grid">
            <div className="info-box">
              <h3 className="info-label">Price</h3>
              <p className="info-value price-value">${product.price.toFixed(2)}</p>
            </div>
            
            <div className="info-box">
              <h3 className="info-label">Available Stock</h3>
              <div className="stock-info">
                <p className={`info-value ${product.stock === 0 ? 'text-danger' : ''}`}>
                  {product.stock}
                </p>
                <span className="info-unit">{product.unit.code === 'UNITS' ? 'u' : 'kg'}</span>
              </div>
            </div>

            <div className="info-box">
              <h3 className="info-label">Unit of Measure</h3>
              <p className="info-value unit-only">{product.unit.label}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProductDetailView;
