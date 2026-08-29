import { createTheme } from '@mui/material/styles'

const theme = createTheme({
  palette: {
    mode: 'light',
    primary: { main: '#1769e0', dark: '#0f4fb5', light: '#eaf3ff' },
    success: { main: '#0aa775' },
    warning: { main: '#f59e0b' },
    background: { default: '#f5f8fc', paper: '#ffffff' },
    text: { primary: '#12233f', secondary: '#66758c' },
    divider: '#e2e9f2',
  },
  shape: { borderRadius: 12 },
  typography: {
    fontFamily: "Inter, system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif",
    h4: { fontSize: '1.65rem', fontWeight: 750, letterSpacing: '-0.025em' },
    h5: { fontSize: '1.25rem', fontWeight: 700 },
    h6: { fontSize: '1rem', fontWeight: 700 },
    button: { fontWeight: 650, textTransform: 'none' },
  },
  components: {
    MuiButton: {
      defaultProps: { disableElevation: true },
      styleOverrides: { root: { borderRadius: 9, minHeight: 40, paddingInline: 16 } },
    },
    MuiPaper: {
      styleOverrides: { root: { backgroundImage: 'none' } },
    },
    MuiTextField: {
      defaultProps: { size: 'small' },
    },
    MuiOutlinedInput: {
      styleOverrides: { root: { borderRadius: 9 } },
    },
    MuiTableCell: {
      styleOverrides: {
        head: { color: '#5a6b84', fontWeight: 700, background: '#f8fafd' },
        root: { borderColor: '#e8edf4' },
      },
    },
  },
})

export default theme
