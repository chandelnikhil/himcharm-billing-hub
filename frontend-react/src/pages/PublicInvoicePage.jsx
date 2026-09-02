import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  Snackbar,
  TextField,
  Typography,
} from '@mui/material'
import {
  DescriptionOutlined,
  Facebook,
  Instagram,
  LinkedIn,
  LocalPhoneOutlined,
  PersonOutlineOutlined,
  RateReviewOutlined,
  SaveOutlined,
} from '@mui/icons-material'
import { getApiError } from '../api/client'
import { publicInvoiceApi } from '../api/services'
import './PublicInvoicePage.css'

const emptyProfile = {
  name: '',
  dateOfBirth: '',
  phone: '',
  email: '',
  gender: '',
  maritalStatus: '',
  anniversaryDate: '',
  spouseName: '',
  spouseDateOfBirth: '',
}

const currencyFormatter = new Intl.NumberFormat('en-IN', {
  style: 'currency',
  currency: 'INR',
  minimumFractionDigits: 2,
})

const formatCurrency = (value) => currencyFormatter.format(Number(value) || 0)

const formatInvoiceDate = (value) => {
  if (!value) return '—'
  return new Intl.DateTimeFormat('en-IN', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value))
}

const formatPaymentMode = (value) => value
  ? value.replaceAll('_', ' ').toLowerCase().replace(/\b\w/g, (letter) => letter.toUpperCase())
  : 'Not specified'

function SocialLinks() {
  return (
    <div className="public-socials" aria-label="Himcharm social media">
      <a href="https://www.facebook.com" target="_blank" rel="noreferrer" aria-label="Facebook"><Facebook /></a>
      <a href="https://www.instagram.com" target="_blank" rel="noreferrer" aria-label="Instagram"><Instagram /></a>
      <a href="https://www.linkedin.com" target="_blank" rel="noreferrer" aria-label="LinkedIn"><LinkedIn /></a>
    </div>
  )
}

function InvoiceView({ invoice, store, customer }) {
  if (!invoice) {
    return (
      <Alert severity="info">Invoice details are not available yet.</Alert>
    )
  }

  const savings = Math.max(0, Number(invoice.subtotal || 0) - Number(invoice.totalAmount || 0))

  return (
    <section className="invoice-card">
      <div className="bill-accent" />
      <div className="bill-heading">
        <div>
          <span className="bill-label">Tax invoice</span>
          <Typography component="h1">{store?.name || 'Himcharm'}</Typography>
          {store?.address && <Typography className="store-address">{store.address}</Typography>}
          {store?.phone && <Typography className="store-phone"><LocalPhoneOutlined /> {store.phone}</Typography>}
        </div>
        <img src="/HimCharm.png" alt="" className="bill-logo" />
      </div>

      <div className="bill-meta">
        <div><span>Invoice number</span><strong>{invoice.invoiceNumber}</strong></div>
        <div><span>Invoice date</span><strong>{formatInvoiceDate(invoice.invoiceDate)}</strong></div>
        <div><span>Billed to</span><strong>{customer?.name || 'Valued customer'}</strong></div>
        <div><span>Mobile number</span><strong>{customer?.phone || '—'}</strong></div>
      </div>

      <div className="bill-items-wrap">
        <table className="bill-items">
          <thead>
            <tr>
              <th>Item</th>
              <th>Qty.</th>
              <th>Rate</th>
              <th>Discount</th>
              <th>Amount</th>
            </tr>
          </thead>
          <tbody>
            {(invoice.items || []).map((item, index) => (
              <tr key={item.id || `${item.itemName}-${index}`}>
                <td data-label="Item">{item.itemName}</td>
                <td data-label="Qty.">{item.quantity}</td>
                <td data-label="Rate">{formatCurrency(item.unitPrice)}</td>
                <td data-label="Discount">{Number(item.discountPercentage || 0).toFixed(2)}%</td>
                <td data-label="Amount">{formatCurrency(item.lineTotal)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="bill-summary">
        <div><span>Subtotal</span><strong>{formatCurrency(invoice.subtotal)}</strong></div>
        {savings > 0 && <div className="savings"><span>You saved</span><strong>− {formatCurrency(savings)}</strong></div>}
        <div className="bill-total"><span>Total paid</span><strong>{formatCurrency(invoice.totalAmount)}</strong></div>
        <div className="payment-mode"><span>Payment mode</span><strong>{formatPaymentMode(invoice.paymentMode)}</strong></div>
      </div>

      <div className="bill-thanks">
        <Typography>Thank you for shopping with us</Typography>
        <span>This is a computer-generated invoice.</span>
      </div>
    </section>
  )
}

function ProfileForm({ profile, onChange, onSubmit, saving }) {
  const isMarried = profile.maritalStatus === 'MARRIED'
  const field = (name) => ({
    value: profile[name],
    onChange: (event) => onChange(name, event.target.value),
  })

  return (
    <section className="profile-card">
      <div className="rewards-banner">
        <div className="rewards-banner-copy">
          <Typography component="h1">Complete your profile</Typography>
          <Typography>Share your special days and unlock exciting rewards.</Typography>
        </div>
      </div>

      <Box component="form" className="profile-form" onSubmit={onSubmit}>
        <TextField label="Name" placeholder="Your full name" required fullWidth {...field('name')} />
        <TextField label="Date of birth" type="date" required fullWidth slotProps={{ inputLabel: { shrink: true } }} {...field('dateOfBirth')} />

        <div className="form-grid">
          <TextField label="Mobile number" required disabled fullWidth {...field('phone')} />
          <TextField label="Email" type="email" placeholder="Your email" fullWidth {...field('email')} />
          <FormControl fullWidth>
            <InputLabel id="gender-label">Gender</InputLabel>
            <Select labelId="gender-label" label="Gender" {...field('gender')}>
              <MenuItem value=""><em>Prefer not to say</em></MenuItem>
              <MenuItem value="FEMALE">Female</MenuItem>
              <MenuItem value="MALE">Male</MenuItem>
              <MenuItem value="OTHER">Other</MenuItem>
            </Select>
          </FormControl>
          <FormControl required fullWidth>
            <InputLabel id="marital-label">Marital status</InputLabel>
            <Select labelId="marital-label" label="Marital status" {...field('maritalStatus')}>
              <MenuItem value="SINGLE">Single</MenuItem>
              <MenuItem value="MARRIED">Married</MenuItem>
              <MenuItem value="OTHER">Other</MenuItem>
            </Select>
          </FormControl>
        </div>

        {isMarried && (
          <div className="marriage-fields">
            <TextField label="Anniversary date" type="date" required fullWidth slotProps={{ inputLabel: { shrink: true } }} {...field('anniversaryDate')} />
            <div className="form-grid">
              <TextField label="Spouse name" placeholder="Your spouse's name" fullWidth {...field('spouseName')} />
              <TextField label="Spouse date of birth" type="date" fullWidth slotProps={{ inputLabel: { shrink: true } }} {...field('spouseDateOfBirth')} />
            </div>
          </div>
        )}

        <Button className="save-profile-button" type="submit" variant="contained" color="success" startIcon={saving ? <CircularProgress size={18} color="inherit" /> : <SaveOutlined />} disabled={saving}>
          {saving ? 'Saving…' : 'Save profile'}
        </Button>
      </Box>
    </section>
  )
}

export default function PublicInvoicePage() {
  const [searchParams] = useSearchParams()
  const invoiceNumber = searchParams.get('invoiceNumber') || ''
  const [activeTab, setActiveTab] = useState('invoice')
  const [details, setDetails] = useState(null)
  const [profile, setProfile] = useState(emptyProfile)
  const [loading, setLoading] = useState(Boolean(invoiceNumber))
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)
  const [notice, setNotice] = useState(null)

  useEffect(() => {
    const controller = new AbortController()

    if (!invoiceNumber) {
      return () => controller.abort()
    }

    publicInvoiceApi.getReviewDetails(invoiceNumber, controller.signal)
      .then((data) => {
        setDetails(data)
        const customer = data.customerProfile || {}
        setProfile({
          ...emptyProfile,
          name: customer.name || '',
          dateOfBirth: customer.dateOfBirth || '',
          phone: customer.phone || data.customerPhoneNumber || '',
          email: customer.email || '',
          gender: customer.gender || '',
          maritalStatus: customer.maritalStatus || '',
          anniversaryDate: customer.anniversaryDate || '',
          spouseName: customer.spouseName || '',
          spouseDateOfBirth: customer.spouseDateOfBirth || '',
        })
      })
      .catch((requestError) => {
        if (requestError.code !== 'ERR_CANCELED') {
          setError(getApiError(requestError, 'We could not open this invoice link.'))
        }
      })
      .finally(() => setLoading(false))

    return () => controller.abort()
  }, [invoiceNumber])

  const displayError = error || (!invoiceNumber
    ? 'This invoice link is incomplete. Please open the original link sent on WhatsApp.'
    : '')

  const updateField = (name, value) => {
    setProfile((current) => {
      const next = { ...current, [name]: value }
      if (name === 'maritalStatus' && value !== 'MARRIED') {
        next.anniversaryDate = ''
        next.spouseName = ''
        next.spouseDateOfBirth = ''
      }
      return next
    })
  }

  const submitProfile = async (event) => {
    event.preventDefault()
    setSaving(true)
    try {
      await publicInvoiceApi.updateProfile(invoiceNumber, {
        name: profile.name,
        phone: profile.phone,
        email: profile.email || null,
        dateOfBirth: profile.dateOfBirth || null,
        gender: profile.gender || null,
        maritalStatus: profile.maritalStatus || null,
        anniversaryDate: profile.anniversaryDate || null,
        spouseName: profile.spouseName || null,
        spouseDateOfBirth: profile.spouseDateOfBirth || null,
      })
      setNotice({ severity: 'success', message: 'Your profile has been saved.' })
    } catch (requestError) {
      setNotice({ severity: 'error', message: getApiError(requestError, 'We could not save your profile. Please try again.') })
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="public-invoice-page">
      <main className="public-content">
        {loading ? (
          <div className="public-loading"><CircularProgress color="success" /><Typography>Opening your invoice…</Typography></div>
        ) : (
          <>
            {displayError && <Alert severity="error" className="public-error">{displayError}</Alert>}
            <Button
              className="google-review-button"
              variant="contained"
              startIcon={<RateReviewOutlined />}
              disabled={!details?.googleReviewUrl}
              onClick={() => window.open(details.googleReviewUrl, '_blank', 'noopener,noreferrer')}
            >
              Rate us on Google
            </Button>

            {activeTab === 'invoice' ? (
              <InvoiceView invoice={details?.invoice} store={details?.store} customer={details?.customerProfile} />
            ) : (
              <ProfileForm profile={profile} onChange={updateField} onSubmit={submitProfile} saving={saving} />
            )}

            <footer className="public-footer">
              <Typography>Stay connected with Himcharm</Typography>
              <SocialLinks />
              <Typography className="footer-note">Thank you for shopping with us.</Typography>
            </footer>
          </>
        )}
      </main>

      <nav className="public-bottom-nav" aria-label="Invoice navigation">
        <button className={activeTab === 'invoice' ? 'active' : ''} onClick={() => setActiveTab('invoice')}>
          <DescriptionOutlined /><span>Invoice</span>
        </button>
        <button className={activeTab === 'profile' ? 'active' : ''} onClick={() => setActiveTab('profile')}>
          <PersonOutlineOutlined /><span>My Profile</span>
        </button>
      </nav>

      <Snackbar open={Boolean(notice)} autoHideDuration={5000} onClose={() => setNotice(null)} anchorOrigin={{ vertical: 'top', horizontal: 'center' }}>
        {notice ? <Alert severity={notice.severity} onClose={() => setNotice(null)}>{notice.message}</Alert> : undefined}
      </Snackbar>
    </div>
  )
}
