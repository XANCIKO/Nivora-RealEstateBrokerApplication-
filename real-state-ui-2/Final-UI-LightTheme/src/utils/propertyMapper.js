const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080').replace(/\/$/, '')

export function toAbsoluteImageUrl(url) {
  if (!url || typeof url !== 'string') {
    return ''
  }

  if (url.startsWith('http://') || url.startsWith('https://') || !url.startsWith('/')) {
    return url
  }

  return API_BASE_URL ? `${API_BASE_URL}${url}` : url
}

export const demoPropertyImages = [
  '/uploads/property-images/demo-coastal-villa.svg',
  '/uploads/property-images/demo-urban-loft.svg',
  '/uploads/property-images/demo-garden-home.svg',
  '/uploads/property-images/demo-skyline-suite.svg',
  '/uploads/property-images/demo-lakeview-house.svg',
  '/uploads/property-images/demo-studio-flat.svg',
].map(toAbsoluteImageUrl)

export function getDemoPropertyImage(seedValue = '') {
  const seed = String(seedValue || 'nivora-demo')
  let hash = 0

  for (let index = 0; index < seed.length; index += 1) {
    hash = (hash * 31 + seed.charCodeAt(index)) >>> 0
  }

  return demoPropertyImages[hash % demoPropertyImages.length]
}

export function normalizeProperty(property, index = 0) {
  const accentPalette = ['#f58b4d', '#2baea1', '#e95887', '#5f82d4']
  const statusValue = property.status
  const normalizedStatus =
    typeof statusValue === 'boolean'
      ? (statusValue ? 'AVAILABLE' : 'SOLD')
      : String(statusValue || 'AVAILABLE').toUpperCase()

  const rawImageUrls = Array.isArray(property.imageUrls)
    ? property.imageUrls.filter((url) => typeof url === 'string' && url.length > 0)
    : (typeof property.imageUrl === 'string' && property.imageUrl.length > 0 ? [property.imageUrl] : [])

  const imageUrls = rawImageUrls.map(toAbsoluteImageUrl)

  return {
    id: property.propId ?? property.id ?? index,
    brokerId: property.brokerId ?? property.broId ?? property.broker?.broId ?? property.broker?.id ?? null,
    configuration: property.configuration || 'Property',
    status: normalizedStatus,
    offerType: (property.offerType || 'SALE').toUpperCase(),
    offerCost: Number(property.offerCost || 0),
    areaSqft: Number(property.areaSqft || 0),
    address: property.address || '',
    street: property.street || '',
    city: property.city || 'Unknown city',
    accent: property.accent || accentPalette[index % accentPalette.length],
    imageUrls,
    imageUrl: imageUrls[0] || getDemoPropertyImage(property.propId ?? property.id ?? index),
  }
}
