import { useCallback, useEffect, useMemo, useState } from 'react'
import {
  Alert,
  Avatar,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  InputAdornment,
  Snackbar,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from '@mui/material'
import SearchRoundedIcon from '@mui/icons-material/SearchRounded'
import CloseRoundedIcon from '@mui/icons-material/CloseRounded'
import PageHeader from '../components/common/PageHeader'
import ModuleCard from '../components/common/ModuleCard'
import StatusChip from '../components/common/StatusChip'
import TableState from '../components/common/TableState'
import { authApi, usersApi } from '../api/services'
import { getApiError } from '../api/client'

const initialForm = { firstName: '', lastName: '', email: '', password: '' }
const formatDate = (value) => value ? new Intl.DateTimeFormat('en-IN', { day: '2-digit', month: 'short', year: 'numeric' }).format(new Date(value)) : '—'

export default function UsersPage() {
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [query, setQuery] = useState('')
  const [open, setOpen] = useState(false)
  const [form, setForm] = useState(initialForm)
  const [saving, setSaving] = useState(false)
  const [formError, setFormError] = useState('')
  const [notice, setNotice] = useState('')

  const loadUsers = useCallback(async () => {
    setLoading(true)
    setError('')
    try { setUsers((await usersApi.list()) || []) }
    catch (requestError) { setError(getApiError(requestError, 'Unable to load users')) }
    finally { setLoading(false) }
  }, [])

  useEffect(() => {
    const task = window.setTimeout(loadUsers, 0)
    return () => window.clearTimeout(task)
  }, [loadUsers])

  const filteredUsers = useMemo(() => {
    const needle = query.trim().toLowerCase()
    if (!needle) return users
    return users.filter((user) => `${user.firstName} ${user.lastName} ${user.email}`.toLowerCase().includes(needle))
  }, [query, users])

  const closeDialog = () => { if (!saving) { setOpen(false); setForm(initialForm); setFormError('') } }
  const update = (field) => (event) => setForm((current) => ({ ...current, [field]: event.target.value }))

  const submit = async (event) => {
    event.preventDefault()
    if (form.password.length < 8 || form.password.length > 15) { setFormError('Password must be between 8 and 15 characters.'); return }
    setSaving(true)
    setFormError('')
    try {
      await authApi.register(form)
      setOpen(false)
      setForm(initialForm)
      setNotice('User added successfully')
      await loadUsers()
    } catch (requestError) { setFormError(getApiError(requestError, 'Unable to add user')) }
    finally { setSaving(false) }
  }

  return (
    <>
      <PageHeader title="Users" description="Manage administrators with full access to Himcharm." actionLabel="New user" onAction={() => setOpen(true)} />
      <ModuleCard>
        <Box sx={{ p: 2.3, display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 2 }}>
          <TextField placeholder="Search users…" value={query} onChange={(event) => setQuery(event.target.value)} sx={{ width: { xs: '100%', sm: 330 } }} InputProps={{ startAdornment: <InputAdornment position="start"><SearchRoundedIcon color="action" fontSize="small" /></InputAdornment> }} />
          <Typography color="text.secondary" sx={{ display: { xs: 'none', sm: 'block' }, fontSize: 13 }}>{filteredUsers.length} user{filteredUsers.length === 1 ? '' : 's'}</Typography>
        </Box>
        <TableContainer><Table sx={{ minWidth: 720 }}><TableHead><TableRow><TableCell>User</TableCell><TableCell>Email</TableCell><TableCell>Access</TableCell><TableCell>Status</TableCell><TableCell>Added on</TableCell></TableRow></TableHead><TableBody>
          {(loading || error || !filteredUsers.length) ? <TableState columns={5} loading={loading} error={error} emptyMessage={query ? 'No users match your search.' : 'Add your first admin user to get started.'} onRetry={loadUsers} /> : filteredUsers.map((user) => (
            <TableRow key={user.id} hover><TableCell><Box sx={{ display: 'flex', alignItems: 'center', gap: 1.3 }}><Avatar sx={{ width: 36, height: 36, bgcolor: '#e5f1ff', color: 'primary.main', fontSize: 13, fontWeight: 750 }}>{`${user.firstName?.[0] || ''}${user.lastName?.[0] || ''}`.toUpperCase()}</Avatar><Box><Typography sx={{ fontSize: 13.5, fontWeight: 700 }}>{user.firstName} {user.lastName}</Typography><Typography variant="caption" color="text.secondary">ID #{user.id}</Typography></Box></Box></TableCell><TableCell>{user.email}</TableCell><TableCell><Typography sx={{ fontSize: 13 }}>Full access</Typography></TableCell><TableCell><StatusChip active={user.isActive} /></TableCell><TableCell>{formatDate(user.createdDate)}</TableCell></TableRow>
          ))}
        </TableBody></Table></TableContainer>
      </ModuleCard>

      <Dialog open={open} onClose={closeDialog} fullWidth maxWidth="sm" slotProps={{ paper: { sx: { borderRadius: 3 } } }}>
        <Box component="form" onSubmit={submit}>
          <DialogTitle sx={{ pr: 6 }}><Typography variant="h5">Add new user</Typography><Typography color="text.secondary" sx={{ fontSize: 13.5, mt: 0.5 }}>All users currently receive administrator access.</Typography><IconButton type="button" onClick={closeDialog} sx={{ position: 'absolute', right: 12, top: 12 }}><CloseRoundedIcon /></IconButton></DialogTitle>
          <DialogContent dividers sx={{ pt: 2.5 }}>
            {formError && <Alert severity="error" sx={{ mb: 2 }}>{formError}</Alert>}
            <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr' }, gap: 2 }}>
              <TextField required label="First name" value={form.firstName} onChange={update('firstName')} />
              <TextField required label="Last name" value={form.lastName} onChange={update('lastName')} />
              <TextField required type="email" label="Email address" value={form.email} onChange={update('email')} sx={{ gridColumn: { sm: '1 / -1' } }} />
              <TextField required type="password" label="Temporary password" helperText="8–15 characters" slotProps={{ htmlInput: { minLength: 8, maxLength: 15 } }} value={form.password} onChange={update('password')} sx={{ gridColumn: { sm: '1 / -1' } }} />
            </Box>
          </DialogContent>
          <DialogActions sx={{ p: 2 }}><Button type="button" onClick={closeDialog} color="inherit">Cancel</Button><Button type="submit" variant="contained" disabled={saving}>{saving ? 'Adding…' : 'Add user'}</Button></DialogActions>
        </Box>
      </Dialog>
      <Snackbar open={Boolean(notice)} autoHideDuration={3500} onClose={() => setNotice('')} message={notice} />
    </>
  )
}
