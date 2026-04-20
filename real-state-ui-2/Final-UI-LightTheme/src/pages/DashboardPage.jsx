import { useEffect, useRef, useState } from 'react'
import { motion } from 'framer-motion'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/useAuth'
import { getMyProfile } from '../services/customerApi'
import { getMyDeals } from '../services/dealApi'
import { getMyListings, deleteProperty, updateProperty } from '../services/propertyApi'
import { normalizeProperty } from '../utils/propertyMapper'
import { useToast } from '../context/useToast'
import ListingCard from '../components/ListingCard'

function formatMoney(value) {
  return `Rs ${Number(value || 0).toLocaleString('en-IN', { maximumFractionDigits: 0 })}`
}

function getDealBadge(dealDate) {
  const time = Date.parse(dealDate)
  if (Number.isNaN(time)) {
    return { label: 'Recorded', className: 'deal-badge-info' }
  }

  const ageDays = Math.floor((Date.now() - time) / (1000 * 60 * 60 * 24))
  if (ageDays <= 30) {
    return { label: 'Recent', className: 'deal-badge-recent' }
  }
  if (ageDays <= 180) {
    return { label: 'Active', className: 'deal-badge-active' }
  }
  return { label: 'Archived', className: 'deal-badge-archived' }
}

function DashboardPage() {
  const { role, session } = useAuth()
  const addToast = useToast()
  const [status, setStatus] = useState('')
  const [profile, setProfile] = useState(null)
  const [deals, setDeals] = useState([])
  const [listings, setListings] = useState([])
  const [showTopButton, setShowTopButton] = useState(false)
  const [deleteConfirm, setDeleteConfirm] = useState(null) // property id to confirm
  const [deleteStep, setDeleteStep] = useState(1)           // 1 = first confirm, 2 = final confirm
  const [editTarget, setEditTarget] = useState(null)       // property object to edit
  const [editForm, setEditForm] = useState({})
  const [actionBusy, setActionBusy] = useState(false)
  const profileRef = useRef(null)
  const dealsRef = useRef(null)

  const MotionDiv = motion.div

  function openEdit(item) {
    setEditTarget(item)
    setEditForm({
      offerType: item.offerType || 'SALE',
      city: item.city || '',
      address: item.address || '',
      street: item.street || '',
      offerCost: item.offerCost ?? item.price ?? '',
      areaSqft: item.areaSqft || '',
      configuration: item.configuration || '',
    })
  }

  async function handleDelete(id) {
    setActionBusy(true)
    try {
      await deleteProperty(id)
      setListings((prev) => prev.filter((l) => (l.id ?? l.propId) !== id))
      addToast('Property deleted successfully.', 'success')
    } catch (e) {
      addToast(e.message || 'Delete failed.', 'error')
    } finally {
      setActionBusy(false)
      setDeleteConfirm(null)
      setDeleteStep(1)
    }
  }

  async function handleEditSave() {
    if (!editTarget) return
    setActionBusy(true)
    try {
      const id = editTarget.id ?? editTarget.propId
      const updated = await updateProperty(id, editForm)
      const norm = normalizeProperty(updated)
      setListings((prev) => prev.map((l) => (l.id ?? l.propId) === id ? norm : l))
      addToast('Property updated successfully.', 'success')
      setEditTarget(null)
    } catch (e) {
      addToast(e.message || 'Update failed.', 'error')
    } finally {
      setActionBusy(false)
    }
  }

  useEffect(() => {
    async function loadDashboard() {
      setStatus('')
      try {
        if (role === 'CUSTOMER') {
          const [myProfile, myDeals] = await Promise.all([getMyProfile(), getMyDeals()])
          setProfile(myProfile)
          setDeals(Array.isArray(myDeals) ? myDeals : [])
        }
        if (role === 'BROKER') {
          const myListings = await getMyListings()
          setListings((Array.isArray(myListings) ? myListings : []).map(normalizeProperty))
        }
      } catch (error) {
        setStatus(error.message)
      }
    }

    loadDashboard()
  }, [role])

  useEffect(() => {
    function onScroll() {
      const hasManyDeals = deals.length >= 4
      setShowTopButton(hasManyDeals && window.scrollY > 260)
    }

    onScroll()
    window.addEventListener('scroll', onScroll, { passive: true })
    return () => window.removeEventListener('scroll', onScroll)
  }, [deals.length])

  return (
    <div className="page-shell">
      <section className="listings-section compact-top">
        <div className="section-head">
          <div>
            <h2>{role} Dashboard</h2>
            <p className="dash-welcome">
              Signed in as <strong>{session?.name || session?.email}</strong>
            </p>
          </div>
        </div>

        {role === 'CUSTOMER' && (
          <div className="dash-stat-strip">
            {[
              { label: 'Total Deals', value: deals.length, icon: '🤝' },
              { label: 'Total Paid', value: `Rs ${deals.reduce((s, d) => s + Number(d.dealCost || 0), 0).toLocaleString('en-IN', { maximumFractionDigits: 0 })}`, icon: '💳' },
              { label: 'Cities Active', value: [...new Set(deals.map((d) => d.city).filter(Boolean))].length || '—', icon: '🏙️' },
            ].map((stat) => (
              <MotionDiv
                key={stat.label}
                className="dash-stat-card"
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.4 }}
              >
                <span className="stat-icon">{stat.icon}</span>
                <strong className="stat-value">{stat.value}</strong>
                <span className="stat-label">{stat.label}</span>
              </MotionDiv>
            ))}
          </div>
        )}

        {role === 'BROKER' && (
          <div className="dash-stat-strip">
            {[
              { label: 'My Listings', value: listings.length, icon: '🏠' },
              { label: 'Cities', value: [...new Set(listings.map((l) => l.city).filter(Boolean))].length || '—', icon: '🏙️' },
              { label: 'For Sale', value: listings.filter((l) => l.offerType === 'SALE').length, icon: '🏷️' },
              { label: 'For Rent', value: listings.filter((l) => l.offerType === 'RENT').length, icon: '🔑' },
            ].map((stat) => (
              <MotionDiv
                key={stat.label}
                className="dash-stat-card"
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.4 }}
              >
                <span className="stat-icon">{stat.icon}</span>
                <strong className="stat-value">{stat.value}</strong>
                <span className="stat-label">{stat.label}</span>
              </MotionDiv>
            ))}
          </div>
        )}

        {status && <p className="status-message">{status}</p>}

        {role === 'CUSTOMER' && (
          <>
            <div className="dash-quick-actions">
              <Link className="cta cta-ghost quick-action-btn" to="/listings">
                Browse Listings
              </Link>
              <button
                type="button"
                className="cta cta-ghost quick-action-btn"
                onClick={() => dealsRef.current?.scrollIntoView({ behavior: 'smooth', block: 'start' })}
              >
                Recent Deals
              </button>
            </div>

            {showTopButton && (
              <button
                type="button"
                className="dashboard-top-center-btn"
                onClick={() => window.scrollTo({ top: 0, behavior: 'smooth' })}
              >
                Back to Top
              </button>
            )}

            <div className="dashboard-layout">
              {profile && (
                <MotionDiv
                  className="profile-card"
                  ref={profileRef}
                  initial={{ opacity: 0, y: 24 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ duration: 0.45 }}
                >
                  <div className="profile-avatar">
                    {(profile.custName || 'C').charAt(0).toUpperCase()}
                  </div>
                  <div className="profile-info">
                    <h3>{profile.custName}</h3>
                    <p className="profile-email">{profile.user?.email}</p>
                    <div className="profile-meta">
                      <span>{profile.user?.mobile}</span>
                      <span>{profile.user?.city}</span>
                      <span className={`role-pill role-pill-${String(profile.user?.role || '').toLowerCase()}`}>
                        {profile.user?.role}
                      </span>
                    </div>
                  </div>
                </MotionDiv>
              )}

              <div ref={dealsRef}>
                <h3 className="subsection-title">My Deals ({deals.length})</h3>
                {deals.length === 0 && (
                  <div className="empty-state">No deals yet. Start from listings.</div>
                )}
                <div className="deal-grid">
                  {deals.map((deal, index) => {
                    const badge = getDealBadge(deal.dealDate)
                    return (
                      <MotionDiv
                        key={deal.dealId}
                        className="deal-card"
                        initial={{ opacity: 0, y: 16 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.05 * index, duration: 0.34 }}
                      >
                        <div className="deal-card-head">
                          <button
                            type="button"
                            className="deal-id deal-id-copy"
                            title="Copy deal ID"
                            onClick={() => {
                              navigator.clipboard.writeText(String(deal.dealId))
                              addToast(`Deal #${deal.dealId} copied!`, 'success')
                            }}
                          >
                            Deal #{deal.dealId} 📋
                          </button>
                          <span className={`deal-badge ${badge.className}`}>{badge.label}</span>
                        </div>
                        <p className="deal-amount">Paid Online: {formatMoney(deal.dealCost)}</p>
                        {Number(deal.totalAmount || 0) > 0 && (
                          <p className="deal-date">Total Price: {formatMoney(deal.totalAmount)}</p>
                        )}
                        {Number(deal.remainingAmount || 0) > 0 && (
                          <p className="deal-date">Remaining: {formatMoney(deal.remainingAmount)}</p>
                        )}
                        <p className="deal-date">Date: {deal.dealDate}</p>
                      </MotionDiv>
                    )
                  })}
                </div>
              </div>
            </div>
          </>
        )}

        {role === 'BROKER' && (
          <>
            <div className="api-panel single-column broker-actions-panel">
              <p className="dash-welcome">
                Do you want to add property? Use the option below.
              </p>
              <div className="dash-quick-actions">
                <Link className="cta cta-primary quick-action-btn" to="/add-property">
                  Add Property
                </Link>
                <Link className="cta cta-ghost quick-action-btn" to="/listings">
                  View Marketplace
                </Link>
              </div>
            </div>

            <section className="broker-listings-section">
              <h3 className="subsection-title">My Listings ({listings.length})</h3>
              {listings.length === 0 && (
                <p className="empty-state">No listings yet. Add your first property.</p>
              )}
              <div className="my-listings-grid dashboard-broker-listings">
                {listings.map((item, index) => {
                  const isSold = item.status === false || String(item?.status || '').toUpperCase() === 'SOLD'
                  const propId = item.id ?? item.propId
                  return (
                    <div key={propId} className="listing-manage-wrap">
                      <ListingCard item={item} index={index} sold={isSold} />
                      {!isSold && (
                        <div className="listing-card-actions">
                          <button
                            type="button"
                            className="lca-btn lca-edit"
                            onClick={() => openEdit(item)}
                          >
                            ✏️ Edit
                          </button>
                          <button
                            type="button"
                            className="lca-btn lca-delete"
                            onClick={() => setDeleteConfirm(propId)}
                          >
                            🗑️ Delete
                          </button>
                        </div>
                      )}
                    </div>
                  )
                })}
              </div>
            </section>
          </>
        )}
      </section>

      {/* ── Delete Confirmation Modal (2-step) ── */}
      {deleteConfirm !== null && (
        <div className="logout-confirm-overlay" onClick={() => { setDeleteConfirm(null); setDeleteStep(1) }}>
          <div className="logout-confirm-modal" onClick={(e) => e.stopPropagation()}>
            {deleteStep === 1 ? (
              <>
                <div className="delete-step-icon">🗑️</div>
                <h3>Delete Property?</h3>
                <p>Are you sure you want to remove this listing? This action cannot be undone.</p>
                <div className="logout-confirm-actions">
                  <button type="button" className="logout-cancel-btn" onClick={() => { setDeleteConfirm(null); setDeleteStep(1) }}>
                    Cancel
                  </button>
                  <button type="button" className="logout-yes-btn" onClick={() => setDeleteStep(2)}>
                    Yes, Continue
                  </button>
                </div>
              </>
            ) : (
              <>
                <div className="delete-step-icon delete-step-icon-danger">⚠️</div>
                <h3 className="delete-final-title">Final Confirmation</h3>
                <p className="delete-final-desc">This will <strong>permanently delete</strong> the property from the marketplace. Customers will no longer be able to find or purchase it.</p>
                <p className="delete-final-warn">Are you absolutely sure?</p>
                <div className="logout-confirm-actions">
                  <button type="button" className="logout-cancel-btn" onClick={() => setDeleteStep(1)} disabled={actionBusy}>
                    ← Go Back
                  </button>
                  <button type="button" className="logout-yes-btn delete-final-btn" onClick={() => handleDelete(deleteConfirm)} disabled={actionBusy}>
                    {actionBusy ? 'Deleting…' : 'Delete Permanently'}
                  </button>
                </div>
              </>
            )}
          </div>
        </div>
      )}

      {/* ── Edit Property Modal ── */}
      {editTarget && (
        <div className="logout-confirm-overlay" onClick={() => setEditTarget(null)}>
          <div className="edit-property-modal" onClick={(e) => e.stopPropagation()}>
            <h3>Edit Property</h3>
            <div className="edit-property-fields">
              {[
                { label: 'Offer Type', key: 'offerType', type: 'select', options: ['SALE', 'RENT'] },
                { label: 'City', key: 'city', type: 'text' },
                { label: 'Address', key: 'address', type: 'text' },
                { label: 'Street', key: 'street', type: 'text' },
                { label: 'Price (₹)', key: 'offerCost', type: 'number' },
                { label: 'Area (sqft)', key: 'areaSqft', type: 'number' },
                { label: 'Configuration', key: 'configuration', type: 'text' },
              ].map(({ label, key, type, options }) => (
                <label key={key} className="edit-field-label">
                  <span>{label}</span>
                  {type === 'select' ? (
                    <select
                      className="edit-field-input"
                      value={editForm[key] || ''}
                      onChange={(e) => setEditForm((f) => ({ ...f, [key]: e.target.value }))}
                    >
                      {options.map((o) => <option key={o} value={o}>{o}</option>)}
                    </select>
                  ) : (
                    <input
                      className="edit-field-input"
                      type={type}
                      value={editForm[key] || ''}
                      onChange={(e) => setEditForm((f) => ({ ...f, [key]: e.target.value }))}
                    />
                  )}
                </label>
              ))}
            </div>
            <div className="logout-confirm-actions">
              <button type="button" className="logout-cancel-btn" onClick={() => setEditTarget(null)} disabled={actionBusy}>
                Cancel
              </button>
              <button type="button" className="logout-yes-btn" onClick={handleEditSave} disabled={actionBusy}>
                {actionBusy ? 'Saving…' : 'Save Changes'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default DashboardPage
