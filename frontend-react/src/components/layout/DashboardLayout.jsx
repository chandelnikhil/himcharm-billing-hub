import { useState } from 'react'
import { Outlet, useLocation, useNavigate } from 'react-router-dom'
import {
  AppBar,
  Avatar,
  Box,
  Divider,
  Drawer,
  IconButton,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Menu,
  MenuItem,
  Toolbar,
  Tooltip,
  Typography,
  useMediaQuery,
} from '@mui/material'
import { useTheme } from '@mui/material/styles'
import DashboardRoundedIcon from '@mui/icons-material/DashboardRounded'
import AnalyticsRoundedIcon from '@mui/icons-material/AnalyticsRounded'
import ReceiptLongRoundedIcon from '@mui/icons-material/ReceiptLongRounded'
import CampaignRoundedIcon from '@mui/icons-material/CampaignRounded'
import GroupsRoundedIcon from '@mui/icons-material/GroupsRounded'
import StorefrontRoundedIcon from '@mui/icons-material/StorefrontRounded'
import MenuRoundedIcon from '@mui/icons-material/MenuRounded'
import NotificationsNoneRoundedIcon from '@mui/icons-material/NotificationsNoneRounded'
import LogoutRoundedIcon from '@mui/icons-material/LogoutRounded'
import KeyboardArrowDownRoundedIcon from '@mui/icons-material/KeyboardArrowDownRounded'
import ShoppingBagRoundedIcon from '@mui/icons-material/ShoppingBagRounded'
import PeopleAltRoundedIcon from '@mui/icons-material/PeopleAltRounded'
import { useAuth } from '../../auth/AuthContext'

const drawerWidth = 252

const navigation = [
  { label: 'Dashboard', path: '/dashboard', icon: DashboardRoundedIcon },
  { label: 'Analytics', path: '/analytics', icon: AnalyticsRoundedIcon, soon: true },
  { label: 'Invoices', path: '/invoices', icon: ReceiptLongRoundedIcon },
  { label: 'Customers', path: '/customers', icon: PeopleAltRoundedIcon },
  { label: 'Campaigns', path: '/campaigns', icon: CampaignRoundedIcon },
  { label: 'Users', path: '/users', icon: GroupsRoundedIcon },
  { label: 'Stores', path: '/stores', icon: StorefrontRoundedIcon },
]

function Brand() {
  return (
    <Box sx={{ height: 72, px: 2.5, display: 'flex', alignItems: 'center', gap: 1.4 }}>
      <Box
        sx={{
          width: 38,
          height: 38,
          display: 'grid',
          placeItems: 'center',
          color: 'white',
          borderRadius: 2.5,
          background: 'linear-gradient(145deg, #1176f4, #0b55c5)',
          boxShadow: '0 8px 18px rgba(23, 105, 224, .24)',
        }}
      >
        <ShoppingBagRoundedIcon fontSize="small" />
      </Box>
      <Box>
        <Typography variant="h6" sx={{ lineHeight: 1.15, fontSize: '1.08rem' }}>Himcharm</Typography>
        <Typography variant="caption" color="text.secondary">Store management</Typography>
      </Box>
    </Box>
  )
}

function Sidebar({ onNavigate }) {
  const location = useLocation()
  const navigate = useNavigate()
  const { user } = useAuth()

  const goTo = (path) => {
    navigate(path)
    onNavigate?.()
  }

  return (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column', bgcolor: '#fff' }}>
      <Brand />
      <Divider />
      <List sx={{ px: 1.3, py: 2 }}>
        {navigation.map(({ label, path, icon: Icon, soon }) => {
          const selected = location.pathname === path
          return (
            <ListItemButton
              key={path}
              selected={selected}
              onClick={() => goTo(path)}
              sx={{
                minHeight: 46,
                mb: 0.6,
                borderRadius: 2,
                color: selected ? 'primary.main' : 'text.secondary',
                '&.Mui-selected': { bgcolor: '#e8f3ff', color: 'primary.main' },
                '&.Mui-selected:hover': { bgcolor: '#e1efff' },
              }}
            >
              <ListItemIcon sx={{ minWidth: 40, color: 'inherit' }}><Icon fontSize="small" /></ListItemIcon>
              <ListItemText primary={label} primaryTypographyProps={{ fontSize: 14, fontWeight: selected ? 700 : 550 }} />
              {soon && <Box sx={{ width: 6, height: 6, borderRadius: '50%', bgcolor: '#b8c4d3' }} />}
            </ListItemButton>
          )
        })}
      </List>
      <Box sx={{ mt: 'auto', p: 1.5 }}>
        <Box sx={{ p: 1.5, border: '1px solid', borderColor: 'divider', borderRadius: 2.5, bgcolor: '#f8fbff', display: 'flex', gap: 1.2, alignItems: 'center' }}>
          <Avatar sx={{ width: 36, height: 36, bgcolor: '#d9eaff', color: 'primary.main', fontSize: 14, fontWeight: 700 }}>
            {(user?.username || 'A').charAt(0).toUpperCase()}
          </Avatar>
          <Box sx={{ minWidth: 0, flex: 1 }}>
            <Typography noWrap sx={{ fontSize: 13.5, fontWeight: 700 }}>{user?.username || 'Admin User'}</Typography>
            <Typography variant="caption" color="text.secondary">Administrator</Typography>
          </Box>
        </Box>
      </Box>
    </Box>
  )
}

export default function DashboardLayout() {
  const theme = useTheme()
  const mobile = useMediaQuery(theme.breakpoints.down('md'))
  const [drawerOpen, setDrawerOpen] = useState(false)
  const [accountAnchor, setAccountAnchor] = useState(null)
  const { user, logout } = useAuth()

  return (
    <Box sx={{ minHeight: '100vh', bgcolor: 'background.default' }}>
      <AppBar
        position="fixed"
        color="inherit"
        elevation={0}
        sx={{
          width: { md: `calc(100% - ${drawerWidth}px)` },
          ml: { md: `${drawerWidth}px` },
          borderBottom: '1px solid',
          borderColor: 'divider',
          bgcolor: 'rgba(255,255,255,.96)',
        }}
      >
        <Toolbar sx={{ minHeight: '64px !important', px: { xs: 2, md: 3 } }}>
          <IconButton onClick={() => setDrawerOpen(true)} sx={{ mr: 1, display: { md: 'none' } }} aria-label="Open navigation">
            <MenuRoundedIcon />
          </IconButton>
          <Box sx={{ flex: 1 }} />
          <Tooltip title="Notifications coming soon">
            <IconButton sx={{ mr: 1 }}><NotificationsNoneRoundedIcon /></IconButton>
          </Tooltip>
          <IconButton onClick={(event) => setAccountAnchor(event.currentTarget)} sx={{ p: 0.5, borderRadius: 2 }}>
            <Avatar sx={{ width: 36, height: 36, bgcolor: '#d8e8ff', color: 'primary.main', fontWeight: 700 }}>
              {(user?.username || 'A').charAt(0).toUpperCase()}
            </Avatar>
            <KeyboardArrowDownRoundedIcon sx={{ ml: 0.5, color: 'text.secondary', fontSize: 19 }} />
          </IconButton>
          <Menu anchorEl={accountAnchor} open={Boolean(accountAnchor)} onClose={() => setAccountAnchor(null)} transformOrigin={{ horizontal: 'right', vertical: 'top' }} anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}>
            <MenuItem onClick={logout}><LogoutRoundedIcon fontSize="small" sx={{ mr: 1.2 }} />Sign out</MenuItem>
          </Menu>
        </Toolbar>
      </AppBar>

      <Box component="nav" sx={{ width: { md: drawerWidth }, flexShrink: { md: 0 } }}>
        <Drawer variant={mobile ? 'temporary' : 'permanent'} open={mobile ? drawerOpen : true} onClose={() => setDrawerOpen(false)} ModalProps={{ keepMounted: true }} sx={{ '& .MuiDrawer-paper': { width: drawerWidth, borderColor: 'divider' } }}>
          <Sidebar onNavigate={() => setDrawerOpen(false)} />
        </Drawer>
      </Box>

      <Box component="main" sx={{ ml: { md: `${drawerWidth}px` }, pt: '64px', minHeight: '100vh' }}>
        <Box sx={{ p: { xs: 2, sm: 2.5, lg: 3.5 }, maxWidth: 1600, mx: 'auto' }}>
          <Outlet />
        </Box>
      </Box>
    </Box>
  )
}
