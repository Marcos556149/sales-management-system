import React, { useState, useEffect, useRef } from 'react';
import {
  Package,
  TrendingUp,
  Loader2,
  DollarSign,
  ShoppingBag,
  ListOrdered,
  AlertCircle
} from 'lucide-react';
import { apiClient } from '../api/client';
import Pagination from './Pagination';
import './SoldProductsRankingSection.css';
import { useToast } from './ToastContext';

const SoldProductsRankingSection = ({ userId, startDate, endDate }) => {
  const { addToast } = useToast();

  // Dynamic filter options state
  const [filterOptions, setFilterOptions] = useState(null);
  const [loadingFilters, setLoadingFilters] = useState(true);

  // Filters & pagination state
  const [metric, setMetric] = useState('REVENUE_GENERATED');
  const [order, setOrder] = useState('MOST_TO_LEAST');
  const [page, setPage] = useState(1); // Frontend page: 1-indexed
  const size = 20;

  // Data & loading state
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const abortControllerRef = useRef(null);

  // 1. Fetch Dynamic Filters on Mount
  useEffect(() => {
    const fetchFilters = async () => {
      try {
        const response = await apiClient.get('/api/statistics/filters/product-ranking');
        setFilterOptions(response.data);
      } catch (err) {
        console.error("Error fetching ranking filters:", err);
        addToast("No se pudieron cargar las opciones de filtro de ranking", "error");
      } finally {
        setLoadingFilters(false);
      }
    };
    fetchFilters();
  }, [addToast]);

  // Helper: Format Date to YYYY-MM-DD
  const formatDateStr = (date) => {
    if (!date) return '';
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const d = String(date.getDate()).padStart(2, '0');
    return `${y}-${m}-${d}`;
  };

  // 2. Fetch Paginated Sold Products when filters, page, or props change
  useEffect(() => {
    // Prevent fetching if dates are not defined yet
    if (!startDate || !endDate) return;

    const fetchSoldProducts = async () => {
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }

      const controller = new AbortController();
      abortControllerRef.current = controller;

      setLoading(true);
      setError(null);

      try {
        const params = {
          startDate: formatDateStr(startDate),
          endDate: formatDateStr(endDate),
          metric,
          order,
          page: page - 1, // backend is 0-based
          size
        };

        if (userId && userId !== 'ALL') {
          params.userId = userId;
        }

        const response = await apiClient.get('/api/statistics/products/sold', {
          params,
          signal: controller.signal
        });

        if (controller === abortControllerRef.current) {
          setData(response.data);
        }
      } catch (err) {
        if (err.name === 'AbortError') return;

        console.error("Error fetching sold products ranking:", err);
        if (controller === abortControllerRef.current) {
          setError(err.message || "No se pudo cargar el ranking de productos vendidos");
          addToast(err.message || "Error al obtener los productos vendidos", "error");
        }
      } finally {
        if (controller === abortControllerRef.current) {
          setLoading(false);
        }
      }
    };

    fetchSoldProducts();

    return () => {
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }
    };
  }, [userId, startDate, endDate, metric, order, page, addToast]);

  // Reset page when filters change
  const handleMetricChange = (e) => {
    setMetric(e.target.value);
    setPage(1);
  };

  const handleOrderChange = (e) => {
    setOrder(e.target.value);
    setPage(1);
  };

  // Render Skeletons for Loading State
  const renderSkeletons = () => {
    return (
      <div className="ranking-table-wrapper">
        <table className="ranking-table skeleton">
          <thead>
            <tr>
              <th>Código de producto</th>
              <th>Nombre del producto</th>
              <th>Cantidad vendida</th>
              <th>Ingresos generados</th>
            </tr>
          </thead>
          <tbody>
            {[...Array(5)].map((_, idx) => (
              <tr key={idx} className="skeleton-row">
                <td><div className="skeleton-bar short" /></td>
                <td><div className="skeleton-bar medium" /></td>
                <td><div className="skeleton-bar short" /></td>
                <td><div className="skeleton-bar short" /></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    );
  };

  const hasData = data && data.content && data.content.length > 0;

  return (
    <div className="sold-products-ranking-card">
      <header className="ranking-header">
        <div className="ranking-title">
          <div className="title-icon-wrapper ranking">
            <ListOrdered size={20} />
          </div>
          <div>
            <h2>Ranking de productos vendidos</h2>
            <p>Lista paginada de productos vendidos basada en los métricas seleccionadas</p>
          </div>
        </div>

        {/* Filters Grid */}
        <div className="ranking-filters">
          <div className="ranking-filter-item">
            <label className="ranking-filter-label">Métrica de ranking</label>
            <select
              value={metric}
              onChange={handleMetricChange}
              className="ranking-select"
              disabled={loadingFilters || !filterOptions}
            >
              {loadingFilters ? (
                <option>Cargando métricas...</option>
              ) : (
                filterOptions?.metricOptions.map(opt => (
                  <option key={opt.code} value={opt.code}>{opt.label}</option>
                ))
              )}
            </select>
          </div>

          <div className="ranking-filter-item">
            <label className="ranking-filter-label">Orden</label>
            <select
              value={order}
              onChange={handleOrderChange}
              className="ranking-select"
              disabled={loadingFilters || !filterOptions}
            >
              {loadingFilters ? (
                <option>Cargando orden...</option>
              ) : (
                filterOptions?.orderOptions.map(opt => (
                  <option key={opt.code} value={opt.code}>{opt.label}</option>
                ))
              )}
            </select>
          </div>
        </div>
      </header>

      {/* Content Area */}
      <div className="ranking-content">
        {loading ? (
          renderSkeletons()
        ) : error ? (
          <div className="ranking-error-state">
            <AlertCircle size={40} className="error-icon" />
            <h3>Error al cargar los datos</h3>
            <p>{error}</p>
          </div>
        ) : !hasData ? (
          <div className="ranking-empty-state">
            <Package size={44} className="empty-icon" />
            <h3>No se encontraron productos</h3>
            <p>No se encontraron productos vendidos con los filtros seleccionados</p>
          </div>
        ) : (
          <>
            <div className="ranking-table-wrapper">
              <table className="ranking-table">
                <thead>
                  <tr>
                    <th>Código de producto</th>
                    <th>Nombre del producto</th>
                    <th>Cantidad vendida</th>
                    <th>Ingresos generados</th>
                  </tr>
                </thead>
                <tbody>
                  {data.content.map((item) => (
                    <tr key={item.productCode} className="ranking-row">
                      <td className="font-mono text-sm code-cell">{item.productCode}</td>
                      <td className="font-medium name-cell">{item.productName}</td>
                      <td className="quantity-cell">
                        <span className="ranking-cell-content qty">
                          <ShoppingBag size={14} className="cell-icon" />
                          <span>{item.quantitySold}</span>
                        </span>
                      </td>
                      <td className="revenue-cell">
                        <span className="ranking-cell-content rev">
                          <DollarSign size={14} className="cell-icon" />
                          <span>${item.revenueGenerated?.toFixed(2) ?? '0.00'}</span>
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Pagination Footer */}
            <div className="ranking-pagination-wrapper">
              <Pagination
                currentPage={page}
                totalPages={data.totalPages || 1}
                totalElements={data.totalElements || 0}
                onPageChange={setPage}
                itemName="productos"
                showShortcuts={false}
              />
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default SoldProductsRankingSection;
