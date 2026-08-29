import axios from 'axios'

const TOKEN_KEY = 'himcharm_access_token'

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:9090/himcharm',
  headers: {
    'Content-Type': 'application/json',
  },
})

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && localStorage.getItem(TOKEN_KEY)) {
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem('himcharm_user')
      window.dispatchEvent(new Event('himcharm:unauthorized'))
    }
    return Promise.reject(error)
  },
)

export const getApiError = (error, fallback = 'Something went wrong') => {
  const response = error.response?.data
  if (response?.errors && typeof response.errors === 'object') {
    return Object.values(response.errors).join(', ')
  }
  return response?.message || error.message || fallback
}

export { TOKEN_KEY }
export default apiClient
