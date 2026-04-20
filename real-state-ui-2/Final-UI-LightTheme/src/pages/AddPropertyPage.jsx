import { useEffect, useState } from 'react'
import { createProperty, uploadPropertyImages } from '../services/propertyApi'
import { INDIAN_CITIES } from '../data/indianCities'

const initialProperty = {
  offerType: 'SALE',
  offerCost: '',
  areaSqft: '',
  address: '',
  street: '',
  city: '',
}

const OFFER_TYPE_OPTIONS = ['SALE', 'RENT']

function AddPropertyPage() {
  const [status, setStatus] = useState('')
  const [statusOk, setStatusOk] = useState(false)
  const [propertyForm, setPropertyForm] = useState(initialProperty)
  const [submitting, setSubmitting] = useState(false)
  const [selectedImages, setSelectedImages] = useState([])
  const [fileInputKey, setFileInputKey] = useState(0)

  const [offerMenuOpen, setOfferMenuOpen] = useState(false)
  const [offerActiveIndex, setOfferActiveIndex] = useState(-1)
  const [createCityInputFocused, setCreateCityInputFocused] = useState(false)
  const [createCityActiveIndex, setCreateCityActiveIndex] = useState(-1)

  const normalizedCreateCityQuery = String(propertyForm.city || '').trim().toLowerCase()
  const createCityPrefixOptions = normalizedCreateCityQuery
    ? INDIAN_CITIES.filter((city) => city.toLowerCase().startsWith(normalizedCreateCityQuery)).slice(0, 10)
    : []
  const showCreateCitySuggestions = createCityInputFocused && normalizedCreateCityQuery.length > 0 && createCityPrefixOptions.length > 0

  useEffect(() => () => {
    selectedImages.forEach((image) => URL.revokeObjectURL(image.previewUrl))
  }, [selectedImages])

  function onPropertyChange(event) {
    const { name, value } = event.target
    setPropertyForm((prev) => ({ ...prev, [name]: value }))
  }

  function onPickCreateOfferType(offerType) {
    setPropertyForm((prev) => ({ ...prev, offerType }))
    setOfferMenuOpen(false)
    setOfferActiveIndex(-1)
  }

  function onPickCreateCity(city) {
    setPropertyForm((prev) => ({ ...prev, city }))
    setCreateCityInputFocused(false)
    setCreateCityActiveIndex(-1)
  }

  function onCreateOfferKeyDown(event) {
    if (event.key === 'ArrowDown') {
      event.preventDefault()
      if (!offerMenuOpen) {
        setOfferMenuOpen(true)
        setOfferActiveIndex(0)
        return
      }
      setOfferActiveIndex((prev) => (prev + 1) % OFFER_TYPE_OPTIONS.length)
      return
    }

    if (event.key === 'ArrowUp') {
      event.preventDefault()
      if (!offerMenuOpen) {
        setOfferMenuOpen(true)
        setOfferActiveIndex(OFFER_TYPE_OPTIONS.length - 1)
        return
      }
      setOfferActiveIndex((prev) => (prev <= 0 ? OFFER_TYPE_OPTIONS.length - 1 : prev - 1))
      return
    }

    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault()
      if (!offerMenuOpen) {
        setOfferMenuOpen(true)
        setOfferActiveIndex(0)
        return
      }
      const index = offerActiveIndex >= 0 ? offerActiveIndex : 0
      onPickCreateOfferType(OFFER_TYPE_OPTIONS[index])
      return
    }

    if (event.key === 'Escape') {
      setOfferMenuOpen(false)
      setOfferActiveIndex(-1)
    }
  }

  function onCreateCityKeyDown(event) {
    if (!createCityPrefixOptions.length) {
      return
    }

    if (event.key === 'ArrowDown') {
      event.preventDefault()
      setCreateCityInputFocused(true)
      setCreateCityActiveIndex((prev) => (prev + 1) % createCityPrefixOptions.length)
      return
    }

    if (event.key === 'ArrowUp') {
      event.preventDefault()
      setCreateCityInputFocused(true)
      setCreateCityActiveIndex((prev) => (prev <= 0 ? createCityPrefixOptions.length - 1 : prev - 1))
      return
    }

    if (event.key === 'Enter' && showCreateCitySuggestions) {
      event.preventDefault()
      const index = createCityActiveIndex >= 0 ? createCityActiveIndex : 0
      onPickCreateCity(createCityPrefixOptions[index])
      return
    }

    if (event.key === 'Tab' && showCreateCitySuggestions) {
      const index = createCityActiveIndex >= 0 ? createCityActiveIndex : 0
      onPickCreateCity(createCityPrefixOptions[index])
      return
    }

    if (event.key === 'Escape') {
      setCreateCityInputFocused(false)
      setCreateCityActiveIndex(-1)
    }
  }

  function onSelectImages(event) {
    const files = Array.from(event.target.files || [])
    if (files.length === 0) {
      return
    }

    const imageFiles = files.filter((file) => file.type.startsWith('image/'))
    if (imageFiles.length === 0) {
      setStatus('Please choose valid image files.')
      setStatusOk(false)
      return
    }

    setSelectedImages((prev) => {
      const combined = [
        ...prev,
        ...imageFiles.map((file) => ({
          file,
          name: file.name,
          previewUrl: URL.createObjectURL(file),
        })),
      ].slice(0, 6)
      
      return combined
    })
    setFileInputKey((prev) => prev + 1)
    setStatus(`${imageFiles.length} image(s) added. Total: ${Math.min(selectedImages.length + imageFiles.length, 6)} images.`)
    setStatusOk(true)
  }

  function onRemoveImage(indexToRemove) {
    setSelectedImages((prev) => {
      const removed = prev[indexToRemove]
      if (removed?.previewUrl) {
        URL.revokeObjectURL(removed.previewUrl)
      }
      return prev.filter((_, index) => index !== indexToRemove)
    })
  }

  async function onCreateProperty(event) {
    event.preventDefault()
    setSubmitting(true)
    setStatus('')
    setStatusOk(false)

    const offerCost = Number(propertyForm.offerCost)
    const areaSqft = Number(propertyForm.areaSqft)
    if (offerCost < 0 || areaSqft < 0) {
      setStatus('Price/Cost and Area cannot be negative.')
      setStatusOk(false)
      setSubmitting(false)
      return
    }

    try {
      const created = await createProperty({
        configuration: `${propertyForm.offerType} Property`,
        offerType: propertyForm.offerType,
        offerCost,
        areaSqft,
        address: propertyForm.address,
        street: propertyForm.street,
        city: propertyForm.city,
      })

      const createdPropertyId = created?.propId ?? created?.id
      if (createdPropertyId && selectedImages.length > 0) {
        await uploadPropertyImages(createdPropertyId, selectedImages.map((image) => image.file))
      }

      setPropertyForm(initialProperty)
      selectedImages.forEach((image) => URL.revokeObjectURL(image.previewUrl))
      setSelectedImages([])
      setCreateCityInputFocused(false)
      setCreateCityActiveIndex(-1)
      setStatus('Property listed successfully!')
      setStatusOk(true)
    } catch (error) {
      setStatus(error.message)
      setStatusOk(false)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="page-shell">
      <section className="listings-section compact-top">
        <div className="section-head">
          <div>
            <h2>Add Property</h2>
            <p>Manage your listings here.</p>
          </div>
        </div>

        {status && (
          <p className={statusOk ? 'status-tag status-ok' : 'status-message'}>{status}</p>
        )}

        <div className="dashboard-layout add-property-layout">
          <div className="broker-form-wrap">
            <h3 className="subsection-title">List a New Property</h3>
            <form className="property-form" onSubmit={onCreateProperty}>
              <div className="form-row">
                <label>
                  Offer Type
                  <div className="city-autocomplete-wrap role-picker-wrap">
                    <button
                      type="button"
                      className="role-picker-trigger"
                      onClick={() => setOfferMenuOpen((prev) => !prev)}
                      onKeyDown={onCreateOfferKeyDown}
                      onBlur={() => setTimeout(() => {
                        setOfferMenuOpen(false)
                        setOfferActiveIndex(-1)
                      }, 120)}
                      aria-haspopup="listbox"
                      aria-expanded={offerMenuOpen}
                      aria-label="Offer type"
                    >
                      <span>{propertyForm.offerType}</span>
                      <span className="role-picker-caret">▾</span>
                    </button>
                    {offerMenuOpen && (
                      <div className="city-autocomplete-list" role="listbox" aria-label="Offer type options">
                        {OFFER_TYPE_OPTIONS.map((option, index) => (
                          <button
                            key={option}
                            type="button"
                            className={`city-autocomplete-item ${offerActiveIndex === index ? 'city-autocomplete-item-active' : ''}`}
                            onMouseDown={() => onPickCreateOfferType(option)}
                            onMouseEnter={() => setOfferActiveIndex(index)}
                          >
                            {option}
                          </button>
                        ))}
                      </div>
                    )}
                  </div>
                </label>
                <label>
                  City
                  <div className="city-autocomplete-wrap">
                    <input
                      name="city"
                      value={propertyForm.city}
                      onChange={onPropertyChange}
                      onFocus={() => {
                        setCreateCityInputFocused(true)
                        setCreateCityActiveIndex(-1)
                      }}
                      onKeyDown={onCreateCityKeyDown}
                      onBlur={() => setTimeout(() => {
                        setCreateCityInputFocused(false)
                        setCreateCityActiveIndex(-1)
                      }, 120)}
                      placeholder="Start typing city"
                      autoComplete="off"
                      required
                    />
                    {showCreateCitySuggestions && (
                      <div className="city-autocomplete-list" role="listbox" aria-label="Broker city suggestions">
                        {createCityPrefixOptions.map((city, index) => (
                          <button
                            key={city}
                            type="button"
                            className={`city-autocomplete-item ${createCityActiveIndex === index ? 'city-autocomplete-item-active' : ''}`}
                            onMouseDown={() => onPickCreateCity(city)}
                            onMouseEnter={() => setCreateCityActiveIndex(index)}
                          >
                            {city}
                          </button>
                        ))}
                      </div>
                    )}
                  </div>
                </label>
              </div>

              <div className="form-row">
                <label>
                  Price / Cost
                  <input
                    type="number"
                    name="offerCost"
                    value={propertyForm.offerCost}
                    onChange={onPropertyChange}
                    placeholder="e.g. 1500000"
                    min="0"
                    required
                  />
                </label>
                <label>
                  Area (sqft)
                  <input
                    type="number"
                    name="areaSqft"
                    value={propertyForm.areaSqft}
                    onChange={onPropertyChange}
                    placeholder="e.g. 1800"
                    min="0"
                    required
                  />
                </label>
              </div>

              <label>
                Address
                <input
                  name="address"
                  value={propertyForm.address}
                  onChange={onPropertyChange}
                  placeholder="Building / complex name"
                  required
                />
              </label>

              <div className="form-row">
                <label>
                  Street
                  <input
                    name="street"
                    value={propertyForm.street}
                    onChange={onPropertyChange}
                    placeholder="Street name"
                    required
                  />
                </label>
              </div>

              <label>
                Property Images (optional)
                <input
                  key={fileInputKey}
                  type="file"
                  accept="image/*"
                  multiple
                  onChange={onSelectImages}
                  style={{ display: 'none' }}
                  id="property-file-input"
                />
                <span className="property-image-help">
                  Add up to 6 images. Uploaded images are saved on the server and visible to customers.
                </span>
                {selectedImages.length < 6 && (
                  <button
                    type="button"
                    className="cta cta-secondary add-image-btn"
                    onClick={() => document.getElementById('property-file-input').click()}
                  >
                    + Add Images
                  </button>
                )}
              </label>

              {selectedImages.length > 0 && (
                <div className="property-image-grid" aria-label="Selected property images">
                  {selectedImages.map((image, index) => (
                    <figure key={`${image.name}-${index}`} className="property-image-thumb-wrap">
                      <img src={image.previewUrl} alt={`Selected property ${index + 1}`} className="property-image-thumb" />
                      <figcaption className="property-image-caption">{image.name}</figcaption>
                      <button
                        type="button"
                        className="property-image-remove"
                        onClick={() => onRemoveImage(index)}
                      >
                        Remove
                      </button>
                    </figure>
                  ))}
                </div>
              )}

              <button type="submit" className="cta cta-primary" disabled={submitting}>
                {submitting ? 'Listing...' : '+ List Property'}
              </button>
            </form>
          </div>
        </div>
      </section>
    </div>
  )
}

export default AddPropertyPage
