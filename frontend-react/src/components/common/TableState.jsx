import { Box, Button, CircularProgress, TableCell, TableRow, Typography } from '@mui/material'
import InboxRoundedIcon from '@mui/icons-material/InboxRounded'
import RefreshRoundedIcon from '@mui/icons-material/RefreshRounded'

export default function TableState({ columns, loading, error, emptyMessage, onRetry }) {
  return (
    <TableRow>
      <TableCell colSpan={columns} sx={{ border: 0 }}>
        <Box sx={{ minHeight: 260, display: 'grid', placeItems: 'center', textAlign: 'center', p: 3 }}>
          {loading ? (
            <Box><CircularProgress size={30} /><Typography color="text.secondary" sx={{ mt: 1.5 }}>Loading data…</Typography></Box>
          ) : error ? (
            <Box>
              <Typography fontWeight={700}>Unable to load data</Typography>
              <Typography color="text.secondary" sx={{ mt: 0.5, mb: 2, fontSize: 14 }}>{error}</Typography>
              <Button variant="outlined" startIcon={<RefreshRoundedIcon />} onClick={onRetry}>Try again</Button>
            </Box>
          ) : (
            <Box><InboxRoundedIcon sx={{ color: '#a8b7ca', fontSize: 42 }} /><Typography fontWeight={700} sx={{ mt: 1 }}>No records yet</Typography><Typography color="text.secondary" sx={{ fontSize: 14 }}>{emptyMessage}</Typography></Box>
          )}
        </Box>
      </TableCell>
    </TableRow>
  )
}
