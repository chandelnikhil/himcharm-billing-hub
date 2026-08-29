import { Chip } from '@mui/material'

export default function StatusChip({ active, label }) {
  const positive = active === true || ['PAID', 'SENT', 'DELIVERED', 'SUCCESS'].includes(active)
  const warning = ['NOT_SENT', 'PENDING'].includes(active)
  const text = label || (active ? 'Active' : 'Inactive')
  return (
    <Chip
      label={text.replaceAll('_', ' ')}
      size="small"
      sx={{
        height: 25,
        fontWeight: 700,
        fontSize: 11.5,
        color: positive ? '#087a57' : warning ? '#a66000' : '#66758c',
        bgcolor: positive ? '#e4f8f1' : warning ? '#fff4d8' : '#edf1f6',
      }}
    />
  )
}
