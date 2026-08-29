import { Box, Button, Typography } from '@mui/material'
import AddRoundedIcon from '@mui/icons-material/AddRounded'

export default function PageHeader({ title, description, actionLabel, onAction }) {
  return (
    <Box sx={{ display: 'flex', alignItems: { xs: 'flex-start', sm: 'center' }, justifyContent: 'space-between', gap: 2, mb: 3 }}>
      <Box>
        <Typography variant="h4">{title}</Typography>
        {description && <Typography color="text.secondary" sx={{ mt: 0.5, fontSize: 14 }}>{description}</Typography>}
      </Box>
      {actionLabel && (
        <Button type="button" variant="contained" startIcon={<AddRoundedIcon />} onClick={onAction} sx={{ flexShrink: 0 }}>
          {actionLabel}
        </Button>
      )}
    </Box>
  )
}
