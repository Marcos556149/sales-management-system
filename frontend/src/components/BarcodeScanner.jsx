import React, { useRef, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useToast } from './ToastContext';

const BarcodeScanner = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { addToast } = useToast();
  
  const bufferRef = useRef('');
  const timeoutRef = useRef(null);

  // Exact match required: active ONLY on the general products view
  const isProductsGeneralView = location.pathname === '/dashboard/products';

  useEffect(() => {
    if (!isProductsGeneralView) {
      bufferRef.current = '';
      return;
    }

    const handleKeyDown = (e) => {
      // Ignore keystrokes if the user has an input, textarea or anything editable focused
      if (
        e.target.tagName === 'INPUT' || 
        e.target.tagName === 'TEXTAREA' || 
        e.target.isContentEditable
      ) {
        return;
      }

      if (e.key === 'Enter') {
        e.preventDefault();
        const code = bufferRef.current.trim();
        if (code) {
          processBarcode(code);
        }
        bufferRef.current = '';
        if (timeoutRef.current) clearTimeout(timeoutRef.current);
        return;
      }

      // Only buffer alphanumeric keys and hyphens (this matches the regex logic)
      if (/^[a-zA-Z0-9-]$/.test(e.key)) {
        bufferRef.current += e.key;

        // Scanners press keys extremely fast (usually < 50ms interval)
        // If there's a pause of > 150ms, assume it was accidental typing and reset
        if (timeoutRef.current) clearTimeout(timeoutRef.current);
        timeoutRef.current = setTimeout(() => {
          bufferRef.current = '';
        }, 150);
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    
    return () => {
      window.removeEventListener('keydown', handleKeyDown);
      if (timeoutRef.current) clearTimeout(timeoutRef.current);
    };
    // processBarcode is hoisted basically or bound to latest closure, but safe to omit from deps
    // to avoid remounting keydown listener every render, since addToast/navigate are stable.
  }, [isProductsGeneralView, navigate, addToast]);

  const validateCode = (code) => {
    // Alphanumeric and hyphens, strictly 8 to 30 characters
    const regex = /^[a-zA-Z0-9-]{8,30}$/;
    return regex.test(code);
  };

  const processBarcode = async (code) => {
    if (!validateCode(code)) {
      addToast('Barcode not recognized, please try again', 'error');
      return; 
    }

    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 5000);

    try {
      const response = await fetch(`/api/products/${code}`, { signal: controller.signal });
      clearTimeout(timeoutId);
      
      if (response.ok) {
        // Product exists
        navigate(`/dashboard/products/${code}`);
      } else {
        const errorData = await response.json().catch(() => ({}));
        
        if (response.status === 404 || response.status === 400) {
           navigate(`/dashboard/products/new?productCode=${code}`);
           // Removed the warning toast as requested
        } else {
           addToast(errorData.error || errorData.message || 'Error searching for product', 'error');
        }
      }
    } catch (error) {
      clearTimeout(timeoutId);
      const msg = error.name === 'AbortError' ? 'Product search timed out' : 'Network error while searching for product';
      addToast(msg, 'error');
    }
  };

  // Render absolutely nothing. It operates purely as an invisible background listener.
  return null;
};

export default BarcodeScanner;
