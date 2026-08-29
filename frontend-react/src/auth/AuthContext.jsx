import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'
import { authApi } from '../api/services'
import { TOKEN_KEY } from '../api/client'

const USER_KEY = 'himcharm_user'
const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem(USER_KEY))
    } catch {
      return null
    }
  })

  const logout = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
    setUser(null)
  }, [])

  useEffect(() => {
    window.addEventListener('himcharm:unauthorized', logout)
    return () => window.removeEventListener('himcharm:unauthorized', logout)
  }, [logout])

  const login = useCallback(async (credentials) => {
    const result = await authApi.login(credentials)
    const authenticatedUser = { username: result.username }
    localStorage.setItem(TOKEN_KEY, result.token)
    localStorage.setItem(USER_KEY, JSON.stringify(authenticatedUser))
    setUser(authenticatedUser)
    return result
  }, [])

  const value = useMemo(
    () => ({ user, isAuthenticated: Boolean(user && localStorage.getItem(TOKEN_KEY)), login, logout }),
    [login, logout, user],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) throw new Error('useAuth must be used inside AuthProvider')
  return context
}
