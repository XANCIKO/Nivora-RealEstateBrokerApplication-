import { useEffect, useRef, useState } from 'react'
import ListingCard from '../components/ListingCard'
import ListingCardSkeleton from '../components/ListingCardSkeleton'
import PropertyDetailsModal from '../components/PropertyDetailsModal'
import { fallbackListings } from '../data/listings'
import { useAuth } from '../context/useAuth'
import { createDeal } from '../services/dealApi'
import { createPaymentIntent } from '../services/stripeApi'
import { searchProperties } from '../services/propertyApi'
import { normalizeProperty } from '../utils/propertyMapper'
import { INDIAN_CITIES } from '../data/indianCities'
import StripePaymentModal from '../components/StripePaymentModal'

const initialFilters = { offer: '', city: '', minCost: '', maxCost: '' }
const LISTINGS_BATCH_SIZE = 48
const OFFER_OPTIONS = [
  { label: 'All offers', value: '' },
  { label: 'SALE', value: 'SALE' },
  { label: 'RENT', value: 'RENT' },
]

function isSoldListing(item) {
  return String(item?.status || '').toUpperCase() === 'SOLD'
}

function ListingsPage() {
  const { role, isAuthenticated, session } = useAuth()
  const stripePublishableKey = import.meta.env.VITE_STRIPE_PUBLISHABLE_KEY || ''
  const [filters, setFilters] = useState(initialFilters)
  const [listings, setListings] = useState(fallbackListings)
  const [status, setStatus] = useState('')
  const [loading, setLoading] = useState(false)
  const [confirmState, setConfirmState] = useState({ propertyId: null, agreed: false, secondsLeft: 0 })
  const [paymentState, setPaymentState] = useState({
    item: null,
    clientSecret: '',
    amountInInr: 0,
    fullAmountInInr: 0,
    remainingAmountInInr: 0,
    advancePercent: 0,
    targetAdvancePercent: 8,
    cappedByGatewayLimit: false,
  })
  const [submittingDealId, setSubmittingDealId] = useState(null)
  const [recentDealCard, setRecentDealCard] = useState(null)
  const [offerMenuOpen, setOfferMenuOpen] = useState(false)
  const [offerActiveIndex, setOfferActiveIndex] = useState(-1)
  const [cityInputFocused, setCityInputFocused] = useState(false)
  const [cityActiveIndex, setCityActiveIndex] = useState(-1)
  const [selectedProperty, setSelectedProperty] = useState(null)
  const orderedListings = [...listings].sort((a, b) => Number(isSoldListing(a)) - Number(isSoldListing(b)))
  const normalizedCityQuery = String(filters.city || '').trim().toLowerCase()
  const cityPrefixOptions = normalizedCityQuery
    ? INDIAN_CITIES.filter((city) => city.toLowerCase().startsWith(normalizedCityQuery)).slice(0, 10)
    : []
  const showCitySuggestions = cityInputFocused && normalizedCityQuery.length > 0 && cityPrefixOptions.length > 0
  const sequenceAwareListings = normalizedCityQuery
    ? orderedListings.filter((item) => String(item.city || '').toLowerCase().startsWith(normalizedCityQuery))
    : orderedListings
  const noResultsForCity = !loading && normalizedCityQuery.length > 0 && sequenceAwareListings.length === 0
  const [visibleCount, setVisibleCount] = useState(LISTINGS_BATCH_SIZE)
  const [isLoadingMore, setIsLoadingMore] = useState(false)
  const loadMoreRef = useRef(null)
  const visibleListings = sequenceAwareListings.slice(0, visibleCount)
  const activeConfirmItem = sequenceAwareListings.find(
    (item) => item.id === confirmState.propertyId && !isSoldListing(item),
  )

  async function loadListings(nextFilters) {
    setLoading(true)
    setStatus('')

    try {
      const result = await searchProperties(nextFilters)
      setListings(result.map(normalizeProperty))
      setVisibleCount(LISTINGS_BATCH_SIZE)
    } catch {
      setListings(fallbackListings)
      setVisibleCount(LISTINGS_BATCH_SIZE)
      setStatus('Showing fallback listings.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadListings(initialFilters)
  }, [])

  useEffect(() => {
    const node = loadMoreRef.current
    if (!node) {
      return undefined
    }

    const observer = new IntersectionObserver(
      (entries) => {
        const [entry] = entries
        if (!entry?.isIntersecting || isLoadingMore || visibleCount >= sequenceAwareListings.length) {
          return
        }

        setIsLoadingMore(true)
        setVisibleCount((prev) => Math.min(prev + LISTINGS_BATCH_SIZE, sequenceAwareListings.length))
        setIsLoadingMore(false)
      },
      { rootMargin: '300px 0px' },
    )

    observer.observe(node)

    return () => observer.disconnect()
  }, [isLoadingMore, visibleCount, sequenceAwareListings.length])

  useEffect(() => {
    if (!confirmState.propertyId || confirmState.secondsLeft <= 0) {
      return undefined
    }

    const timer = setTimeout(() => {
      setConfirmState((prev) => ({
        ...prev,
        secondsLeft: Math.max(0, prev.secondsLeft - 1),
      }))
    }, 1000)

    return () => clearTimeout(timer)
  }, [confirmState.propertyId, confirmState.secondsLeft])

  function onChange(event) {
    const { name, value } = event.target
    setFilters((prev) => ({ ...prev, [name]: value }))
  }

  function onPickCity(city) {
    setFilters((prev) => ({ ...prev, city }))
    setCityInputFocused(false)
    setCityActiveIndex(-1)
  }

  function onPickOffer(value) {
    setFilters((prev) => ({ ...prev, offer: value }))
    setOfferMenuOpen(false)
    setOfferActiveIndex(-1)
  }

  function onOfferTriggerKeyDown(event) {
    if (event.key === 'ArrowDown') {
      event.preventDefault()
      if (!offerMenuOpen) {
        setOfferMenuOpen(true)
        setOfferActiveIndex(0)
        return
      }
      setOfferActiveIndex((prev) => (prev + 1) % OFFER_OPTIONS.length)
      return
    }

    if (event.key === 'ArrowUp') {
      event.preventDefault()
      if (!offerMenuOpen) {
        setOfferMenuOpen(true)
        setOfferActiveIndex(OFFER_OPTIONS.length - 1)
        return
      }
      setOfferActiveIndex((prev) => (prev <= 0 ? OFFER_OPTIONS.length - 1 : prev - 1))
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
      onPickOffer(OFFER_OPTIONS[index].value)
      return
    }

    if (event.key === 'Escape') {
      setOfferMenuOpen(false)
      setOfferActiveIndex(-1)
    }
  }

  function onCityInputKeyDown(event) {
    if (!cityPrefixOptions.length) {
      return
    }

    if (event.key === 'ArrowDown') {
      event.preventDefault()
      setCityInputFocused(true)
      setCityActiveIndex((prev) => (prev + 1) % cityPrefixOptions.length)
      return
    }

    if (event.key === 'ArrowUp') {
      event.preventDefault()
      setCityInputFocused(true)
      setCityActiveIndex((prev) => (prev <= 0 ? cityPrefixOptions.length - 1 : prev - 1))
      return
    }

    if (event.key === 'Enter' && showCitySuggestions) {
      event.preventDefault()
      const index = cityActiveIndex >= 0 ? cityActiveIndex : 0
      onPickCity(cityPrefixOptions[index])
      return
    }

    if (event.key === 'Tab' && showCitySuggestions) {
      const index = cityActiveIndex >= 0 ? cityActiveIndex : 0
      onPickCity(cityPrefixOptions[index])
      return
    }

    if (event.key === 'Escape') {
      setCityInputFocused(false)
      setCityActiveIndex(-1)
    }
  }

  async function onSubmit(event) {
    event.preventDefault()
    await loadListings(filters)
  }

  function onResetFilters() {
    setFilters(initialFilters)
    setVisibleCount(LISTINGS_BATCH_SIZE)
    setStatus('')
  }

  function onOpenConfirm(item) {
    if (isSoldListing(item)) {
      setStatus('This listing is already sold.')
      return
    }

    if (!isAuthenticated || role !== 'CUSTOMER') {
      setStatus('Login as CUSTOMER to continue.')
      return
    }

    setStatus('')
    setConfirmState({ propertyId: item.id, agreed: false, secondsLeft: 5 })
  }

  function onToggleAgree(event) {
    const { checked } = event.target
    setConfirmState((prev) => ({ ...prev, agreed: checked }))
  }

  function onCancelConfirm() {
    setConfirmState({ propertyId: null, agreed: false, secondsLeft: 0 })
  }

  function onCloseDealModal() {
    setRecentDealCard(null)
  }

  function onClosePaymentModal() {
    setPaymentState({
      item: null,
      clientSecret: '',
      amountInInr: 0,
      fullAmountInInr: 0,
      remainingAmountInInr: 0,
      advancePercent: 0,
      targetAdvancePercent: 8,
      cappedByGatewayLimit: false,
    })
    setSubmittingDealId(null)
  }

  function onSaveDealAsText() {
    if (!recentDealCard) return

    const content = [
      `${recentDealCard.action} Successfully`,
      `Buyer Name: ${recentDealCard.buyerName || 'Customer'}`,
      `Buyer Email: ${recentDealCard.buyerEmail || 'Not available'}`,
      `Deal ID: ${recentDealCard.id}`,
      `Total Price: Rs ${Number(recentDealCard.totalAmount || recentDealCard.amount || 0).toLocaleString('en-IN')}`,
      `Paid Online: Rs ${Number(recentDealCard.amount || 0).toLocaleString('en-IN')}`,
      `Remaining Balance: Rs ${Number(recentDealCard.remainingAmount || 0).toLocaleString('en-IN')}`,
      `Advance Percentage: ${Number(recentDealCard.advancePercent || 0)}%`,
      `Area: ${Number(recentDealCard.areaSqft || 0).toLocaleString('en-IN')} sqft`,
      `City: ${recentDealCard.city || 'Unknown city'}`,
      `Street: ${recentDealCard.street || 'N/A'}`,
      `Address: ${recentDealCard.address || 'Address unavailable'}`,
      `Confirmed on: ${recentDealCard.confirmedAt}`,
    ].join('\n')

    const blob = new Blob([content], { type: 'text/plain;charset=utf-8' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `deal-${recentDealCard.id}.txt`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
  }

  function onPrintDeal() {
    if (!recentDealCard) return

    const printWindow = window.open('', '_blank', 'width=900,height=700')
    if (!printWindow) return

    const html = `
      <!doctype html>
      <html>
        <head>
          <title>Nivora Receipt INV-${recentDealCard.id}</title>
          <style>
            body { font-family: Georgia, serif; margin: 0; background: #f5f1ea; color: #1f1a16; }
            .sheet { max-width: 820px; margin: 24px auto; background: white; padding: 32px; border: 1px solid #d8c8af; position: relative; }
            .watermark { position: absolute; inset: 0; display: grid; place-items: center; font-size: 110px; color: rgba(162,130,84,0.08); font-weight: 700; letter-spacing: 0.16em; transform: rotate(-24deg); pointer-events: none; }
            .head { display: flex; justify-content: space-between; gap: 24px; border-bottom: 1px dashed #ccb997; padding-bottom: 16px; }
            .eyebrow { margin: 0; text-transform: uppercase; letter-spacing: .14em; font-size: 12px; color: #8d7757; }
            h1 { margin: 8px 0 0; font-size: 34px; }
            .tag { border: 1px solid #c9b089; padding: 6px 10px; border-radius: 999px; font-size: 12px; font-weight: 700; }
            .grid { display: grid; grid-template-columns: repeat(2, minmax(0,1fr)); gap: 16px; margin-top: 18px; }
            .grid div span { display: block; font-size: 12px; text-transform: uppercase; color: #8d7757; letter-spacing: .08em; }
            .grid div strong { display: block; margin-top: 4px; }
            table { width: 100%; border-collapse: collapse; margin-top: 20px; }
            th, td { border: 1px solid #dfd2be; padding: 12px; text-align: left; }
            th { background: #f8f4ed; font-size: 12px; text-transform: uppercase; letter-spacing: .08em; color: #8d7757; }
            .total { margin-top: 20px; display: flex; justify-content: space-between; background: #f6efe2; border: 1px solid #dbc7a6; padding: 16px; font-size: 18px; font-weight: 700; }
          </style>
        </head>
        <body>
          <div class="sheet">
            <div class="watermark">PAID</div>
            <div class="head">
              <div>
                <p class="eyebrow">Nivora Receipt</p>
                <h1>${recentDealCard.action} Invoice</h1>
              </div>
              <div>
                <div class="tag">INV-${recentDealCard.id}</div>
              </div>
            </div>
            <div class="grid">
              <div><span>Buyer Name</span><strong>${recentDealCard.buyerName || 'Customer'}</strong></div>
              <div><span>Buyer Email</span><strong>${recentDealCard.buyerEmail || 'Not available'}</strong></div>
              <div><span>Confirmed On</span><strong>${recentDealCard.confirmedAt}</strong></div>
              <div><span>Offer Type</span><strong>${recentDealCard.offerType || 'SALE'}</strong></div>
            </div>
            <table>
              <thead>
                <tr><th>Description</th><th>Area</th><th>Amount</th></tr>
              </thead>
              <tbody>
                <tr>
                  <td>${recentDealCard.address || 'Property'}<br><small>${recentDealCard.street || 'Street unavailable'}, ${recentDealCard.city || 'Unknown city'}</small></td>
                  <td>${Number(recentDealCard.areaSqft || 0).toLocaleString('en-IN')} sqft</td>
                  <td>Rs ${Number(recentDealCard.amount || 0).toLocaleString('en-IN')}</td>
                </tr>
              </tbody>
            </table>
            <div class="total"><span>Total Property Price</span><span>Rs ${Number(recentDealCard.totalAmount || recentDealCard.amount || 0).toLocaleString('en-IN')}</span></div>
            <div class="total"><span>Paid Online (${Number(recentDealCard.advancePercent || 0)}%)</span><span>Rs ${Number(recentDealCard.amount || 0).toLocaleString('en-IN')}</span></div>
            <div class="total"><span>Remaining Balance</span><span>Rs ${Number(recentDealCard.remainingAmount || 0).toLocaleString('en-IN')}</span></div>
          </div>
        </body>
      </html>`

    printWindow.document.open()
    printWindow.document.write(html)
    printWindow.document.close()
    printWindow.focus()
    printWindow.print()
  }

  async function onConfirmBuyOrRent(item) {
    if (confirmState.secondsLeft > 0 || !confirmState.agreed) {
      return
    }

    try {
      if (!stripePublishableKey) {
        throw new Error('Stripe publishable key is missing. Add VITE_STRIPE_PUBLISHABLE_KEY to frontend env.')
      }

      setSubmittingDealId(item.id)
      const paymentIntent = await createPaymentIntent(item.id)
      if (!paymentIntent?.clientSecret) {
        throw new Error('Unable to start payment. Please try again.')
      }

      const isAdvanceCapped = Boolean(paymentIntent.cappedByGatewayLimit)
      const effectiveAdvancePercent = Number(paymentIntent.advancePercent || 100)

      setPaymentState({
        item,
        clientSecret: paymentIntent.clientSecret,
        amountInInr: Number(paymentIntent.payableAmountInInr || item.offerCost || 0),
        fullAmountInInr: Number(paymentIntent.fullAmountInInr || item.offerCost || 0),
        remainingAmountInInr: Number(paymentIntent.remainingAmountInInr || 0),
        advancePercent: effectiveAdvancePercent,
        targetAdvancePercent: Number(paymentIntent.targetAdvancePercent || (item.offerType === 'SALE' ? 8 : 100)),
        cappedByGatewayLimit: isAdvanceCapped,
      })

      if (isAdvanceCapped && item.offerType === 'SALE') {
        setStatus(`8% advance exceeds gateway limit for this property. ${effectiveAdvancePercent.toFixed(2)}% will be paid online and the remaining balance is paid offline.`)
      } else {
        setStatus('')
      }

      onCancelConfirm()
    } catch (error) {
      setStatus(error.message)
      setSubmittingDealId(null)
    }
  }

  async function onPaymentSuccess(paymentIntentId) {
    const item = paymentState.item
    if (!item) return

    try {
      const createdDeal = await createDeal(item.id)
      setStatus('Payment successful and deal created.')
      setRecentDealCard({
        id: item.id,
        action: item.offerType === 'RENT' ? 'Rented' : 'Purchased',
        buyerName: session?.name || session?.email || 'Customer',
        buyerEmail: session?.email || 'Not available',
        amount: Number(createdDeal?.dealCost ?? paymentState.amountInInr ?? item.offerCost ?? 0),
        totalAmount: Number(createdDeal?.totalAmount ?? paymentState.fullAmountInInr ?? item.offerCost ?? 0),
        remainingAmount: Number(createdDeal?.remainingAmount ?? paymentState.remainingAmountInInr ?? 0),
        advancePercent: Number(createdDeal?.advancePercent ?? paymentState.advancePercent ?? 100),
        areaSqft: item.areaSqft,
        address: item.address,
        city: item.city,
        street: item.street,
        offerType: item.offerType,
        paymentIntentId,
        confirmedAt: new Date().toLocaleString('en-IN'),
      })

      setPaymentState({
        item: null,
        clientSecret: '',
        amountInInr: 0,
        fullAmountInInr: 0,
        remainingAmountInInr: 0,
        advancePercent: 0,
        targetAdvancePercent: 8,
        cappedByGatewayLimit: false,
      })
      setSubmittingDealId(null)
      await loadListings(filters)
    } catch (error) {
      setStatus(error.message)
      setSubmittingDealId(null)
    }
  }

  return (
    <div className="page-shell discover-shell">
      <section className="listings-section compact-top">
        <div className="section-head">
          <h2>Discover</h2>
          <p>Search by city, offer, and budget.</p>
        </div>

        <form className="filter-grid" onSubmit={onSubmit}>
          <div className="city-autocomplete-wrap role-picker-wrap">
            <button
              type="button"
              className="role-picker-trigger"
              onClick={() => setOfferMenuOpen((prev) => !prev)}
              onKeyDown={onOfferTriggerKeyDown}
              onBlur={() => setTimeout(() => {
                setOfferMenuOpen(false)
                setOfferActiveIndex(-1)
              }, 120)}
              aria-haspopup="listbox"
              aria-expanded={offerMenuOpen}
              aria-label="Offer type"
            >
              <span>{filters.offer || 'All offers'}</span>
              <span className="role-picker-caret">▾</span>
            </button>
            {offerMenuOpen && (
              <div className="city-autocomplete-list" role="listbox" aria-label="Offer options">
                {OFFER_OPTIONS.map((option, index) => (
                  <button
                    key={option.label}
                    type="button"
                    className={`city-autocomplete-item ${offerActiveIndex === index ? 'city-autocomplete-item-active' : ''}`}
                    onMouseDown={() => onPickOffer(option.value)}
                    onMouseEnter={() => setOfferActiveIndex(index)}
                  >
                    {option.label}
                  </button>
                ))}
              </div>
            )}
          </div>
          <div className="city-autocomplete-wrap">
            <input
              name="city"
              value={filters.city}
              onChange={onChange}
              onFocus={() => {
                setCityInputFocused(true)
                setCityActiveIndex(-1)
              }}
              onKeyDown={onCityInputKeyDown}
              onBlur={() => setTimeout(() => {
                setCityInputFocused(false)
                setCityActiveIndex(-1)
              }, 120)}
              placeholder="Type city"
              autoComplete="off"
            />
            {showCitySuggestions && (
              <div className="city-autocomplete-list" role="listbox" aria-label="City suggestions">
                {cityPrefixOptions.map((city, index) => (
                  <button
                    key={city}
                    type="button"
                    className={`city-autocomplete-item ${cityActiveIndex === index ? 'city-autocomplete-item-active' : ''}`}
                    onMouseDown={() => onPickCity(city)}
                    onMouseEnter={() => setCityActiveIndex(index)}
                  >
                    {city}
                  </button>
                ))}
              </div>
            )}
          </div>
          <input type="number" name="minCost" value={filters.minCost} onChange={onChange} placeholder="Min cost" min="0" />
          <input type="number" name="maxCost" value={filters.maxCost} onChange={onChange} placeholder="Max cost" min="0" />
          <button type="submit" className="cta cta-primary" disabled={loading}>
            {loading ? 'Searching...' : 'Search'}
          </button>
        </form>

        {status && <p className="status-tag">{status}</p>}

        {noResultsForCity ? (
          <div className="empty-state discover-empty-state">
            <h3>No listings found for "{filters.city}"</h3>
            <p>Try another city name, broaden your budget range, or clear filters to explore all properties.</p>
            <button type="button" className="cta cta-ghost" onClick={onResetFilters}>
              Clear filters
            </button>
          </div>
        ) : loading ? (
          <div className="listing-grid">
            {Array.from({ length: 6 }).map((_, i) => (
              <ListingCardSkeleton key={i} />
            ))}
          </div>
        ) : (
          <div className="listing-grid">
            {visibleListings.map((item, index) => {
              const sold = isSoldListing(item)
              return (
                <div key={item.id} className="listing-with-action">
                  <ListingCard item={item} index={index} sold={sold} />
                  <div className="listing-actions-row">
                    <button
                      type="button"
                      className="cta cta-ghost full-width"
                      onClick={() => onOpenConfirm(item)}
                      disabled={sold}
                    >
                      {sold ? 'Sold Out' : (item.offerType === 'RENT' ? 'Rent Now' : 'Buy Now')}
                    </button>
                    <button
                      type="button"
                      className="cta cta-ghost full-width"
                      onClick={() => setSelectedProperty(item)}
                    >
                      View Details
                    </button>
                  </div>
                </div>
              )
            })}
          </div>
        )}

        {activeConfirmItem && (
          <div
            className="confirm-overlay"
            role="dialog"
            aria-modal="true"
            aria-label="Listing confirmation"
            onClick={onCancelConfirm}
          >
            <div className="confirm-panel confirm-popup" onClick={(event) => event.stopPropagation()}>
              <h3 className="confirm-popup-title">
                Confirm {activeConfirmItem.offerType === 'RENT' ? 'Rental' : 'Purchase'}
              </h3>

              <label className="confirm-check">
                <input
                  type="checkbox"
                  checked={confirmState.agreed}
                  onChange={onToggleAgree}
                />
                <span>
                  I confirm this {activeConfirmItem.offerType === 'RENT' ? 'rental' : 'purchase'}.
                </span>
              </label>

              {activeConfirmItem.offerType === 'SALE' && (
                <div className="confirm-advance-badge">
                  <span className="badge-dot" />
                  8% advance booking &mdash; balance paid at handover
                </div>
              )}

              <p className="confirm-timer">
                {confirmState.secondsLeft > 0
                  ? `🔒 Unlocks in ${confirmState.secondsLeft}s…`
                  : '✓ Ready — tap Confirm to proceed'}
              </p>

              <div className="confirm-actions">
                <button
                  type="button"
                  className="cta cta-primary"
                  disabled={confirmState.secondsLeft > 0 || !confirmState.agreed || submittingDealId === activeConfirmItem.id}
                  onClick={() => onConfirmBuyOrRent(activeConfirmItem)}
                >
                  {submittingDealId === activeConfirmItem.id ? 'Confirming...' : 'Confirm'}
                </button>
                <button type="button" className="cta cta-ghost" onClick={onCancelConfirm}>
                  Cancel
                </button>
              </div>
            </div>
          </div>
        )}

        <div ref={loadMoreRef} className="listings-load-anchor" aria-hidden="true" />
        {visibleCount < sequenceAwareListings.length && (
          <p className="listings-load-hint">Loading more properties...</p>
        )}

        {paymentState.clientSecret && paymentState.item && (
          <StripePaymentModal
            clientSecret={paymentState.clientSecret}
            publishableKey={stripePublishableKey}
            amountInInr={paymentState.amountInInr}
            paymentLabel={
              paymentState.item.offerType === 'SALE'
                ? paymentState.cappedByGatewayLimit
                  ? `Advance (${Number(paymentState.advancePercent || 0).toFixed(2)}% capped)`
                  : `Advance (${Number(paymentState.advancePercent || 8).toFixed(2)}%)`
                : 'Amount'
            }
            onClose={onClosePaymentModal}
            onSuccess={onPaymentSuccess}
          />
        )}

        {recentDealCard && (
          <div className="deal-modal-overlay" role="dialog" aria-modal="true" aria-label="Deal confirmation">
            <div className="deal-modal invoice-modal">
              <div className="invoice-watermark" aria-hidden="true">PAID</div>
              <div className="invoice-head">
                <div>
                  <p className="invoice-eyebrow">Nivora Receipt</p>
                  <h3 className="invoice-title">{recentDealCard.action} Invoice</h3>
                </div>
                <div className="invoice-head-meta">
                  <span className="deal-success-tag">INV-{recentDealCard.id}</span>
                  <span className="invoice-status">Paid</span>
                </div>
              </div>

              <div className="invoice-stamp" aria-hidden="true">Verified Receipt</div>

              <div className="invoice-meta-grid">
                <p><span>Buyer Name</span><strong>{recentDealCard.buyerName}</strong></p>
                <p><span>Buyer Email</span><strong>{recentDealCard.buyerEmail}</strong></p>
                <p><span>Invoice ID</span><strong>INV-{recentDealCard.id}</strong></p>
                <p><span>Confirmed On</span><strong>{recentDealCard.confirmedAt}</strong></p>
                <p><span>Offer Type</span><strong>{recentDealCard.offerType || 'SALE'}</strong></p>
                <p><span>Location</span><strong>{recentDealCard.city || 'Unknown city'}</strong></p>
              </div>

              <div className="invoice-table">
                <div className="invoice-table-head">
                  <span>Description</span>
                  <span>Area</span>
                  <span>Amount</span>
                </div>
                <div className="invoice-table-row">
                  <div>
                    <strong>{recentDealCard.address || 'Property Purchase'}</strong>
                    <p>{recentDealCard.street || 'Street unavailable'}, {recentDealCard.city || 'Unknown city'}</p>
                  </div>
                  <span>{Number(recentDealCard.areaSqft || 0).toLocaleString('en-IN')} sqft</span>
                  <strong>Rs {Number(recentDealCard.amount || 0).toLocaleString('en-IN')}</strong>
                </div>
              </div>

              <div className="invoice-total-box">
                <span>Total Property Price</span>
                <strong>Rs {Number(recentDealCard.totalAmount || recentDealCard.amount || 0).toLocaleString('en-IN')}</strong>
              </div>

              <div className="invoice-total-box">
                <span>Paid Online ({Number(recentDealCard.advancePercent || 0)}%)</span>
                <strong>Rs {Number(recentDealCard.amount || 0).toLocaleString('en-IN')}</strong>
              </div>

              <div className="invoice-total-box">
                <span>Remaining Balance</span>
                <strong>Rs {Number(recentDealCard.remainingAmount || 0).toLocaleString('en-IN')}</strong>
              </div>

              <div className="deal-modal-actions">
                <button type="button" className="cta cta-secondary" onClick={onPrintDeal}>
                  Print
                </button>
                <button type="button" className="cta cta-primary" onClick={onSaveDealAsText}>
                  Save as Text
                </button>
                <button type="button" className="cta cta-ghost" onClick={onCloseDealModal}>
                  Close
                </button>
              </div>
            </div>
          </div>
        )}

        {selectedProperty && (
          <PropertyDetailsModal 
            property={selectedProperty} 
            onClose={() => setSelectedProperty(null)} 
          />
        )}
      </section>
    </div>
  )
}

export default ListingsPage
