import React from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';

const Pagination = ({ 
  currentPage, 
  totalPages, 
  totalElements, 
  onPageChange,
  itemName = "items",
  showShortcuts = true
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
          {showShortcuts && <span className="btn-shortcut">←</span>}
          <span>Previous</span>
        </button>
        <button 
          className="pagination-btn" 
          onClick={() => onPageChange(Math.min(totalPages, currentPage + 1))}
          disabled={currentPage >= totalPages || totalPages === 0}
        >
          <span>Next</span>
          {showShortcuts && <span className="btn-shortcut">→</span>}
          <ChevronRight size={16} />
        </button>
      </div>
    </div>
  );
};

export default Pagination;
