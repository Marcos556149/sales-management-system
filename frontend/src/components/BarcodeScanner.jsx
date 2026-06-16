import React, { useRef, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useToast } from './ToastContext';
import { productService } from '../services/productService';
import { isAdmin as checkIsAdmin } from '../utils/authUtils';


const BarcodeScanner = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { addToast } = useToast();

  const bufferRef = useRef('');
  const timeoutRef = useRef(null);

  useEffect(() => {
    const handleKeyDown = (e) => {
      // Global scanner navigation ONLY allowed in Products or Sales catalog screens
      const allowedPaths = ['/dashboard/products', '/dashboard/sales'];
      if (
        e.target.tagName === 'INPUT' ||
        e.target.tagName === 'TEXTAREA' ||
        e.target.isContentEditable ||
        !allowedPaths.includes(location.pathname)
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
  }, [navigate, addToast, location.pathname]);

  // Clear buffer on location change to prevent garbage from previous screens
  useEffect(() => {
    bufferRef.current = '';
    if (timeoutRef.current) clearTimeout(timeoutRef.current);
  }, [location.pathname]);

  const validateCode = (code) => {
    // Alphanumeric and hyphens, strictly 8 to 30 characters
    const regex = /^[a-zA-Z0-9-]{8,30}$/;
    return regex.test(code);
  };

  const processBarcode = async (code) => {
    if (!validateCode(code)) {
      // Only show error if it looks like a failed scan (more than 2 chars)
      // Small buffers are usually accidental keystrokes or shortcuts
      if (code.length > 2) {
        addToast('Código de barras no reconocido, inténtelo nuevamente.', 'error');
      }
      return;
    }

    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 5000);

    try {
      // Use the service for consistency and to get the data in one go
      const response = await productService.getProduct(code, { signal: controller.signal });
      clearTimeout(timeoutId);

      // Handle navigation based on context
      if (location.pathname === '/dashboard/sales') {
        // From Sales: go to Register Sale and open add modal
        navigate('/dashboard/sales/new', { state: { initialProduct: response.data } });
      } else {
        // From Products: go to Product Detail
        navigate(`/dashboard/products/${code}`, { state: { product: response.data } });
      }

    } catch (err) {
      clearTimeout(timeoutId);

      if (err.name === 'AbortError') {
        addToast('La búsqueda del producto excedió el tiempo de espera.', 'error');
      } else if (err.status === 404 || err.status === 400) {
        if (location.pathname === '/dashboard/sales') {
          // From Sales: just show error toast, don't navigate to register product
          addToast(err.message || 'Producto no encontrado en el sistema', 'error');
        } else {
          // From Products: navigate to create ONLY if admin
          if (checkIsAdmin()) {
            navigate(`/dashboard/products/new?productCode=${code}`);
          } else {
            addToast(err.message || `No se encontró el producto con código '${code}'`, 'error');
          }
        }
      } else {
        // Network or other error
        addToast(err.message || 'Error al buscar el producto', 'error');
      }
    }
  };

  // Render absolutely nothing. It operates purely as an invisible background listener.
  return null;
};

export default BarcodeScanner;
