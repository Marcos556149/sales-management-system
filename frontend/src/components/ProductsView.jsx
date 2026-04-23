import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Plus, Filter, Edit2, Ban, CheckCircle2, X, RefreshCw } from 'lucide-react';
import Pagination from './Pagination';
import ConfirmModal from './ConfirmModal';
import { useToast } from './ToastContext';
import { useProductsContext } from './ProductsContext';
import { productService } from '../services/productService';
import { TEXTS } from '../constants/texts';
import { useKeyboardShortcuts } from '../hooks/useKeyboardShortcuts';
import './ProductsView.css';

const ProductsView = () => {
  const navigate = useNavigate();
  const { addToast } = useToast();

  // --- Context State ---
  const {
      searchTerm, setSearchTerm,
      appliedSearch, setAppliedSearch,
      statusFilter, setStatusFilter,
      stockLevelFilter, setStockLevelFilter,
      sortOrder, setSortOrder,
      pageFrontend, setPageFrontend,
      productsData: products, setProductsData: setProducts,
      totalPages, setTotalPages,
      totalElements, setTotalElements,
      totalGlobalElements, setTotalGlobalElements,
      statusOptions, setStatusOptions,
      stockLevelOptions, setStockLevelOptions,
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
  const searchInputRef = useRef(null);

  // Handle row click
  const handleRowClick = async (id) => {
    if (actionLoading) return;
    
    setActionLoading(true);
    try {
      // Check if product exists before navigating
      const response = await productService.getProduct(id);
      // Pass the product data to the detail view to avoid a second request
      navigate(`/dashboard/products/${id}`, { state: { product: response.data } });
    } catch (err) {
      if (err.status === 400) {
        addToast(err.message || `Product with code ${id} not found`, 'error');
        // Refresh the list to remove the missing product
        setRefreshTrigger(prev => prev + 1);
      } else {
        addToast(err.message || 'Error checking product', 'error');
      }
    } finally {
      setActionLoading(false);
    }
  };

  const handleEditClick = async (productCode) => {
    if (actionLoading) return;
    
    setActionLoading(true);
    try {
      // Check existence and get fresh data
      const response = await productService.getProduct(productCode);
      navigate(`/dashboard/products/edit/${productCode}`, { state: { product: response.data } });
    } catch (err) {
      if (err.status === 400) {
        addToast(err.message || `Product with code ${productCode} not found`, 'error');
        setRefreshTrigger(prev => prev + 1);
      } else {
        addToast(err.message || 'Error checking product', 'error');
      }
    } finally {
      setActionLoading(false);
    }
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
        const response = await productService.getFilters({ signal: controller.signal });
        clearTimeout(timeoutId);
        
        setStatusOptions(response.data.statusOptions || []);
        setSortOptions(response.data.nameSortOptions || []);
        setStockLevelOptions(response.data.stockLevelOptions || []);
      } catch (err) {
        clearTimeout(timeoutId);
        console.error("Error loading filters:", err);
        setStatusOptions([]);
        setSortOptions([]);
        setStockLevelOptions([]);
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

  const prevParams = useRef({ appliedSearch, statusFilter, stockLevelFilter, sortOrder, pageFrontend, refreshTrigger });

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
      prevParams.current.stockLevelFilter !== stockLevelFilter ||
      prevParams.current.sortOrder !== sortOrder ||
      prevParams.current.pageFrontend !== pageFrontend ||
      prevParams.current.refreshTrigger !== refreshTrigger;

    prevParams.current = { appliedSearch, statusFilter, stockLevelFilter, sortOrder, pageFrontend, refreshTrigger };

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
        const params = {
          statusFilter,
          stockFilter: stockLevelFilter,
          nameSort: sortOrder,
          page: pageFrontend - 1,
          size: 50
        };
        const trimmedSearch = appliedSearch.trim();
        if (trimmedSearch.length > 0) {
          params.searchCodeOrName = trimmedSearch;
        }

        const response = await productService.getProducts(params, { signal: abortController.signal });
        clearTimeout(timeoutId);

        const data = response.data;
        
        if (abortControllerRef.current === abortController) {
          if (data.content !== undefined) {
            setProducts(data.content);
            setTotalPages(data.totalPages || 1);
            setTotalElements(data.totalElements || 0);
            setTotalGlobalElements(data.totalGlobalElements !== undefined ? data.totalGlobalElements : null);
          } else {
            setProducts(Array.isArray(data) ? data : []);
            setTotalPages(1);
            setTotalElements(0);
            setTotalGlobalElements(null);
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
  }, [appliedSearch, statusFilter, stockLevelFilter, sortOrder, pageFrontend, refreshTrigger]); // Prevent double-fetching on mount

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
      const res = await productService.deactivateProduct(productToDeactivate, { signal: controller.signal });
      clearTimeout(timeoutId);
      addToast(res.message || TEXTS.products.deactivateSuccess, 'success');
    } catch (err) {
      clearTimeout(timeoutId);
      const msg = err.name === 'AbortError' ? 'Deactivation request timed out' : (err.message || TEXTS.common.errorOccurred);
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
      const res = await productService.activateProduct(productToActivate, { signal: controller.signal });
      clearTimeout(timeoutId);
      addToast(res.message || TEXTS.products.activateSuccess, 'success');
    } catch (err) {
      clearTimeout(timeoutId);
      const msg = err.name === 'AbortError' ? 'Activation request timed out' : (err.message || TEXTS.common.errorOccurred);
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
  
  // Register contextual shortcuts
  useKeyboardShortcuts(React.useMemo(() => ({
    'shift+n': () => navigate('/dashboard/products/new'),
    'ctrl+shift+r': () => handleManualRefresh(),
    '/': () => searchInputRef.current?.focus(),
    'arrowright': () => {
      if (pageFrontend < totalPages) {
        setPageFrontend(prev => prev + 1);
      }
    },
    'arrowleft': () => {
      if (pageFrontend > 1) {
        setPageFrontend(prev => prev - 1);
      }
    }
  }), [navigate, handleManualRefresh, pageFrontend, totalPages, setPageFrontend]));

  return (
    <div className="view-container">
      {/* Top action bar */}
      <div className="view-toolbar">
        <div className="toolbar-left">
          <div className="search-bar">
            <Search className="search-icon" size={18} />
            <input 
              ref={searchInputRef}
              type="text" 
              placeholder="Search by name or code..." 
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onKeyDown={handleKeyDown}
            />
            <div className="search-actions">
              {searchTerm && (
                <button 
                  className="clear-search-btn" 
                  onClick={handleClearSearch}
                  title="Clear Search"
                >
                  <X size={16} />
                </button>
              )}
              <span className="search-hint">/</span>
            </div>
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
            <div className="filter-item">
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
            {/* Stock Level Filter */}
            <div className="filter-item">
              <span style={{ fontSize: '0.9rem', color: '#6b7280', fontWeight: '500' }}>{TEXTS.products.stockLevel}</span>
              <select 
                value={stockLevelFilter} 
                onChange={(e) => {
                  setStockLevelFilter(e.target.value);
                  setPageFrontend(1);
                }}
                className="select-dropdown"
                disabled={filtersLoading || stockLevelOptions.length === 0}
              >
                {filtersLoading ? (
                  <option value="ALL">{TEXTS.common.loading}</option>
                ) : stockLevelOptions.length === 0 ? (
                  <option value="ALL">All Stock</option>
                ) : (
                  stockLevelOptions.map(opt => (
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
          <button 
            className="btn-secondary" 
            onClick={handleManualRefresh} 
            disabled={loading}
          >
            <RefreshCw size={18} className={loading ? "spin-animation" : ""} />
            <span>{loading ? 'Refreshing...' : 'Refresh'}</span>
            <span className="btn-shortcut">Ctrl+Shift+R</span>
          </button>
          <button 
            className="btn-primary"
            onClick={() => navigate('/dashboard/products/new')}
          >
            <Plus size={18} />
            <span>New Product</span>
            <span className="btn-shortcut" style={{ backgroundColor: 'rgba(255, 255, 255, 0.2)', color: 'white', borderColor: 'rgba(255, 255, 255, 0.3)' }}>Shift+N</span>
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
            {totalGlobalElements === 0 
              ? <p>No products available</p>
              : <p>No products match the search criteria</p>
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
                      {(() => {
                        const stock = product.productStock ?? 0;
                        const min = product.minimumStock ?? 0;
                        if (stock === 0) {
                          return <span style={{ color: 'var(--danger-color, #ef4444)', fontWeight: 'bold' }}>{TEXTS.products.outOfStock}</span>;
                        }
                        if (stock <= min) {
                          return (
                            <span style={{ color: 'var(--danger-color, #ef4444)', fontWeight: 'bold' }}>
                              {stock} <span className="unit-text">{product.unitOfMeasure?.code === 'UNITS' ? 'u' : (product.unitOfMeasure?.label || '')}</span>
                            </span>
                          );
                        }
                        return (
                          <span>
                            {stock} <span className="unit-text">{product.unitOfMeasure?.code === 'UNITS' ? 'u' : (product.unitOfMeasure?.label || '')}</span>
                          </span>
                        );
                      })()}
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
                          handleEditClick(product.productCode);
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
