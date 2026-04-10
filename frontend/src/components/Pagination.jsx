import React from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';

const Pagination = ({ 
  currentPage, 
  totalPages, 
  totalElements, 
  onPageChange,
  itemName = "items"
}) => {
  return (
    <div className="pagination-bar">
      <span className="pagination-info">
        Page {currentPage} of {totalPages} ({totalElements} {itemName})
      </span>
      <div className="pagination-controls">
        <button 
          className="pagination-btn" 
          onClick={() => onPageChange(Math.max(1, currentPage - 1))}
          disabled={currentPage === 1}
        >
          <ChevronLeft size={16} />
          Previous
        </button>
        <button 
          className="pagination-btn" 
          onClick={() => onPageChange(Math.min(totalPages, currentPage + 1))}
          disabled={currentPage >= totalPages || totalPages === 0}
        >
          Next
          <ChevronRight size={16} />
        </button>
      </div>
    </div>
  );
};

export default Pagination;
