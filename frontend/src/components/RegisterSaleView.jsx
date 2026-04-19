import React, { useState, useEffect, useRef, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, X, Plus, Trash2, ShoppingCart, Check, AlertCircle, ArrowLeft, ArrowRight } from 'lucide-react';
import { useToast } from './ToastContext';
import './RegisterSaleView.css';

// --- MODAL COMPONENT ---
const AddProductModal = ({ isOpen, product, onClose, onAdd }) => {
  const [qty, setQty] = useState(1);
  const [error, setError] = useState('');
  const inputRef = useRef(null);

  useEffect(() => {
    if (isOpen) {
      setQty(1);
      setError('');
      setTimeout(() => {
        if (inputRef.current) {
          inputRef.current.focus();
          inputRef.current.select();
        }
      }, 100);
    }
  }, [isOpen]);

  if (!isOpen || !product) return null;

  const handleAdd = () => {
    const numQty = Number(qty);
    if (isNaN(numQty) || numQty <= 0) {
      setError('Quantity must be greater than 0');
      return;
    }
    
    // Check if whole number required
    const isUnits = product.unitOfMeasure?.code === 'UNITS';
    if (isUnits && !Number.isInteger(numQty)) {
       setError(`The product ${product.productName} only accepts whole numbers because it is sold by unit.`);
       return;
    }

    // Checking against local stock initially, but we must also consider cart later.
    // The parent will check total cart stock.
    onAdd(product, numQty);
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      handleAdd();
    } else if (e.key === 'Escape') {
      onClose();
    }
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
              type="number" 
              className="pos-modal-qty"
              value={qty}
              min="0"
              step={product.unitOfMeasure?.code === 'UNITS' ? "1" : "0.01"}
              onChange={(e) => {
                setQty(e.target.value);
                setError('');
              }}
              onKeyDown={handleKeyDown}
              ref={inputRef}
            />
            {error && <span style={{ color: '#ef4444', fontSize: '0.85rem', textAlign: 'center', marginTop: '4px' }}>{error}</span>}
          </div>
        </div>
        <div className="pos-modal-footer">
          <button className="pos-btn-cancel" onClick={onClose}>Cancel</button>
          <button className="pos-btn-add" onClick={handleAdd}>
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
  const { addToast } = useToast();

  // --- Search Catalog State ---
  const [searchTerm, setSearchTerm] = useState('');
  const [appliedSearch, setAppliedSearch] = useState('');
  const [products, setProducts] = useState([]);
  const [loadingProducts, setLoadingProducts] = useState(false);
  
  // Pagination State
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  // --- Cart State ---
  const [cartItems, setCartItems] = useState([]); // { product: {...}, quantity: 1, subtotal: X }
  const [isSubmitting, setIsSubmitting] = useState(false);

  // --- Modal State ---
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  
  const abortControllerRef = useRef(null);

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
      params.append('statusFilter', 'ACTIVE'); // ONLY ACTIVE
      params.append('nameSort', 'ASCENDING');
      params.append('page', page - 1);
      params.append('size', 10); // Show fewer per page for POS

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
         setProducts(data.content || (Array.isArray(data) ? data : []));
         setTotalPages(data.totalPages || 1);
      }
    } catch (err) {
      clearTimeout(timeoutId);
      if (err.name !== 'AbortError' && abortControllerRef.current === abortController) {
        // Just empty results if search fails or 404
        if (err.message && err.message.toLowerCase().includes('no products')) {
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
  }, [appliedSearch, page, addToast]);

  useEffect(() => {
    fetchProducts();
    return () => {
      if (abortControllerRef.current) abortControllerRef.current.abort();
    };
  }, [fetchProducts]);

  // Cart Functions
  const handleOpenAddModal = (product) => {
    if (product.productStock <= 0) {
      addToast(`Product ${product.productName} is out of stock`, "error");
      return;
    }
    setSelectedProduct(product);
    setIsModalOpen(true);
  };

  const handleAddToCart = (product, quantityStr) => {
    const quantity = Number(quantityStr);
    
    setCartItems(prev => {
      const existingItemIndex = prev.findIndex(item => item.product.productCode === product.productCode);
      let newCart = [...prev];
      
      let cartQuantityToVerify = quantity;
      
      if (existingItemIndex >= 0) {
        cartQuantityToVerify += prev[existingItemIndex].quantity;
      }

      // Check Stock
      if (cartQuantityToVerify > product.productStock) {
        addToast(`Cannot add. Stock available is ${product.productStock}`, "error");
        return prev;
      }

      if (existingItemIndex >= 0) {
        // Update existing row
        newCart[existingItemIndex] = {
          ...newCart[existingItemIndex],
          quantity: cartQuantityToVerify,
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

    setIsModalOpen(false);
    setSelectedProduct(null);
  };

  const handleUpdateCartItemQty = (index, valueStr) => {
    if (valueStr === '') {
       // Temporarily allow empty string while user types
       const newCart = [...cartItems];
       newCart[index].quantity = '';
       newCart[index].subtotal = 0;
       setCartItems(newCart);
       return;
    }

    const value = Number(valueStr);
    const item = cartItems[index];

    // UX Error Checks manually
    const isUnits = item.product.unitOfMeasure?.code === 'UNITS';
    if (isUnits && !Number.isInteger(value)) {
       addToast(`The product ${item.product.productName} only accepts whole numbers because it is sold by unit.`, "error");
       return;
    }

    if (value < 0) return; // Prevent negatives completely

    if (value > item.product.productStock) {
       addToast(`Quantity cannot exceed available stock (${item.product.productStock})`, "error");
       // Keep original
       return;
    }

    const newCart = [...cartItems];
    newCart[index].quantity = value;
    newCart[index].subtotal = value * item.product.productPrice;
    setCartItems(newCart);
  };
  
  const handleVerifyQtyBlur = (index) => {
      const item = cartItems[index];
      if (item.quantity === '' || Number(item.quantity) === 0) {
          // If they leave it empty or 0, we can remove the item or reset it to 1.
          // Professional POS usually asks to confirm delete or resets. Let's just remove if 0/empty.
          handleRemoveFromCart(index);
      }
  };

  const handleRemoveFromCart = (index) => {
    setCartItems(prev => prev.filter((_, i) => i !== index));
  };

  const calculateTotal = () => {
    return cartItems.reduce((acc, item) => acc + (Number(item.subtotal) || 0), 0);
  };

  const handleConfirmSale = async () => {
    if (cartItems.length === 0) {
      addToast("Sale must contain at least one product", "error");
      return;
    }

    // Additional safeguard: verify no empty quantities
    const invalidItems = cartItems.filter(item => item.quantity === '' || Number(item.quantity) <= 0);
    if (invalidItems.length > 0) {
       addToast("All products must have a quantity greater than 0", "error");
       return;
    }

    setIsSubmitting(true);
    const payload = {
      saleDetails: cartItems.map(item => ({
         productCode: item.product.productCode,
         productQuantity: Number(item.quantity)
      }))
    };

    try {
      const response = await fetch('/api/sales', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        let errorMessage = errorData.error || errorData.message;
        if (!errorMessage && Object.keys(errorData).length > 0) {
          const firstKey = Object.keys(errorData)[0];
          if (typeof errorData[firstKey] === 'string') {
            errorMessage = errorData[firstKey];
          }
        }
        throw new Error(errorMessage || "Failed to create sale");
      }

      addToast("Sale registered successfully", "success");
      setCartItems([]);
      // Maybe navigate to sales list
      navigate('/dashboard/sales');

    } catch (err) {
       console.error("Sale submission error:", err);
       // Handle Backend exceptions like InvalidSaleDataException, ProductNotFoundException
       addToast(err.message, "error");
    } finally {
       setIsSubmitting(false);
    }
  };

  return (
    <div className="pos-container">
      {/* LEFT: PRODUCTS CATALOG */}
      <div className="pos-catalog-panel">
        <div className="pos-catalog-header">
           <h2>Products</h2>
           <div className="pos-search-wrapper">
              <Search className="pos-search-icon" size={18} />
              <input 
                type="text" 
                placeholder="Scan barcode or search name..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
              {searchTerm && (
                <button 
                  className="clear-search-btn"
                  onClick={() => { setSearchTerm(''); setAppliedSearch(''); setPage(1); }}
                  style={{ position: 'absolute', right: '10px', top: '50%', transform: 'translateY(-50%)', background:'none', border:'none', cursor:'pointer' }}
                >
                  <X size={16} />
                </button>
              )}
           </div>
        </div>

        <div className="pos-catalog-content">
           {loadingProducts ? (
              <div style={{ padding: '20px', textAlign: 'center', color: '#6b7280' }}>Loading products...</div>
           ) : products.length === 0 ? (
              <div style={{ padding: '40px 20px', textAlign: 'center', color: '#6b7280' }}>
                 No active products found
              </div>
           ) : (
             <table className="pos-product-list">
               <thead>
                 <tr>
                   <th>Code</th>
                   <th>Product</th>
                   <th>Price</th>
                   <th>Stock</th>
                 </tr>
               </thead>
               <tbody>
                  {products.map(p => (
                    <tr 
                      key={p.productCode} 
                      className="pos-product-row"
                      onClick={() => handleOpenAddModal(p)}
                    >
                       <td className="font-mono text-sm" style={{ color: '#6b7280' }}>{p.productCode}</td>
                       <td className="pos-product-name">{p.productName}</td>
                       <td style={{ fontWeight: 500, color: '#059669' }}>${p.productPrice?.toFixed(2)}</td>
                       <td>
                          {p.productStock <= 0 ? (
                             <span style={{ color: '#ef4444', fontSize: '0.85rem', fontWeight: 600 }}>Out of Stock</span>
                          ) : (
                             <span>{p.productStock} <span style={{ fontSize: '0.8rem', color: '#9ca3af' }}>{p.unitOfMeasure?.code === 'UNITS' ? 'u' : (p.unitOfMeasure?.label || '')}</span></span>
                          )}
                       </td>
                    </tr>
                  ))}
               </tbody>
             </table>
           )}
        </div>

        {totalPages > 1 && (
           <div className="pos-pagination">
             <button 
               className="pos-page-btn" 
               disabled={page === 1}
               onClick={() => setPage(p => p - 1)}
             >
               <ArrowLeft size={16} /> Prev
             </button>
             <span style={{ fontSize: '0.9rem', color: '#4b5563' }}>Page {page} of {totalPages}</span>
             <button 
               className="pos-page-btn" 
               disabled={page === totalPages}
               onClick={() => setPage(p => p + 1)}
             >
               Next <ArrowRight size={16} />
             </button>
           </div>
        )}
      </div>

      {/* RIGHT: SALE CART */}
      <div className="pos-cart-panel">
         <div className="pos-cart-header">
            <ShoppingCart size={24} color="#374151" />
            <h2>Current Sale</h2>
         </div>

         <div className="pos-cart-items">
            {cartItems.length === 0 ? (
               <div className="pos-empty-cart">
                  <ShoppingCart size={48} opacity={0.2} />
                  <p>Cart is empty</p>
                  <span style={{ fontSize: '0.85rem' }}>Select products from the catalog to add them here</span>
               </div>
            ) : (
               cartItems.map((item, index) => (
                  <div className="pos-cart-item" key={item.product.productCode}>
                     <div className="pos-item-details">
                        <div className="pos-item-name">{item.product.productName}</div>
                        <div className="pos-item-price-info">
                           ${item.product.productPrice.toFixed(2)} / {item.product.unitOfMeasure?.code === 'UNITS' ? 'u' : item.product.unitOfMeasure?.label}
                        </div>
                        <div style={{ fontSize: '0.75rem', color: '#9ca3af', marginTop: '2px' }}>
                           Code: {item.product.productCode}
                        </div>
                     </div>
                     <div className="pos-item-actions">
                        <input 
                           type="number"
                           className="pos-qty-input"
                           value={item.quantity}
                           min="0"
                           step={item.product.unitOfMeasure?.code === 'UNITS' ? "1" : "0.01"}
                           onChange={(e) => handleUpdateCartItemQty(index, e.target.value)}
                           onBlur={() => handleVerifyQtyBlur(index)}
                        />
                        <div className="pos-item-subtotal">
                           ${Number(item.subtotal || 0).toFixed(2)}
                        </div>
                        <button 
                           className="pos-remove-btn"
                           onClick={() => handleRemoveFromCart(index)}
                           title="Remove Item"
                        >
                           <Trash2 size={16} />
                        </button>
                     </div>
                  </div>
               ))
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
                  </>
               )}
            </button>
         </div>
      </div>

      <AddProductModal 
         isOpen={isModalOpen}
         product={selectedProduct}
         onClose={() => { setIsModalOpen(false); setSelectedProduct(null); }}
         onAdd={handleAddToCart}
      />
    </div>
  );
};

export default RegisterSaleView;
