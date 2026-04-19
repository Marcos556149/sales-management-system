import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Calendar, Filter, Edit2, Printer, X, RefreshCw, Trash2, Plus } from 'lucide-react';
import Pagination from './Pagination';
import { useToast } from './ToastContext';
import DatePicker from 'react-datepicker';
import { enUS } from 'date-fns/locale';
import 'react-datepicker/dist/react-datepicker.css';
import './ProductsView.css';
import './SalesView.css';
import { useSalesContext } from './SalesContext';

const CustomDateInput = React.forwardRef(({ value, onClick }, ref) => (
  <button 
    type="button"
    className="select-dropdown custom-date-button" 
    onClick={onClick} 
    ref={ref}
  >
    <span>{value}</span>
    <Calendar size={16} className="datepicker-inline-icon" />
  </button>
));

const SalesView = () => {
  const navigate = useNavigate();
  const { addToast } = useToast();
  
  const {
      dateFilter, setDateFilter,
      sortOrder, setSortOrder,
      pageFrontend, setPageFrontend,
      salesData: sales, setSalesData: setSales,
      totalPages, setTotalPages,
      totalElements, setTotalElements,
      scrollPositionRef,
      isCached, setIsCached,
      getTodayFormatted
  } = useSalesContext();

  // --- Options State ---
  const [sortOptions, setSortOptions] = useState([]);
  const [filtersLoading, setFiltersLoading] = useState(true);

  // --- Data State ---
  const [loading, setLoading] = useState(!isCached);
  const [error, setError] = useState(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const abortControllerRef = useRef(null);

  // Format Helpers
  const formatTime = (timeStr) => {
    if (!timeStr) return '';
    // timeStr could be "HH:mm:ss" or "HH:mm:ss.SSS"
    const parts = timeStr.split(':');
    if (parts.length >= 2) {
      return `${parts[0]}:${parts[1]}`;
    }
    return timeStr;
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    // Usually YYYY-MM-DD
    const parts = dateStr.split('-');
    if (parts.length === 3) {
      return `${parts[2]}/${parts[1]}/${parts[0]}`; // DD/MM/YYYY
    }
    return dateStr;
  };

  // Handle row click
  const handleRowClick = (id) => {
    navigate(`/dashboard/sales/${id}`);
  };

  // 0. Fetch Filter Options on Mount
  useEffect(() => {
    const fetchFilters = async () => {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 8000);
      try {
        const response = await fetch('/api/sales/filters', { signal: controller.signal });
        clearTimeout(timeoutId);
        if (!response.ok) throw new Error('Failed to fetch filters');
        const data = await response.json();
        
        setSortOptions(data.sortOptions || []);
      } catch (err) {
        clearTimeout(timeoutId);
        console.error("Error loading filters:", err);
        setSortOptions([]);
        if (err.name === 'AbortError') {
           addToast("Filters request timed out", "error");
        }
      } finally {
        setFiltersLoading(false);
      }
    };
    fetchFilters();
  }, [addToast]);

  const prevParams = useRef({ dateFilter, sortOrder, pageFrontend, refreshTrigger });

  // Scroll Position Management
  useEffect(() => {
    const container = document.querySelector('.content-area');
    
    // If we came back from another view and have data, restore scroll
    if (isCached && sales.length > 0) {
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
  }, [isCached, sales.length, scrollPositionRef]); // Run once on mount or context change

  // 1. Fetch data when filters/page changes
  useEffect(() => {
    const paramsChanged = 
      prevParams.current.dateFilter !== dateFilter ||
      prevParams.current.sortOrder !== sortOrder ||
      prevParams.current.pageFrontend !== pageFrontend ||
      prevParams.current.refreshTrigger !== refreshTrigger;

    prevParams.current = { dateFilter, sortOrder, pageFrontend, refreshTrigger };

    // Skip fetch entirely if we are cached and this is just a mount/remount
    if (!paramsChanged && isCached) {
      return;
    }

    const fetchSales = async () => {
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
        
        params.append('date', dateFilter);
        params.append('timeSort', sortOrder);
        // Frontend uses 1-based index, backend uses 0-based
        params.append('page', pageFrontend - 1);
        params.append('size', 50);

        const response = await fetch(`/api/sales?${params.toString()}`, {
          signal: abortController.signal
        });

        clearTimeout(timeoutId);

        if (!response.ok) {
          const errorData = await response.json().catch(() => ({}));
          
          let errorMessage = errorData.error || errorData.message;
          if (!errorMessage && Object.keys(errorData).length > 0) {
            // Mapping MethodArgumentTypeMismatchException or Field validations
            const firstKey = Object.keys(errorData)[0];
            if (typeof errorData[firstKey] === 'string') {
              errorMessage = errorData[firstKey];
            }
          }

          // Using standard HTTP status responses matching Product's style
          if (response.status === 404) {
             throw new Error(errorMessage || "No sales found for this date.");
          }
          throw new Error(errorMessage || `Error: ${response.status}`);
        }

        const data = await response.json();
        
        if (abortControllerRef.current === abortController) {
          if (data.content !== undefined) {
            setSales(data.content);
            setTotalPages(data.totalPages || 1);
            setTotalElements(data.totalElements || 0);
          } else {
            setSales(Array.isArray(data) ? data : []);
            setTotalPages(1);
            setTotalElements(0);
          }
          setIsCached(true); // Mark that we successfully fetched and cached data
        }
      } catch (err) {
        clearTimeout(timeoutId);
        if (err.name === 'AbortError') {
          if (abortControllerRef.current === abortController) {
             setError('Request timed out. Please refresh the page.');
             addToast("Sales list request timed out", "error");
          } else {
             console.log('Previous request cancelled');
          }
        } else if (abortControllerRef.current === abortController) {
          console.error("Failed to fetch sales:", err);
          // 404 sets No sales found, show cleanly
          if (err.message && err.message.toLowerCase().includes('no sales found')) {
            setSales([]);
            setTotalPages(1);
            setTotalElements(0);
            setIsCached(true); // Empty list is still a valid cache
            // Don't show the red error box for empty lists, just empty state
          } else {
            setError(err.message || 'Failed to load sales. Please try again later.');
            addToast(err.message || "The list could not be refreshed. Please try again.", "error");
          }
        }
      } finally {
        if (abortControllerRef.current === abortController) {
          setLoading(false);
        }
      }
    };

    // Debounce slightly to kill the React StrictMode double-mount network request
    const mountDebounceReq = setTimeout(() => {
      fetchSales();
    }, 15);
    
    // Cleanup on unmount or re-run
    return () => {
      clearTimeout(mountDebounceReq);
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }
    };
  }, [dateFilter, sortOrder, pageFrontend, refreshTrigger]);


  // Actions
  const handleManualRefresh = () => {
    setPageFrontend(1);
    setRefreshTrigger(prev => prev + 1);
  };

  const hasActiveFilters = dateFilter !== getTodayFormatted() || sortOrder !== 'DESCENDING';

  return (
    <div className="view-container sales-view-container">
      {/* Top action bar */}
      <div className="view-toolbar">
        <div className="toolbar-left">
          
          <div className="filter-group">
            {/* Date Filter */}
            <div className="filter-item">
              <span className="date-filter-label">Filter Date:</span>
              <div className="custom-datepicker-wrapper">
                <DatePicker
                  selected={(() => {
                    if (!dateFilter) return new Date();
                    const [y, m, d] = dateFilter.split('-');
                    return new Date(parseInt(y), parseInt(m) - 1, parseInt(d));
                  })()}
                  onChange={(date) => {
                    if (date) {
                      const year = date.getFullYear();
                      const month = String(date.getMonth() + 1).padStart(2, '0');
                      const day = String(date.getDate()).padStart(2, '0');
                      setDateFilter(`${year}-${month}-${day}`);
                      setPageFrontend(1);
                    }
                  }}
                  dateFormat="yyyy-MM-dd"
                  locale={enUS}
                  todayButton="Today"
                  customInput={<CustomDateInput />}
                  wrapperClassName="date-picker-wrapper"
                  popperPlacement="bottom-start"
                  showPopperArrow={false}
                />
              </div>
            </div>
            
            {/* Sort Order Control */}
            <div className="filter-item" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <span className="date-filter-label">Order by Time:</span>
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
                  <option value="DESCENDING">Loading...</option>
                ) : sortOptions.length === 0 ? (
                  <>
                    <option value="DESCENDING">Descending</option>
                    <option value="ASCENDING">Ascending</option>
                  </>
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
            onClick={() => navigate('/dashboard/sales/new')}
          >
            <Plus size={18} />
            <span>New Sale</span>
          </button>
        </div>
      </div>

      {/* Content Area - Data Table */}
      <div className="table-card sales-table-card">
        {loading ? (
          <div className="loading-state">
             <p>Loading sales...</p>
          </div>
        ) : error ? (
          <div className="error-state">
            <p>{error}</p>
          </div>
        ) : sales.length === 0 ? (
          <div className="empty-state">
            {hasActiveFilters 
              ? <p>No sales match the search criteria</p>
              : <p>No sales found</p>
            }
          </div>
        ) : (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Sale ID</th>
                  <th>Date</th>
                  <th>Time</th>
                  <th>Seller</th>
                  <th>Total</th>
                  <th className="text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {sales.map(sale => (
                  <tr 
                    key={sale.saleId} 
                    onClick={() => handleRowClick(sale.saleId)}
                    style={{ cursor: 'pointer' }}
                    className="interactive-row"
                  >
                    <td className="font-mono text-sm">#{sale.saleId}</td>
                    <td className="font-medium">{formatDate(sale.saleDate)}</td>
                    <td>{formatTime(sale.saleTime)}</td>
                    <td>{sale.userName || 'Unknown'}</td>
                    <td className="total-amount-cell">${sale.totalAmount?.toFixed(2) ?? '0.00'}</td>
                    <td className="actions-cell text-right" onClick={(e) => e.stopPropagation()}>
                        <button 
                          className="action-btn edit-btn" 
                          title="Edit Sale"
                          onClick={(e) => {
                            e.stopPropagation();
                            addToast("Edit sale coming soon", "info");
                          }}
                        >
                          <Edit2 size={16} />
                        </button>
                        <button 
                          className="action-btn print-btn" 
                          title="Print Ticket"
                          onClick={(e) => {
                            e.stopPropagation();
                            addToast("Printing coming soon", "info");
                          }}
                        >
                          <Printer size={16} />
                        </button>
                        <button 
                          className="action-btn deactivate-btn" 
                          title="Delete Sale"
                          onClick={(e) => {
                            e.stopPropagation();
                            addToast("Delete sale coming soon", "info");
                          }}
                        >
                          <Trash2 size={16} />
                        </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
        
        {/* Pagination Bar */}
        {!loading && sales.length > 0 && (
          <Pagination 
            currentPage={pageFrontend}
            totalPages={totalPages}
            totalElements={totalElements}
            onPageChange={setPageFrontend}
            itemName="sales"
          />
        )}
      </div>
    </div>
  );
};

export default SalesView;
