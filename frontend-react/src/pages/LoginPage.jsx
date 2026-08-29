import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  IconButton,
  InputAdornment,
  Paper,
  TextField,
  Typography,
} from '@mui/material'
import ShoppingBagRoundedIcon from '@mui/icons-material/ShoppingBagRounded'
import VisibilityOutlinedIcon from '@mui/icons-material/VisibilityOutlined'
import VisibilityOffOutlinedIcon from '@mui/icons-material/VisibilityOffOutlined'
import ArrowForwardRoundedIcon from '@mui/icons-material/ArrowForwardRounded'
import StorefrontRoundedIcon from '@mui/icons-material/StorefrontRounded'
import ReceiptLongRoundedIcon from '@mui/icons-material/ReceiptLongRounded'
import InsightsRoundedIcon from '@mui/icons-material/InsightsRounded'
import { useAuth } from '../auth/AuthContext'
import { getApiError } from '../api/client'

export default function LoginPage() {
  const navigate = useNavigate()
  const { login } = useAuth()
  const [form, setForm] = useState({ username: '', password: '' })
  const [showPassword, setShowPassword] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleSubmit = async (event) => {
    event.preventDefault()
    setError('')
    setLoading(true)
    try {
      await login(form)
      navigate('/dashboard', { replace: true })
    } catch (requestError) {
      setError(getApiError(requestError, 'Invalid email or password'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <Box sx={{ minHeight: '100vh', display: 'grid', gridTemplateColumns: { xs: '1fr', md: 'minmax(440px, .9fr) 1.1fr' }, bgcolor: '#fff' }}>
      <Box sx={{ display: 'flex', flexDirection: 'column', px: { xs: 3, sm: 7, lg: 10 }, py: { xs: 3, md: 5 } }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.3 }}>
          <Box sx={{ width: 40, height: 40, display: 'grid', placeItems: 'center', color: '#fff', borderRadius: 2.5, background: 'linear-gradient(145deg, #1176f4, #0b55c5)', boxShadow: '0 8px 20px rgba(23,105,224,.24)' }}>
            <ShoppingBagRoundedIcon fontSize="small" />
          </Box>
          <Box><Typography sx={{ fontWeight: 800, fontSize: 18, lineHeight: 1.1 }}>Himcharm</Typography><Typography variant="caption" color="text.secondary">Store management</Typography></Box>
        </Box>

        <Box sx={{ width: '100%', maxWidth: 430, my: 'auto', py: 7 }}>
          <Typography variant="h4" sx={{ fontSize: { xs: 28, sm: 34 }, mb: 1 }}>Welcome back</Typography>
          <Typography color="text.secondary" sx={{ mb: 4 }}>Sign in to manage your stores, users, and invoices.</Typography>
          {error && <Alert severity="error" sx={{ mb: 2.5 }}>{error}</Alert>}
          <Box component="form" onSubmit={handleSubmit} noValidate>
            <Typography component="label" htmlFor="username" sx={{ display: 'block', fontSize: 13, fontWeight: 700, mb: 0.8 }}>Email address</Typography>
            <TextField id="username" fullWidth required autoFocus placeholder="admin@himcharm.com" value={form.username} onChange={(event) => setForm({ ...form, username: event.target.value })} inputProps={{ type: 'email' }} sx={{ mb: 2.3 }} />
            <Typography component="label" htmlFor="password" sx={{ display: 'block', fontSize: 13, fontWeight: 700, mb: 0.8 }}>Password</Typography>
            <TextField
              id="password"
              fullWidth
              required
              placeholder="Enter your password"
              type={showPassword ? 'text' : 'password'}
              value={form.password}
              onChange={(event) => setForm({ ...form, password: event.target.value })}
              InputProps={{ endAdornment: <InputAdornment position="end"><IconButton onClick={() => setShowPassword((visible) => !visible)} edge="end" aria-label="Toggle password visibility">{showPassword ? <VisibilityOffOutlinedIcon /> : <VisibilityOutlinedIcon />}</IconButton></InputAdornment> }}
            />
            <Button type="submit" fullWidth variant="contained" size="large" disabled={loading || !form.username || !form.password} endIcon={!loading && <ArrowForwardRoundedIcon />} sx={{ mt: 3, height: 48 }}>
              {loading ? <CircularProgress size={22} color="inherit" /> : 'Sign in'}
            </Button>
          </Box>
          <Typography color="text.secondary" sx={{ textAlign: 'center', mt: 3, fontSize: 12.5 }}>Secure access for authorized administrators only</Typography>
        </Box>
      </Box>

      <Box sx={{ display: { xs: 'none', md: 'flex' }, position: 'relative', overflow: 'hidden', m: 2, ml: 0, borderRadius: 4, color: '#fff', background: 'linear-gradient(145deg, #0c58c7 0%, #1769e0 52%, #1f80ed 100%)', alignItems: 'center', justifyContent: 'center', p: 7 }}>
        <Box sx={{ position: 'absolute', inset: 0, opacity: .13, backgroundImage: 'radial-gradient(circle at 20% 20%, white 0 2px, transparent 2px)', backgroundSize: '34px 34px' }} />
        <Box sx={{ position: 'absolute', width: 420, height: 420, border: '80px solid rgba(255,255,255,.06)', borderRadius: '50%', right: -160, top: -150 }} />
        <Box sx={{ position: 'relative', width: '100%', maxWidth: 600 }}>
          <Typography sx={{ fontWeight: 750, fontSize: { md: 34, lg: 43 }, lineHeight: 1.15, letterSpacing: '-.03em', maxWidth: 550 }}>Everything you need to run your stores, in one place.</Typography>
          <Typography sx={{ mt: 2, mb: 5, color: 'rgba(255,255,255,.78)', fontSize: 16, maxWidth: 480 }}>A clear view of daily operations, invoices, and your team—designed to keep work moving.</Typography>
          <Box sx={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 2 }}>
            {[{ icon: StorefrontRoundedIcon, label: 'Store operations' }, { icon: ReceiptLongRoundedIcon, label: 'Smart invoicing' }, { icon: InsightsRoundedIcon, label: 'Clear insights' }].map(({ icon: Icon, label }) => (
              <Paper key={label} elevation={0} sx={{ p: 2.2, color: '#fff', bgcolor: 'rgba(255,255,255,.11)', border: '1px solid rgba(255,255,255,.16)', backdropFilter: 'blur(6px)' }}>
                <Icon sx={{ mb: 2 }} /><Typography sx={{ fontSize: 13, fontWeight: 650 }}>{label}</Typography>
              </Paper>
            ))}
          </Box>
        </Box>
      </Box>
    </Box>
  )
}
