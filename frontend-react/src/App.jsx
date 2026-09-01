import { lazy, Suspense } from 'react'
import { Navigate, Route, Routes } from 'react-router-dom'
import { Box, CircularProgress } from '@mui/material'
import { useAuth } from './auth/AuthContext'

const DashboardLayout = lazy(() => import('./components/layout/DashboardLayout'))
const LoginPage = lazy(() => import('./pages/LoginPage'))
const DashboardPage = lazy(() => import('./pages/DashboardPage'))
const UsersPage = lazy(() => import('./pages/UsersPage'))
const StoresPage = lazy(() => import('./pages/StoresPage'))
const InvoicesPage = lazy(() => import('./pages/InvoicesPage'))
const CustomersPage = lazy(() => import('./pages/CustomersPage'))
const ComingSoonPage = lazy(() => import('./pages/ComingSoonPage'))
const PublicInvoicePage = lazy(() => import('./pages/PublicInvoicePage'))

function AppLoader() {
  return <Box sx={{ minHeight: '100vh', display: 'grid', placeItems: 'center', bgcolor: 'background.default' }}><CircularProgress size={32} /></Box>
}

function ProtectedRoute() {
  const { isAuthenticated } = useAuth()
  return isAuthenticated ? <DashboardLayout /> : <Navigate to="/login" replace />
}

function App() {
  const { isAuthenticated } = useAuth()

  return (
    <Suspense fallback={<AppLoader />}>
      <Routes>
        <Route path="/whatsapp/invoice" element={<PublicInvoicePage />} />
        <Route path="/public/invoice" element={<PublicInvoicePage />} />
        <Route
          path="/login"
          element={isAuthenticated ? <Navigate to="/dashboard" replace /> : <LoginPage />}
        />
        <Route element={<ProtectedRoute />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/users" element={<UsersPage />} />
          <Route path="/stores" element={<StoresPage />} />
          <Route path="/invoices" element={<InvoicesPage />} />
          <Route path="/customers" element={<CustomersPage />} />
          <Route path="/analytics" element={<ComingSoonPage title="Analytics" />} />
          <Route path="/campaigns" element={<ComingSoonPage title="Campaigns" />} />
        </Route>
        <Route path="*" element={<Navigate to={isAuthenticated ? '/dashboard' : '/login'} replace />} />
      </Routes>
    </Suspense>
  )
}

export default App
