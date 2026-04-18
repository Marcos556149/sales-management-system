import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Edit2, Printer, Receipt, Trash2 } from 'lucide-react';
import { useToast } from './ToastContext';
import './ProductDetailView.css'; // Inheriting structural SaaS standard like .view-container, .detail-toolbar, .detail-card
import './SaleDetailView.css';

// --- Custom hook to handle data fetching ---
const useSaleDetail = (saleId) => {
  const [sale, setSale] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const refetch = () => setRefreshTrigger(prev => prev + 1);

  useEffect(() => {
    let isMounted = true;

    const fetchSale = async () => {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 8000);
      setLoading(true);
      setError(null);
      try {
        const response = await fetch(`/api/sales/${saleId}`, { signal: controller.signal });
        clearTimeout(timeoutId);
        
        if (!response.ok) {
          const errorData = await response.json().catch(() => ({}));
          
          let errorMessage = errorData.error || errorData.message;
          if (!errorMessage && Object.keys(errorData).length > 0) {
            // Mapping MethodArgumentTypeMismatchException -> usually { "saleId": "Incorrect value: abc" }
            const firstKey = Object.keys(errorData)[0];
            if (typeof errorData[firstKey] === 'string') {
              errorMessage = errorData[firstKey];
            }
          }

          // In Sales matching your rule, 400 with "Sale not found" might be returned by SaleNotFoundException mapped into SaleException.
          // Or 404. In either case, if the message includes "not found" we can normalize it exactly.
          if ((response.status === 400 || response.status === 404) && 
             (errorMessage && errorMessage.toLowerCase().includes('not found'))) {
              throw new Error("Sale not found");
          }
          
          throw new Error(errorMessage || `Error loading sale: ${response.status}`);
        }

        const data = await response.json();
        if (isMounted) {
          setSale(data);
        }
      } catch (err) {
        clearTimeout(timeoutId);
        if (isMounted) {
          const msg = err.name === 'AbortError' ? 'Sale request timed out' : (err.message || 'Error loading sale');
          setError(msg);
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    };

    if (saleId) {
      fetchSale();
    }

    return () => {
      isMounted = false;
    };
  }, [saleId, refreshTrigger]);

  return { sale, loading, error, refetch };
};

// --- Main View Component ---
const SaleDetailView = () => {
  const { id: saleId } = useParams();
  const navigate = useNavigate();
  const { addToast } = useToast();
  
  const { sale, loading, error } = useSaleDetail(saleId);
  const [actionLoading, setActionLoading] = useState(false);

  const handleBack = () => {
    navigate('/dashboard/sales');
  };

  const formatDateTimeFull = (dateStr, timeStr) => {
    if (!dateStr || !timeStr) return '';
    try {
      const [year, month, day] = dateStr.split('-');
      const dateObj = new Date(year, month - 1, day);
      
      const dateOpts = { year: 'numeric', month: 'long', day: 'numeric' };
      const formattedDate = dateObj.toLocaleDateString('en-US', dateOpts);
      
      const [hours, minutes] = timeStr.split(':');
      return `${formattedDate} • ${hours}:${minutes}`;
    } catch(e) {
      return `${dateStr} at ${timeStr}`;
    }
  };

  if (loading) {
    return (
      <div className="view-container">
        <div className="detail-toolbar">
          <button className="btn-secondary" onClick={handleBack}>
            <ArrowLeft size={16} />
            <span>Back to Sales</span>
          </button>
        </div>
        <div className="detail-card" style={{ padding: '48px', textAlign: 'center', color: 'var(--text-secondary)' }}>
          <p>Loading sale details...</p>
        </div>
      </div>
    );
  }

  // 400/404 mappings correctly show the backend normalized message "Sale not found" 
  if (error || !sale) {
    return (
      <div className="view-container">
        <div className="detail-toolbar">
          <button className="btn-secondary" onClick={handleBack}>
            <ArrowLeft size={16} />
            <span>Back to Sales</span>
          </button>
        </div>
        <div className="not-found-card">
          <h2>{error || "Sale not found"}</h2>
        </div>
      </div>
    );
  }

  // Extracted total values
  const totalAmount = sale.totalAmount?.toFixed(2) ?? '0.00';

  return (
    <div className="view-container">
      {/* Top action bar */}
      <div className="detail-toolbar">
        <button className="btn-secondary" onClick={handleBack}>
          <ArrowLeft size={16} />
          <span>Back to Sales</span>
        </button>
        
        <div className="detail-actions">
          <button 
            className="btn-outline-primary whitespace-nowrap"
            disabled={actionLoading}
            onClick={() => addToast("Edit sale coming soon", "info")}
          >
            <Edit2 size={16} />
            <span>Edit Sale</span>
          </button>
          
          <button 
            className="btn-outline-success whitespace-nowrap"
            disabled={actionLoading}
            onClick={() => addToast("Printing coming soon", "info")}
          >
            <Printer size={16} />
            <span>Print Ticket</span>
          </button>
          
          <button 
            className="btn-outline-danger whitespace-nowrap"
            disabled={actionLoading}
            onClick={() => addToast("Delete sale coming soon", "error")}
          >
            <Trash2 size={16} />
            <span>Delete Sale</span>
          </button>
        </div>
      </div>

      {/* Main SaaS Details Card */}
      <div className="detail-card">
        
        {/* HEADER: Resumen de Venta */}
        <div className="sale-detail-header-card">
          <div className="sale-info-group">
            <Receipt size={36} className="sale-main-icon" strokeWidth={1.5} />
            <div className="sale-header-text">
              <h2 className="sale-title">Sale #{sale.saleId}</h2>
              <div className="sale-meta-row">
                <span className="sale-meta-item seller-badge">Seller: {sale.userName || 'Unknown'}</span>
                <span className="sale-meta-dot">•</span>
                <span className="sale-meta-item date-badge">{formatDateTimeFull(sale.saleDate, sale.saleTime)}</span>
              </div>
            </div>
          </div>
        </div>

        {/* DETAILS: Productos Vendidos */}
        <div className="sale-items-section">
          <div className="sale-items-header">
            <h3 className="sale-items-title">Item Details</h3>
          </div>
          
          {sale.saleDetails && sale.saleDetails.length > 0 ? (
            <div className="table-responsive">
              <table className="items-table">
                <thead>
                  <tr>
                    <th>Code</th>
                    <th>Product</th>
                    <th>Quantity</th>
                    <th>Unit Price</th>
                    <th>Subtotal</th>
                  </tr>
                </thead>
                <tbody>
                  {sale.saleDetails.map((item, index) => (
                    <tr key={`${item.productCode}-${index}`} className="interactive-row">
                      <td className="font-mono text-sm">{item.productCode}</td>
                      <td className="font-medium">{item.productName}</td>
                      <td className="stock-cell">
                        {item.productQuantity} <span className="unit-text">{item.unitOfMeasure?.label || ''}</span>
                      </td>
                      <td>${item.salePrice?.toFixed(2) ?? '0.00'}</td>
                      <td className="subtotal-cell">${item.subtotal?.toFixed(2) ?? '0.00'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
             <div className="empty-state">
              <p>No items found for this sale</p>
            </div>
          )}
        </div>

        {/* SUMMARY FOOTER */}
        <div className="sale-summary-footer">
          <div className="summary-total-only">
            <span className="summary-total-label">Sale Total</span>
            <span className="summary-total-value">${totalAmount}</span>
          </div>
        </div>
        
      </div>
    </div>
  );
};

export default SaleDetailView;
