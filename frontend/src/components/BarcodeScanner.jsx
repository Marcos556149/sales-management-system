import React, { useRef, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useToast } from './ToastContext';
import { productService } from '../services/productService';


const BarcodeScanner = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { addToast } = useToast();
  
  const bufferRef = useRef('');
  const timeoutRef = useRef(null);

  useEffect(() => {
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
        const code = bufferRef.current.trim();
        if (code) {
          e.preventDefault();
          e.stopImmediatePropagation(); // Neutralize Enter for modals

          // Only process the code if NO blocking modal is open
          if (!document.body.classList.contains('modal-open-blocking')) {
            processBarcode(code);
          }
        }
        bufferRef.current = '';
        if (timeoutRef.current) clearTimeout(timeoutRef.current);
        return;
      }

      // Only buffer alphanumeric keys and hyphens
      if (/^[a-zA-Z0-9-]$/.test(e.key)) {
        bufferRef.current += e.key;

        if (timeoutRef.current) clearTimeout(timeoutRef.current);
        timeoutRef.current = setTimeout(() => {
          bufferRef.current = '';
        }, 150);
      }
    };

    window.addEventListener('keydown', handleKeyDown, true); // Use capture phase for global priority
    
    return () => {
      window.removeEventListener('keydown', handleKeyDown, true);
      if (timeoutRef.current) clearTimeout(timeoutRef.current);
    };
  }, [navigate, addToast]);

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
      // Use the service for consistency and to get the data in one go
      const response = await productService.getProduct(code, { signal: controller.signal });
      clearTimeout(timeoutId);
      
      // If we are here, the product exists (the service throws for non-2xx)
      // Pass the product in the state so ProductDetailView doesn't have to fetch again
      navigate(`/dashboard/products/${code}`, { state: { product: response.data } });
      
    } catch (err) {
      clearTimeout(timeoutId);
      
      if (err.name === 'AbortError') {
        addToast('Product search timed out', 'error');
      } else if (err.status === 404 || err.status === 400) {
        // Product doesn't exist, navigate to create
        navigate(`/dashboard/products/new?productCode=${code}`);
      } else {
        // Network or other error
        addToast(err.message || 'Error searching for product', 'error');
      }
    }
  };

  // Render absolutely nothing. It operates purely as an invisible background listener.
  return null;
};

export default BarcodeScanner;
