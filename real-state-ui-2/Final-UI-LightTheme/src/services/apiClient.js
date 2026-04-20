const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || '').replace(/\/$/, '')
const SESSION_KEY = 'realestate.auth.session'

function getSessionStorage() {
  try {
    return window.sessionStorage
  } catch {
    return null
  }
}

function getLegacyLocalStorage() {
  try {
    return window.localStorage
  } catch {
    return null
  }
}

function buildUrl(path) {
  if (!API_BASE_URL) {
    return path
  }

  return `${API_BASE_URL}${path}`
}

export function getAuthSession() {
  const sessionStore = getSessionStorage()
  const legacyStore = getLegacyLocalStorage()

  const raw = sessionStore?.getItem(SESSION_KEY)
  if (!raw) {
    // One-time migration to keep current user logged in after switching storage strategy.
    const legacyRaw = legacyStore?.getItem(SESSION_KEY)
    if (!legacyRaw) {
      return null
    }

    try {
      const parsedLegacy = JSON.parse(legacyRaw)
      sessionStore?.setItem(SESSION_KEY, JSON.stringify(parsedLegacy))
      legacyStore?.removeItem(SESSION_KEY)
      return parsedLegacy
    } catch {
      legacyStore?.removeItem(SESSION_KEY)
      return null
    }
  }

  try {
    return JSON.parse(raw)
  } catch {
    sessionStore?.removeItem(SESSION_KEY)
    return null
  }
}

export function setAuthSession(session) {
  const sessionStore = getSessionStorage()
  const legacyStore = getLegacyLocalStorage()
  sessionStore?.setItem(SESSION_KEY, JSON.stringify(session))
  legacyStore?.removeItem(SESSION_KEY)
}

export function clearAuthSession() {
  const sessionStore = getSessionStorage()
  const legacyStore = getLegacyLocalStorage()
  sessionStore?.removeItem(SESSION_KEY)
  legacyStore?.removeItem(SESSION_KEY)
}

export async function apiRequest(path, options = {}) {
  const {
    method = 'GET',
    body,
    auth = false,
  } = options

  const isFormData = typeof FormData !== 'undefined' && body instanceof FormData
  const headers = {}

  if (!isFormData) {
    headers['Content-Type'] = 'application/json'
  }

  if (auth) {
    const session = getAuthSession()
    if (session?.token) {
      headers.Authorization = `Bearer ${session.token}`
    }
  }

  const response = await fetch(buildUrl(path), {
    method,
    headers,
    body: body
      ? (isFormData ? body : JSON.stringify(body))
      : undefined,
  })

  let payload = null
  try {
    payload = await response.json()
  } catch {
    payload = null
  }

  if (!response.ok || payload?.success === false) {
    const message = payload?.message || `Request failed (${response.status})`
    throw new Error(message)
  }

  return payload?.data
}
