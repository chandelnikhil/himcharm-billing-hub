import { useCallback, useEffect, useState } from 'react'
import {
  Box,
  Button,
  Chip,
  FormControl,
  MenuItem,
  Select,
  Stack,
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
import AutoAwesomeRoundedIcon from '@mui/icons-material/AutoAwesomeRounded'
import CakeRoundedIcon from '@mui/icons-material/CakeRounded'
import CalendarMonthRoundedIcon from '@mui/icons-material/CalendarMonthRounded'
import FavoriteRoundedIcon from '@mui/icons-material/FavoriteRounded'
import SendRoundedIcon from '@mui/icons-material/SendRounded'
import PageHeader from '../components/common/PageHeader'
import ModuleCard from '../components/common/ModuleCard'
import TableState from '../components/common/TableState'
import { campaignsApi } from '../api/services'
import { getApiError } from '../api/client'

const emptyFilters = () => ({ fromDate: '', toDate: '', campaignType: '' })

const formatDate = (value) => value
  ? new Intl.DateTimeFormat('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })
    .format(new Date(`${value}T00:00:00`))
  : '—'

const formatDateTime = (value) => value
  ? new Intl.DateTimeFormat('en-IN', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(value))
  : '—'

const statusColors = {
  SENT: { color: '#1769e0', bgcolor: '#eaf3ff' },
  DELIVERED: { color: '#087a57', bgcolor: '#e4f8f1' },
  READ: { color: '#087a57', bgcolor: '#e4f8f1' },
  FAILED: { color: '#b42318', bgcolor: '#feeceb' },
}

function MessageStatus({ value }) {
  return (
    <Chip
      size="small"
      label={value || 'Unknown'}
      sx={{ height: 25, fontSize: 11.5, fontWeight: 750, ...statusColors[value] }}
    />
  )
}

function CampaignBanner({ manual = false }) {
  return (
    <Box
      sx={{
        position: 'relative',
        overflow: 'hidden',
        p: { xs: 2.5, sm: 3 },
        mb: 2.5,
        borderRadius: 3,
        color: '#fff',
        background: manual
          ? 'linear-gradient(125deg, #402070 0%, #7136a8 58%, #a04bb6 100%)'
          : 'linear-gradient(125deg, #075e54 0%, #087c6d 58%, #10a57e 100%)',
        boxShadow: manual
          ? '0 14px 30px rgba(87, 38, 125, .17)'
          : '0 14px 30px rgba(5, 110, 91, .17)',
      }}
    >
      <Box sx={{ position: 'absolute', width: 210, height: 210, borderRadius: '50%', right: -55, top: -100, bgcolor: 'rgba(255,255,255,.08)' }} />
      <Box sx={{ position: 'absolute', width: 120, height: 120, borderRadius: '50%', right: 115, bottom: -85, bgcolor: 'rgba(255,255,255,.06)' }} />
      <Stack direction={{ xs: 'column', sm: 'row' }} alignItems={{ xs: 'flex-start', sm: 'center' }} spacing={2.2} sx={{ position: 'relative' }}>
        <Box sx={{ width: 52, height: 52, flexShrink: 0, display: 'grid', placeItems: 'center', borderRadius: 2.5, bgcolor: 'rgba(255,255,255,.16)', border: '1px solid rgba(255,255,255,.22)' }}>
          {manual ? <SendRoundedIcon /> : <AutoAwesomeRoundedIcon />}
        </Box>
        <Box sx={{ flex: 1 }}>
          <Typography sx={{ fontSize: { xs: 19, sm: 21 }, fontWeight: 800, letterSpacing: '-.02em' }}>
            {manual ? 'Manual campaigns' : 'Automated campaigns'}
          </Typography>
          <Typography sx={{ mt: .5, maxWidth: 650, color: 'rgba(255,255,255,.82)', fontSize: 13.5 }}>
            {manual
              ? 'Create and send festival campaigns to your customers. Campaign creation is coming soon.'
              : 'Birthday and anniversary wishes are sent automatically, so every special moment is remembered.'}
          </Typography>
        </Box>
        <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
          {manual ? (
            <Chip icon={<CalendarMonthRoundedIcon />} label="Festivals" sx={{ color: '#fff', bgcolor: 'rgba(255,255,255,.14)', '& .MuiChip-icon': { color: '#fff' } }} />
          ) : (
            <>
              <Chip icon={<CakeRoundedIcon />} label="Birthdays" sx={{ color: '#fff', bgcolor: 'rgba(255,255,255,.14)', '& .MuiChip-icon': { color: '#fff' } }} />
              <Chip icon={<FavoriteRoundedIcon />} label="Anniversaries" sx={{ color: '#fff', bgcolor: 'rgba(255,255,255,.14)', '& .MuiChip-icon': { color: '#fff' } }} />
            </>
          )}
        </Stack>
      </Stack>
    </Box>
  )
}

function FilterField({ label, children }) {
  return (
    <Box sx={{ width: { xs: '100%', sm: 185 } }}>
      <Typography color="text.secondary" sx={{ mb: .7, fontSize: 12.5, fontWeight: 650 }}>{label}</Typography>
      {children}
    </Box>
  )
}

export default function CampaignsPage() {
  const [messages, setMessages] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [page, setPage] = useState(0)
  const [totalMessages, setTotalMessages] = useState(0)
  const [filters, setFilters] = useState(emptyFilters)
  const [appliedFilters, setAppliedFilters] = useState(emptyFilters)

  const loadMessages = useCallback(async (targetPage = page) => {
    setLoading(true)
    setError('')
    try {
      const params = { page: targetPage }
      if (appliedFilters.fromDate) params.fromDate = appliedFilters.fromDate
      if (appliedFilters.toDate) params.toDate = appliedFilters.toDate
      if (appliedFilters.campaignType) params.campaignType = appliedFilters.campaignType
      const response = await campaignsApi.listAutomatedMessages(params)
      setMessages(response?.content || [])
      setTotalMessages(response?.totalElements || 0)
    } catch (requestError) {
      setError(getApiError(requestError, 'Unable to load campaign messages'))
    } finally {
      setLoading(false)
    }
  }, [appliedFilters, page])

  useEffect(() => {
    const task = window.setTimeout(loadMessages, 0)
    return () => window.clearTimeout(task)
  }, [loadMessages])

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

  const hasFilters = Object.values(filters).some(Boolean) || Object.values(appliedFilters).some(Boolean)

  return (
    <>
      <PageHeader title="Campaigns" description="Monitor automated greetings and manage customer outreach." />

      <CampaignBanner />

      <ModuleCard sx={{ mb: 3.5 }}>
        <Box sx={{ p: { xs: 2, sm: 2.3 }, display: 'flex', flexWrap: 'wrap', alignItems: 'flex-end', gap: 1.5 }}>
          <FilterField label="Start date">
            <TextField fullWidth type="date" value={filters.fromDate} onChange={(event) => setFilters((current) => ({ ...current, fromDate: event.target.value }))} inputProps={{ 'aria-label': 'Start date' }} />
          </FilterField>
          <FilterField label="End date">
            <TextField fullWidth type="date" value={filters.toDate} onChange={(event) => setFilters((current) => ({ ...current, toDate: event.target.value }))} inputProps={{ 'aria-label': 'End date' }} />
          </FilterField>
          <FilterField label="Campaign type">
            <FormControl fullWidth size="small">
              <Select displayEmpty value={filters.campaignType} onChange={(event) => setFilters((current) => ({ ...current, campaignType: event.target.value }))} inputProps={{ 'aria-label': 'Campaign type' }}>
                <MenuItem value="">All campaigns</MenuItem>
                <MenuItem value="BIRTHDAY">Birthday</MenuItem>
                <MenuItem value="ANNIVERSARY">Anniversary</MenuItem>
              </Select>
            </FormControl>
          </FilterField>
          <Button variant="contained" onClick={applyFilters}>Apply filters</Button>
          <Button color="inherit" onClick={clearFilters} disabled={!hasFilters}>Clear</Button>
          <Typography color="text.secondary" sx={{ ml: { lg: 'auto' }, pb: 1.1, fontSize: 13 }}>
            {totalMessages} message{totalMessages === 1 ? '' : 's'}
          </Typography>
        </Box>

        <TableContainer>
          <Table sx={{ minWidth: 1420 }}>
            <TableHead>
              <TableRow>
                <TableCell>Customer</TableCell>
                <TableCell>Phone number</TableCell>
                <TableCell>Date of birth</TableCell>
                <TableCell>Anniversary</TableCell>
                <TableCell>Message type</TableCell>
                <TableCell>Status</TableCell>
                <TableCell>Failure reason</TableCell>
                <TableCell>Failed at</TableCell>
                <TableCell>Sent at</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {(loading || error || !messages.length) ? (
                <TableState columns={9} loading={loading} error={error} emptyMessage="No automated messages match the selected filters." onRetry={() => loadMessages()} />
              ) : messages.map((message) => (
                <TableRow key={message.id} hover>
                  <TableCell><Typography sx={{ fontSize: 13.5, fontWeight: 750 }}>{message.customerName || '—'}</Typography></TableCell>
                  <TableCell sx={{ fontWeight: 650, whiteSpace: 'nowrap' }}>{message.customerPhoneNumber || '—'}</TableCell>
                  <TableCell sx={{ whiteSpace: 'nowrap' }}>{formatDate(message.customerBirthDate)}</TableCell>
                  <TableCell sx={{ whiteSpace: 'nowrap' }}>{formatDate(message.customerAnniversaryDate)}</TableCell>
                  <TableCell>
                    <Chip size="small" icon={message.messageType === 'BIRTHDAY' ? <CakeRoundedIcon /> : <FavoriteRoundedIcon />} label={message.messageType} variant="outlined" sx={{ fontSize: 11.5, fontWeight: 700, '& .MuiChip-icon': { fontSize: 15 } }} />
                  </TableCell>
                  <TableCell><MessageStatus value={message.messageStatus} /></TableCell>
                  <TableCell sx={{ minWidth: 210, maxWidth: 300 }}><Typography title={message.failureReason || ''} noWrap sx={{ fontSize: 13, color: message.failureReason ? 'error.main' : 'text.secondary' }}>{message.failureReason || '—'}</Typography></TableCell>
                  <TableCell sx={{ whiteSpace: 'nowrap' }}>{formatDateTime(message.failedAt)}</TableCell>
                  <TableCell sx={{ whiteSpace: 'nowrap' }}>{formatDateTime(message.sentAt)}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
        <TablePagination component="div" count={totalMessages} page={page} onPageChange={(_, nextPage) => setPage(nextPage)} rowsPerPage={30} rowsPerPageOptions={[30]} labelRowsPerPage="Rows per page" />
      </ModuleCard>

      <CampaignBanner manual />
    </>
  )
}
