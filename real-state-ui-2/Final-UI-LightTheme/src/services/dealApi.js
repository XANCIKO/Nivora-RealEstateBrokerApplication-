import { apiRequest } from './apiClient'

export function createDeal(propertyId) {
  return apiRequest(`/api/deals/property/${propertyId}`, {
    method: 'POST',
    auth: true,
  })
}

export function getMyDeals() {
  return apiRequest('/api/deals/my-deals', { auth: true })
}

export function listDeals() {
  return apiRequest('/api/deals', { auth: true })
}
