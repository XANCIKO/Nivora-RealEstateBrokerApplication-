import { apiRequest } from './apiClient'

export function listBrokers() {
  return apiRequest('/api/brokers', { auth: true })
}

export function getBrokerById(id) {
  return apiRequest(`/api/brokers/${id}`, { auth: true })
}

export function updateBroker(id, payload) {
  return apiRequest(`/api/brokers/${id}`, {
    method: 'PUT',
    body: payload,
    auth: true,
  })
}

export function deleteBroker(id) {
  return apiRequest(`/api/brokers/${id}`, {
    method: 'DELETE',
    auth: true,
  })
}
