import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Plus, Filter, Edit2, Ban, CheckCircle2, X } from 'lucide-react';
import Pagination from './Pagination';
import ConfirmModal from './ConfirmModal';
import { useToast } from './ToastContext';
import './ProductsView.css';

const ProductsView = () => {
  const navigate = useNavigate();
  const { addToast } = useToast();

  // --- UI State ---
  const [searchTerm, setSearchTerm] = useState('');
  
  // --- API Query State ---
  const [appliedSearch, setAppliedSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [sortOrder, setSortOrder] = useState('ASCENDING');

  // --- Filter Options State ---
  const [statusOptions, setStatusOptions] = useState([]);
  const [sortOptions, setSortOptions] = useState([]);
  const [filtersLoading, setFiltersLoading] = useState(true);

  // --- Data State ---
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);
  const [actionLoading, setActionLoading] = useState(false);
  
  // --- Modal State ---
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [productToDeactivate, setProductToDeactivate] = useState(null);
  
  // --- Pagination State ---
  const [pageFrontend, setPageFrontend] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  const abortControllerRef = useRef(null);

  // Handle row click
  const handleRowClick = (id) => {
    navigate(`/dashboard/products/${id}`);
  };

  // 0. Fetch Filter Options on Mount
  useEffect(() => {
    const fetchFilters = async () => {
      try {
        const response = await fetch('/api/products/filters');
        if (!response.ok) throw new Error('Failed to fetch filters');
        const data = await response.json();
        
        setStatusOptions(data.statusOptions || []);
        setSortOptions(data.sortOptions || []);
      } catch (err) {
        console.error("Error loading filters:", err);
        setStatusOptions([]);
        setSortOptions([]);
      } finally {
        setFiltersLoading(false);
      }
    };
    fetchFilters();
  }, []);

  // 1. Debounce for search input
  // Updates `appliedSearch` when the user stops typing for 400ms
  useEffect(() => {
    // If it's only spaces (but not empty string), ignore it
    if (searchTerm.length > 0 && searchTerm.trim().length === 0) {
      return;
    }

    const handler = setTimeout(() => {
      setAppliedSearch((prev) => {
        if (prev !== searchTerm) {
          setPageFrontend(1);
          return searchTerm;
        }
        return prev;
      });
    }, 400);

    // Cleanup the timeout if the user keeps typing
    return () => {
      clearTimeout(handler);
    };
  }, [searchTerm]);

  // 2. Fetch data when filters/search/page changes
  useEffect(() => {
    const fetchProducts = async () => {
      // Cancel previous request if it exists to avoid race conditions
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }

      const abortController = new AbortController();
      abortControllerRef.current = abortController;

      setLoading(true);
      setError(null);

      try {
        const params = new URLSearchParams();
        
        // Only append search if it's not empty or just spaces
        const trimmedSearch = appliedSearch.trim();
        if (trimmedSearch.length > 0) {
          params.append('searchCodeOrName', trimmedSearch);
        }

        params.append('statusFilter', statusFilter);
        params.append('nameSort', sortOrder);
        // Add pagination params (page is 0-based for backend)
        params.append('page', pageFrontend - 1);
        params.append('size', 50);

        const response = await fetch(`/api/products?${params.toString()}`, {
          signal: abortController.signal
        });

        if (!response.ok) {
          throw new Error(`Error: ${response.status} ${response.statusText}`);
        }

        const data = await response.json();
        
        // Prevent setting state if this request was aborted or a new one started
        if (abortControllerRef.current === abortController) {
          if (data.content !== undefined) {
            setProducts(data.content);
            setTotalPages(data.totalPages || 1);
            setTotalElements(data.totalElements || 0);
          } else {
            // Fallback just in case
            setProducts(Array.isArray(data) ? data : []);
            setTotalPages(1);
            setTotalElements(0);
          }
        }
      } catch (err) {
        if (err.name === 'AbortError') {
          // It's normal, request was cancelled
          console.log('Previous request cancelled');
        } else if (abortControllerRef.current === abortController) {
          console.error("Failed to fetch products:", err);
          setError('Failed to load products. Please try again later.');
        }
      } finally {
        if (abortControllerRef.current === abortController) {
          setLoading(false);
        }
      }
    };

    fetchProducts();
    
    // Cleanup on unmount
    return () => {
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }
    };
  }, [appliedSearch, statusFilter, sortOrder, pageFrontend, refreshTrigger]);

  // Handle immediate search on Enter key
  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      if (searchTerm.length > 0 && searchTerm.trim().length === 0) {
        return;
      }
      if (appliedSearch !== searchTerm) {
        setPageFrontend(1);
        setAppliedSearch(searchTerm);
      }
    }
  };

  // Clear search input and filters
  const handleClearSearch = () => {
    setSearchTerm('');
    setPageFrontend(1);
    setAppliedSearch('');
  };

  const handleDeactivateProduct = (productCode) => {
    setProductToDeactivate(productCode);
    setIsModalOpen(true);
  };

  const confirmDeactivateProduct = async () => {
    if (!productToDeactivate) return;

    setActionLoading(true);
    try {
      const response = await fetch(`/api/products/${productToDeactivate}/deactivate`, {
        method: 'PATCH',
      });

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
        addToast(message || "Product successfully deactivated", 'success');
      }
    } catch (err) {
      addToast(err.message || "An error occurred", 'error');
    } finally {
      setPageFrontend(1);
      setRefreshTrigger(prev => prev + 1);
      setActionLoading(false);
      setIsModalOpen(false);
      setProductToDeactivate(null);
    }
  };

  // Check if we have active filters besides the defaults
  const hasActiveFilters = appliedSearch.trim().length > 0 || statusFilter !== 'ALL' || sortOrder !== 'ASCENDING';

  return (
    <div className="view-container">
      {/* Top action bar */}
      <div className="view-toolbar">
        <div className="toolbar-left">
          <div className="search-bar">
            <Search className="search-icon" size={18} />
            <input 
              type="text" 
              placeholder="Search by name or code..." 
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onKeyDown={handleKeyDown}
            />
            {searchTerm && (
              <button 
                className="clear-search-btn" 
                onClick={handleClearSearch}
                title="Clear Search"
              >
                <X size={16} />
              </button>
            )}
          </div>
          
          <div className="filter-group">
            {/* Status Filter */}
            <div className="filter-item">
              <Filter size={16} className="filter-icon" />
              <select 
                value={statusFilter} 
                onChange={(e) => {
                  setStatusFilter(e.target.value);
                  setPageFrontend(1);
                }}
                className="select-dropdown"
                disabled={filtersLoading || statusOptions.length === 0}
              >
                {filtersLoading ? (
                  <option value="ALL">Loading...</option>
                ) : statusOptions.length === 0 ? (
                  <option value="ALL">All Status</option>
                ) : (
                  statusOptions.map(opt => (
                    <option key={opt.code} value={opt.code}>
                      {opt.label}
                    </option>
                  ))
                )}
              </select>
            </div>
            
            {/* Sort Order Control */}
            <div className="filter-item" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <span style={{ fontSize: '0.9rem', color: '#6b7280', fontWeight: '500' }}>Order by Name:</span>
              <select 
                value={sortOrder} 
                onChange={(e) => {
                  setSortOrder(e.target.value);
                  setPageFrontend(1);
                }}
                className="select-dropdown sort-dropdown"
                disabled={filtersLoading || sortOptions.length === 0}
              >
                {filtersLoading ? (
                  <option value="ASCENDING">Loading...</option>
                ) : sortOptions.length === 0 ? (
                  <option value="ASCENDING">Ascending</option>
                ) : (
                  sortOptions.map(opt => (
                    <option key={opt.code} value={opt.code}>
                      {opt.label}
                    </option>
                  ))
                )}
              </select>
            </div>
          </div>
        </div>

        <div className="toolbar-right">
          <button className="btn-primary">
            <Plus size={18} />
            <span>New Product</span>
          </button>
        </div>
      </div>

      {/* Content Area - Data Table */}
      <div className="table-card">
        {loading ? (
          <div className="loading-state">
             <p>Loading products...</p>
          </div>
        ) : error ? (
          <div className="error-state">
            <p>{error}</p>
          </div>
        ) : products.length === 0 ? (
          <div className="empty-state">
            {hasActiveFilters 
              ? <p>No products match the search criteria</p>
              : <p>No products available</p>
            }
          </div>
        ) : (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Code</th>
                  <th>Name</th>
                  <th>Price</th>
                  <th>Stock</th>
                  <th>Status</th>
                  <th className="text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {products.map(product => (
                  <tr 
                    key={product.productCode} 
                    onClick={() => handleRowClick(product.productCode)}
                    style={{ cursor: 'pointer' }}
                    className="interactive-row"
                  >
                    <td className="font-mono text-sm">{product.productCode}</td>
                    <td className="font-medium">{product.productName}</td>
                    <td>${product.productPrice?.toFixed(2) ?? '0.00'}</td>
                    <td className="stock-cell">
                      {product.productStock ?? '0'} <span className="unit-text">{product.unitOfMeasure?.code === 'UNITS' ? 'u' : (product.unitOfMeasure?.label || '')}</span>
                    </td>
                    <td>
                      <span className={`status-badge ${(product.productStatus?.code || '').toLowerCase()}`}>
                        {product.productStatus?.label || 'Unknown'}
                      </span>
                    </td>
                    <td className="actions-cell text-right" onClick={(e) => e.stopPropagation()}>
                      <button className="action-btn edit-btn" title="Edit Product">
                        <Edit2 size={16} />
                      </button>
                      {product.productStatus?.code === 'ACTIVE' ? (
                        <button 
                          className="action-btn deactivate-btn" 
                          title="Deactivate Product"
                          disabled={actionLoading}
                          onClick={(e) => {
                            e.stopPropagation();
                            handleDeactivateProduct(product.productCode);
                          }}
                        >
                          <Ban size={16} />
                        </button>
                      ) : (
                        <button 
                          className="action-btn activate-btn" 
                          title="Activate Product"
                          disabled={actionLoading}
                          onClick={(e) => e.stopPropagation()}
                        >
                          <CheckCircle2 size={16} />
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
        
        {/* Pagination Bar */}
        {!loading && products.length > 0 && (
          <Pagination 
            currentPage={pageFrontend}
            totalPages={totalPages}
            totalElements={totalElements}
            onPageChange={setPageFrontend}
            itemName="products"
          />
        )}
      </div>

      <ConfirmModal
        isOpen={isModalOpen}
        title="Confirm Deactivation"
        message="Are you sure you want to deactivate this product?"
        onConfirm={confirmDeactivateProduct}
        onCancel={() => {
          setIsModalOpen(false);
          setProductToDeactivate(null);
        }}
        isConfirming={actionLoading}
      />
    </div>
  );
};

export default ProductsView;
