import apiClient from './client'

const unwrap = (response) => response.data?.data ?? response.data

export const authApi = {
  login: (credentials) => apiClient.post('/auth/login', credentials).then(unwrap),
  register: (user) => apiClient.post('/auth/register', user).then(unwrap),
}

export const usersApi = {
  list: () => apiClient.get('/users').then(unwrap),
}

export const storesApi = {
  list: () => apiClient.get('/stores').then(unwrap),
  get: (id) => apiClient.get(`/stores/${id}`).then(unwrap),
  create: (store) => apiClient.post('/stores', store).then(unwrap),
  update: (id, store) => apiClient.put(`/stores/${id}`, store).then(unwrap),
}

export const customersApi = {
  list: (params = {}) => apiClient.get('/customers', { params }).then(unwrap),
  get: (id) => apiClient.get(`/customers/${id}`).then(unwrap),
  create: (customer) => apiClient.post('/customers', customer).then(unwrap),
  update: (id, customer) => apiClient.put(`/customers/${id}`, customer).then(unwrap),
}

export const invoicesApi = {
  list: (params = {}) => apiClient.get('/invoices', { params }).then(unwrap),
  get: (id) => apiClient.get(`/invoices/${id}`).then(unwrap),
  create: (invoice) => apiClient.post('/invoices', invoice).then(unwrap),
}

export const dashboardApi = {
  get: (params = {}) => apiClient.get('/dashboard', { params }).then(unwrap),
}

export const campaignsApi = {
  listAutomatedMessages: (params = {}) => apiClient
    .get('/whatsapp/messages/automated-campaigns', { params })
    .then(unwrap),
}

export const publicInvoiceApi = {
  getReviewDetails: (invoiceNumber, signal) => apiClient
    .get('/whatsapp/invoice', { params: { invoiceNumber }, signal })
    .then(unwrap),
  updateProfile: (invoiceNumber, profile) => apiClient
    .put('/whatsapp/invoice/profile', profile, { params: { invoiceNumber } })
    .then(unwrap),
}
