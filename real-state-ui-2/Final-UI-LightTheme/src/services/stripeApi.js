import { apiRequest } from './apiClient'

export function createPaymentIntent(propertyId) {
  return apiRequest(`/api/payments/intent/${propertyId}`, {
    method: 'POST',
    auth: true,
  })
}
