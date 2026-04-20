import { apiRequest, setAuthSession } from './apiClient'

function normalizeAuthPayload(payload) {
  return {
    ...payload,
    email: String(payload?.email || '').trim().toLowerCase(),
  }
}

export async function registerUser(payload) {
  const data = await apiRequest('/api/auth/register', {
    method: 'POST',
    body: normalizeAuthPayload(payload),
  })
  return data
}

export async function loginUser(payload) {
  const data = await apiRequest('/api/auth/login', {
    method: 'POST',
    body: normalizeAuthPayload(payload),
  })
  setAuthSession(data)
  return data
}

export async function requestPasswordReset(payload) {
  return await apiRequest('/api/auth/forgot-password', {
    method: 'POST',
    body: normalizeAuthPayload(payload),
  })
}

export async function verifyResetOtp(payload) {
  return await apiRequest('/api/auth/verify-reset-otp', {
    method: 'POST',
    body: normalizeAuthPayload(payload),
  })
}

export async function resetPassword(payload) {
  return await apiRequest('/api/auth/reset-password', {
    method: 'POST',
    body: payload,
  })
}
