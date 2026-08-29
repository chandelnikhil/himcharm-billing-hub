import { useCallback, useEffect, useMemo, useState } from 'react'
import {
  Alert,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Divider,
  IconButton,
  InputAdornment,
  MenuItem,
  Snackbar,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  TextField,
  Tooltip,
  Typography,
} from '@mui/material'
import SearchRoundedIcon from '@mui/icons-material/SearchRounded'
import CloseRoundedIcon from '@mui/icons-material/CloseRounded'
import DeleteOutlineRoundedIcon from '@mui/icons-material/DeleteOutlineRounded'
import AddRoundedIcon from '@mui/icons-material/AddRounded'
import VisibilityOutlinedIcon from '@mui/icons-material/VisibilityOutlined'
import PageHeader from '../components/common/PageHeader'
import ModuleCard from '../components/common/ModuleCard'
import StatusChip from '../components/common/StatusChip'
import TableState from '../components/common/TableState'
import { invoicesApi, storesApi } from '../api/services'
import { getApiError } from '../api/client'

const blankItem = () => ({ itemName: '', quantity: 1, unitPrice: '', discountPercentage: 0 })
const initialForm = () => ({ storeId: '', customerPhoneNumber: '', paymentMode: 'UPI', items: [blankItem()] })
const money = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', minimumFractionDigits: 2 })
const formatDate = (value) => value ? new Intl.DateTimeFormat('en-IN', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' }).format(new Date(value)) : '—'

export default function InvoicesPage() {
  const [invoices, setInvoices] = useState([])
  const [stores, setStores] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [query, setQuery] = useState('')
  const [open, setOpen] = useState(false)
  const [detail, setDetail] = useState(null)
  const [form, setForm] = useState(initialForm)
  const [saving, setSaving] = useState(false)
  const [formError, setFormError] = useState('')
  const [notice, setNotice] = useState('')
  const [page, setPage] = useState(0)
  const [totalInvoices, setTotalInvoices] = useState(0)
  const [filters, setFilters] = useState({ fromDate: '', toDate: '', storeId: '' })
  const [appliedFilters, setAppliedFilters] = useState({ fromDate: '', toDate: '', storeId: '' })

  const loadData = useCallback(async (targetPage = page) => {
    setLoading(true); setError('')
    try {
      const params = { page: targetPage }
      if (appliedFilters.fromDate) params.fromDate = appliedFilters.fromDate
      if (appliedFilters.toDate) params.toDate = appliedFilters.toDate
      if (appliedFilters.storeId) params.storeId = appliedFilters.storeId
      const [invoiceData, storeData] = await Promise.all([invoicesApi.list(params), storesApi.list()])
      setInvoices(invoiceData?.content || [])
      setTotalInvoices(invoiceData?.totalElements || 0)
      setStores(storeData || [])
    } catch (requestError) { setError(getApiError(requestError, 'Unable to load invoices')) }
    finally { setLoading(false) }
  }, [appliedFilters, page])
  useEffect(() => {
    const task = window.setTimeout(loadData, 0)
    return () => window.clearTimeout(task)
  }, [loadData])

  const storeMap = useMemo(() => new Map(stores.map((store) => [String(store.id), store])), [stores])
  const filteredInvoices = useMemo(() => {
    const needle = query.trim().toLowerCase()
    return needle ? invoices.filter((invoice) => `${invoice.invoiceNumber} ${invoice.paymentMode} ${storeMap.get(String(invoice.storeId))?.name || ''}`.toLowerCase().includes(needle)) : invoices
  }, [invoices, query, storeMap])
  const total = form.items.reduce((sum, item) => sum + (Number(item.quantity) * Number(item.unitPrice || 0) * (1 - Number(item.discountPercentage || 0) / 100)), 0)

  const updateItem = (index, field, value) => setForm((current) => ({ ...current, items: current.items.map((item, itemIndex) => itemIndex === index ? { ...item, [field]: value } : item) }))
  const removeItem = (index) => setForm((current) => ({ ...current, items: current.items.filter((_, itemIndex) => itemIndex !== index) }))
  const closeDialog = () => { if (!saving) setOpen(false) }
  const applyFilters = () => {
    if (filters.fromDate && filters.toDate && filters.fromDate > filters.toDate) {
      setError('From date cannot be after to date')
      return
    }
    setError('')
    setPage(0)
    setAppliedFilters(filters)
  }
  const clearFilters = () => {
    const emptyFilters = { fromDate: '', toDate: '', storeId: '' }
    setFilters(emptyFilters)
    setPage(0)
    setAppliedFilters(emptyFilters)
  }

  const submit = async (event) => {
    event.preventDefault(); setSaving(true); setFormError('')
    const invalidItem = form.items.some((item) => !item.itemName.trim() || Number(item.quantity) < 1 || Number(item.unitPrice) < 0 || Number(item.discountPercentage) < 0 || Number(item.discountPercentage) > 100)
    if (invalidItem) { setFormError('Check each item name, quantity, price, and discount.'); setSaving(false); return }
    const payload = { ...form, storeId: Number(form.storeId), items: form.items.map((item) => ({ itemName: item.itemName.trim(), quantity: Number(item.quantity), unitPrice: Number(item.unitPrice), discountPercentage: Number(item.discountPercentage || 0) })) }
    try {
      await invoicesApi.create(payload)
      setOpen(false); setForm(initialForm()); setNotice('Invoice created successfully')
      setPage(0)
      await loadData(0)
    } catch (requestError) { setFormError(getApiError(requestError, 'Unable to create invoice')) }
    finally { setSaving(false) }
  }

  return (
    <>
      <PageHeader title="Invoices" description="Create invoices and review transactions across your stores." actionLabel="New invoice" onAction={() => { setForm(initialForm()); setFormError(''); setOpen(true) }} />
      <ModuleCard>
        <Box sx={{ p: 2.3, display: 'flex', flexWrap: 'wrap', alignItems: 'flex-end', gap: 1.5 }}>
          <Box sx={{ width: { xs: '100%', md: 280 } }}><Typography color="text.secondary" sx={{ mb: .7, fontSize: 12.5, fontWeight: 650 }}>Search</Typography><TextField fullWidth size="small" placeholder="Search this page…" value={query} onChange={(event) => setQuery(event.target.value)} InputProps={{ startAdornment: <InputAdornment position="start"><SearchRoundedIcon color="action" fontSize="small" /></InputAdornment> }} /></Box>
          <Box sx={{ width: { xs: '100%', sm: 180 } }}><Typography color="text.secondary" sx={{ mb: .7, fontSize: 12.5, fontWeight: 650 }}>Start date</Typography><TextField fullWidth size="small" type="date" value={filters.fromDate} onChange={(event) => setFilters((current) => ({ ...current, fromDate: event.target.value }))} inputProps={{ 'aria-label': 'Start date' }} /></Box>
          <Box sx={{ width: { xs: '100%', sm: 180 } }}><Typography color="text.secondary" sx={{ mb: .7, fontSize: 12.5, fontWeight: 650 }}>End date</Typography><TextField fullWidth size="small" type="date" value={filters.toDate} onChange={(event) => setFilters((current) => ({ ...current, toDate: event.target.value }))} inputProps={{ 'aria-label': 'End date' }} /></Box>
          <Box sx={{ width: { xs: '100%', sm: 170 } }}><Typography color="text.secondary" sx={{ mb: .7, fontSize: 12.5, fontWeight: 650 }}>Store</Typography><TextField fullWidth select size="small" value={filters.storeId} onChange={(event) => setFilters((current) => ({ ...current, storeId: event.target.value }))} inputProps={{ 'aria-label': 'Store' }}><MenuItem value="">All stores</MenuItem>{stores.map((store) => <MenuItem key={store.id} value={store.id}>{store.storeCode}</MenuItem>)}</TextField></Box>
          <Button variant="contained" onClick={applyFilters} sx={{ height: 40 }}>Apply</Button>
          <Button color="inherit" onClick={clearFilters} disabled={!filters.fromDate && !filters.toDate && !filters.storeId && !appliedFilters.fromDate && !appliedFilters.toDate && !appliedFilters.storeId} sx={{ height: 40 }}>Clear</Button>
          <Typography color="text.secondary" sx={{ ml: { sm: 'auto' }, fontSize: 13 }}>{totalInvoices} invoice{totalInvoices === 1 ? '' : 's'}</Typography>
        </Box>
        <TableContainer><Table sx={{ minWidth: 900 }}><TableHead><TableRow><TableCell>Invoice number</TableCell><TableCell>Store</TableCell><TableCell>Date</TableCell><TableCell>Payment</TableCell><TableCell>WhatsApp</TableCell><TableCell align="right">Items</TableCell><TableCell align="right">Amount</TableCell><TableCell align="right">Action</TableCell></TableRow></TableHead><TableBody>
          {(loading || error || !filteredInvoices.length) ? <TableState columns={8} loading={loading} error={error} emptyMessage={query ? 'No invoices match your search.' : 'Create your first invoice to begin tracking sales.'} onRetry={() => loadData()} /> : filteredInvoices.map((invoice) => (
            <TableRow key={invoice.id} hover><TableCell><Typography sx={{ color: 'primary.main', fontSize: 13.5, fontWeight: 750 }}>{invoice.invoiceNumber || `#${invoice.id}`}</Typography></TableCell><TableCell>{storeMap.get(String(invoice.storeId))?.name || `Store #${invoice.storeId}`}</TableCell><TableCell>{formatDate(invoice.invoiceDate)}</TableCell><TableCell><StatusChip active="SUCCESS" label={invoice.paymentMode || 'Not set'} /></TableCell><TableCell><StatusChip active={invoice.whatsappStatus} label={invoice.whatsappStatus || 'NOT SENT'} /></TableCell><TableCell align="right">{invoice.items?.length || 0}</TableCell><TableCell align="right" sx={{ fontWeight: 750 }}>{money.format(invoice.totalAmount || 0)}</TableCell><TableCell align="right"><Tooltip title="View invoice"><IconButton size="small" onClick={() => setDetail(invoice)}><VisibilityOutlinedIcon fontSize="small" /></IconButton></Tooltip></TableCell></TableRow>
          ))}
        </TableBody></Table></TableContainer>
        <TablePagination component="div" count={totalInvoices} page={page} onPageChange={(_, nextPage) => setPage(nextPage)} rowsPerPage={30} rowsPerPageOptions={[30]} labelRowsPerPage="Rows per page" />
      </ModuleCard>

      <Dialog open={open} onClose={closeDialog} fullWidth maxWidth="md" slotProps={{ paper: { sx: { borderRadius: 3 } } }}>
        <Box component="form" onSubmit={submit}>
        <DialogTitle sx={{ pr: 6 }}><Typography variant="h5">Create invoice</Typography><Typography color="text.secondary" sx={{ fontSize: 13.5, mt: 0.5 }}>Enter customer and item details. Totals are calculated automatically.</Typography><IconButton type="button" onClick={closeDialog} sx={{ position: 'absolute', right: 12, top: 12 }}><CloseRoundedIcon /></IconButton></DialogTitle>
        <DialogContent dividers sx={{ pt: 2.5 }}>
          {formError && <Alert severity="error" sx={{ mb: 2 }}>{formError}</Alert>}
          {!stores.length && !loading && <Alert severity="info" sx={{ mb: 2 }}>Create an active store before creating an invoice.</Alert>}
          <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: '1.2fr 1fr 1fr' }, gap: 2 }}>
            <TextField select required label="Store" value={form.storeId} onChange={(event) => setForm((current) => ({ ...current, storeId: event.target.value }))}>{stores.filter((store) => store.active).map((store) => <MenuItem key={store.id} value={store.id}>{store.name} ({store.storeCode})</MenuItem>)}</TextField>
            <TextField required label="Customer phone" value={form.customerPhoneNumber} onChange={(event) => setForm((current) => ({ ...current, customerPhoneNumber: event.target.value }))} inputProps={{ maxLength: 20 }} />
            <TextField select label="Payment mode" value={form.paymentMode} onChange={(event) => setForm((current) => ({ ...current, paymentMode: event.target.value }))}>{['UPI', 'CREDIT', 'DEBIT'].map((mode) => <MenuItem key={mode} value={mode}>{mode}</MenuItem>)}</TextField>
          </Box>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mt: 3.5, mb: 1.5 }}><Typography variant="h6">Invoice items</Typography><Button type="button" size="small" startIcon={<AddRoundedIcon />} onClick={() => setForm((current) => ({ ...current, items: [...current.items, blankItem()] }))}>Add item</Button></Box>
          <Box sx={{ display: 'grid', gap: 1.3 }}>
            {form.items.map((item, index) => (
              <Box key={index} sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr 1fr', sm: 'minmax(180px, 2fr) .7fr 1fr .8fr auto' }, gap: 1.2, alignItems: 'start', p: 1.5, bgcolor: '#f8fafd', border: '1px solid', borderColor: 'divider', borderRadius: 2 }}>
                <TextField required label="Item name" value={item.itemName} onChange={(event) => updateItem(index, 'itemName', event.target.value)} inputProps={{ maxLength: 255 }} sx={{ gridColumn: { xs: '1 / -1', sm: 'auto' } }} />
                <TextField required type="number" label="Qty" value={item.quantity} onChange={(event) => updateItem(index, 'quantity', event.target.value)} inputProps={{ min: 1, step: 1 }} />
                <TextField required type="number" label="Unit price" value={item.unitPrice} onChange={(event) => updateItem(index, 'unitPrice', event.target.value)} inputProps={{ min: 0, step: .01 }} />
                <TextField type="number" label="Discount %" value={item.discountPercentage} onChange={(event) => updateItem(index, 'discountPercentage', event.target.value)} inputProps={{ min: 0, max: 100, step: .01 }} />
                <IconButton type="button" disabled={form.items.length === 1} onClick={() => removeItem(index)} color="error" sx={{ mt: .2 }}><DeleteOutlineRoundedIcon /></IconButton>
              </Box>
            ))}
          </Box>
          <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 2.5 }}><Box sx={{ width: { xs: '100%', sm: 280 }, p: 2, bgcolor: '#edf5ff', borderRadius: 2, display: 'flex', justifyContent: 'space-between' }}><Typography fontWeight={650}>Invoice total</Typography><Typography fontWeight={800} color="primary.main">{money.format(total)}</Typography></Box></Box>
        </DialogContent>
        <DialogActions sx={{ p: 2 }}><Button type="button" onClick={closeDialog} color="inherit">Cancel</Button><Button type="submit" variant="contained" disabled={saving || !stores.length}>{saving ? 'Creating…' : 'Create invoice'}</Button></DialogActions>
        </Box>
      </Dialog>

      <Dialog open={Boolean(detail)} onClose={() => setDetail(null)} fullWidth maxWidth="sm" slotProps={{ paper: { sx: { borderRadius: 3 } } }}>
        {detail && <><DialogTitle sx={{ pr: 6 }}><Typography variant="h5">{detail.invoiceNumber || `Invoice #${detail.id}`}</Typography><Typography color="text.secondary" sx={{ fontSize: 13.5, mt: .5 }}>{formatDate(detail.invoiceDate)}</Typography><IconButton onClick={() => setDetail(null)} sx={{ position: 'absolute', right: 12, top: 12 }}><CloseRoundedIcon /></IconButton></DialogTitle><DialogContent dividers>
          <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2, mb: 2.5 }}><Box><Typography variant="caption" color="text.secondary">STORE</Typography><Typography fontWeight={700}>{storeMap.get(String(detail.storeId))?.name || `Store #${detail.storeId}`}</Typography></Box><Box><Typography variant="caption" color="text.secondary">PAYMENT</Typography><Typography fontWeight={700}>{detail.paymentMode || 'Not set'}</Typography></Box></Box>
          <Divider sx={{ mb: 2 }} />
          {detail.items?.map((item) => <Box key={item.id} sx={{ display: 'flex', justifyContent: 'space-between', gap: 2, py: 1.2 }}><Box><Typography fontWeight={650} sx={{ fontSize: 13.5 }}>{item.itemName}</Typography><Typography variant="caption" color="text.secondary">{item.quantity} × {money.format(item.unitPrice)}{item.discountPercentage ? ` · ${item.discountPercentage}% off` : ''}</Typography></Box><Typography fontWeight={700}>{money.format(item.lineTotal || 0)}</Typography></Box>)}
          <Divider sx={{ my: 2 }} /><Box sx={{ display: 'flex', justifyContent: 'space-between' }}><Typography variant="h6">Total</Typography><Typography variant="h6" color="primary.main">{money.format(detail.totalAmount || 0)}</Typography></Box>
        </DialogContent><DialogActions sx={{ p: 2 }}><Button variant="contained" onClick={() => setDetail(null)}>Done</Button></DialogActions></>}
      </Dialog>
      <Snackbar open={Boolean(notice)} autoHideDuration={3500} onClose={() => setNotice('')} message={notice} />
    </>
  )
}
