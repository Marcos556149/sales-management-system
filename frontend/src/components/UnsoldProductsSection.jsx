import React, { useState, useEffect, useRef } from 'react';
import { 
  Package, 
  Loader2, 
  AlertCircle,
  ClipboardList
} from 'lucide-react';
import { apiClient } from '../api/client';
import Pagination from './Pagination';
import './UnsoldProductsSection.css';
import { useToast } from './ToastContext';

const UnsoldProductsSection = ({ userId, startDate, endDate }) => {
  const { addToast } = useToast();
  
  // Filters & pagination state
  const [page, setPage] = useState(1); // Frontend page: 1-indexed
  const size = 20;

  // Data & loading state
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const abortControllerRef = useRef(null);

  // Helper: Format Date to YYYY-MM-DD
  const formatDateStr = (date) => {
    if (!date) return '';
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const d = String(date.getDate()).padStart(2, '0');
    return `${y}-${m}-${d}`;
  };

  // Fetch Unsold Products when filters, page, or props change
  useEffect(() => {
    // Prevent fetching if dates are not defined yet
    if (!startDate || !endDate) return;

    const fetchUnsoldProducts = async () => {
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }

      const controller = new AbortController();
      abortControllerRef.current = controller;
      
      setLoading(true);
      setError(null);

      try {
        const params = {
          startDate: formatDateStr(startDate),
          endDate: formatDateStr(endDate),
          page: page - 1, // backend is 0-based
          size
        };

        if (userId && userId !== 'ALL') {
          params.userId = userId;
        }

        const response = await apiClient.get('/api/statistics/products/unsold', {
          params,
          signal: controller.signal
        });

        if (controller === abortControllerRef.current) {
          setData(response.data);
        }
      } catch (err) {
        if (err.name === 'AbortError') return;
        
        console.error("Error fetching unsold products:", err);
        if (controller === abortControllerRef.current) {
          setError(err.message || "Failed to load unsold products");
          addToast(err.message || "Error fetching unsold products", "error");
        }
      } finally {
        if (controller === abortControllerRef.current) {
          setLoading(false);
        }
      }
    };

    fetchUnsoldProducts();

    return () => {
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }
    };
  }, [userId, startDate, endDate, page, addToast]);

  // Reset page when filters change
  useEffect(() => {
    setPage(1);
  }, [userId, startDate, endDate]);

  // Render Skeletons for Loading State
  const renderSkeletons = () => {
    return (
      <div className="unsold-table-wrapper">
        <table className="unsold-table skeleton">
          <thead>
            <tr>
              <th>Product Code</th>
              <th>Product Name</th>
            </tr>
          </thead>
          <tbody>
            {[...Array(5)].map((_, idx) => (
              <tr key={idx} className="skeleton-row">
                <td><div className="skeleton-bar short" /></td>
                <td><div className="skeleton-bar medium" /></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    );
  };

  const hasData = data && data.content && data.content.length > 0;

  return (
    <div className="unsold-products-card">
      <header className="unsold-header">
        <div className="unsold-title">
          <div className="title-icon-wrapper unsold">
            <ClipboardList size={20} />
          </div>
          <div>
            <h2>Unsold Products</h2>
            <p>Paginatable list of products that had no sales within the selected filters</p>
          </div>
        </div>
      </header>

      {/* Content Area */}
      <div className="unsold-content">
        {loading ? (
          renderSkeletons()
        ) : error ? (
          <div className="unsold-error-state">
            <AlertCircle size={40} className="error-icon" />
            <h3>Error Loading Data</h3>
            <p>{error}</p>
          </div>
        ) : !hasData ? (
          <div className="unsold-empty-state">
            <Package size={44} className="empty-icon" />
            <h3>No unsold products for the selected filters</h3>
          </div>
        ) : (
          <>
            <div className="unsold-table-wrapper">
              <table className="unsold-table">
                <thead>
                  <tr>
                    <th>Product Code</th>
                    <th>Product Name</th>
                  </tr>
                </thead>
                <tbody>
                  {data.content.map((item) => (
                    <tr key={item.productCode} className="unsold-row">
                      <td className="font-mono text-sm code-cell">{item.productCode}</td>
                      <td className="font-medium name-cell">{item.productName}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Pagination Footer */}
            <div className="unsold-pagination-wrapper">
              <Pagination 
                currentPage={page}
                totalPages={data.totalPages || 1}
                totalElements={data.totalElements || 0}
                onPageChange={setPage}
                itemName="products"
                showShortcuts={false}
              />
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default UnsoldProductsSection;
