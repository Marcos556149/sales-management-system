import React, { useState, useEffect } from 'react';
import { 
  BarChart3, 
  Users, 
  Calendar as CalendarIcon, 
  Filter, 
  Play,
  TrendingUp,
  Loader2,
  DollarSign,
  ShoppingBag,
  Clock,
  Package,
  FileDown
} from 'lucide-react';
import DatePicker from 'react-datepicker';
import { enUS } from 'date-fns/locale';
import 'react-datepicker/dist/react-datepicker.css';
import './StatisticsView.css';
import { apiClient } from '../api/client';
import { useToast } from './ToastContext';
import SalesTimeSeriesChart from './SalesTimeSeriesChart';
import TopProductsSection from './TopProductsSection';
import SoldProductsRankingSection from './SoldProductsRankingSection';
import UnsoldProductsSection from './UnsoldProductsSection';
import ExportStatisticsReportModal from './ExportStatisticsReportModal';

/**
 * IsolatedDateInput - Internal component for the statistics section
 * Totally independent from SalesView to ensure isolation.
 */
const IsolatedDateInput = React.forwardRef(({ value, onClick, onManualChange, onTyping, forceSync }, ref) => {
  const [day, setDay] = useState('');
  const [month, setMonth] = useState('');
  const [year, setYear] = useState('');

  useEffect(() => {
    if (value && value.includes('-')) {
      const [y, m, d] = value.split('-');
      setYear(y);
      setMonth(m);
      setDay(d);
    } else {
      setYear('');
      setMonth('');
      setDay('');
    }
  }, [value, forceSync]);

  const validateAndFormat = (d, m, y, trigger) => {
    let dInt = parseInt(d, 10);
    let mInt = parseInt(m, 10);
    let yInt = parseInt(y, 10);

    if (isNaN(dInt) || isNaN(mInt) || isNaN(yInt)) {
      if (value && value.includes('-')) {
        const [oY, oM, oD] = value.split('-');
        setDay(oD); setMonth(oM); setYear(oY);
      }
      return;
    }

    const normY = Math.max(yInt, 1);
    const normM = Math.min(Math.max(mInt, 1), 12);
    
    const daysInMonth = (y, m) => {
      if (m === 2) {
        const leap = (y % 4 === 0 && (y % 100 !== 0 || y % 400 === 0));
        return leap ? 29 : 28;
      }
      return [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][m - 1];
    };

    const maxD = daysInMonth(normY, normM);
    const normD = Math.min(Math.max(dInt, 1), maxD);

    const formatted = `${String(normY).padStart(4, '0')}-${String(normM).padStart(2, '0')}-${String(normD).padStart(2, '0')}`;
    
    setDay(String(normD).padStart(2, '0'));
    setMonth(String(normM).padStart(2, '0'));
    setYear(String(normY).padStart(4, '0'));

    if (trigger && formatted !== value) {
      onManualChange(formatted);
    }
  };

  const handleChange = (e, setter, len) => {
    const val = e.target.value.replace(/[^0-9]/g, '').slice(0, len);
    setter(val);
    if (onTyping) onTyping();
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      validateAndFormat(day, month, year, true);
      e.target.blur();
    }
  };

  return (
    <div className="stats-iso-date-container" ref={ref}>
      <div className="stats-iso-date-inputs">
        <input 
          className="stats-iso-date-field stats-iso-day" 
          value={day} 
          onChange={e => handleChange(e, setDay, 2)}
          onBlur={() => validateAndFormat(day, month, year, true)}
          onKeyDown={handleKeyDown}
          placeholder="DD"
        />
        <span className="stats-iso-separator">/</span>
        <input 
          className="stats-iso-date-field stats-iso-month" 
          value={month} 
          onChange={e => handleChange(e, setMonth, 2)}
          onBlur={() => validateAndFormat(day, month, year, true)}
          onKeyDown={handleKeyDown}
          placeholder="MM"
        />
        <span className="stats-iso-separator">/</span>
        <input 
          className="stats-iso-date-field stats-iso-year" 
          value={year} 
          onChange={e => handleChange(e, setYear, 4)}
          onBlur={() => validateAndFormat(day, month, year, true)}
          onKeyDown={handleKeyDown}
          placeholder="YYYY"
        />
      </div>
      <button type="button" className="stats-iso-cal-btn" onClick={onClick}>
        <CalendarIcon size={18} />
      </button>
    </div>
  );
});

const StatisticsView = () => {
  const { addToast } = useToast();
  
  // State for Filters
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState("ALL");
  const [startDate, setStartDate] = useState(new Date());
  const [endDate, setEndDate] = useState(new Date());
  
  // UI State
  const [loadingUsers, setLoadingUsers] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [syncCounter, setSyncCounter] = useState(0);
  const [statsData, setStatsData] = useState(null);
  const [timeSeriesData, setTimeSeriesData] = useState(null);
  const [topProductsData, setTopProductsData] = useState(null);
  const [loadingChart, setLoadingChart] = useState(false);
  const [appliedFilters, setAppliedFilters] = useState(null);
  const [isExportOpen, setIsExportOpen] = useState(false);
  
  // Debug state changes
  useEffect(() => {
    console.log("[StatisticsView] Filter State Changed:", { 
      selectedUser, 
      startDate: startDate.toISOString().split('T')[0], 
      endDate: endDate.toISOString().split('T')[0] 
    });
  }, [selectedUser, startDate, endDate]);

  // Fetch Users for Filter
  useEffect(() => {
    const controller = new AbortController();
    
    const fetchUsers = async () => {
      try {
        const response = await apiClient.get('/api/statistics/filters/users', {
          signal: controller.signal
        });
        setUsers(Array.isArray(response.data) ? response.data : []);
      } catch (err) {
        if (err.name === 'AbortError') return;
        console.error("Error fetching users for stats:", err);
        addToast("Could not load users for filtering", "error");
      } finally {
        setLoadingUsers(false);
      }
    };

    // Small delay to let StrictMode's second mount take over if needed
    const timer = setTimeout(() => {
      fetchUsers();
    }, 10);

    return () => {
      clearTimeout(timer);
      controller.abort();
    };
  }, [addToast]);

  const handleGenerate = async () => {
    setGenerating(true);
    setLoadingChart(true);
    setStatsData(null);
    setTimeSeriesData(null);
    setTopProductsData(null);
    setAppliedFilters(null);
    
    // Format dates for backend (YYYY-MM-DD)
    const formatStr = (date) => {
      const targetDate = date || new Date();
      const y = targetDate.getFullYear();
      const m = String(targetDate.getMonth() + 1).padStart(2, '0');
      const d = String(targetDate.getDate()).padStart(2, '0');
      return `${y}-${m}-${d}`;
    };

    const params = {
      startDate: formatStr(startDate),
      endDate: formatStr(endDate)
    };

    // Only send userId if a specific user is selected
    if (selectedUser && selectedUser !== "ALL") {
      params.userId = selectedUser;
    }

    console.log("[StatisticsView] Sending request with params:", params);

    try {
      // 1. Fetch Total Sales
      const salesResponse = await apiClient.get('/api/statistics/sales/total-sales', { params });
      const totalSales = salesResponse.data.totalSales;

      if (totalSales === 0) {
        addToast("No data available for the selected criteria", "info");
        setStatsData(null);
        setTopProductsData(null);
        return;
      }

      // 2. Fetch Total Revenue, Average Ticket, Peak Hours, Time Series and Top Products in parallel
      const [revenueRes, averageRes, peakHoursRes, timeSeriesRes, topProductsRes] = await Promise.all([
        apiClient.get('/api/statistics/sales/total-revenue', { params }),
        apiClient.get('/api/statistics/sales/average-ticket', { params }),
        apiClient.get('/api/statistics/sales/peak-hours', { params }),
        apiClient.get('/api/statistics/sales/time-series', { params }),
        apiClient.get('/api/statistics/products/top', { params })
      ]);

      setStatsData({
        totalSales,
        totalRevenue: revenueRes.data.totalRevenue,
        averageTicket: averageRes.data.averageTicket,
        highestRevenueHour: peakHoursRes.data.highestRevenueHour,
        highestSalesHour: peakHoursRes.data.highestSalesHour
      });

      setTimeSeriesData(timeSeriesRes.data);
      setTopProductsData(topProductsRes.data);
      setAppliedFilters({
        userId: selectedUser,
        startDate,
        endDate
      });

    } catch (err) {
      console.error("Error generating statistics:", err);
      const errorMessage = err.message || "An error occurred while fetching statistics";
      addToast(errorMessage, "error");
    } finally {
      setGenerating(false);
      setLoadingChart(false);
    }
  };

  // Keyboard Shortcut: Ctrl + Enter to generate statistics
  useEffect(() => {
    const handleGlobalKeyDown = (e) => {
      if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
        e.preventDefault();
        if (!generating) {
          handleGenerate();
        }
      }
    };

    window.addEventListener('keydown', handleGlobalKeyDown);
    return () => {
      window.removeEventListener('keydown', handleGlobalKeyDown);
    };
  }, [generating, selectedUser, startDate, endDate]);

  // Keyboard Shortcut: Ctrl + Shift + K to open export modal
  useEffect(() => {
    const handleGlobalShortcut = (e) => {
      if ((e.ctrlKey || e.metaKey) && e.shiftKey && e.key.toLowerCase() === 'k') {
        e.preventDefault();
        setIsExportOpen(true);
      }
    };

    window.addEventListener('keydown', handleGlobalShortcut);
    return () => {
      window.removeEventListener('keydown', handleGlobalShortcut);
    };
  }, []);

  const toDateStr = (date) => {
    if (!date) return "";
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const d = String(date.getDate()).padStart(2, '0');
    return `${y}-${m}-${d}`;
  };

  const fromDateStr = (str) => {
    if (!str) return new Date();
    const [y, m, d] = str.split('-');
    return new Date(parseInt(y), parseInt(m) - 1, parseInt(d));
  };

  return (
    <div id="statistics-section-isolated">
      {/* Header */}
      <header className="stats-iso-header">
        <div className="stats-iso-title-group">
          <h1>Sales Statistics</h1>
          <p>Analyze your business performance and product trends.</p>
        </div>
        <div className="stats-iso-icon">
          <TrendingUp size={40} color="#3182ce" opacity={0.2} />
        </div>
      </header>

      {/* Filters Card */}
      <section className="stats-iso-filters-card">
        <div className="stats-iso-filters-grid">
          
          {/* User Filter */}
          <div className="stats-iso-filter-item">
            <label className="stats-iso-label">
              <Users size={16} />
              Filter by Seller
            </label>
            <select 
              className="stats-iso-select"
              value={selectedUser}
              onChange={(e) => setSelectedUser(e.target.value)}
              disabled={loadingUsers}
            >
              <option value="ALL">All Users</option>
              {users.map(u => (
                <option key={u.userId} value={u.userId}>{u.userName}</option>
              ))}
            </select>
          </div>

          {/* Start Date */}
          <div className="stats-iso-filter-item">
            <label className="stats-iso-label">
              <CalendarIcon size={16} />
              Start Date
            </label>
            <DatePicker
              selected={startDate}
              onChange={date => setStartDate(date)}
              onSelect={(date) => {
                if (date && toDateStr(date) === toDateStr(startDate)) {
                  setSyncCounter(prev => prev + 1);
                }
              }}
              dateFormat="yyyy-MM-dd"
              locale={enUS}
              todayButton="Today"
              customInput={
                <IsolatedDateInput 
                  value={toDateStr(startDate)}
                  onManualChange={str => setStartDate(fromDateStr(str))}
                  forceSync={syncCounter}
                />
              }
            />
          </div>

          {/* End Date */}
          <div className="stats-iso-filter-item">
            <label className="stats-iso-label">
              <CalendarIcon size={16} />
              End Date
            </label>
            <DatePicker
              selected={endDate}
              onChange={date => setEndDate(date)}
              onSelect={(date) => {
                if (date && toDateStr(date) === toDateStr(endDate)) {
                  setSyncCounter(prev => prev + 1);
                }
              }}
              dateFormat="yyyy-MM-dd"
              locale={enUS}
              todayButton="Today"
              customInput={
                <IsolatedDateInput 
                  value={toDateStr(endDate)}
                  onManualChange={str => setEndDate(fromDateStr(str))}
                  forceSync={syncCounter}
                />
              }
            />
          </div>
        </div>

        {/* Generate Button Container - Below Filters */}
        <div className="stats-iso-action-container">
          <button 
            className="stats-iso-btn-primary"
            onClick={handleGenerate}
            disabled={generating}
            title="Ctrl + Enter"
          >
            {generating ? (
              <Loader2 className="spin-animation" size={20} />
            ) : (
              <Play size={20} fill="currentColor" />
            )}
            <span>Generate Statistics</span>
            {!generating && (
              <span className="btn-shortcut" style={{ backgroundColor: 'rgba(255, 255, 255, 0.2)', color: 'white', borderColor: 'rgba(255, 255, 255, 0.3)' }}>Ctrl+Enter</span>
            )}
          </button>

          <button 
            type="button"
            className="stats-iso-btn-secondary"
            onClick={() => setIsExportOpen(true)}
            disabled={generating}
            title="Ctrl + Shift + K"
          >
            <FileDown size={20} />
            <span>Export PDF</span>
            <span className="btn-shortcut" style={{ backgroundColor: 'rgba(49, 130, 206, 0.1)', color: '#3182ce', borderColor: 'rgba(49, 130, 206, 0.2)' }}>Ctrl+Shift+K</span>
          </button>
        </div>
      </section>

      {/* Statistics Results */}
      {!generating && statsData && (
        <>
          <div className="stats-iso-section-header">
            <TrendingUp size={22} className="section-header-icon" />
            <div>
              <h2>Sales Information</h2>
              <p>General metrics and store revenue evolution over time</p>
            </div>
          </div>
          <div className="stats-iso-results-grid">
          {/* Total Sales Card */}
          <div className="stats-iso-card stats-card-blue">
            <div className="stats-card-icon">
              <ShoppingBag size={24} />
            </div>
            <div className="stats-card-info">
              <h3>Total Sales</h3>
              <p className="stats-card-value">{statsData.totalSales}</p>
              <span className="stats-card-label">Orders processed</span>
            </div>
          </div>

          {/* Total Revenue Card */}
          <div className="stats-iso-card stats-card-emerald">
            <div className="stats-card-icon">
              <DollarSign size={24} />
            </div>
            <div className="stats-card-info">
              <h3>Total Revenue</h3>
              <p className="stats-card-value">
                ${statsData.totalRevenue}
              </p>
              <span className="stats-card-label">Gross earnings</span>
            </div>
          </div>

          {/* Average Ticket Card */}
          <div className="stats-iso-card stats-card-indigo">
            <div className="stats-card-icon">
              <TrendingUp size={24} />
            </div>
            <div className="stats-card-info">
              <h3>Average Ticket</h3>
              <p className="stats-card-value">
                ${statsData.averageTicket}
              </p>
              <span className="stats-card-label">Per transaction</span>
            </div>
          </div>

          {/* Highest Sales Hour Card */}
          <div className="stats-iso-card stats-card-orange">
            <div className="stats-card-icon">
              <Clock size={24} />
            </div>
            <div className="stats-card-info">
              <h3>Peak Sales Hour</h3>
              <p className="stats-card-value">
                {statsData.highestSalesHour || "N/A"}
              </p>
              <span className="stats-card-label">Most orders</span>
            </div>
          </div>

          {/* Highest Revenue Hour Card */}
          <div className="stats-iso-card stats-card-purple">
            <div className="stats-card-icon">
              <Clock size={24} />
            </div>
            <div className="stats-card-info">
              <h3>Peak Revenue Hour</h3>
              <p className="stats-card-value">
                {statsData.highestRevenueHour || "N/A"}
              </p>
              <span className="stats-card-label">Highest earnings</span>
            </div>
          </div>
        </div>
        </>
      )}

      {/* Time Series Chart */}
      {(generating || timeSeriesData) && (
        <section className="stats-iso-chart-section">
          <SalesTimeSeriesChart 
            data={timeSeriesData} 
            loading={loadingChart} 
            startDate={startDate}
            endDate={endDate}
          />
        </section>
      )}

      {/* Products Information Section */}
      {(generating || topProductsData) && (
        <section className="stats-iso-chart-section">
          <div className="stats-iso-section-header">
            <Package size={22} className="section-header-icon" />
            <div>
              <h2>Products Information</h2>
              <p>Analyze top selling products and inventory performance</p>
            </div>
          </div>
          <TopProductsSection 
            data={topProductsData} 
            loading={generating} 
            startDate={startDate}
            endDate={endDate}
          />
          {appliedFilters && (
            <>
              <SoldProductsRankingSection 
                userId={appliedFilters.userId}
                startDate={appliedFilters.startDate}
                endDate={appliedFilters.endDate}
              />
              <UnsoldProductsSection 
                userId={appliedFilters.userId}
                startDate={appliedFilters.startDate}
                endDate={appliedFilters.endDate}
              />
            </>
          )}
        </section>
      )}

      <ExportStatisticsReportModal
        isOpen={isExportOpen}
        onClose={() => setIsExportOpen(false)}
        filters={{
          userId: selectedUser === "ALL" ? null : selectedUser,
          startDate: toDateStr(startDate),
          endDate: toDateStr(endDate)
        }}
      />
    </div>
  );
};

export default StatisticsView;
