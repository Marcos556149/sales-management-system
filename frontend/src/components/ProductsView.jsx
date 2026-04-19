import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Plus, Filter, Edit2, Ban, CheckCircle2, X, RefreshCw } from 'lucide-react';
import Pagination from './Pagination';
import ConfirmModal from './ConfirmModal';
import { useToast } from './ToastContext';
import { useProductsContext } from './ProductsContext';
import './ProductsView.css';

const ProductsView = () => {
  const navigate = useNavigate();
  const { addToast } = useToast();

  // --- Context State ---
  const {
      searchTerm, setSearchTerm,
      appliedSearch, setAppliedSearch,
      statusFilter, setStatusFilter,
      sortOrder, setSortOrder,
      pageFrontend, setPageFrontend,
      productsData: products, setProductsData: setProducts,
      totalPages, setTotalPages,
      totalElements, setTotalElements,
      statusOptions, setStatusOptions,
      sortOptions, setSortOptions,
      scrollPositionRef,
      isCached, setIsCached
  } = useProductsContext();

  const [filtersLoading, setFiltersLoading] = useState(statusOptions.length === 0);

  // --- Data State ---
  const [loading, setLoading] = useState(!isCached);
  const [error, setError] = useState(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);
  const [actionLoading, setActionLoading] = useState(false);
  
  // --- Modal State ---
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [productToDeactivate, setProductToDeactivate] = useState(null);
  
  const [isActivateModalOpen, setIsActivateModalOpen] = useState(false);
  const [productToActivate, setProductToActivate] = useState(null);

  const abortControllerRef = useRef(null);

  // Handle row click
  const handleRowClick = (id) => {
    navigate(`/dashboard/products/${id}`);
  };

  // 0. Fetch Filter Options on Mount
  useEffect(() => {
    const fetchFilters = async () => {
      if (statusOptions.length > 0 && sortOptions.length > 0) {
        setFiltersLoading(false);
        return;
      }
      
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 8000);
      try {
        const response = await fetch('/api/products/filters', { signal: controller.signal });
        clearTimeout(timeoutId);
        if (!response.ok) throw new Error('Failed to fetch filters');
        const data = await response.json();
        
        setStatusOptions(data.statusOptions || []);
        setSortOptions(data.sortOptions || []);
      } catch (err) {
        clearTimeout(timeoutId);
        console.error("Error loading filters:", err);
        setStatusOptions([]);
        setSortOptions([]);
        if (err.name === 'AbortError') {
           addToast("Filters request timed out", "error");
        }
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

  const prevParams = useRef({ appliedSearch, statusFilter, sortOrder, pageFrontend, refreshTrigger });

  // Scroll Position Management
  useEffect(() => {
    const container = document.querySelector('.content-area');
    
    // If we came back from another view and have data, restore scroll
    if (isCached && products.length > 0) {
      if (container && scrollPositionRef.current) {
         requestAnimationFrame(() => {
           container.scrollTop = scrollPositionRef.current;
         });
      }
    }

    const handleScroll = () => {
      if (container) {
        scrollPositionRef.current = container.scrollTop;
      }
    };
    
    if (container) {
      container.addEventListener('scroll', handleScroll, { passive: true });
    }
    
    return () => {
      if (container) {
        container.removeEventListener('scroll', handleScroll);
      }
    };
  }, [isCached, products.length, scrollPositionRef]); // Run once on mount or when context changes

  // 2. Fetch data when filters/search/page changes
  useEffect(() => {
    const paramsChanged = 
      prevParams.current.appliedSearch !== appliedSearch ||
      prevParams.current.statusFilter !== statusFilter ||
      prevParams.current.sortOrder !== sortOrder ||
      prevParams.current.pageFrontend !== pageFrontend ||
      prevParams.current.refreshTrigger !== refreshTrigger;

    prevParams.current = { appliedSearch, statusFilter, sortOrder, pageFrontend, refreshTrigger };

    if (!paramsChanged && isCached) {
      return;
    }

    const fetchProducts = async () => {
      // Cancel previous request if it exists to avoid race conditions
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }

      const abortController = new AbortController();
      abortControllerRef.current = abortController;

      const timeoutId = setTimeout(() => abortController.abort(), 10000);

      setLoading(true);
      setError(null);

      try {
        const params = new URLSearchParams();
        
        const trimmedSearch = appliedSearch.trim();
        if (trimmedSearch.length > 0) {
          params.append('searchCodeOrName', trimmedSearch);
        }

        params.append('statusFilter', statusFilter);
        params.append('nameSort', sortOrder);
        params.append('page', pageFrontend - 1);
        params.append('size', 50);

        const response = await fetch(`/api/products?${params.toString()}`, {
          signal: abortController.signal
        });

        clearTimeout(timeoutId);

        if (!response.ok) {
          const errorData = await response.json().catch(() => ({}));
          throw new Error(errorData.error || errorData.message || `Error: ${response.status}`);
        }

        const data = await response.json();
        
        if (abortControllerRef.current === abortController) {
          if (data.content !== undefined) {
            setProducts(data.content);
            setTotalPages(data.totalPages || 1);
            setTotalElements(data.totalElements || 0);
          } else {
            setProducts(Array.isArray(data) ? data : []);
            setTotalPages(1);
            setTotalElements(0);
          }
          setIsCached(true); // Cache fetched data
        }
      } catch (err) {
        clearTimeout(timeoutId);
        if (err.name === 'AbortError') {
          if (abortControllerRef.current === abortController) {
             setError('Request timed out. Please refresh the page.');
             addToast("Product list request timed out", "error");
          } else {
             console.log('Previous request cancelled');
          }
        } else if (abortControllerRef.current === abortController) {
          console.error("Failed to fetch products:", err);
          setError(err.message || 'Failed to load products. Please try again later.');
          addToast(err.message || "The list could not be refreshed. Please try again.", "error");
        }
      } finally {
        if (abortControllerRef.current === abortController) {
          setLoading(false);
        }
      }
    };

    const mountDebounceReq = setTimeout(() => {
      fetchProducts();
    }, 15);
    
    // Cleanup on unmount or re-run
    return () => {
      clearTimeout(mountDebounceReq);
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }
    };
  }, [appliedSearch, statusFilter, sortOrder, pageFrontend, refreshTrigger]); // Prevent double-fetching on mount

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

  const handleManualRefresh = () => {
    setPageFrontend(1);
    setRefreshTrigger(prev => prev + 1);
  };

  const handleDeactivateProduct = (productCode) => {
    setProductToDeactivate(productCode);
    setIsModalOpen(true);
  };

  const confirmDeactivateProduct = async () => {
    if (!productToDeactivate) return;

    setActionLoading(true);
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 8000);

    try {
      const response = await fetch(`/api/products/${productToDeactivate}/deactivate`, {
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
      }
    } catch (err) {
      clearTimeout(timeoutId);
      const msg = err.name === 'AbortError' ? 'Deactivation request timed out' : (err.message || "An error occurred");
      addToast(msg, 'error');
    } finally {
      setPageFrontend(1);
      setRefreshTrigger(prev => prev + 1);
      setActionLoading(false);
      setIsModalOpen(false);
      setProductToDeactivate(null);
    }
  };

  const handleActivateProduct = (productCode) => {
    setProductToActivate(productCode);
    setIsActivateModalOpen(true);
  };

  const confirmActivateProduct = async () => {
    if (!productToActivate) return;

    setActionLoading(true);
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 8000);

    try {
      const response = await fetch(`/api/products/${productToActivate}/activate`, {
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
      }
    } catch (err) {
      clearTimeout(timeoutId);
      const msg = err.name === 'AbortError' ? 'Activation request timed out' : (err.message || "An error occurred");
      addToast(msg, 'error');
    } finally {
      setPageFrontend(1);
      setRefreshTrigger(prev => prev + 1);
      setActionLoading(false);
      setIsActivateModalOpen(false);
      setProductToActivate(null);
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

        <div className="toolbar-right" style={{ display: 'flex', gap: '12px' }}>
          <button 
            className="btn-secondary" 
            onClick={handleManualRefresh} 
            disabled={loading}
          >
            <RefreshCw size={18} className={loading ? "spin-animation" : ""} />
            <span>{loading ? 'Refreshing...' : 'Refresh'}</span>
          </button>
          <button 
            className="btn-primary"
            onClick={() => navigate('/dashboard/products/new')}
          >
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
                      <button 
                        className="action-btn edit-btn" 
                        title="Edit Product"
                        onClick={(e) => {
                          e.stopPropagation();
                          navigate(`/dashboard/products/edit/${product.productCode}`);
                        }}
                      >
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
                          onClick={(e) => {
                            e.stopPropagation();
                            handleActivateProduct(product.productCode);
                          }}
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

      <ConfirmModal
        isOpen={isActivateModalOpen}
        title="Confirm Activation"
        message="Are you sure you want to activate this product?"
        onConfirm={confirmActivateProduct}
        onCancel={() => {
          setIsActivateModalOpen(false);
          setProductToActivate(null);
        }}
        isConfirming={actionLoading}
        confirmText="Activate"
        confirmButtonTheme="success"
      />
    </div>
  );
};

export default ProductsView;
