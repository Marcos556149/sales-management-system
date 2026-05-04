import React, { useState, useEffect, useRef, useCallback } from 'react';
import { useNavigate, useLocation, useBlocker } from 'react-router-dom';
import { Search, X, Plus, Trash2, ShoppingCart, Check, AlertCircle, ArrowLeft, ArrowRight, RefreshCw, AlertTriangle, Info } from 'lucide-react';
import { useToast } from './ToastContext';
import { apiClient } from '../api/client';
import Pagination from './Pagination';
import { useKeyboardShortcuts } from '../hooks/useKeyboardShortcuts';
import { useSalesContext } from './SalesContext';
import ConfirmModal from './ConfirmModal';
import { printTicket } from '../utils/printUtils';
import './RegisterSaleView.css';

const restrictNumericInput = (e) => {
  if (e.key === ',' || e.key === '-') {
    e.preventDefault();
    return;
  }
  if ([8, 46, 9, 27, 13, 110, 190].includes(e.keyCode) ||
    (e.keyCode === 65 && (e.ctrlKey === true || e.metaKey === true)) ||
    (e.keyCode >= 35 && e.keyCode <= 40)) {
    return;
  }
  if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
    e.preventDefault();
  }
};

const validateQty = (qtyStr, product) => {
  const raw = String(qtyStr).trim();
  if (raw === '') {
    return "Product quantity is required";
  }

  const numRegex = /^-?\d+(\.\d{1,2})?$/;
  if (!numRegex.test(raw)) {
    return "Product quantity must be a valid number with up to 2 decimals (use . as separator)";
  }

  const num = Number(raw);
  if (num <= 0) {
    return "Product quantity must be greater than 0";
  }

  const parts = raw.split('.');
  if (parts[0].length > 10) {
    return "Product quantity must have up to 10 digits and 2 decimals";
  }

  if (product.unitOfMeasure?.code === 'UNITS' && !Number.isInteger(num)) {
    const productLabel = `${product.productCode} - ${product.productName}`;
    return `Product '${productLabel}' only accepts whole numbers because it is sold by units`;
  }

  return '';
};

// --- MODAL COMPONENT ---
const AddProductModal = ({ isOpen, product, onClose, onAdd }) => {
  const [qty, setQty] = useState('1');
  const [error, setError] = useState('');
  const inputRef = useRef(null);

  useEffect(() => {
    if (isOpen) {
      document.body.classList.add('modal-open-blocking');
      setQty('1');
      setError('');
      setTimeout(() => {
        if (inputRef.current) {
          inputRef.current.focus();
          inputRef.current.select();
        }
      }, 100);
    } else {
      document.body.classList.remove('modal-open-blocking');
    }
    return () => {
      document.body.classList.remove('modal-open-blocking');
    };
  }, [isOpen]);

  useEffect(() => {
    if (!isOpen) return;

    const handleGlobalKeyDown = (e) => {
      // Escape closes the modal
      if (e.key === 'Escape') {
        e.preventDefault();
        e.stopPropagation();
        onClose();
      }
      // Enter confirms adding the product (if not already handled by input)
      // Note: input's onKeyDown handles it too, but this is a backup
      else if (e.key === 'Enter' && document.activeElement?.tagName !== 'INPUT') {
        e.preventDefault();
        e.stopPropagation();
        handleAdd();
      }
    };

    window.addEventListener('keydown', handleGlobalKeyDown, true);
    return () => window.removeEventListener('keydown', handleGlobalKeyDown, true);
  }, [isOpen, onClose]);

  if (!isOpen || !product) return null;

  const handleAdd = () => {
    const errorMsg = validateQty(qty, product);
    if (errorMsg) {
      setError(errorMsg);
      return;
    }

    onAdd(product, Number(qty));
  };

  const handleChange = (e) => {
    const value = e.target.value;
    if (value.includes(',') || value.includes('-')) return;
    const parts = value.split('.');
    if (parts.length > 2) return; // Prevent more than one decimal point
    if (parts[0].length > 10) return;
    if (parts.length > 1 && parts[1].length > 2) return;
    setQty(value);
    setError('');
  };

  const handleKeyDownNumeric = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      e.stopPropagation();
      handleAdd();
      return;
    } else if (e.key === 'Escape') {
      e.preventDefault();
      e.stopPropagation();
      onClose();
      return;
    }

    restrictNumericInput(e);
  };

  return (
    <div className="pos-modal-overlay" onClick={onClose}>
      <div className="pos-modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="pos-modal-header">
          <h3>Add Product to Sale</h3>
          <button className="pos-modal-close" onClick={onClose}><X size={20} /></button>
        </div>
        <div className="pos-modal-body">
          <div className="pos-modal-row">
            <span className="pos-modal-label">Code:</span>
            <span className="pos-modal-value font-mono">{product.productCode}</span>
          </div>
          <div className="pos-modal-row">
            <span className="pos-modal-label">Name:</span>
            <span className="pos-modal-value">{product.productName}</span>
          </div>
          <div className="pos-modal-row">
            <span className="pos-modal-label">Price:</span>
            <span className="pos-modal-value">${product.productPrice?.toFixed(2) ?? '0.00'}</span>
          </div>
          <div className="pos-modal-row">
            <span className="pos-modal-label">Available Stock:</span>
            <span className="pos-modal-value">
              {product.productStock} {product.unitOfMeasure?.code === 'UNITS' ? 'u' : (product.unitOfMeasure?.label || '')}
            </span>
          </div>

          <div className="pos-modal-group">
            <label>Quantity</label>
            <input
              type="text"
              className={`pos-modal-qty ${error ? 'error' : ''}`}
              value={qty}
              onChange={handleChange}
              onKeyDown={handleKeyDownNumeric}
              ref={inputRef}
              placeholder="0.01"
              style={error ? { borderColor: '#ef4444', boxShadow: '0 0 0 3px rgba(239, 68, 68, 0.2)' } : {}}
            />
            <div style={{ minHeight: '36px', marginTop: '4px', textAlign: 'center', width: '100%', display: 'flex', alignItems: 'flex-start', justifyContent: 'center', wordBreak: 'break-word' }}>
              <span style={{ color: '#ef4444', fontSize: '0.85rem', lineHeight: '1.2' }}>{error || '\u00A0'}</span>
            </div>
          </div>
        </div>
        <div className="pos-modal-footer">
          <button className="pos-btn-cancel" onClick={onClose}>Cancel</button>
          <button className="pos-btn-add" onClick={(e) => { e.stopPropagation(); handleAdd(); }}>
            <Plus size={18} />
            Add to Cart
          </button>
        </div>
      </div>
    </div>
  );
};

// --- MAIN VIEW COMPONENT ---
const RegisterSaleView = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { addToast } = useToast();
  const { setIsCached } = useSalesContext();

  // --- Search Catalog State ---
  const [searchTerm, setSearchTerm] = useState('');
  const [appliedSearch, setAppliedSearch] = useState('');
  const [products, setProducts] = useState([]);
  const [loadingProducts, setLoadingProducts] = useState(false);

  // Pagination State
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [totalGlobalElements, setTotalGlobalElements] = useState(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  // --- Cart State ---
  const [cartItems, setCartItems] = useState([]); // { product: {...}, quantity: 1, subtotal: X }
  const [isSubmitting, setIsSubmitting] = useState(false);
  const isSubmittingRef = useRef(false);

  // Navigation Guard: Block if cart is not empty
  const blocker = useBlocker(
    ({ nextLocation }) =>
      cartItems.length > 0 && !isSubmittingRef.current && nextLocation.pathname !== location.pathname
  );

  // --- Modal State ---
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [showPrintModal, setShowPrintModal] = useState(false);
  const [lastSaleResponse, setLastSaleResponse] = useState(null);
  const [focusedIndex, setFocusedIndex] = useState(-1);
  const [activeSection, setActiveSection] = useState('catalog'); // 'catalog' or 'cart'
  const [focusedCartIndex, setFocusedCartIndex] = useState(-1);

  const abortControllerRef = useRef(null);
  const searchInputRef = useRef(null);
  const cartInputRefs = useRef([]);
  const cartItemsContainerRef = useRef(null);
  const scannerBufferRef = useRef('');
  const scannerTimeoutRef = useRef(null);

  // --- Barcode Scanner Logic ---
  useEffect(() => {
    const handleKeyDown = (e) => {
      // Ignore if user is typing in an input field
      if (
        e.target.tagName === 'INPUT' ||
        e.target.tagName === 'TEXTAREA' ||
        e.target.isContentEditable
      ) {
        return;
      }

      if (e.key === 'Enter') {
        const code = scannerBufferRef.current.trim();
        if (code) {
          e.preventDefault();
          e.stopImmediatePropagation(); // Neutralize Enter for modals and other listeners

          // Only process the code if NO blocking modal is open
          if (!document.body.classList.contains('modal-open-blocking')) {
            processScannedCode(code);
          }
        }
        scannerBufferRef.current = '';
        if (scannerTimeoutRef.current) clearTimeout(scannerTimeoutRef.current);
        return;
      }

      if (/^[a-zA-Z0-9-]$/.test(e.key)) {
        scannerBufferRef.current += e.key;
        if (scannerTimeoutRef.current) clearTimeout(scannerTimeoutRef.current);
        scannerTimeoutRef.current = setTimeout(() => {
          scannerBufferRef.current = '';
        }, 150);
      }
    };

    window.addEventListener('keydown', handleKeyDown, true); // Use capture phase
    return () => {
      window.removeEventListener('keydown', handleKeyDown, true);
      if (scannerTimeoutRef.current) clearTimeout(scannerTimeoutRef.current);
    };
  }, [products]); // Re-run if products change just in case, but actually dependencies should be empty if processScannedCode is stable

  const processScannedCode = async (code) => {
    const regex = /^[a-zA-Z0-9-]{8,30}$/;
    if (!regex.test(code)) {
      addToast('Barcode not recognized, please try again', 'error');
      return;
    }

    try {
      const res = await apiClient.get(`/api/products/${code}`);
      if (res.data) {
        handleOpenAddModal(res.data);
      }
    } catch (err) {
      if (err.status === 404) {
        addToast("Product not found in system", "error");
      } else {
        addToast(err.message || "Error searching product", "error");
      }
    }
  };

  // 1. Debounce Search
  useEffect(() => {
    // If it's only spaces (but not empty string), ignore it
    if (searchTerm.length > 0 && searchTerm.trim().length === 0) {
      return;
    }

    const handler = setTimeout(() => {
      setAppliedSearch((prev) => {
        if (prev !== searchTerm) {
          setPage(1); // reset page on new search
          return searchTerm;
        }
        return prev;
      });
    }, 400);

    return () => clearTimeout(handler);
  }, [searchTerm]);

  // 2. Fetch Active Products
  const fetchProducts = useCallback(async () => {
    if (abortControllerRef.current) {
      abortControllerRef.current.abort();
    }
    const abortController = new AbortController();
    abortControllerRef.current = abortController;

    const timeoutId = setTimeout(() => abortController.abort(), 8000);
    setLoadingProducts(true);

    try {
      const params = new URLSearchParams();
      const trimmedSearch = appliedSearch.trim();
      if (trimmedSearch.length > 0) {
        params.append('searchCodeOrName', trimmedSearch);
      }
      params.append('nameSort', 'ASCENDING');
      params.append('page', page - 1);
      params.append('size', 10); // Show fewer per page for POS

      const res = await apiClient.get(`/api/products/sales?${params.toString()}`, {
        signal: abortController.signal
      });
      clearTimeout(timeoutId);

      const data = res.data;
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
      }
    } catch (err) {
      clearTimeout(timeoutId);
      if (err.name !== 'AbortError' && abortControllerRef.current === abortController) {
        if (err.status === 400 || err.status === 404) {
          setProducts([]);
          setTotalPages(1);
        } else {
          addToast("Could not load products for POS", "error");
        }
      }
    } finally {
      if (abortControllerRef.current === abortController) {
        setLoadingProducts(false);
      }
    }
  }, [appliedSearch, page, refreshTrigger, addToast]);

  useEffect(() => {
    const mountDebounceReq = setTimeout(() => {
      fetchProducts();
    }, 15);
    return () => {
      clearTimeout(mountDebounceReq);
      if (abortControllerRef.current) abortControllerRef.current.abort();
    };
  }, [fetchProducts]);

  // Reset focus when products change
  useEffect(() => {
    setFocusedIndex(-1);
  }, [products]);

  // Scroll focused row into view
  useEffect(() => {
    if (activeSection === 'catalog' && focusedIndex >= 0) {
      const row = document.getElementById(`pos-row-${focusedIndex}`);
      if (row) {
        row.scrollIntoView({ block: 'nearest' });
      }
    }
  }, [focusedIndex, activeSection]);

  // Handle Cart Item Focus
  useEffect(() => {
    if (activeSection === 'cart' && focusedCartIndex >= 0 && cartInputRefs.current[focusedCartIndex]) {
      const input = cartInputRefs.current[focusedCartIndex];
      input.focus();
      input.select();

      // Ensure the cart item row is visible
      const cartRow = input.closest('.pos-cart-row');
      if (cartRow) {
        cartRow.scrollIntoView({ block: 'nearest', behavior: 'smooth' });
      }
    }
  }, [focusedCartIndex, activeSection]);

  // Keyboard Shortcuts for Pagination
  useKeyboardShortcuts(React.useMemo(() => ({
    'arrowright': () => {
      if (!isModalOpen && activeSection === 'catalog' && page < totalPages) {
        setPage(prev => prev + 1);
      }
    },
    'arrowleft': () => {
      if (!isModalOpen && activeSection === 'catalog' && page > 1) {
        setPage(prev => prev - 1);
      }
    },
    'ctrl+arrowright': () => {
      if (isModalOpen) return;
      if (cartItems.length > 0) {
        setActiveSection('cart');
        setFocusedCartIndex(0);
        setFocusedIndex(-1);
      }
    },
    'ctrl+arrowleft': () => {
      if (isModalOpen) return;
      if (document.activeElement && document.activeElement.tagName === 'INPUT') {
        document.activeElement.blur();
      }
      setActiveSection('catalog');
      setFocusedCartIndex(-1);
      setFocusedIndex(0);
    },
    'delete': () => {
      if (isModalOpen) return;
      if (activeSection === 'cart' && focusedCartIndex >= 0) {
        handleRemoveFromCart(focusedCartIndex);
        setFocusedCartIndex(-1);
      }
    },
    'arrowdown': () => {
      if (isModalOpen) return;
      if (activeSection === 'catalog' && products.length > 0) {
        setFocusedIndex(prev => Math.min(prev + 1, products.length - 1));
      } else if (activeSection === 'cart' && cartItems.length > 0) {
        setFocusedCartIndex(prev => Math.min(prev + 1, cartItems.length - 1));
      }
    },
    'arrowup': () => {
      if (isModalOpen) return;
      if (activeSection === 'catalog' && products.length > 0) {
        setFocusedIndex(prev => Math.max(prev - 1, 0));
      } else if (activeSection === 'cart' && cartItems.length > 0) {
        setFocusedCartIndex(prev => Math.max(prev - 1, 0));
      }
    },
    'enter': () => {
      if (isModalOpen) return;
      if (activeSection === 'catalog') {
        if (document.activeElement && document.activeElement.tagName === 'BUTTON') return;
        if (focusedIndex >= 0 && focusedIndex < products.length) {
          handleOpenAddModal(products[focusedIndex]);
        }
      }
    },
    '/': () => {
      if (!isModalOpen) {
        setActiveSection('catalog');
        searchInputRef.current?.focus();
      }
    },
    'ctrl+shift+k': () => {
      if (!isModalOpen) {
        setPage(1);
        setRefreshTrigger(prev => prev + 1);
      }
    },
    'ctrl+b': () => {
      if (!isModalOpen) navigate('/dashboard/sales');
    },
    'f9': () => {
      if (!isModalOpen) handleConfirmSale();
    }
  }), [navigate, page, totalPages, isModalOpen, products, focusedIndex, activeSection, cartItems.length, focusedCartIndex]));
  // Cart Functions
  const lastCloseTimeRef = useRef(0);

  const handleOpenAddModal = useCallback((product) => {
    if (Date.now() - lastCloseTimeRef.current < 100) return;

    if (product.productStock <= 0) {
      addToast(`Product ${product.productName} is out of stock`, "error");
      return;
    }
    setSelectedProduct(product);
    setIsModalOpen(true);
  }, [addToast]);

  const handleCloseAddModal = useCallback(() => {
    setIsModalOpen(false);
    setSelectedProduct(null);
    lastCloseTimeRef.current = Date.now();
  }, []);

  // Handle initial product from state (e.g., scanned from Sales catalog)
  useEffect(() => {
    if (location.state?.initialProduct) {
      handleOpenAddModal(location.state.initialProduct);
      // Clear state to avoid reopening on refresh
      window.history.replaceState({}, document.title);
    }
  }, [location.state, handleOpenAddModal]);

  const handleSearchKeyDown = (e) => {
    if (e.key === 'ArrowDown') {
      e.preventDefault();
      setFocusedIndex(prev => Math.min(prev + 1, products.length - 1));
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      setFocusedIndex(prev => Math.max(prev - 1, 0));
    } else if (e.key === 'Enter') {
      e.preventDefault();
      if (focusedIndex >= 0 && focusedIndex < products.length) {
        handleOpenAddModal(products[focusedIndex]);
      } else if (searchTerm !== appliedSearch) {
        setAppliedSearch(searchTerm);
        setPage(1);
      } else if (products.length === 1) {
        handleOpenAddModal(products[0]);
      }
    } else if (e.key === 'ArrowRight' && e.ctrlKey && cartItems.length > 0) {
      e.preventDefault();
      setActiveSection('cart');
      setFocusedCartIndex(0);
      setFocusedIndex(-1);
    }
  };

  const handleAddToCart = (product, quantityStr) => {
    const quantity = Number(quantityStr);

    setCartItems(prev => {
      const existingItemIndex = prev.findIndex(item => item.product.productCode === product.productCode);
      let newCart = [...prev];

      let cartQuantityToVerify = quantity;

      if (existingItemIndex >= 0) {
        // Ensure we add numbers, not strings
        const existingQty = Number(prev[existingItemIndex].quantity) || 0;
        cartQuantityToVerify = Math.round((quantity + existingQty) * 100) / 100;
      }

      // Allow adding beyond local stock; backend will ultimately validate.

      if (existingItemIndex >= 0) {
        // Update existing row
        newCart[existingItemIndex] = {
          ...newCart[existingItemIndex],
          quantity: String(cartQuantityToVerify),
          subtotal: cartQuantityToVerify * product.productPrice
        };
      } else {
        // Add new row
        newCart.push({
          product,
          quantity,
          subtotal: quantity * product.productPrice
        });
      }

      return newCart;
    });

    handleCloseAddModal();
  };

  const handleUpdateCartItemQty = (index, valueStr) => {
    if (valueStr.includes(',') || valueStr.includes('-')) return;
    const parts = valueStr.split('.');
    if (parts.length > 2) return; // Prevent more than one decimal point
    if (parts[0].length > 10) return;
    if (parts.length > 1 && parts[1].length > 2) return;

    const newCart = [...cartItems];
    const item = newCart[index];

    if (valueStr === '') {
      item.quantity = '';
      item.subtotal = 0;
      item.error = '';
      setCartItems(newCart);
      return;
    }

    const value = Number(valueStr);

    item.quantity = valueStr;
    item.subtotal = isNaN(value) ? 0 : value * item.product.productPrice;
    item.error = '';

    setCartItems(newCart);
  };

  const handleVerifyQtyBlur = (index) => {
    const item = cartItems[index];
    if (item.quantity === '' || Number(item.quantity) <= 0) {
      // If they leave it empty or 0 (or negative, though restricted), remove the item.
      handleRemoveFromCart(index);
    }
  };

  const handleRemoveFromCart = (index) => {
    setCartItems(prev => prev.filter((_, i) => i !== index));
  };

  const calculateTotal = () => {
    return cartItems.reduce((acc, item) => acc + (Number(item.subtotal) || 0), 0);
  };

  const finishSaleFlow = (response) => {
    addToast(response?.message || "Sale registered successfully", "success");
    isSubmittingRef.current = true;
    setIsCached(false); // Force sales list refresh
    setCartItems([]);
    navigate('/dashboard/sales');
  };

  const handleConfirmSale = async () => {
    if (cartItems.length === 0) {
      addToast("Sale must contain at least one product", "error");
      return;
    }

    let hasErrors = false;
    const newCart = [...cartItems];

    newCart.forEach((item, index) => {
      const errorMsg = validateQty(item.quantity, item.product);
      if (errorMsg) {
        newCart[index].error = errorMsg;
        hasErrors = true;
      } else {
        newCart[index].error = '';
      }
    });

    if (hasErrors) {
      setCartItems(newCart);
      return;
    }

    setIsSubmitting(true);
    const payload = {
      saleDetails: cartItems.map(item => ({
        productCode: item.product.productCode,
        productQuantity: Number(item.quantity)
      }))
    };

    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 10000);

    try {
      const res = await apiClient.post('/api/sales', payload, { signal: controller.signal });
      clearTimeout(timeoutId);

      setLastSaleResponse(res);
      setShowPrintModal(true);

    } catch (err) {
      clearTimeout(timeoutId);
      console.error("Sale submission error:", err);

      if (err.name === 'AbortError') {
        addToast('Request timed out. Please check your connection.', 'error');
      } else {
        const data = err.details || {};
        const errorData = data.error || {};

        // Backend standardized error format handling
        if (err.status === 400 || err.status === 404) {
          if (errorData.field) {
            addToast(errorData.message, 'error');
          } else if (typeof data === 'object' && !errorData.message && Object.keys(data).length > 0 && !data.error && !data.code && !data.message) {
            // Fallback for map of errors if any
            Object.values(data).forEach(msg => {
              if (typeof msg === 'string') addToast(msg, 'error');
            });
          } else {
            const msg = errorData.message || data.message || err.message || 'Invalid data provided';
            addToast(msg, 'error');
          }
        } else {
          const msg = err.message || 'An error occurred while registering the sale';
          addToast(msg, 'error');
        }
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="view-container">


      <div className="pos-container">
        {/* LEFT: PRODUCTS CATALOG */}
        <div className="pos-catalog-panel">
          <div className="pos-catalog-header-section" style={{ display: 'flex', flexDirection: 'column', flexShrink: 0 }}>
            <div style={{ marginBottom: '16px', marginTop: '4px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '12px' }}>
              <button className="btn-secondary" onClick={() => navigate('/dashboard/sales')}>
                <ArrowLeft size={16} />
                <span>Back to Sales</span>
                <span className="btn-shortcut">Ctrl+B</span>
              </button>
              <button
                className="btn-secondary"
                onClick={(e) => {
                  e.stopPropagation();
                  if (cartItems.length > 0) {
                    setActiveSection('cart');
                    setFocusedCartIndex(0);
                    setFocusedIndex(-1);
                  } else {
                    addToast("Add products to the cart first", "error");
                  }
                }}
              >
                <span style={{ fontSize: '0.95rem' }}>Go to Cart</span>
                <span className="btn-shortcut" style={{ marginLeft: '8px' }}>Ctrl + →</span>
              </button>
            </div>
            <div className="pos-catalog-header" onClick={() => { setActiveSection('catalog'); setFocusedCartIndex(-1); }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <h2>Products</h2>
                <div className="tooltip-container" tabIndex="0">
                  <Info size={18} className="info-icon" style={{ cursor: 'help' }} />
                  <span className="tooltip-text">
                    Inactive products or products out of stock are not shown in the list.
                  </span>
                </div>
              </div>
              <div className="pos-toolbar-right">
                <div className="pos-search-wrapper">
                  <Search className="pos-search-icon" size={18} />
                  <input
                    ref={searchInputRef}
                    type="text"
                    placeholder="Search by name or code..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    onKeyDown={handleSearchKeyDown}
                  />
                  <div className="pos-search-actions">
                    {searchTerm && (
                      <button
                        className="clear-search-btn"
                        title="Clear Search"
                        onClick={() => { setSearchTerm(''); setAppliedSearch(''); setPage(1); }}
                      >
                        <X size={16} />
                      </button>
                    )}
                    <span className="search-hint">/</span>
                  </div>
                </div>

                <button
                  className="btn-secondary"
                  onClick={() => { setPage(1); setRefreshTrigger(prev => prev + 1); }}
                  disabled={loadingProducts}
                >
                  <RefreshCw size={18} className={loadingProducts ? "spin-animation" : ""} />
                  <span>{loadingProducts ? 'Refreshing...' : 'Refresh'}</span>
                  <span className="btn-shortcut">Ctrl+Shift+K</span>
                </button>
              </div>
            </div>
          </div>

          <div className="pos-catalog-content" onClick={() => { setActiveSection('catalog'); setFocusedCartIndex(-1); }}>
            {loadingProducts ? (
              <div style={{ padding: '20px', textAlign: 'center', color: '#6b7280' }}>Loading products...</div>
            ) : products.length === 0 ? (
              <div style={{ padding: '40px 20px', textAlign: 'center', color: '#6b7280' }}>
                {totalGlobalElements === 0
                  ? "No active products with available stock found"
                  : "No products match the search criteria"
                }
              </div>
            ) : (
              <table className="pos-product-list">
                <thead>
                  <tr>
                    <th style={{ width: '15%' }}>Code</th>
                    <th style={{ width: '45%' }}>Product</th>
                    <th style={{ width: '25%' }}>Price</th>
                    <th style={{ width: '15%' }}>Stock</th>
                  </tr>
                </thead>
                <tbody>
                  {products.map((p, index) => (
                    <tr
                      key={p.productCode}
                      id={`pos-row-${index}`}
                      className={`pos-product-row ${focusedIndex === index ? 'focused' : ''}`}
                      onClick={() => handleOpenAddModal(p)}
                    >
                      <td className="font-mono text-sm" style={{ color: '#6b7280' }}>{p.productCode}</td>
                      <td className="pos-product-name">{p.productName}</td>
                      <td style={{ fontWeight: 500, color: '#059669' }}>${p.productPrice?.toFixed(2)}</td>
                      <td style={{ whiteSpace: 'nowrap' }}>
                        {(() => {
                          const stock = p.productStock ?? 0;
                          const min = p.minimumStock ?? 0;
                          if (stock <= min) {
                            return (
                              <span style={{ color: 'var(--danger-color, #ef4444)', fontWeight: 'bold' }}>
                                {stock} <span style={{ fontSize: '0.8rem', color: 'var(--danger-color, #ef4444)' }}>{p.unitOfMeasure?.code === 'UNITS' ? 'u' : (p.unitOfMeasure?.label || '')}</span>
                              </span>
                            );
                          }
                          return (
                            <span>{stock} <span style={{ fontSize: '0.8rem', color: '#9ca3af' }}>{p.unitOfMeasure?.code === 'UNITS' ? 'u' : (p.unitOfMeasure?.label || '')}</span></span>
                          );
                        })()}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>

          {!loadingProducts && totalPages > 0 && (
            <div style={{ padding: '0 20px 20px' }}>
              <Pagination
                currentPage={page}
                totalPages={totalPages}
                totalElements={totalElements}
                onPageChange={setPage}
                itemName="products"
              />
            </div>
          )}
        </div>

        {/* RIGHT: SALE CART */}
        <div className="pos-cart-panel" onClick={() => {
          setActiveSection('cart');
          setFocusedIndex(-1);
          if (cartItems.length > 0 && focusedCartIndex === -1) setFocusedCartIndex(0);
        }}>
          {/* Cart Header aligned with Catalog Header Top */}
          <div style={{
            height: '40px',
            marginBottom: '16px',
            marginTop: '4px',
            display: 'flex',
            alignItems: 'center',
            gap: '16px',
            borderBottom: '1px solid #e5e7eb',
            paddingBottom: '12px',
            boxSizing: 'content-box'
          }}>
            <button
              className="btn-secondary"
              onClick={(e) => {
                e.stopPropagation();
                setActiveSection('catalog');
                setFocusedCartIndex(-1);
                setFocusedIndex(0);
              }}
            >
              <span style={{ fontSize: '0.95rem' }}>Catalog</span>
              <span className="btn-shortcut" style={{ marginLeft: '8px' }}>Ctrl + ←</span>
            </button>
            <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginLeft: '4px' }}>
              <ShoppingCart size={24} color="#374151" />
              <h2 style={{ margin: 0, fontSize: '1.5rem', fontWeight: 700, color: '#111827' }}>Current Sale</h2>
            </div>
          </div>

          {/* Original Header space if needed, but we used the top space for alignment */}
          <div className="pos-cart-items" ref={cartItemsContainerRef}>
            {cartItems.length === 0 ? (
              <div className="pos-empty-cart">
                <ShoppingCart size={48} opacity={0.2} />
                <p>Cart is empty</p>
                <span style={{ fontSize: '0.85rem' }}>Select products from the catalog to add them here</span>
              </div>
            ) : (
              <table className="pos-cart-table">
                <thead>
                  <tr>
                    <th>Product</th>
                    <th style={{ width: '80px', textAlign: 'center' }}>Quantity</th>
                    <th style={{ width: '110px', textAlign: 'left' }}>Price</th>
                    <th style={{ width: '110px', textAlign: 'left' }}>Subtotal</th>
                    <th style={{ width: '40px' }}></th>
                  </tr>
                </thead>
                <tbody>
                  {cartItems.map((item, index) => (
                    <tr
                      key={item.product.productCode}
                      className={`pos-cart-row ${activeSection === 'cart' && focusedCartIndex === index ? 'focused' : ''}`}
                      onClick={() => {
                        setActiveSection('cart');
                        setFocusedCartIndex(index);
                        setFocusedIndex(-1);
                      }}
                    >
                      <td>
                        <div className="pos-cart-product-info">
                          <div className="pos-cart-product-name" title={item.product.productName}>
                            {item.product.productName}
                          </div>
                          <div className="pos-cart-product-code">{item.product.productCode}</div>
                        </div>
                      </td>
                      <td>
                        <div className="pos-cart-qty-wrapper">
                          <input
                            ref={el => cartInputRefs.current[index] = el}
                            type="text"
                            className={`pos-cart-qty-input ${item.error ? 'error' : ''}`}
                            value={item.quantity}
                            onFocus={() => {
                              setActiveSection('cart');
                              setFocusedCartIndex(index);
                              setFocusedIndex(-1);
                            }}
                            onChange={(e) => handleUpdateCartItemQty(index, e.target.value)}
                            onBlur={() => handleVerifyQtyBlur(index)}
                            onKeyDown={(e) => {
                              if (e.key === 'Enter') {
                                e.preventDefault();
                                e.target.blur();
                                return;
                              }
                              if (e.key === 'ArrowDown') {
                                e.preventDefault();
                                setFocusedCartIndex(prev => Math.min(prev + 1, cartItems.length - 1));
                                return;
                              }
                              if (e.key === 'ArrowUp') {
                                e.preventDefault();
                                setFocusedCartIndex(prev => Math.max(prev - 1, 0));
                                return;
                              }
                              if (e.key === 'ArrowLeft' && e.ctrlKey) {
                                e.preventDefault();
                                e.target.blur();
                                setActiveSection('catalog');
                                setFocusedCartIndex(-1);
                                setFocusedIndex(0);
                                return;
                              }
                              if (e.key === 'Delete') {
                                e.preventDefault();
                                handleRemoveFromCart(index);
                                setFocusedCartIndex(-1);
                                return;
                              }
                              restrictNumericInput(e);
                            }}
                          />
                          {item.error && (
                            <div className="pos-cart-error-tooltip">
                              {item.error}
                            </div>
                          )}
                        </div>
                      </td>
                      <td style={{ textAlign: 'left', fontSize: '0.9rem', color: '#6b7280' }}>
                        ${item.product.productPrice.toFixed(2)} / {item.product.unitOfMeasure?.code === 'UNITS' ? 'u' : item.product.unitOfMeasure?.label}
                      </td>
                      <td style={{ textAlign: 'left', fontWeight: 600, color: '#111827' }}>
                        ${Number(item.subtotal || 0).toFixed(2)}
                      </td>
                      <td style={{ textAlign: 'center' }}>
                        <button
                          className="pos-cart-del-btn"
                          onClick={(e) => {
                            e.stopPropagation();
                            handleRemoveFromCart(index);
                            if (focusedCartIndex === index) setFocusedCartIndex(-1);
                          }}
                          title="Remove Item"
                        >
                          <Trash2 size={16} />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>

          <div className="pos-cart-footer">
            <div className="pos-cart-summary">
              <span className="pos-summary-label">Total</span>
              <span className="pos-summary-value">${calculateTotal().toFixed(2)}</span>
            </div>
            <button
              className="pos-confirm-btn"
              disabled={cartItems.length === 0 || isSubmitting}
              onClick={handleConfirmSale}
            >
              {isSubmitting ? 'Processing...' : (
                <>
                  <Check size={20} />
                  Confirm Sale
                  <span className="btn-shortcut" style={{ marginLeft: 'auto', backgroundColor: 'rgba(255, 255, 255, 0.2)', color: 'white', borderColor: 'rgba(255, 255, 255, 0.3)' }}>F9</span>
                </>
              )}
            </button>
          </div>
        </div>

        <AddProductModal
          isOpen={isModalOpen}
          product={selectedProduct}
          onClose={handleCloseAddModal}
          onAdd={handleAddToCart}
        />
        <ConfirmModal
          isOpen={blocker.state === "blocked"}
          title="Unsaved Sale"
          message="You have products in your cart. Are you sure you want to leave? The cart will be cleared."
          onConfirm={() => blocker.proceed()}
          onCancel={() => blocker.reset()}
          confirmText="Leave"
          confirmButtonTheme="danger"
        />
        <ConfirmModal
          isOpen={showPrintModal}
          title="Print Receipt"
          message="Sale registered successfully. Do you want to print the receipt?"
          onConfirm={async () => {
            setShowPrintModal(false);
            try {
              // The sale ID is now returned in the 'data' field of the response
              const saleId = lastSaleResponse?.data;
              if (saleId) {
                await printTicket(saleId);
              } else {
                console.warn("No sale ID found in response, skipping print");
              }
            } catch (err) {
              addToast(err.message || "Could not print ticket, but sale was registered", "error");
            } finally {
              finishSaleFlow(lastSaleResponse);
            }
          }}
          onCancel={() => {
            setShowPrintModal(false);
            finishSaleFlow(lastSaleResponse);
          }}
          confirmText="Yes, Print"
          cancelText="No"
          confirmButtonTheme="success"
        />
      </div>
    </div>
  );
};

export default RegisterSaleView;
