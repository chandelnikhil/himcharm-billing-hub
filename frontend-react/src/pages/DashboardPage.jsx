import { useCallback, useEffect, useMemo, useState } from 'react'
import { Alert, Avatar, Box, Button, CircularProgress, MenuItem, TextField, Typography } from '@mui/material'
import { alpha } from '@mui/material/styles'
import { BarElement, CategoryScale, Chart as ChartJS, Legend, LineElement, LinearScale, PointElement, Tooltip } from 'chart.js'
import { Chart } from 'react-chartjs-2'
import PaymentsRoundedIcon from '@mui/icons-material/PaymentsRounded'
import ReceiptLongRoundedIcon from '@mui/icons-material/ReceiptLongRounded'
import BalanceRoundedIcon from '@mui/icons-material/BalanceRounded'
import CalendarMonthRoundedIcon from '@mui/icons-material/CalendarMonthRounded'
import RefreshRoundedIcon from '@mui/icons-material/RefreshRounded'
import PageHeader from '../components/common/PageHeader'
import ModuleCard from '../components/common/ModuleCard'
import { dashboardApi, storesApi } from '../api/services'
import { getApiError } from '../api/client'

ChartJS.register(CategoryScale, LinearScale, BarElement, PointElement, LineElement, Tooltip, Legend)

const toIsoDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const defaultToDate = new Date()
const defaultFromDate = new Date(defaultToDate)
defaultFromDate.setDate(defaultToDate.getDate() - 29)
const initialFilters = { storeId: '', fromDate: toIsoDate(defaultFromDate), toDate: toIsoDate(defaultToDate) }

const emptyMetrics = {
  revenue: 0,
  bills: 0,
  averageOrderValue: 0,
  newRevenue: 0,
  repeatRevenue: 0,
  newBills: 0,
  repeatBills: 0,
  newAverageOrderValue: 0,
  repeatAverageOrderValue: 0,
}

const formatCompact = (value, currency = false) => {
  const amount = Number(value || 0)
  const prefix = currency ? '₹' : ''
  if (Math.abs(amount) >= 10_000_000) return `${prefix}${(amount / 10_000_000).toFixed(1)}Cr`
  if (Math.abs(amount) >= 100_000) return `${prefix}${(amount / 100_000).toFixed(1)}L`
  if (Math.abs(amount) >= 1_000) return `${prefix}${(amount / 1_000).toFixed(1)}K`
  return `${prefix}${Math.round(amount).toLocaleString('en-IN')}`
}

const formatPrecise = (value, currency = false) => `${currency ? '₹' : ''}${Number(value || 0).toLocaleString('en-IN', { maximumFractionDigits: currency ? 2 : 0 })}`

function MetricCard({ title, value, icon: Icon, iconColor, iconBackground, newValue, repeatValue, currency }) {
  const combined = Number(newValue || 0) + Number(repeatValue || 0)
  const newPercentage = combined ? (Number(newValue || 0) / combined) * 100 : 50
  const repeatPercentage = combined ? 100 - newPercentage : 50

  return (
    <ModuleCard sx={{ p: 2, boxShadow: 'none' }}>
      <Box sx={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: 2 }}>
        <Box>
          <Typography sx={{ fontSize: 23, fontWeight: 800, lineHeight: 1.2 }}>{formatCompact(value, currency)}</Typography>
          <Typography color="text.secondary" sx={{ mt: 0.35, fontSize: 13.5 }}>{title}</Typography>
        </Box>
        <Avatar sx={{ width: 44, height: 44, bgcolor: iconBackground, color: iconColor }}><Icon fontSize="small" /></Avatar>
      </Box>
      <Box sx={{ display: 'flex', height: 16, mt: 2.2, overflow: 'hidden', borderRadius: 8, color: '#fff' }}>
        <Box sx={{ width: `${newPercentage}%`, minWidth: 42, bgcolor: '#6647ee', display: 'grid', placeItems: 'center' }}><Typography sx={{ fontSize: 10.5 }}>New</Typography></Box>
        <Box sx={{ width: `${repeatPercentage}%`, minWidth: 48, bgcolor: '#1bc879', display: 'grid', placeItems: 'center' }}><Typography sx={{ fontSize: 10.5 }}>Repeat</Typography></Box>
      </Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 1.1 }}>
        <Box><Typography sx={{ color: '#5e4bd8', fontSize: 12, fontWeight: 650 }}>{formatCompact(newValue, currency)}</Typography><Typography sx={{ color: '#7b61e8', fontSize: 10.5 }}>({newPercentage.toFixed(1)}%)</Typography></Box>
        <Box sx={{ textAlign: 'right' }}><Typography sx={{ color: '#119b61', fontSize: 12, fontWeight: 650 }}>{formatCompact(repeatValue, currency)}</Typography><Typography sx={{ color: '#18a86b', fontSize: 10.5 }}>({repeatPercentage.toFixed(1)}%)</Typography></Box>
      </Box>
    </ModuleCard>
  )
}

export default function DashboardPage() {
  const [stores, setStores] = useState([])
  const [filters, setFilters] = useState(initialFilters)
  const [dashboard, setDashboard] = useState({ metrics: emptyMetrics, trend: [] })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const loadDashboard = useCallback(async (requestedFilters) => {
    setLoading(true)
    setError('')
    try {
      const result = await dashboardApi.get({
        fromDate: requestedFilters.fromDate,
        toDate: requestedFilters.toDate,
        ...(requestedFilters.storeId ? { storeId: requestedFilters.storeId } : {}),
      })
      setDashboard({ metrics: result?.metrics || emptyMetrics, trend: result?.trend || [] })
    } catch (requestError) {
      setError(getApiError(requestError, 'Unable to load dashboard data'))
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    const task = window.setTimeout(() => {
      storesApi.list().then((result) => setStores(result || [])).catch(() => setStores([]))
      loadDashboard(initialFilters)
    }, 0)
    return () => window.clearTimeout(task)
  }, [loadDashboard])

  const chartData = useMemo(() => ({
    labels: dashboard.trend.map((point) => new Intl.DateTimeFormat('en-IN', { day: 'numeric', month: 'short' }).format(new Date(`${point.date}T00:00:00`))),
    datasets: [
      { type: 'bar', label: 'Revenue', data: dashboard.trend.map((point) => point.revenue), backgroundColor: '#19c687', hoverBackgroundColor: '#0eaf75', borderRadius: 5, borderSkipped: false, barPercentage: 0.72, categoryPercentage: 0.78, yAxisID: 'revenue', order: 2 },
      { type: 'line', label: 'Bills', data: dashboard.trend.map((point) => point.bills), borderColor: '#5278d8', backgroundColor: '#5278d8', pointBackgroundColor: '#fff', pointBorderColor: '#5278d8', pointBorderWidth: 2, pointRadius: 3, pointHoverRadius: 5, borderWidth: 2, tension: 0.35, yAxisID: 'bills', order: 1 },
    ],
  }), [dashboard.trend])

  const chartOptions = useMemo(() => ({
    responsive: true,
    maintainAspectRatio: false,
    interaction: { mode: 'index', intersect: false },
    plugins: {
      legend: { position: 'top', align: 'center', labels: { usePointStyle: true, pointStyle: 'circle', boxWidth: 8, color: '#68778d', font: { family: 'Inter, system-ui, sans-serif', size: 11 } } },
      tooltip: { backgroundColor: '#14233c', padding: 12, cornerRadius: 8, callbacks: { label: (context) => context.dataset.label === 'Revenue' ? ` Revenue: ${formatPrecise(context.raw, true)}` : ` Bills: ${formatPrecise(context.raw)}` } },
    },
    scales: {
      x: { grid: { display: false }, border: { display: false }, ticks: { color: '#7a8799', maxRotation: 0, autoSkip: true, maxTicksLimit: 10, font: { size: 10.5 } } },
      revenue: { position: 'left', beginAtZero: true, border: { display: false }, grid: { color: '#edf1f5' }, ticks: { color: '#13a970', callback: (value) => formatCompact(value, true), font: { size: 10.5 } }, title: { display: true, text: 'Revenue', color: '#13a970', align: 'end', font: { weight: 700 } } },
      bills: { position: 'right', beginAtZero: true, border: { display: false }, grid: { drawOnChartArea: false }, ticks: { color: '#5278d8', precision: 0, font: { size: 10.5 } }, title: { display: true, text: 'Bills', color: '#5278d8', align: 'end', font: { weight: 700 } } },
    },
  }), [])

  const metrics = dashboard.metrics
  const filtersInvalid = !filters.fromDate || !filters.toDate || filters.fromDate > filters.toDate

  return (
    <>
      <PageHeader title="Business at a Glance ✨" description="Track revenue and billing performance across your stores." />
      <Box sx={{ display: 'flex', flexWrap: 'wrap', alignItems: 'flex-end', gap: 1.5, mb: 2.5 }}>
        <TextField select label="Store" value={filters.storeId} onChange={(event) => setFilters((current) => ({ ...current, storeId: event.target.value }))} sx={{ minWidth: { xs: '100%', sm: 240 } }}>
          <MenuItem value="">All stores</MenuItem>
          {stores.filter((store) => store.active).map((store) => <MenuItem key={store.id} value={store.id}>{store.name}</MenuItem>)}
        </TextField>
        <TextField label="From date" type="date" value={filters.fromDate} onChange={(event) => setFilters((current) => ({ ...current, fromDate: event.target.value }))} slotProps={{ inputLabel: { shrink: true }, htmlInput: { max: filters.toDate } }} />
        <TextField label="To date" type="date" value={filters.toDate} onChange={(event) => setFilters((current) => ({ ...current, toDate: event.target.value }))} slotProps={{ inputLabel: { shrink: true }, htmlInput: { min: filters.fromDate, max: toIsoDate(new Date()) } }} />
        <Button variant="contained" startIcon={<CalendarMonthRoundedIcon />} onClick={() => loadDashboard(filters)} disabled={loading || filtersInvalid}>Apply filters</Button>
      </Box>
      {error && <Alert severity="error" action={<Button color="inherit" size="small" startIcon={<RefreshRoundedIcon />} onClick={() => loadDashboard(filters)}>Retry</Button>} sx={{ mb: 2 }}>{error}</Alert>}
      <Box sx={{ position: 'relative', bgcolor: '#f3f0e7', border: '1px solid #e9e3d4', borderRadius: 3, p: { xs: 1.5, sm: 2.2 } }}>
        <Typography variant="h5" sx={{ mb: 1.8 }}>Sales</Typography>
        <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', lg: '282px minmax(0, 1fr)' }, gap: 2 }}>
          <Box sx={{ display: 'grid', gap: 1.5 }}>
            <MetricCard title="Revenue" value={metrics.revenue} currency icon={PaymentsRoundedIcon} iconColor="#0ba86d" iconBackground="#e3f8ef" newValue={metrics.newRevenue} repeatValue={metrics.repeatRevenue} />
            <MetricCard title="No. of Bills" value={metrics.bills} icon={ReceiptLongRoundedIcon} iconColor="#6955e7" iconBackground="#efedff" newValue={metrics.newBills} repeatValue={metrics.repeatBills} />
            <MetricCard title="Avg. Order Value" value={metrics.averageOrderValue} currency icon={BalanceRoundedIcon} iconColor="#e5a00a" iconBackground="#fff5dc" newValue={metrics.newAverageOrderValue} repeatValue={metrics.repeatAverageOrderValue} />
          </Box>
          <ModuleCard sx={{ position: 'relative', minHeight: { xs: 390, lg: 550 }, p: { xs: 1.5, sm: 2.5 }, boxShadow: 'none' }}>
            {dashboard.trend.length ? <Box sx={{ height: { xs: 350, lg: 500 } }}><Chart type="bar" data={chartData} options={chartOptions} /></Box> : <Box sx={{ height: { xs: 350, lg: 500 }, display: 'grid', placeItems: 'center', color: 'text.secondary', textAlign: 'center' }}><Box><ReceiptLongRoundedIcon sx={{ fontSize: 44, color: '#afbbc9' }} /><Typography fontWeight={700} sx={{ mt: 1 }}>No sales in this period</Typography><Typography sx={{ fontSize: 13, mt: .4 }}>Try another store or date range.</Typography></Box></Box>}
          </ModuleCard>
        </Box>
        {loading && <Box sx={{ position: 'absolute', inset: 0, display: 'grid', placeItems: 'center', bgcolor: alpha('#fff', .72), zIndex: 2, borderRadius: 3 }}><CircularProgress size={34} /></Box>}
      </Box>
    </>
  )
}
