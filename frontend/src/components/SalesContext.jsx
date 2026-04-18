import React, { createContext, useContext, useState, useRef } from 'react';
import { Outlet } from 'react-router-dom';

const SalesContext = createContext(null);

export const useSalesContext = () => {
  const context = useContext(SalesContext);
  if (!context) {
    throw new Error('useSalesContext must be used within a SalesProvider');
  }
  return context;
};

export const SalesLayout = () => {
  const getTodayFormatted = () => {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  // State
  const [dateFilter, setDateFilter] = useState(getTodayFormatted());
  const [sortOrder, setSortOrder] = useState('DESCENDING');
  const [pageFrontend, setPageFrontend] = useState(1);
  const [isCached, setIsCached] = useState(false);
  
  // Data State cache
  const [salesData, setSalesData] = useState([]);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  
  // Scroll Ref
  const scrollPositionRef = useRef(0);

  return (
    <SalesContext.Provider value={{
      dateFilter, setDateFilter,
      sortOrder, setSortOrder,
      pageFrontend, setPageFrontend,
      salesData, setSalesData,
      totalPages, setTotalPages,
      totalElements, setTotalElements,
      scrollPositionRef,
      isCached, setIsCached,
      getTodayFormatted
    }}>
      <Outlet />
    </SalesContext.Provider>
  );
};
