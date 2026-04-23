import React, { createContext, useContext, useState, useRef } from 'react';
import { Outlet } from 'react-router-dom';

const ProductsContext = createContext(null);

export const useProductsContext = () => {
  const context = useContext(ProductsContext);
  if (!context) {
    throw new Error('useProductsContext must be used within a ProductsProvider');
  }
  return context;
};

export const ProductsLayout = () => {
  // State
  const [searchTerm, setSearchTerm] = useState('');
  const [appliedSearch, setAppliedSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [stockLevelFilter, setStockLevelFilter] = useState('ALL');
  const [sortOrder, setSortOrder] = useState('ASCENDING');
  const [pageFrontend, setPageFrontend] = useState(1);
  const [isCached, setIsCached] = useState(false);
  
  // Data State cache
  const [productsData, setProductsData] = useState([]);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [totalGlobalElements, setTotalGlobalElements] = useState(null);
  
  // Filters Cache (Optional, to prevent refetching during navigation)
  const [statusOptions, setStatusOptions] = useState([]);
  const [stockLevelOptions, setStockLevelOptions] = useState([]);
  const [sortOptions, setSortOptions] = useState([]);

  // Scroll Ref
  const scrollPositionRef = useRef(0);

  return (
    <ProductsContext.Provider value={{
      searchTerm, setSearchTerm,
      appliedSearch, setAppliedSearch,
      statusFilter, setStatusFilter,
      stockLevelFilter, setStockLevelFilter,
      sortOrder, setSortOrder,
      pageFrontend, setPageFrontend,
      productsData, setProductsData,
      totalPages, setTotalPages,
      totalElements, setTotalElements,
      totalGlobalElements, setTotalGlobalElements,
      statusOptions, setStatusOptions,
      stockLevelOptions, setStockLevelOptions,
      sortOptions, setSortOptions,
      scrollPositionRef,
      isCached, setIsCached
    }}>
      <Outlet />
    </ProductsContext.Provider>
  );
};
