import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Calendar, Filter, Edit2, Printer, X, RefreshCw, Trash2, Plus } from 'lucide-react';
import Pagination from './Pagination';
import { useToast } from './ToastContext';
import DatePicker from 'react-datepicker';
import { es } from 'date-fns/locale';
import 'react-datepicker/dist/react-datepicker.css';
import './ProductsView.css';
import './SalesView.css';
import { useSalesContext } from './SalesContext';
import { useKeyboardShortcuts } from '../hooks/useKeyboardShortcuts';
import { apiClient } from '../api/client';
import ConfirmModal from './ConfirmModal';
import { printTicket } from '../utils/printUtils';

const SmartDateInput = React.forwardRef(({ value, onClick, onManualChange, onTyping, onClearFocus, forceSync }, ref) => {
  const [day, setDay] = useState('');
  const [month, setMonth] = useState('');
  const [year, setYear] = useState('');

  // Sync internal state with external value (YYYY-MM-DD)
  useEffect(() => {
    if (value && value.includes('-')) {
      const [y, m, d] = value.split('-');
      setYear(y);
      setMonth(m);
      setDay(d);
    } else {
      // Clear if empty
      setYear('');
      setMonth('');
      setDay('');
    }
  }, [value, forceSync]);

  const validateAndNotify = (d, m, y, triggerSearch = false) => {
    let dInt = parseInt(d, 10);
    let mInt = parseInt(m, 10);
    let yInt = parseInt(y, 10);

    // If something is not a number, sync back to last valid
    if (isNaN(dInt) || isNaN(mInt) || isNaN(yInt)) {
      if (value && value.includes('-')) {
        const [oldY, oldM, oldD] = value.split('-');
        setDay(oldD);
        setMonth(oldM);
        setYear(oldY);
      }
      return;
    }

    const normalizedYear = Math.max(yInt, 1);
    const normalizedMonth = Math.min(Math.max(mInt, 1), 12);

    const getDaysInMonth = (year, month) => {
      if (month === 2) {
        const isLeap = (year % 4 === 0 && (year % 100 !== 0 || year % 400 === 0));
        return isLeap ? 29 : 28;
      }
      return [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month - 1];
    };

    const maxDays = getDaysInMonth(normalizedYear, normalizedMonth);
    const normalizedDay = Math.min(Math.max(dInt, 1), maxDays);

    const newDateStr = `${String(normalizedYear).padStart(4, '0')}-${String(normalizedMonth).padStart(2, '0')}-${String(normalizedDay).padStart(2, '0')}`;

    // Update local state for visual consistency
    setDay(String(normalizedDay).padStart(2, '0'));
    setMonth(String(normalizedMonth).padStart(2, '0'));
    setYear(String(normalizedYear).padStart(4, '0'));

    // Only notify parent if triggerSearch is true
    if (triggerSearch && newDateStr !== value) {
      onManualChange(newDateStr);
    }
  };

  const handleInputChange = (e, setter, maxLen) => {
    const val = e.target.value.replace(/[^0-9]/g, '').slice(0, maxLen);
    setter(val);
    if (onTyping) onTyping();
  };

  const handleBlur = () => {
    // Correct visual formatting but don't trigger search
    validateAndNotify(day, month, year, false);
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      // Validate and trigger search
      validateAndNotify(day, month, year, true);
      // Remove focus
      e.target.blur();
    }
  };

  return (
    <div className="smart-date-container" ref={ref}>
      <div className="smart-date-inputs">
        <input
          type="text"
          className="smart-date-field day-field"
          value={day}
          onChange={(e) => handleInputChange(e, setDay, 2)}
          onBlur={handleBlur}
          onFocus={onClearFocus}
          onKeyDown={handleKeyDown}
          placeholder="DD"
        />
        <span className="date-separator">/</span>
        <input
          type="text"
          className="smart-date-field month-field"
          value={month}
          onChange={(e) => handleInputChange(e, setMonth, 2)}
          onBlur={handleBlur}
          onFocus={onClearFocus}
          onKeyDown={handleKeyDown}
          placeholder="MM"
        />
        <span className="date-separator">/</span>
        <input
          type="text"
          className="smart-date-field year-field"
          value={year}
          onChange={(e) => handleInputChange(e, setYear, 4)}
          onBlur={handleBlur}
          onFocus={onClearFocus}
          onKeyDown={handleKeyDown}
          placeholder="YYYY"
        />
      </div>
      <button
        type="button"
        className="smart-date-picker-btn"
        onClick={(e) => {
          if (onClearFocus) onClearFocus();
          if (onClick) onClick(e);
        }}
        title="Abrir Calendario"
      >
        <Calendar size={16} />
      </button>
    </div>
  );
});

const SalesView = () => {
  const navigate = useNavigate();
  const { addToast } = useToast();

  const {
    searchSaleId, setSearchSaleId,
    appliedSearchSaleId, setAppliedSearchSaleId,
    dateFilter, setDateFilter,
    sortOrder, setSortOrder,
    pageFrontend, setPageFrontend,
    salesData: sales, setSalesData: setSales,
    totalPages, setTotalPages,
    totalElements, setTotalElements,
    totalGlobalElements, setTotalGlobalElements,
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
  const [actionLoading, setActionLoading] = useState(false);
  const [focusedIndex, setFocusedIndex] = useState(-1);
  const [showPrintModal, setShowPrintModal] = useState(false);
  const [selectedSaleId, setSelectedSaleId] = useState(null);
  const [dateResetCounter, setDateResetCounter] = useState(0);

  const abortControllerRef = useRef(null);
  const searchInputRef = useRef(null);

  // Debounce for search input
  useEffect(() => {
    const handler = setTimeout(() => {
      setAppliedSearchSaleId((prev) => {
        if (prev !== searchSaleId) {
          setPageFrontend(1);
          return searchSaleId;
        }
        return prev;
      });
    }, 400);

    return () => clearTimeout(handler);
  }, [searchSaleId, setAppliedSearchSaleId, setPageFrontend]);

  // Reset focus when sales change
  useEffect(() => {
    setFocusedIndex(-1);
  }, [sales]);

  // Scroll focused row into view
  useEffect(() => {
    if (focusedIndex >= 0) {
      const row = document.getElementById(`sale-row-${focusedIndex}`);
      if (row) {
        row.scrollIntoView({ block: 'nearest' });
      }
    }
  }, [focusedIndex]);

  // Handle immediate search on Enter key and Arrow Navigation
  const handleSearchKeyDown = (e) => {
    if (e.key === 'ArrowDown') {
      e.preventDefault();
      setFocusedIndex(prev => Math.min(prev + 1, sales.length - 1));
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      setFocusedIndex(prev => Math.max(prev - 1, 0));
    } else if (e.key === 'Enter') {
      e.preventDefault();
      if (focusedIndex >= 0 && focusedIndex < sales.length) {
        handleRowClick(sales[focusedIndex].saleId);
      } else if (appliedSearchSaleId !== searchSaleId) {
        setPageFrontend(1);
        setAppliedSearchSaleId(searchSaleId);
      } else if (sales.length === 1) {
        handleRowClick(sales[0].saleId);
      }
    }
  };

  const handleClearSearch = () => {
    setSearchSaleId('');
    setPageFrontend(1);
    setAppliedSearchSaleId('');
  };

  const handleSearchChange = (e) => {
    const val = e.target.value;
    // Allow empty string to clear the input
    if (val === '') {
      setSearchSaleId('');
      setFocusedIndex(-1);
      return;
    }
    // Remove all non-numeric characters
    const numericVal = val.replace(/[^0-9]/g, '');
    // Prevent 0 as first character or standalone "0"
    if (numericVal.length > 0 && parseInt(numericVal, 10) >= 1) {
      // Remove leading zeros by parsing and re-stringifying
      setSearchSaleId(parseInt(numericVal, 10).toString());
      setFocusedIndex(-1);
    }
  };

  // Format Helpers
  const formatTime = (timeStr) => {
    if (!timeStr) return '';
    // timeStr could be "HH:mm:ss" or "HH:mm:ss.SSS"
    const parts = timeStr.split(':');
    if (parts.length >= 3) {
      const seconds = parts[2].split('.')[0];
      return `${parts[0]}:${parts[1]}:${seconds}`;
    } else if (parts.length === 2) {
      return `${parts[0]}:${parts[1]}:00`;
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
  const handleRowClick = async (id) => {
    if (actionLoading) return;

    setActionLoading(true);
    try {
      const response = await apiClient.get(`/api/sales/${id}`);
      navigate(`/dashboard/sales/${id}`, { state: { sale: response.data } });
    } catch (err) {
      if (err.status === 400 || err.status === 404) {
        addToast(err.message || `No se encontró la venta con ID '${id}'`, 'error');
        // Refresh the list to remove the missing sale
        setRefreshTrigger(prev => prev + 1);
      } else {
        addToast(err.message || 'Error consultando la venta', 'error');
      }
    } finally {
      setActionLoading(false);
    }
  };

  // 0. Fetch Filter Options on Mount
  useEffect(() => {
    const fetchFilters = async () => {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 8000);
      try {
        const response = await apiClient.get('/api/sales/filters', { signal: controller.signal });
        clearTimeout(timeoutId);

        setSortOptions(response.data.timeSortOptions || []);
      } catch (err) {
        clearTimeout(timeoutId);
        console.error("Error cargando filtros:", err);
        setSortOptions([]);
        if (err.name === 'AbortError') {
          addToast("La solicitud de filtros tardó demasiado", "error");
        }
      } finally {
        setFiltersLoading(false);
      }
    };
    fetchFilters();
  }, [addToast]);

  const prevParams = useRef({ dateFilter, sortOrder, pageFrontend, refreshTrigger, appliedSearchSaleId });

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
      prevParams.current.refreshTrigger !== refreshTrigger ||
      prevParams.current.appliedSearchSaleId !== appliedSearchSaleId;

    prevParams.current = { dateFilter, sortOrder, pageFrontend, refreshTrigger, appliedSearchSaleId };

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

        if (dateFilter) {
          params.append('date', dateFilter);
        }
        if (appliedSearchSaleId) {
          params.append('searchSaleId', appliedSearchSaleId);
        }
        params.append('timeSort', sortOrder);
        // Frontend uses 1-based index, backend uses 0-based
        params.append('page', pageFrontend - 1);
        params.append('size', 50);

        const queryString = params.toString();
        const response = await apiClient.get(`/api/sales?${queryString}`, {
          signal: abortController.signal
        });

        clearTimeout(timeoutId);

        const data = response.data;

        if (abortControllerRef.current === abortController) {
          if (data.content !== undefined) {
            setSales(data.content);
            setTotalPages(data.totalPages || 1);
            setTotalElements(data.totalElements || 0);
            setTotalGlobalElements(data.totalGlobalElements !== undefined ? data.totalGlobalElements : null);
          } else {
            setSales(Array.isArray(data) ? data : []);
            setTotalPages(1);
            setTotalElements(0);
            setTotalGlobalElements(null);
          }
          setIsCached(true); // Mark that we successfully fetched and cached data
        }
      } catch (err) {
        clearTimeout(timeoutId);
        if (err.name === 'AbortError') {
          if (abortControllerRef.current === abortController) {
            setError('La solicitud tardó demasiado. Por favor, recarga la página.');
            addToast("La solicitud de ventas tardó demasiado", "error");
          } else {
            console.log('Previous request cancelled');
          }
        } else if (abortControllerRef.current === abortController) {
          console.error("Error cargando ventas:", err);
          // 404 sets No sales found, show cleanly
          if (err.message && err.message.toLowerCase().includes('No se encontraron ventas')) {
            setSales([]);
            setTotalPages(1);
            setTotalElements(0);
            setTotalGlobalElements(null);
            setIsCached(true); // Empty list is still a valid cache
            // Don't show the red error box for empty lists, just empty state
          } else {
            setError(err.message || 'No se pudieron cargar las ventas. Intenta nuevamente más tarde.');
            addToast(err.message || "No se pudo actualizar la lista. Intenta nuevamente.", "error");
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
  }, [dateFilter, sortOrder, pageFrontend, refreshTrigger, appliedSearchSaleId]);


  // Actions
  const handleManualRefresh = () => {
    setPageFrontend(1);
    setRefreshTrigger(prev => prev + 1);
  };

  const hasActiveFilters = dateFilter !== '' || sortOrder !== 'NEWEST_FIRST' || appliedSearchSaleId !== '';

  // Register contextual shortcuts
  useKeyboardShortcuts(React.useMemo(() => ({
    'alt+n': () => navigate('/dashboard/sales/new'),
    'ctrl+shift+k': () => handleManualRefresh(),
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
    },
    'arrowdown': () => {
      if (sales.length > 0) {
        setFocusedIndex(prev => Math.min(prev + 1, sales.length - 1));
      }
    },
    'arrowup': () => {
      if (sales.length > 0) {
        setFocusedIndex(prev => Math.max(prev - 1, 0));
      }
    },
    'enter': () => {
      if (document.activeElement && document.activeElement.tagName === 'BUTTON') return;
      if (focusedIndex >= 0 && focusedIndex < sales.length) {
        handleRowClick(sales[focusedIndex].saleId);
      }
    }
  }), [navigate, handleManualRefresh, pageFrontend, totalPages, setPageFrontend, sales, focusedIndex]));

  return (
    <div className="view-container sales-view-container">
      {/* Top action bar */}
      <div className="view-toolbar">
        <div className="toolbar-left">

          <div className="search-bar">
            <Search className="search-icon" size={18} />
            <input
              ref={searchInputRef}
              type="text"
              placeholder="Buscar por ID de Venta…"
              value={searchSaleId}
              onChange={handleSearchChange}
              onFocus={() => setFocusedIndex(-1)}
              onKeyDown={handleSearchKeyDown}
            />
            <div className="search-actions">
              {searchSaleId && (
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
            {/* Date Filter */}
            <div className="filter-item">
              <span className="date-filter-label">Filtrar por fecha:</span>
              <div className="custom-datepicker-wrapper">
                <DatePicker
                  selected={(() => {
                    if (!dateFilter) return null;
                    const [y, m, d] = dateFilter.split('-');
                    return new Date(parseInt(y), parseInt(m) - 1, parseInt(d));
                  })()}
                  onChange={(date) => {
                    if (date) {
                      const year = date.getFullYear();
                      const month = String(date.getMonth() + 1).padStart(2, '0');
                      const day = String(date.getDate()).padStart(2, '0');
                      const newDateStr = `${year}-${month}-${day}`;

                      // If the date is the same as current filter, onChange might not be triggered 
                      // by some interactions, but when it is, we still update.
                      setDateFilter(newDateStr);
                      setPageFrontend(1);
                    }
                  }}
                  onSelect={(date) => {
                    if (date) {
                      const year = date.getFullYear();
                      const month = String(date.getMonth() + 1).padStart(2, '0');
                      const day = String(date.getDate()).padStart(2, '0');
                      const selectedDateStr = `${year}-${month}-${day}`;

                      // If user selects the SAME date that is already in context, 
                      // we force the SmartDateInput to sync its internal state.
                      if (selectedDateStr === dateFilter) {
                        setDateResetCounter(prev => prev + 1);
                      }
                    }
                  }}
                  dateFormat="yyyy-MM-dd"
                  locale={es}
                  todayButton="Hoy"
                  customInput={
                    <SmartDateInput
                      onClearFocus={() => setFocusedIndex(-1)}
                      onTyping={() => setFocusedIndex(-1)}
                      forceSync={dateResetCounter}
                      onManualChange={(dateStr) => {
                        setDateFilter(dateStr);
                        setPageFrontend(1);
                      }}
                    />
                  }
                  wrapperClassName="date-picker-wrapper"
                  popperPlacement="bottom-start"
                  showPopperArrow={false}
                />
              </div>
            </div>

            {/* Sort Order Control */}
            <div className="filter-item" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <span className="date-filter-label">Ordenar por hora:</span>
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
                  <option value="NEWEST_FIRST">Cargando…</option>
                ) : sortOptions.length === 0 ? (
                  <option value="NEWEST_FIRST">Más recientes primero</option>
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
            <span>{loading ? 'Actualizando...' : 'Actualizar'}</span>
            <span className="btn-shortcut">Ctrl+Shift+K</span>
          </button>

          <button
            className="btn-primary"
            onClick={() => navigate('/dashboard/sales/new')}
          >
            <Plus size={18} />
            <span>Nueva Venta</span>
            <span className="btn-shortcut" style={{ backgroundColor: 'rgba(255, 255, 255, 0.2)', color: 'white', borderColor: 'rgba(255, 255, 255, 0.3)' }}>Alt+N</span>
          </button>
        </div>
      </div>

      {/* Content Area - Data Table */}
      <div className="table-card sales-table-card">
        {loading ? (
          <div className="loading-state">
            <p>Cargando ventas…</p>
          </div>
        ) : error ? (
          <div className="error-state">
            <p>{error}</p>
          </div>
        ) : sales.length === 0 ? (
          <div className="empty-state">
            {totalGlobalElements === 0
              ? <p>No se encontraron ventas</p>
              : <p>Ninguna venta coincide con los criterios de búsqueda</p>
            }
          </div>
        ) : (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID de Venta</th>
                  <th>Fecha</th>
                  <th>Hora</th>
                  <th>Vendedor</th>
                  <th>Total</th>
                  <th className="text-right">Acciones</th>
                </tr>
              </thead>
              <tbody>
                {sales.map((sale, index) => (
                  <tr
                    key={sale.saleId}
                    id={`sale-row-${index}`}
                    onClick={() => handleRowClick(sale.saleId)}
                    style={{ cursor: 'pointer' }}
                    className={`interactive-row ${focusedIndex === index ? 'focused' : ''}`}
                  >
                    <td className="font-mono text-sm">{sale.saleId}</td>
                    <td className="font-medium">{formatDate(sale.saleDate)}</td>
                    <td>{formatTime(sale.saleTime)}</td>
                    <td>{sale.userName || 'Desconocido'}</td>
                    <td className="total-amount-cell">${sale.totalAmount?.toFixed(2) ?? '0.00'}</td>
                    <td className="actions-cell text-right" onClick={(e) => e.stopPropagation()}>
                      <button
                        className="action-btn print-btn"
                        title="Imprimir Ticket"
                        onClick={(e) => {
                          e.stopPropagation();
                          setSelectedSaleId(sale.saleId);
                          setShowPrintModal(true);
                        }}
                      >
                        <Printer size={16} />
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
            itemName="ventas"
          />
        )}
      </div>

      <ConfirmModal
        isOpen={showPrintModal}
        title="Imprimir Ticket"
        message="¿Deseas imprimir el ticket?"
        onConfirm={async () => {
          setShowPrintModal(false);
          if (!selectedSaleId) return;

          setActionLoading(true);
          try {
            await printTicket(selectedSaleId);
          } catch (err) {
            if (err.status === 400 || err.status === 404) {
              addToast(err.message || `No se encontró la venta con ID '${selectedSaleId}'`, 'error');
              // Refresh the list to remove the missing sale
              setRefreshTrigger(prev => prev + 1);
            } else {
              addToast(err.message || "No se pudo imprimir el ticket", "error");
            }
          } finally {
            setActionLoading(false);
            setSelectedSaleId(null);
          }
        }}
        onCancel={() => {
          setShowPrintModal(false);
          setSelectedSaleId(null);
        }}
        confirmText="Sí, Imprimir"
        cancelText="No"
        confirmButtonTheme="success"
      />
    </div>
  );
};

export default SalesView;
