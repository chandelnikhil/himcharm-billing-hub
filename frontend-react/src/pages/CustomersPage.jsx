import { useCallback, useEffect, useState } from 'react'
import {
  Box,
  Button,
  InputAdornment,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  TextField,
  Typography,
} from '@mui/material'
import SearchRoundedIcon from '@mui/icons-material/SearchRounded'
import PageHeader from '../components/common/PageHeader'
import ModuleCard from '../components/common/ModuleCard'
import StatusChip from '../components/common/StatusChip'
import TableState from '../components/common/TableState'
import { customersApi } from '../api/services'
import { getApiError } from '../api/client'

const emptyFilters = () => ({ fromDate: '', toDate: '', phone: '' })
const formatDate = (value) => value
  ? new Intl.DateTimeFormat('en-IN', { day: '2-digit', month: 'short', year: 'numeric' }).format(new Date(`${value}T00:00:00`))
  : '—'
const formatDateTime = (value) => value
  ? new Intl.DateTimeFormat('en-IN', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' }).format(new Date(value))
  : '—'

export default function CustomersPage() {
  const [customers, setCustomers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [page, setPage] = useState(0)
  const [totalCustomers, setTotalCustomers] = useState(0)
  const [filters, setFilters] = useState(emptyFilters)
  const [appliedFilters, setAppliedFilters] = useState(emptyFilters)

  const loadCustomers = useCallback(async (targetPage = page) => {
    setLoading(true)
    setError('')
    try {
      const params = { page: targetPage }
      if (appliedFilters.fromDate) params.fromDate = appliedFilters.fromDate
      if (appliedFilters.toDate) params.toDate = appliedFilters.toDate
      if (appliedFilters.phone.trim()) params.phone = appliedFilters.phone.trim()
      const response = await customersApi.list(params)
      setCustomers(response?.content || [])
      setTotalCustomers(response?.totalElements || 0)
    } catch (requestError) {
      setError(getApiError(requestError, 'Unable to load customers'))
    } finally {
      setLoading(false)
    }
  }, [appliedFilters, page])

  useEffect(() => {
    const task = window.setTimeout(loadCustomers, 0)
    return () => window.clearTimeout(task)
  }, [loadCustomers])

  const applyFilters = () => {
    if (filters.fromDate && filters.toDate && filters.fromDate > filters.toDate) {
      setError('Start date cannot be after end date')
      return
    }
    setError('')
    setPage(0)
    setAppliedFilters(filters)
  }

  const clearFilters = () => {
    setFilters(emptyFilters())
    setPage(0)
    setAppliedFilters(emptyFilters())
  }

  return (
    <>
      <PageHeader title="Customers" description="Review customer profiles and contact information." />
      <ModuleCard>
        <Box sx={{ p: 2.3, display: 'flex', flexWrap: 'wrap', alignItems: 'flex-end', gap: 1.5 }}>
          <Box sx={{ width: { xs: '100%', sm: 240 } }}><Typography color="text.secondary" sx={{ mb: .7, fontSize: 12.5, fontWeight: 650 }}>Phone number</Typography><TextField fullWidth size="small" placeholder="Search phone…" value={filters.phone} onChange={(event) => setFilters((current) => ({ ...current, phone: event.target.value }))} inputProps={{ maxLength: 20 }} InputProps={{ startAdornment: <InputAdornment position="start"><SearchRoundedIcon color="action" fontSize="small" /></InputAdornment> }} /></Box>
          <Box sx={{ width: { xs: '100%', sm: 180 } }}><Typography color="text.secondary" sx={{ mb: .7, fontSize: 12.5, fontWeight: 650 }}>Start date</Typography><TextField fullWidth size="small" type="date" value={filters.fromDate} onChange={(event) => setFilters((current) => ({ ...current, fromDate: event.target.value }))} inputProps={{ 'aria-label': 'Start date' }} /></Box>
          <Box sx={{ width: { xs: '100%', sm: 180 } }}><Typography color="text.secondary" sx={{ mb: .7, fontSize: 12.5, fontWeight: 650 }}>End date</Typography><TextField fullWidth size="small" type="date" value={filters.toDate} onChange={(event) => setFilters((current) => ({ ...current, toDate: event.target.value }))} inputProps={{ 'aria-label': 'End date' }} /></Box>
          <Button variant="contained" onClick={applyFilters} sx={{ height: 40 }}>Apply</Button>
          <Button color="inherit" onClick={clearFilters} disabled={!filters.phone && !filters.fromDate && !filters.toDate && !appliedFilters.phone && !appliedFilters.fromDate && !appliedFilters.toDate} sx={{ height: 40 }}>Clear</Button>
          <Typography color="text.secondary" sx={{ ml: { sm: 'auto' }, fontSize: 13 }}>{totalCustomers} customer{totalCustomers === 1 ? '' : 's'}</Typography>
        </Box>

        <TableContainer><Table sx={{ minWidth: 1050 }}><TableHead><TableRow><TableCell>Name</TableCell><TableCell>Phone</TableCell><TableCell>Email</TableCell><TableCell>Date of birth</TableCell><TableCell>Marital status</TableCell><TableCell>Anniversary</TableCell><TableCell>Created</TableCell></TableRow></TableHead><TableBody>
          {(loading || error || !customers.length) ? <TableState columns={7} loading={loading} error={error} emptyMessage="No customers match the selected filters." onRetry={() => loadCustomers()} /> : customers.map((customer) => (
            <TableRow key={customer.id} hover>
              <TableCell><Typography sx={{ fontSize: 13.5, fontWeight: 750 }}>{customer.name || '—'}</Typography></TableCell>
              <TableCell sx={{ fontWeight: 650 }}>{customer.phone}</TableCell>
              <TableCell>{customer.email || '—'}</TableCell>
              <TableCell>{formatDate(customer.dateOfBirth)}</TableCell>
              <TableCell>{customer.maritalStatus ? <StatusChip active="SUCCESS" label={customer.maritalStatus} /> : '—'}</TableCell>
              <TableCell>{formatDate(customer.anniversaryDate)}</TableCell>
              <TableCell>{formatDateTime(customer.createdAt)}</TableCell>
            </TableRow>
          ))}
        </TableBody></Table></TableContainer>
        <TablePagination component="div" count={totalCustomers} page={page} onPageChange={(_, nextPage) => setPage(nextPage)} rowsPerPage={30} rowsPerPageOptions={[30]} labelRowsPerPage="Rows per page" />
      </ModuleCard>
    </>
  )
}
