import { motion } from 'framer-motion'
import { getDemoPropertyImage } from '../utils/propertyMapper'

function formatMoney(value) {
  return `Rs ${Number(value || 0).toLocaleString('en-IN', { maximumFractionDigits: 0 })}`
}

function pickListingImage(item, index) {
  const uploadedImages = Array.isArray(item?.imageUrls)
    ? item.imageUrls.filter((url) => typeof url === 'string' && url.length > 0)
    : []

  if (uploadedImages.length > 0) {
    return uploadedImages[0]
  }

  if (typeof item?.imageUrl === 'string' && item.imageUrl.length > 0) {
    return item.imageUrl
  }

  return getDemoPropertyImage(item?.id || `${item?.address || ''}-${index}`)
}

function ListingCard({ item, index, sold = false }) {
  const MotionArticle = motion.article
  const imageUrl = pickListingImage(item, index)

  return (
    <MotionArticle
      className="listing-card"
      initial={{ opacity: 0, y: 60 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true, amount: 0.35 }}
      transition={{ delay: 0.1 * index, duration: 0.6, ease: 'easeOut' }}
    >
      <div
        className="listing-card-bg"
        style={{ backgroundImage: `url(${imageUrl})` }}
        aria-hidden="true"
      />
      <div
        className={`listing-chip ${sold ? 'listing-chip-sold' : item.offerType === 'RENT' ? 'listing-chip-rent' : 'listing-chip-sale'}`}
        style={{ borderColor: sold ? '#d89a8e' : item.accent }}
      >
        {sold ? 'SOLD' : (item.offerType || 'SALE')}
      </div>
      <h3>{item.address || `${item.city || 'City'} Property`}</h3>
      <p className="listing-price">{formatMoney(Number(item.offerCost || 0))}</p>
      <div className="listing-meta">
        <span>{Number(item.areaSqft || 0).toLocaleString()} sqft</span>
        <span>{item.city || 'Unknown city'}</span>
        <span>{item.street || 'Prime street'}</span>
      </div>
      <p className="listing-address">{item.address || 'Address unavailable'}</p>
    </MotionArticle>
  )
}

export default ListingCard
