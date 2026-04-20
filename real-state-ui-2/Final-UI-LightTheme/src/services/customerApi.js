import { apiRequest } from './apiClient'

export function listCustomers() {
  return apiRequest('/api/customers', { auth: true })
}

export function getMyProfile() {
  return apiRequest('/api/customers/me', { auth: true })
}

export function getCustomerById(id) {
  return apiRequest(`/api/customers/${id}`, { auth: true })
}

export function updateCustomer(id, payload) {
  return apiRequest(`/api/customers/${id}`, {
    method: 'PUT',
    body: payload,
    auth: true,
  })
}

export function deleteCustomer(id) {
  return apiRequest(`/api/customers/${id}`, {
    method: 'DELETE',
    auth: true,
  })
}
