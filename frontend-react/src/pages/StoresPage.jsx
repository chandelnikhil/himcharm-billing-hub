import { useCallback, useEffect, useMemo, useState } from 'react'
import {
  Alert,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  InputAdornment,
  Link,
  Snackbar,
  Switch,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Tooltip,
  Typography,
} from '@mui/material'
import SearchRoundedIcon from '@mui/icons-material/SearchRounded'
import CloseRoundedIcon from '@mui/icons-material/CloseRounded'
import EditOutlinedIcon from '@mui/icons-material/EditOutlined'
import OpenInNewRoundedIcon from '@mui/icons-material/OpenInNewRounded'
import PageHeader from '../components/common/PageHeader'
import ModuleCard from '../components/common/ModuleCard'
import StatusChip from '../components/common/StatusChip'
import TableState from '../components/common/TableState'
import { storesApi } from '../api/services'
import { getApiError } from '../api/client'

const initialForm = { storeCode: '', name: '', phone: '', address: '', googleReviewUrl: '', active: true }
const formatDate = (value) => value ? new Intl.DateTimeFormat('en-IN', { day: '2-digit', month: 'short', year: 'numeric' }).format(new Date(value)) : '—'

export default function StoresPage() {
  const [stores, setStores] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [query, setQuery] = useState('')
  const [open, setOpen] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [form, setForm] = useState(initialForm)
  const [saving, setSaving] = useState(false)
  const [formError, setFormError] = useState('')
  const [notice, setNotice] = useState('')

  const loadStores = useCallback(async () => {
    setLoading(true); setError('')
    try { setStores((await storesApi.list()) || []) }
    catch (requestError) { setError(getApiError(requestError, 'Unable to load stores')) }
    finally { setLoading(false) }
  }, [])
  useEffect(() => {
    const task = window.setTimeout(loadStores, 0)
    return () => window.clearTimeout(task)
  }, [loadStores])

  const filteredStores = useMemo(() => {
    const needle = query.trim().toLowerCase()
    return needle ? stores.filter((store) => `${store.name} ${store.storeCode} ${store.phone} ${store.address}`.toLowerCase().includes(needle)) : stores
  }, [query, stores])

  const openNew = () => { setEditingId(null); setForm(initialForm); setFormError(''); setOpen(true) }
  const openEdit = (store) => { setEditingId(store.id); setForm({ storeCode: store.storeCode || '', name: store.name || '', phone: store.phone || '', address: store.address || '', googleReviewUrl: store.googleReviewUrl || '', active: store.active ?? true }); setFormError(''); setOpen(true) }
  const closeDialog = () => { if (!saving) setOpen(false) }
  const update = (field) => (event) => setForm((current) => ({ ...current, [field]: event.target.value }))

  const submit = async (event) => {
    event.preventDefault(); setSaving(true); setFormError('')
    try {
      if (editingId) await storesApi.update(editingId, form)
      else await storesApi.create(form)
      setOpen(false); setForm(initialForm); setNotice(`Store ${editingId ? 'updated' : 'created'} successfully`)
      await loadStores()
    } catch (requestError) { setFormError(getApiError(requestError, `Unable to ${editingId ? 'update' : 'create'} store`)) }
    finally { setSaving(false) }
  }

  return (
    <>
      <PageHeader title="Stores" description="Create and manage every Himcharm store location." actionLabel="New store" onAction={openNew} />
      <ModuleCard>
        <Box sx={{ p: 2.3, display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 2 }}><TextField placeholder="Search stores…" value={query} onChange={(event) => setQuery(event.target.value)} sx={{ width: { xs: '100%', sm: 350 } }} InputProps={{ startAdornment: <InputAdornment position="start"><SearchRoundedIcon color="action" fontSize="small" /></InputAdornment> }} /><Typography color="text.secondary" sx={{ display: { xs: 'none', sm: 'block' }, fontSize: 13 }}>{filteredStores.length} store{filteredStores.length === 1 ? '' : 's'}</Typography></Box>
        <TableContainer><Table sx={{ minWidth: 900 }}><TableHead><TableRow><TableCell>Store</TableCell><TableCell>Contact</TableCell><TableCell>Address</TableCell><TableCell>Review page</TableCell><TableCell>Status</TableCell><TableCell>Created</TableCell><TableCell align="right">Action</TableCell></TableRow></TableHead><TableBody>
          {(loading || error || !filteredStores.length) ? <TableState columns={7} loading={loading} error={error} emptyMessage={query ? 'No stores match your search.' : 'Create your first store location to get started.'} onRetry={loadStores} /> : filteredStores.map((store) => (
            <TableRow key={store.id} hover><TableCell><Typography sx={{ fontSize: 13.5, fontWeight: 700 }}>{store.name}</Typography><Typography variant="caption" color="primary.main" sx={{ fontWeight: 700 }}>{store.storeCode}</Typography></TableCell><TableCell>{store.phone || '—'}</TableCell><TableCell><Tooltip title={store.address}><Typography noWrap sx={{ maxWidth: 230, fontSize: 13 }}>{store.address}</Typography></Tooltip></TableCell><TableCell>{store.googleReviewUrl ? <Link href={store.googleReviewUrl} target="_blank" rel="noreferrer" underline="hover" sx={{ fontSize: 13, fontWeight: 650, display: 'inline-flex', alignItems: 'center', gap: .5 }}>Open <OpenInNewRoundedIcon sx={{ fontSize: 14 }} /></Link> : '—'}</TableCell><TableCell><StatusChip active={store.active} /></TableCell><TableCell>{formatDate(store.createdAt)}</TableCell><TableCell align="right"><Tooltip title="Edit store"><IconButton size="small" onClick={() => openEdit(store)}><EditOutlinedIcon fontSize="small" /></IconButton></Tooltip></TableCell></TableRow>
          ))}
        </TableBody></Table></TableContainer>
      </ModuleCard>

      <Dialog open={open} onClose={closeDialog} fullWidth maxWidth="sm" slotProps={{ paper: { sx: { borderRadius: 3 } } }}>
        <Box component="form" onSubmit={submit}>
          <DialogTitle sx={{ pr: 6 }}><Typography variant="h5">{editingId ? 'Edit store' : 'Create new store'}</Typography><Typography color="text.secondary" sx={{ fontSize: 13.5, mt: 0.5 }}>{editingId ? 'Update this store’s operational details.' : 'Add a location to your Himcharm network.'}</Typography><IconButton type="button" onClick={closeDialog} sx={{ position: 'absolute', right: 12, top: 12 }}><CloseRoundedIcon /></IconButton></DialogTitle>
          <DialogContent dividers sx={{ pt: 2.5 }}>
            {formError && <Alert severity="error" sx={{ mb: 2 }}>{formError}</Alert>}
            <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr' }, gap: 2 }}>
              <TextField required label="Store code" value={form.storeCode} onChange={update('storeCode')} slotProps={{ htmlInput: { maxLength: 30 } }} />
              <TextField required label="Store name" value={form.name} onChange={update('name')} slotProps={{ htmlInput: { maxLength: 150 } }} />
              <TextField label="Phone number" value={form.phone} onChange={update('phone')} slotProps={{ htmlInput: { maxLength: 20 } }} sx={{ gridColumn: { sm: '1 / -1' } }} />
              <TextField required multiline minRows={2} label="Address" value={form.address} onChange={update('address')} slotProps={{ htmlInput: { maxLength: 500 } }} sx={{ gridColumn: { sm: '1 / -1' } }} />
              <TextField required type="url" label="Google review URL" value={form.googleReviewUrl} onChange={update('googleReviewUrl')} slotProps={{ htmlInput: { maxLength: 1000 } }} sx={{ gridColumn: { sm: '1 / -1' } }} />
              <Box sx={{ gridColumn: { sm: '1 / -1' }, display: 'flex', justifyContent: 'space-between', alignItems: 'center', border: '1px solid', borderColor: 'divider', borderRadius: 2, px: 1.8, py: 1 }}><Box><Typography sx={{ fontSize: 13.5, fontWeight: 700 }}>Active store</Typography><Typography color="text.secondary" sx={{ fontSize: 12 }}>Available for invoices and daily operations</Typography></Box><Switch checked={form.active} onChange={(event) => setForm((current) => ({ ...current, active: event.target.checked }))} /></Box>
            </Box>
          </DialogContent>
          <DialogActions sx={{ p: 2 }}><Button type="button" onClick={closeDialog} color="inherit">Cancel</Button><Button type="submit" variant="contained" disabled={saving}>{saving ? 'Saving…' : editingId ? 'Save changes' : 'Create store'}</Button></DialogActions>
        </Box>
      </Dialog>
      <Snackbar open={Boolean(notice)} autoHideDuration={3500} onClose={() => setNotice('')} message={notice} />
    </>
  )
}
