import indianCities from 'indian-cities-json'

const rawCities = Array.isArray(indianCities?.cities) ? indianCities.cities : []

export const INDIAN_CITIES = [...new Set(
  rawCities
    .map((item) => String(item?.name || '').trim())
    .filter(Boolean)
)].sort((a, b) => a.localeCompare(b))
