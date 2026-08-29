import { Paper } from '@mui/material'

export default function ModuleCard({ children, sx }) {
  return (
    <Paper variant="outlined" sx={{ borderColor: 'divider', borderRadius: 3, overflow: 'hidden', boxShadow: '0 4px 18px rgba(31, 64, 104, .035)', ...sx }}>
      {children}
    </Paper>
  )
}
