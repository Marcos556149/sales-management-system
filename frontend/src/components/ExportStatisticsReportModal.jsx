import React, { useState, useEffect } from 'react';
import { FileText, X, FileDown, Loader2 } from 'lucide-react';
import { useToast } from './ToastContext';
import { apiClient } from '../api/client';
import './ExportStatisticsReportModal.css';

const ExportStatisticsReportModal = ({ isOpen, onClose, filters }) => {
  const { addToast } = useToast();

  // Form State
  const [includeSales, setIncludeSales] = useState(true);
  const [includeProduct, setIncludeProduct] = useState(true);
  const [metric, setMetric] = useState('REVENUE_GENERATED');
  const [order, setOrder] = useState('MOST_TO_LEAST');
  const [soldLimit, setSoldLimit] = useState(20);
  const [unsoldLimit, setUnsoldLimit] = useState(20);

  // Dynamic filter options state
  const [filterOptions, setFilterOptions] = useState(null);
  const [loadingFilters, setLoadingFilters] = useState(false);
  const [exporting, setExporting] = useState(false);

  // Fetch Dynamic Filters when Modal opens
  useEffect(() => {
    if (isOpen) {
      const fetchFilters = async () => {
        setLoadingFilters(true);
        try {
          const response = await apiClient.get('/api/statistics/filters/product-ranking');
          setFilterOptions(response.data);
          
          // Set initial values if options are available
          if (response.data?.metricOptions?.length > 0) {
            setMetric(response.data.metricOptions[0].code);
          }
          if (response.data?.orderOptions?.length > 0) {
            setOrder(response.data.orderOptions[0].code);
          }
        } catch (err) {
          console.error("Error fetching ranking filters:", err);
          addToast("Could not load ranking filter options", "error");
        } finally {
          setLoadingFilters(false);
        }
      };
      
      fetchFilters();
    } else {
      // Reset state when closing
      setIncludeSales(true);
      setIncludeProduct(true);
      setMetric('REVENUE_GENERATED');
      setOrder('MOST_TO_LEAST');
      setSoldLimit(20);
      setUnsoldLimit(20);
    }
  }, [isOpen, addToast]);

  const handleExport = async (e) => {
    if (e) e.preventDefault();

    if (!includeSales && !includeProduct) {
      addToast("At least one report section must be selected", "error");
      return;
    }

    setExporting(true);
    try {
      const payload = {
        userId: filters?.userId ?? null,
        startDate: filters?.startDate ?? '',
        endDate: filters?.endDate ?? '',
        includeSalesInformation: includeSales,
        includeProductInformation: includeProduct,
        soldProductsLimit: Number(soldLimit),
        unsoldProductsLimit: Number(unsoldLimit),
        metric: metric,
        order: order
      };

      const response = await apiClient.post('/api/statistics/report/pdf', payload);
      const blob = response.data;

      // Extract filename from response headers or use default
      let filename = 'sales-statistics-report.pdf';
      const contentDisposition = response.headers?.get('content-disposition');
      if (contentDisposition) {
        const filenameMatch = contentDisposition.match(/filename=(.+)/);
        if (filenameMatch && filenameMatch[1]) {
          filename = filenameMatch[1].replace(/['"]/g, '');
        }
      }

      // Download file programmatically
      const blobUrl = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = blobUrl;
      link.download = filename;
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(blobUrl);

      addToast("Report exported successfully", "success");
      onClose();
    } catch (err) {
      console.error("Error exporting statistics PDF:", err);
      const msg = err.message;
      if (
        msg === "No data available for the selected criteria" || 
        msg === "At least one report section must be selected"
      ) {
        addToast(msg, "error");
      } else {
        addToast("An error occurred while generating the PDF report", "error");
      }
    } finally {
      setExporting(false);
    }
  };

  // Keyboard shortcuts inside the modal
  useEffect(() => {
    if (!isOpen) return;

    const handleKeyDown = (e) => {
      e.stopPropagation();

      if (e.key === 'Escape') {
        e.preventDefault();
        if (!exporting) onClose();
      } else if (e.key === 'Enter' && (e.ctrlKey || e.metaKey)) {
        e.preventDefault();
        if (!exporting && !loadingFilters) handleExport();
      }
    };

    window.addEventListener('keydown', handleKeyDown, true);
    return () => {
      window.removeEventListener('keydown', handleKeyDown, true);
    };
  }, [isOpen, exporting, loadingFilters, includeSales, includeProduct, metric, order, soldLimit, unsoldLimit, filters]);

  if (!isOpen) return null;

  return (
    <div className="pdf-modal-overlay">
      <div className="pdf-modal-content">
        <div className="pdf-modal-header">
          <h3 className="pdf-modal-title">
            <FileText size={20} />
            Export Statistics Report
          </h3>
          <button 
            type="button"
            className="pdf-modal-close-btn" 
            onClick={onClose}
            disabled={exporting}
            aria-label="Close"
          >
            <X size={20} />
          </button>
        </div>

        {loadingFilters ? (
          <div className="pdf-modal-loading">
            <Loader2 size={32} className="pdf-spin-animation" />
            <p>Loading configurations...</p>
          </div>
        ) : (
          <form onSubmit={handleExport} noValidate>
            <div className="pdf-modal-body">
              
              {/* SECTION 1: Included Sections */}
              <div className="pdf-form-section">
                <h4 className="pdf-section-title">Included Sections</h4>
                <div className="pdf-checkbox-group">
                  <label className="pdf-checkbox-label">
                    <input 
                      type="checkbox"
                      id="includeSalesInformation"
                      checked={includeSales}
                      onChange={(e) => setIncludeSales(e.target.checked)}
                      disabled={exporting}
                    />
                    <span>Sales Information</span>
                  </label>
                  <label className="pdf-checkbox-label">
                    <input 
                      type="checkbox"
                      id="includeProductInformation"
                      checked={includeProduct}
                      onChange={(e) => setIncludeProduct(e.target.checked)}
                      disabled={exporting}
                    />
                    <span>Product Information</span>
                  </label>
                </div>
              </div>

              {/* SECTION 2: Product Ranking Configuration */}
              <div className={`pdf-form-section ${!includeProduct ? 'disabled' : ''}`}>
                <h4 className="pdf-section-title">Product Ranking Configuration</h4>
                <div className="pdf-form-grid">
                  <div className="pdf-form-group">
                    <label htmlFor="metric">Ranking Metric</label>
                    <select
                      id="metric"
                      value={metric}
                      onChange={(e) => setMetric(e.target.value)}
                      disabled={exporting || !filterOptions || !includeProduct}
                      className="pdf-select"
                    >
                      {filterOptions?.metricOptions?.map(opt => (
                        <option key={opt.code} value={opt.code}>{opt.label}</option>
                      )) || <option value="REVENUE_GENERATED">Revenue Generated</option>}
                    </select>
                  </div>

                  <div className="pdf-form-group">
                    <label htmlFor="order">Ranking Order</label>
                    <select
                      id="order"
                      value={order}
                      onChange={(e) => setOrder(e.target.value)}
                      disabled={exporting || !filterOptions || !includeProduct}
                      className="pdf-select"
                    >
                      {filterOptions?.orderOptions?.map(opt => (
                        <option key={opt.code} value={opt.code}>{opt.label}</option>
                      )) || <option value="MOST_TO_LEAST">Most sold → least sold</option>}
                    </select>
                  </div>
                </div>
              </div>

              {/* SECTION 3: Product Limits */}
              <div className={`pdf-form-section ${!includeProduct ? 'disabled' : ''}`}>
                <h4 className="pdf-section-title">Product Limits</h4>
                <div className="pdf-form-grid">
                  <div className="pdf-form-group">
                    <label htmlFor="soldProductsLimit">Sold Products Limit</label>
                    <select
                      id="soldProductsLimit"
                      value={soldLimit}
                      onChange={(e) => setSoldLimit(Number(e.target.value))}
                      disabled={exporting || !includeProduct}
                      className="pdf-select"
                    >
                      <option value={10}>10</option>
                      <option value={20}>20</option>
                      <option value={50}>50</option>
                      <option value={100}>100</option>
                    </select>
                  </div>

                  <div className="pdf-form-group">
                    <label htmlFor="unsoldProductsLimit">Unsold Products Limit</label>
                    <select
                      id="unsoldProductsLimit"
                      value={unsoldLimit}
                      onChange={(e) => setUnsoldLimit(Number(e.target.value))}
                      disabled={exporting || !includeProduct}
                      className="pdf-select"
                    >
                      <option value={10}>10</option>
                      <option value={20}>20</option>
                      <option value={50}>50</option>
                      <option value={100}>100</option>
                    </select>
                  </div>
                </div>
              </div>

            </div>

            <div className="pdf-modal-footer">
              <button 
                type="button" 
                className="pdf-btn-secondary" 
                onClick={onClose}
                disabled={exporting}
              >
                Cancel
              </button>
              <button 
                type="submit" 
                className="pdf-btn-primary" 
                disabled={exporting}
              >
                {exporting ? (
                  <>
                    <Loader2 size={16} className="pdf-spin-animation" style={{ marginRight: '8px' }} />
                    Generating PDF...
                  </>
                ) : (
                  <>
                    <FileDown size={16} style={{ marginRight: '8px' }} />
                    Export PDF
                  </>
                )}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};

export default ExportStatisticsReportModal;
