import { useMemo, useState } from 'react'
import { clearAuthSession, getAuthSession, setAuthSession } from '../services/apiClient'
import { AuthContext } from './auth-context'

export function AuthProvider({ children }) {
  const [session, setSession] = useState(() => getAuthSession())

  const value = useMemo(() => ({
    session,
    isAuthenticated: Boolean(session?.token),
    role: session?.role || null,
    login: (nextSession) => {
      setAuthSession(nextSession)
      setSession(nextSession)
    },
    logout: () => {
      clearAuthSession()
      setSession(null)
    },
  }), [session])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
