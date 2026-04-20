import { apiRequest } from './apiClient'

function buildQuery(filters = {}) {
  const params = new URLSearchParams()

  if (filters.offer) {
    params.set('offer', filters.offer)
  }
  if (filters.city) {
    params.set('city', filters.city)
  }
  if (filters.minCost) {
    params.set('minCost', String(filters.minCost))
  }
  if (filters.maxCost) {
    params.set('maxCost', String(filters.maxCost))
  }

  const query = params.toString()
  return query ? `?${query}` : ''
}

export function searchProperties(filters) {
  return apiRequest(`/api/properties/search${buildQuery(filters)}`)
}

export function listProperties() {
  return apiRequest('/api/properties', { auth: true })
}

export function getPropertyById(id) {
  return apiRequest(`/api/properties/${id}`)
}

export function getPropertyBrokerContact(id) {
  return apiRequest(`/api/properties/${id}/broker-contact`)
}

export function createProperty(payload) {
  return apiRequest('/api/properties', {
    method: 'POST',
    body: payload,
    auth: true,
  })
}

export function updateProperty(id, payload) {
  return apiRequest(`/api/properties/${id}`, {
    method: 'PUT',
    body: payload,
    auth: true,
  })
}

export function uploadPropertyImages(id, files) {
  const formData = new FormData()
  files.forEach((file) => formData.append('images', file))

  return apiRequest(`/api/properties/${id}/images`, {
    method: 'POST',
    body: formData,
    auth: true,
  })
}

export function deleteProperty(id) {
  return apiRequest(`/api/properties/${id}`, {
    method: 'DELETE',
    auth: true,
  })
}

export function getMyListings() {
  return apiRequest('/api/properties/my-listings', { auth: true })
}
