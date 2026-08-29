import { useCallback, useEffect, useMemo, useState } from 'react'
import {
  Alert,
  Avatar,
  Box,
  CircularProgress,
  Grid,
  LinearProgress,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from '@mui/material'
import StorefrontRoundedIcon from '@mui/icons-material/StorefrontRounded'
import ReceiptLongRoundedIcon from '@mui/icons-material/ReceiptLongRounded'
import GroupsRoundedIcon from '@mui/icons-material/GroupsRounded'
import PaymentsRoundedIcon from '@mui/icons-material/PaymentsRounded'
import TrendingUpRoundedIcon from '@mui/icons-material/TrendingUpRounded'
import PageHeader from '../components/common/PageHeader'
import ModuleCard from '../components/common/ModuleCard'
import StatusChip from '../components/common/StatusChip'
import { invoicesApi, storesApi, usersApi } from '../api/services'
import { getApiError } from '../api/client'

const money = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 })
const date = new Intl.DateTimeFormat('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })

function StatCard({ title, value, icon: Icon, color, tint, helper }) {
  return (
    <ModuleCard sx={{ p: 2.5, height: '100%' }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <Box><Typography color="text.secondary" sx={{ fontSize: 13.5, fontWeight: 600 }}>{title}</Typography><Typography sx={{ fontSize: 26, fontWeight: 750, mt: 1 }}>{value}</Typography></Box>
        <Avatar variant="rounded" sx={{ width: 44, height: 44, bgcolor: tint, color, borderRadius: 2.5 }}><Icon /></Avatar>
      </Box>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.6, mt: 2, color: 'success.main' }}><TrendingUpRoundedIcon sx={{ fontSize: 17 }} /><Typography sx={{ fontSize: 12.5, fontWeight: 650 }}>{helper}</Typography></Box>
    </ModuleCard>
  )
}

export default function DashboardPage() {
  const [data, setData] = useState({ users: [], stores: [], invoices: [] })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const loadDashboard = useCallback(async () => {
    setLoading(true)
    setError('')
    try {
      const [users, stores, invoices] = await Promise.all([usersApi.list(), storesApi.list(), invoicesApi.list()])
      setData({ users: users || [], stores: stores || [], invoices: invoices?.content || invoices || [] })
    } catch (requestError) {
      setError(getApiError(requestError, 'Could not connect to the API'))
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    const task = window.setTimeout(loadDashboard, 0)
    return () => window.clearTimeout(task)
  }, [loadDashboard])

  const totalRevenue = useMemo(() => data.invoices.reduce((sum, invoice) => sum + Number(invoice.totalAmount || 0), 0), [data.invoices])
  const averageOrder = data.invoices.length ? totalRevenue / data.invoices.length : 0
  const recentInvoices = [...data.invoices].sort((a, b) => new Date(b.invoiceDate) - new Date(a.invoiceDate)).slice(0, 5)
  const maxAmount = Math.max(...recentInvoices.map((invoice) => Number(invoice.totalAmount || 0)), 1)

  return (
    <>
      <PageHeader title="Dashboard" description="An overview of your store operations and recent activity." />
      {error && <Alert severity="warning" action={<Typography component="button" onClick={loadDashboard} sx={{ border: 0, bgcolor: 'transparent', color: 'inherit', fontWeight: 700, cursor: 'pointer' }}>Retry</Typography>} sx={{ mb: 2.5 }}>{error}</Alert>}
      {loading ? <Box sx={{ height: 380, display: 'grid', placeItems: 'center' }}><CircularProgress /></Box> : (
        <>
          <Grid container spacing={2.2}>
            <Grid size={{ xs: 12, sm: 6, xl: 3 }}><StatCard title="Total Stores" value={data.stores.length.toLocaleString('en-IN')} icon={StorefrontRoundedIcon} color="#087c59" tint="#ddf8ee" helper={`${data.stores.filter((store) => store.active).length} active locations`} /></Grid>
            <Grid size={{ xs: 12, sm: 6, xl: 3 }}><StatCard title="Total Invoices" value={data.invoices.length.toLocaleString('en-IN')} icon={ReceiptLongRoundedIcon} color="#1769e0" tint="#e4f1ff" helper="All recorded invoices" /></Grid>
            <Grid size={{ xs: 12, sm: 6, xl: 3 }}><StatCard title="Revenue" value={money.format(totalRevenue)} icon={PaymentsRoundedIcon} color="#6f51d8" tint="#eee9ff" helper="Across all stores" /></Grid>
            <Grid size={{ xs: 12, sm: 6, xl: 3 }}><StatCard title="Admin Users" value={data.users.length.toLocaleString('en-IN')} icon={GroupsRoundedIcon} color="#0a9e8b" tint="#ddf8f4" helper="Full-access team members" /></Grid>
          </Grid>

          <Grid container spacing={2.2} sx={{ mt: 0.2 }}>
            <Grid size={{ xs: 12, lg: 8 }}>
              <ModuleCard>
                <Box sx={{ p: 2.5, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}><Box><Typography variant="h6">Recent invoices</Typography><Typography color="text.secondary" sx={{ fontSize: 13, mt: 0.4 }}>Latest transactions from all stores</Typography></Box><Typography color="primary.main" sx={{ fontSize: 13, fontWeight: 700 }}>Avg. {money.format(averageOrder)}</Typography></Box>
                <TableContainer><Table><TableHead><TableRow><TableCell>Invoice</TableCell><TableCell>Date</TableCell><TableCell>Store</TableCell><TableCell>Payment</TableCell><TableCell align="right">Amount</TableCell></TableRow></TableHead><TableBody>
                  {recentInvoices.length ? recentInvoices.map((invoice) => <TableRow key={invoice.id} hover><TableCell sx={{ fontWeight: 700, color: 'primary.main' }}>{invoice.invoiceNumber || `#${invoice.id}`}</TableCell><TableCell>{invoice.invoiceDate ? date.format(new Date(invoice.invoiceDate)) : '—'}</TableCell><TableCell>Store #{invoice.storeId}</TableCell><TableCell><StatusChip active="SUCCESS" label={invoice.paymentMode || 'Not set'} /></TableCell><TableCell align="right" sx={{ fontWeight: 700 }}>{money.format(invoice.totalAmount || 0)}</TableCell></TableRow>) : <TableRow><TableCell colSpan={5} align="center" sx={{ py: 7, color: 'text.secondary' }}>No invoices have been created yet.</TableCell></TableRow>}
                </TableBody></Table></TableContainer>
              </ModuleCard>
            </Grid>
            <Grid size={{ xs: 12, lg: 4 }}>
              <ModuleCard sx={{ p: 2.5, height: '100%' }}>
                <Typography variant="h6">Invoice value overview</Typography><Typography color="text.secondary" sx={{ fontSize: 13, mt: 0.4, mb: 3 }}>Your five most recent transactions</Typography>
                {recentInvoices.length ? recentInvoices.map((invoice) => <Box key={invoice.id} sx={{ mb: 2.4 }}><Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.8 }}><Typography noWrap sx={{ maxWidth: '60%', fontSize: 12.5, fontWeight: 650 }}>{invoice.invoiceNumber || `Invoice #${invoice.id}`}</Typography><Typography sx={{ fontSize: 12.5, fontWeight: 700 }}>{money.format(invoice.totalAmount || 0)}</Typography></Box><LinearProgress variant="determinate" value={(Number(invoice.totalAmount || 0) / maxAmount) * 100} sx={{ height: 7, borderRadius: 5, bgcolor: '#edf2f7', '& .MuiLinearProgress-bar': { borderRadius: 5 } }} /></Box>) : <Box sx={{ py: 8, textAlign: 'center', color: 'text.secondary' }}>Invoice activity will appear here.</Box>}
              </ModuleCard>
            </Grid>
          </Grid>
        </>
      )}
    </>
  )
}
