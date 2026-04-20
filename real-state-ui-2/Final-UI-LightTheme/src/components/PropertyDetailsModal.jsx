import { useState, useEffect } from 'react'
import { motion } from 'framer-motion'
import { getPropertyBrokerContact } from '../services/propertyApi'
import { demoPropertyImages, getDemoPropertyImage } from '../utils/propertyMapper'

function PropertyDetailsModal({ property, onClose }) {
  const [currentImageIndex, setCurrentImageIndex] = useState(0)
  const [brokerInfo, setBrokerInfo] = useState(null)
  const [loading, setLoading] = useState(true)

  const imageUrls = Array.isArray(property?.imageUrls)
    ? property.imageUrls.filter((url) => typeof url === 'string' && url.length > 0)
    : []

  const fallbackImage = getDemoPropertyImage(property?.id || property?.address || 'modal-image')
  const displayImages = imageUrls.length > 0 ? imageUrls : [fallbackImage]
  const currentImage = displayImages[currentImageIndex]

  useEffect(() => {
    async function fetchBrokerInfo() {
      try {
        if (property?.id) {
          const broker = await getPropertyBrokerContact(property.id)
          setBrokerInfo(broker)
        }
      } catch (error) {
        console.error('Failed to fetch broker info:', error)
      } finally {
        setLoading(false)
      }
    }

    fetchBrokerInfo()
  }, [property?.id])

  function goToPreviousImage() {
    setCurrentImageIndex((prev) => (prev === 0 ? displayImages.length - 1 : prev - 1))
  }

  function goToNextImage() {
    setCurrentImageIndex((prev) => (prev === displayImages.length - 1 ? 0 : prev + 1))
  }

  return (
    <motion.div
      className="modal-overlay"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      onClick={onClose}
    >
      <motion.div
        className="property-details-modal"
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        exit={{ opacity: 0, scale: 0.9 }}
        onClick={(e) => e.stopPropagation()}
      >
        <button className="modal-close-btn" onClick={onClose} aria-label="Close modal">
          ✕
        </button>

        {/* Image Gallery */}
        <div className="property-gallery">
          <div className="gallery-main">
            <img
              src={currentImage}
              alt={`Property ${currentImageIndex + 1}`}
              onError={(event) => {
                if (event.currentTarget.src !== fallbackImage) {
                  event.currentTarget.src = fallbackImage
                }
              }}
            />
            {displayImages.length > 1 && (
              <>
                <button className="gallery-nav gallery-nav-prev" onClick={goToPreviousImage}>
                  ‹
                </button>
                <button className="gallery-nav gallery-nav-next" onClick={goToNextImage}>
                  ›
                </button>
                <div className="gallery-counter">
                  {currentImageIndex + 1} / {displayImages.length}
                </div>
              </>
            )}
          </div>

          {displayImages.length > 1 && (
            <div className="gallery-thumbnails">
              {displayImages.map((img, idx) => (
                <button
                  key={idx}
                  className={`thumbnail ${idx === currentImageIndex ? 'active' : ''}`}
                  onClick={() => setCurrentImageIndex(idx)}
                >
                  <img
                    src={img}
                    alt={`Thumbnail ${idx + 1}`}
                    onError={(event) => {
                      event.currentTarget.src = demoPropertyImages[idx % demoPropertyImages.length]
                    }}
                  />
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Property Details */}
        <div className="property-info-section">
          <h2>{property?.address || 'Property'}</h2>
          <div className="property-details-grid">
            <div>
              <p className="detail-label">Price / Cost</p>
              <p className="detail-value">Rs {Number(property?.offerCost || 0).toLocaleString('en-IN')}</p>
            </div>
            <div>
              <p className="detail-label">Area</p>
              <p className="detail-value">{Number(property?.areaSqft || 0).toLocaleString()} sqft</p>
            </div>
            <div>
              <p className="detail-label">City</p>
              <p className="detail-value">{property?.city || 'N/A'}</p>
            </div>
            <div>
              <p className="detail-label">Type</p>
              <p className="detail-value">{property?.offerType || 'N/A'}</p>
            </div>
          </div>

          <div className="property-location">
            <p className="detail-label">Location Details</p>
            <p>{property?.street || 'Street not available'}</p>
          </div>

          {/* Broker Contact */}
          <div className="broker-contact-section">
            <h3>Contact Broker</h3>
            {loading ? (
              <p>Loading broker information...</p>
            ) : brokerInfo ? (
              <div className="broker-info">
                <p className="broker-name">{brokerInfo.fullName || brokerInfo.name || 'Broker'}</p>
                <div className="broker-contact-item">
                  <span className="contact-label">Email:</span>
                  <a href={`mailto:${brokerInfo.email}`} className="contact-value">
                    {brokerInfo.email}
                  </a>
                </div>
                {(brokerInfo.phone || brokerInfo.mobile) && (
                  <div className="broker-contact-item">
                    <span className="contact-label">Phone:</span>
                    <a href={`tel:${brokerInfo.phone || brokerInfo.mobile}`} className="contact-value">
                      {brokerInfo.phone || brokerInfo.mobile}
                    </a>
                  </div>
                )}
              </div>
            ) : (
              <p>Broker information not available</p>
            )}
          </div>
        </div>
      </motion.div>
    </motion.div>
  )
}

export default PropertyDetailsModal
