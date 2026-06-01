import React, { useMemo } from 'react';
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer
} from 'recharts';
import { DollarSign, ShoppingCart, TrendingUp } from 'lucide-react';
import './SalesTimeSeriesChart.css';

/**
 * Custom Tooltip for Revenue Chart
 */
const RevenueTooltip = ({ active, payload, label }) => {
  if (active && payload && payload.length) {
    return (
      <div className="chart-custom-tooltip">
        <p className="tooltip-label">{label}</p>
        <div className="tooltip-items">
          <div className="tooltip-item revenue">
            <span className="dot"></span>
            <span className="label">Revenue:</span>
            <span className="value">${payload[0].value}</span>
          </div>
        </div>
      </div>
    );
  }
  return null;
};

/**
 * Custom Tooltip for Sales Chart
 */
const SalesTooltip = ({ active, payload, label }) => {
  if (active && payload && payload.length) {
    return (
      <div className="chart-custom-tooltip">
        <p className="tooltip-label">{label}</p>
        <div className="tooltip-items">
          <div className="tooltip-item sales">
            <span className="dot"></span>
            <span className="label">Sales Count:</span>
            <span className="value">{payload[0].value} orders</span>
          </div>
        </div>
      </div>
    );
  }
  return null;
};

/**
 * SalesTimeSeriesChart Component (Renders two separate modern dashboard charts)
 */
const SalesTimeSeriesChart = ({ data, loading, startDate, endDate }) => {
  
  // 0. Format Date Range for Chart Subtitles
  const formattedRange = useMemo(() => {
    if (!startDate || !endDate) return '';
    const options = { year: 'numeric', month: 'short', day: 'numeric' };
    const startStr = startDate.toLocaleDateString('en-US', options);
    const endStr = endDate.toLocaleDateString('en-US', options);
    return `${startStr} – ${endStr}`;
  }, [startDate, endDate]);
  
  // 1. Data Transformation
  const chartData = useMemo(() => {
    if (!data || !data.revenueOverTime || !data.salesOverTime) return [];
    
    // Create a map by label to merge data
    const merged = {};
    
    data.revenueOverTime.forEach(item => {
      merged[item.label] = { 
        name: item.label,
        revenue: item.value,
        sales: 0 
      };
    });
    
    data.salesOverTime.forEach(item => {
      if (merged[item.label]) {
        merged[item.label].sales = item.value;
      } else {
        merged[item.label] = { 
          name: item.label,
          revenue: 0,
          sales: item.value 
        };
      }
    });
    
    return Object.values(merged).sort((a, b) => a.name.localeCompare(b.name));
  }, [data]);

  // 2. Formatters
  const formatYAxisRevenue = (value) => {
    if (value >= 1000000000000) return `$${(value / 1000000000000).toFixed(1)}T`;
    if (value >= 1000000000) return `$${(value / 1000000000).toFixed(1)}B`;
    if (value >= 1000000) return `$${(value / 1000000).toFixed(1)}M`;
    if (value >= 1000) return `$${(value / 1000).toFixed(1)}k`;
    return `$${value}`;
  };

  const formatYAxisSales = (value) => {
    if (value >= 1000000000000) return `${(value / 1000000000000).toFixed(1)}T`;
    if (value >= 1000000000) return `${(value / 1000000000).toFixed(1)}B`;
    if (value >= 1000000) return `${(value / 1000000).toFixed(1)}M`;
    if (value >= 1000) return `${(value / 1000).toFixed(1)}k`;
    return Math.floor(value) === value ? value : '';
  };

  const formatXAxisLabel = (label) => {
    if (!label) return '';
    
    // 1. HOUR: "14:00" or "22:00 - 22:59"
    if (label.includes(':')) {
      return label;
    }
    
    const dateParts = label.split('-');
    
    // 2. DAY: "2026-05-11"
    if (dateParts.length === 3) {
      const date = new Date(dateParts[0], dateParts[1] - 1, dateParts[2]);
      return date.toLocaleDateString('en-US', { day: 'numeric', month: 'short' });
    }
    
    // 3. MONTH: "2026-05"
    if (dateParts.length === 2) {
      const date = new Date(dateParts[0], dateParts[1] - 1, 1);
      return date.toLocaleDateString('en-US', { month: 'short', year: 'numeric' });
    }
    
    // 4. YEAR: "2026"
    return label;
  };

  // 3. Loading State (Skeleton)
  if (loading) {
    return (
      <div className="stats-dashboard-charts-grid">
        <div className="stats-chart-card skeleton-container">
          <div className="skeleton-header"></div>
          <div className="skeleton-chart"></div>
        </div>
        <div className="stats-chart-card skeleton-container">
          <div className="skeleton-header"></div>
          <div className="skeleton-chart"></div>
        </div>
      </div>
    );
  }

  // 4. Empty State
  if (!chartData || chartData.length === 0) {
    return (
      <div className="stats-chart-card empty-state">
        <div className="empty-content">
          <TrendingUp size={48} className="empty-icon" />
          <h3>No activity recorded</h3>
          <p>No data available for the selected criteria.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="stats-dashboard-charts-grid">
      
      {/* Chart 1: Revenue Evolution */}
      <div className="stats-chart-card">
        <header className="chart-header">
          <div className="chart-title">
            <div className="title-icon-wrapper revenue">
              <DollarSign size={20} />
            </div>
            <div>
              <h2>Total Revenue</h2>
              <p>{formattedRange || 'Earnings evolution over the selected period'}</p>
            </div>
          </div>
        </header>

        <div className="chart-container">
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart
              data={chartData}
              margin={{ top: 20, right: 20, left: 10, bottom: 10 }}
            >
              <defs>
                <linearGradient id="colorRevenueDual" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#6366f1" stopOpacity={0.25}/>
                  <stop offset="95%" stopColor="#6366f1" stopOpacity={0}/>
                </linearGradient>
              </defs>
              
              <CartesianGrid 
                strokeDasharray="4 4" 
                vertical={false} 
                stroke="#f1f5f9" 
              />
              
              <XAxis 
                dataKey="name" 
                tickFormatter={formatXAxisLabel}
                axisLine={false}
                tickLine={false}
                tick={{ fill: '#94a3b8', fontSize: 11, fontWeight: 500 }}
                tickMargin={6}
                minTickGap={25}
              />
              
              <YAxis 
                tickFormatter={formatYAxisRevenue}
                axisLine={false}
                tickLine={false}
                tick={{ fill: '#6366f1', fontSize: 11, fontWeight: 600 }}
                tickMargin={6}
                width={60}
              />
              
              <Tooltip 
                content={<RevenueTooltip />} 
                cursor={{ stroke: '#e2e8f0', strokeWidth: 1, strokeDasharray: '4 4' }}
              />
              
              <Area
                type="monotone"
                dataKey="revenue"
                stroke="#6366f1"
                strokeWidth={3}
                fillOpacity={1}
                fill="url(#colorRevenueDual)"
                animationDuration={1200}
                activeDot={{ r: 6, fill: '#6366f1', stroke: '#fff', strokeWidth: 2 }}
              />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Chart 2: Sales Count Evolution */}
      <div className="stats-chart-card">
        <header className="chart-header">
          <div className="chart-title">
            <div className="title-icon-wrapper sales">
              <ShoppingCart size={20} />
            </div>
            <div>
              <h2>Sales Volume</h2>
              <p>{formattedRange || 'Number of completed sales over time'}</p>
            </div>
          </div>
        </header>

        <div className="chart-container">
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart
              data={chartData}
              margin={{ top: 20, right: 20, left: 10, bottom: 10 }}
            >
              <defs>
                <linearGradient id="colorSalesDual" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#334155" stopOpacity={0.15}/>
                  <stop offset="95%" stopColor="#334155" stopOpacity={0}/>
                </linearGradient>
              </defs>
              
              <CartesianGrid 
                strokeDasharray="4 4" 
                vertical={false} 
                stroke="#f1f5f9" 
              />
              
              <XAxis 
                dataKey="name" 
                tickFormatter={formatXAxisLabel}
                axisLine={false}
                tickLine={false}
                tick={{ fill: '#94a3b8', fontSize: 11, fontWeight: 500 }}
                tickMargin={6}
                minTickGap={25}
              />
              
              <YAxis 
                tickFormatter={formatYAxisSales}
                axisLine={false}
                tickLine={false}
                tick={{ fill: '#334155', fontSize: 11, fontWeight: 600 }}
                tickMargin={6}
                width={40}
              />
              
              <Tooltip 
                content={<SalesTooltip />} 
                cursor={{ stroke: '#e2e8f0', strokeWidth: 1, strokeDasharray: '4 4' }}
              />
              
              <Area
                type="monotone"
                dataKey="sales"
                stroke="#334155"
                strokeWidth={3}
                fillOpacity={1}
                fill="url(#colorSalesDual)"
                animationDuration={1200}
                activeDot={{ r: 6, fill: '#334155', stroke: '#fff', strokeWidth: 2 }}
              />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </div>

    </div>
  );
};

export default SalesTimeSeriesChart;
