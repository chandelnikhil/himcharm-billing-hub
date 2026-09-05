import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  InputLabel,
  MenuItem,
  Rating,
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
      <a href="https://www.facebook.com/himcharm" target="_blank" rel="noreferrer" aria-label="Facebook"><Facebook /></a>
      <a href="https://www.instagram.com/himcharm_apparels?igsh=MTd3c2RtdXB1Ym9qcA%3D%3D" target="_blank" rel="noreferrer" aria-label="Instagram"><Instagram /></a>
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

function FeedbackDialog({ open, rating, feedback, saving, onRatingChange, onFeedbackChange, onSubmit }) {
  const needsFeedback = rating > 0 && rating < 4
  const canSubmit = rating > 0 && (!needsFeedback || feedback.trim())

  return (
    <Dialog open={open} disableEscapeKeyDown fullWidth maxWidth="xs" slotProps={{ paper: { sx: { borderRadius: 3 } } }}>
      <Box component="form" onSubmit={onSubmit}>
        <DialogTitle sx={{ pb: 1, textAlign: 'center', fontWeight: 800 }}>How was your experience?</DialogTitle>
        <DialogContent sx={{ textAlign: 'center' }}>
          <Typography color="text.secondary" sx={{ mb: 2, fontSize: 14 }}>Your rating helps us serve you better.</Typography>
          <Rating value={rating} onChange={(_, value) => onRatingChange(value || 0)} size="large" sx={{ fontSize: 46 }} />
          {needsFeedback && (
            <TextField
              autoFocus
              fullWidth
              multiline
              minRows={3}
              label="Tell us what we can improve"
              value={feedback}
              onChange={(event) => onFeedbackChange(event.target.value)}
              inputProps={{ maxLength: 2000 }}
              sx={{ mt: 2.5, textAlign: 'left' }}
            />
          )}
          {rating >= 4 && <Typography sx={{ mt: 2, color: 'success.main', fontSize: 13.5 }}>Thank you! You’ll be taken to Google after your rating is saved.</Typography>}
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2.5 }}>
          <Button type="submit" variant="contained" color="success" disabled={saving || !canSubmit} startIcon={saving ? <CircularProgress size={17} color="inherit" /> : <RateReviewOutlined />}>
            {saving ? 'Saving…' : 'Submit rating'}
          </Button>
        </DialogActions>
      </Box>
    </Dialog>
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
  const [feedbackOpen, setFeedbackOpen] = useState(false)
  const [rating, setRating] = useState(0)
  const [feedback, setFeedback] = useState('')
  const [savingFeedback, setSavingFeedback] = useState(false)

  useEffect(() => {
    const controller = new AbortController()

    if (!invoiceNumber) {
      return () => controller.abort()
    }

    publicInvoiceApi.getReviewDetails(invoiceNumber, controller.signal)
      .then((data) => {
        setDetails(data)
        setFeedbackOpen(true)
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

  const submitFeedback = async (event) => {
    event.preventDefault()
    if (!rating || (rating < 4 && !feedback.trim())) return

    const shouldOpenGoogle = rating >= 4 && Boolean(details?.googleReviewUrl)
    const googleReviewTab = shouldOpenGoogle ? window.open('about:blank', '_blank') : null
    if (googleReviewTab) googleReviewTab.opener = null

    setSavingFeedback(true)
    try {
      await publicInvoiceApi.saveFeedback(invoiceNumber, {
        rating,
        feedback: rating < 4 ? feedback.trim() : null,
      })
      setFeedbackOpen(false)
      setNotice({ severity: 'success', message: 'Thank you. Your feedback has been saved.' })
      if (googleReviewTab) {
        googleReviewTab.location.href = details.googleReviewUrl
      } else if (shouldOpenGoogle) {
        window.open(details.googleReviewUrl, '_blank', 'noopener,noreferrer')
      }
    } catch (requestError) {
      googleReviewTab?.close()
      setNotice({ severity: 'error', message: getApiError(requestError, 'We could not save your feedback. Please try again.') })
    } finally {
      setSavingFeedback(false)
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
              onClick={() => setFeedbackOpen(true)}
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
      <FeedbackDialog
        open={feedbackOpen}
        rating={rating}
        feedback={feedback}
        saving={savingFeedback}
        onRatingChange={setRating}
        onFeedbackChange={setFeedback}
        onSubmit={submitFeedback}
      />
    </div>
  )
}
