import React, { useMemo } from 'react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer
} from 'recharts';
import { ShoppingBag, DollarSign, Package } from 'lucide-react';
import './TopProductsSection.css';

/**
 * Custom Tooltip for Top Products Chart
 */
const CustomProductTooltip = ({ active, payload, isRevenue }) => {
  if (active && payload && payload.length) {
    const data = payload[0].payload;
    const value = payload[0].value;
    const formattedValue = isRevenue ? `$${value}` : value;

    return (
      <div className="product-tooltip-container">
        <div className="product-tooltip-header">
          <span className="product-tooltip-code">{data.productCode}</span>
        </div>
        <p className="product-tooltip-name">{data.productName}</p>
        <div className="product-tooltip-divider"></div>
        <div className="product-tooltip-value-row">
          <span className={`product-tooltip-dot ${isRevenue ? 'revenue' : 'quantity'}`}></span>
          <span className="product-tooltip-label">{isRevenue ? 'Ingresos' : 'Unidades Vendidas'}</span>
          <span className="product-tooltip-val">{formattedValue}</span>
        </div>
      </div>
    );
  }
  return null;
};

/**
 * Custom Y-Axis Tick to handle long product names by truncating gracefully
 */
const CustomYAxisTick = ({ x, y, payload }) => {
  const name = payload.value;
  const truncated = name.length > 14 ? `${name.substring(0, 12)}...` : name;
  return (
    <g transform={`translate(${x},${y})`}>
      <text
        x={-10}
        y={4}
        textAnchor="end"
        fill="#64748b"
        fontSize={11}
        fontWeight={600}
      >
        {truncated}
      </text>
    </g>
  );
};

const TopProductsSection = ({ data, loading, startDate, endDate }) => {

  // Format date range for headers
  const formattedRange = useMemo(() => {
    if (!startDate || !endDate) return '';
    const options = { year: 'numeric', month: 'short', day: 'numeric' };
    const startStr = startDate.toLocaleDateString('es-AR', options);
    const endStr = endDate.toLocaleDateString('es-AR', options);
    return `${startStr} – ${endStr}`;
  }, [startDate, endDate]);

  const hasQuantityData = data && data.topProductsByQuantity && data.topProductsByQuantity.length > 0;
  const hasRevenueData = data && data.topProductsByRevenue && data.topProductsByRevenue.length > 0;

  // Format revenue X Axis ticks (e.g. $10k, $1.5M)
  const formatXAxisRevenue = (value) => {
    if (value >= 1000000) return `$${(value / 1000000).toFixed(1)}M`;
    if (value >= 1000) return `$${(value / 1000).toFixed(1)}k`;
    return `$${value}`;
  };

  // Format quantity X Axis ticks (simple integer representation)
  const formatXAxisQuantity = (value) => {
    return Math.floor(value) === value ? value : value.toFixed(1);
  };

  // Loading skeleton state
  if (loading) {
    return (
      <div className="top-products-grid">
        <div className="top-products-card skeleton-container">
          <div className="skeleton-header"></div>
          <div className="skeleton-chart"></div>
        </div>
        <div className="top-products-card skeleton-container">
          <div className="skeleton-header"></div>
          <div className="skeleton-chart"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="top-products-grid">

      {/* Chart 1: Quantity Sold */}
      <div className="top-products-card">
        <header className="chart-header">
          <div className="chart-title">
            <div className="title-icon-wrapper quantity">
              <ShoppingBag size={20} />
            </div>
            <div>
              <h2>Productos Más Vendidos por Cantidad (hasta 10 productos)</h2>
              <p>{formattedRange || 'Ranking de productos por unidades vendidas'}</p>
            </div>
          </div>
        </header>

        {hasQuantityData ? (
          <div className="product-chart-container">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart
                layout="vertical"
                data={data.topProductsByQuantity}
                margin={{ top: 15, right: 30, left: 10, bottom: 5 }}
              >
                <defs>
                  <linearGradient id="colorQuantity" x1="0" y1="0" x2="1" y2="0">
                    <stop offset="5%" stopColor="#10b981" stopOpacity={0.95} />
                    <stop offset="95%" stopColor="#059669" stopOpacity={0.75} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="4 4" vertical={true} horizontal={false} stroke="#f1f5f9" />
                <XAxis
                  type="number"
                  axisLine={false}
                  tickLine={false}
                  tickFormatter={formatXAxisQuantity}
                  tick={{ fill: '#94a3b8', fontSize: 11, fontWeight: 500 }}
                />
                <YAxis
                  type="category"
                  dataKey="productName"
                  axisLine={false}
                  tickLine={false}
                  width={110}
                  tick={<CustomYAxisTick />}
                />
                <Tooltip
                  content={<CustomProductTooltip isRevenue={false} />}
                  cursor={{ fill: 'rgba(16, 185, 129, 0.04)' }}
                />
                <Bar
                  dataKey="quantitySold"
                  fill="url(#colorQuantity)"
                  radius={[0, 6, 6, 0]}
                  barSize={16}
                  animationDuration={1200}
                />
              </BarChart>
            </ResponsiveContainer>
          </div>
        ) : (
          <div className="top-products-empty">
            <div className="empty-icon-wrapper quantity">
              <Package size={40} />
            </div>
            <h3>No se encontraron productos</h3>
            <p>No hay datos de productos disponibles para los filtros seleccionados</p>
          </div>
        )}
      </div>

      {/* Chart 2: Revenue Generated */}
      <div className="top-products-card">
        <header className="chart-header">
          <div className="chart-title">
            <div className="title-icon-wrapper revenue">
              <DollarSign size={20} />
            </div>
            <div>
              <h2>Productos con Mayores Ingresos (hasta 10 productos)</h2>
              <p>{formattedRange || 'Ranking de productos por ingresos totales'}</p>
            </div>
          </div>
        </header>

        {hasRevenueData ? (
          <div className="product-chart-container">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart
                layout="vertical"
                data={data.topProductsByRevenue}
                margin={{ top: 15, right: 30, left: 10, bottom: 5 }}
              >
                <defs>
                  <linearGradient id="colorRevenue" x1="0" y1="0" x2="1" y2="0">
                    <stop offset="5%" stopColor="#8b5cf6" stopOpacity={0.95} />
                    <stop offset="95%" stopColor="#7c3aed" stopOpacity={0.75} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="4 4" vertical={true} horizontal={false} stroke="#f1f5f9" />
                <XAxis
                  type="number"
                  axisLine={false}
                  tickLine={false}
                  tickFormatter={formatXAxisRevenue}
                  tick={{ fill: '#94a3b8', fontSize: 11, fontWeight: 500 }}
                />
                <YAxis
                  type="category"
                  dataKey="productName"
                  axisLine={false}
                  tickLine={false}
                  width={110}
                  tick={<CustomYAxisTick />}
                />
                <Tooltip
                  content={<CustomProductTooltip isRevenue={true} />}
                  cursor={{ fill: 'rgba(139, 92, 246, 0.04)' }}
                />
                <Bar
                  dataKey="revenueGenerated"
                  fill="url(#colorRevenue)"
                  radius={[0, 6, 6, 0]}
                  barSize={16}
                  animationDuration={1200}
                />
              </BarChart>
            </ResponsiveContainer>
          </div>
        ) : (
          <div className="top-products-empty">
            <div className="empty-icon-wrapper revenue">
              <Package size={40} />
            </div>
            <h3>No se encontraron productos</h3>
            <p>No hay datos de productos disponibles para los filtros seleccionados</p>
          </div>
        )}
      </div>

    </div>
  );
};

export default TopProductsSection;
