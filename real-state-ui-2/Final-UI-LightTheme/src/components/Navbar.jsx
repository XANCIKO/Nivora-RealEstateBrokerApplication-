import { useEffect, useState } from 'react'
import { Link, NavLink } from 'react-router-dom'
import { useAuth } from '../context/useAuth'
import { getMyProfile } from '../services/customerApi'
import { getBrokerById, listBrokers } from '../services/brokerApi'
import nivoraLogo from '../assets/nivora-logo.svg'

function Navbar() {
  const { isAuthenticated, role, session, logout } = useAuth()
  const [menuOpen, setMenuOpen] = useState(false)
  const [profileMenuOpen, setProfileMenuOpen] = useState(false)
  const [logoutDialogOpen, setLogoutDialogOpen] = useState(false)
  const [profileDetails, setProfileDetails] = useState(null)
  const [isCompactHeader, setIsCompactHeader] = useState(false)

  function closeMenu() { setMenuOpen(false) }
  function closeProfileMenu() { setProfileMenuOpen(false) }
  function closeAllMenus() {
    setMenuOpen(false)
    setProfileMenuOpen(false)
  }
  function closeLogoutDialog() { setLogoutDialogOpen(false) }
  function onConfirmLogout() {
    setLogoutDialogOpen(true)
  }

  function onLogoutApproved() {
    closeMenu()
    closeProfileMenu()
    closeLogoutDialog()
    logout()
  }

  useEffect(() => {
    function onKeyDown(event) {
      if (event.key === 'Escape') {
        setProfileMenuOpen(false)
        setLogoutDialogOpen(false)
      }
    }

    window.addEventListener('keydown', onKeyDown)
    return () => window.removeEventListener('keydown', onKeyDown)
  }, [])

  useEffect(() => {
    function onScroll() {
      setIsCompactHeader(window.scrollY > 28)
    }

    onScroll()
    window.addEventListener('scroll', onScroll, { passive: true })
    return () => window.removeEventListener('scroll', onScroll)
  }, [])

  useEffect(() => {
    async function loadProfileDetails() {
      if (!isAuthenticated || !profileMenuOpen) {
        return
      }

      try {
        if (role === 'CUSTOMER') {
          const profile = await getMyProfile()
          setProfileDetails({
            name: profile?.custName || profile?.user?.name || session?.name || 'User',
            email: profile?.user?.email || session?.email || 'No email available',
            city: profile?.user?.city || session?.city || null,
            mobile: profile?.user?.mobile || session?.mobile || null,
          })
          return
        }

        if (role === 'BROKER') {
          const brokerId = session?.brokerId || session?.id || session?.userId

          if (brokerId) {
            const broker = await getBrokerById(brokerId)
            setProfileDetails({
              name: broker?.brokerName || broker?.user?.name || session?.name || 'Broker',
              email: broker?.user?.email || session?.email || 'No email available',
              city: broker?.user?.city || session?.city || null,
              mobile: broker?.user?.mobile || session?.mobile || null,
            })
            return
          }

          const brokers = await listBrokers()
          const byEmail = Array.isArray(brokers)
            ? brokers.find((item) => String(item?.user?.email || '').toLowerCase() === String(session?.email || '').toLowerCase())
            : null

          setProfileDetails({
            name: byEmail?.brokerName || byEmail?.user?.name || session?.name || 'Broker',
            email: byEmail?.user?.email || session?.email || 'No email available',
            city: byEmail?.user?.city || session?.city || null,
            mobile: byEmail?.user?.mobile || session?.mobile || null,
          })
          return
        }

        setProfileDetails(null)
      } catch {
        setProfileDetails({
          name: session?.name || 'User',
          email: session?.email || 'No email available',
          city: session?.city || null,
          mobile: session?.mobile || null,
        })
      }
    }

    loadProfileDetails()
  }, [isAuthenticated, profileMenuOpen, role, session])

  const displayName = profileDetails?.name || session?.name || 'User'
  const displayEmail = profileDetails?.email || session?.email || 'No email available'
  const displayCity = profileDetails?.city || session?.city || 'N/A'
  const displayMobile = profileDetails?.mobile || session?.mobile || 'N/A'

  return (
    <>
      <header className={`site-header ${isCompactHeader ? 'nav-compact' : ''}`}>
        <div className="site-header-inner">
        <Link className="brand" to="/" onClick={closeAllMenus}>
          <img className="brand-logo" src={nivoraLogo} alt="Nivora" />
        </Link>

        <nav className="nav-links">
          <NavLink to="/" onClick={closeAllMenus}>Home</NavLink>
          <NavLink to="/listings" onClick={closeAllMenus}>Discover</NavLink>
          {isAuthenticated && role === 'BROKER' && <NavLink to="/add-property" onClick={closeAllMenus}>Add Property</NavLink>}
          {isAuthenticated && <NavLink to="/dashboard" onClick={closeAllMenus}>Dashboard</NavLink>}
        </nav>

        <div className="auth-links">
          {isAuthenticated ? (
            <>
              <span className={`role-pill role-pill-${String(role || '').toLowerCase()}`}>{role}</span>
              <button
                type="button"
                className="profile-menu-trigger"
                aria-label="Open profile menu"
                aria-expanded={profileMenuOpen}
                onClick={() => setProfileMenuOpen((prev) => !prev)}
              >
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <path d="M12 12.2a4.1 4.1 0 1 0 0-8.2 4.1 4.1 0 0 0 0 8.2Zm0 2.3c-3.9 0-7.1 2.1-8.3 5.1-.2.6.2 1.2.9 1.2h14.8c.7 0 1.1-.6.9-1.2-1.2-3-4.4-5.1-8.3-5.1Z" fill="currentColor" />
                </svg>
              </button>
              <button type="button" className="text-btn logout-top-btn" onClick={onConfirmLogout}>
                Logout
              </button>
            </>
          ) : (
            <>
              <NavLink to="/login" onClick={closeAllMenus}>Login</NavLink>
              <NavLink to="/signup" className="btn-nav" onClick={closeAllMenus}>
                Sign up
              </NavLink>
            </>
          )}
        </div>

        <button
          type="button"
          className={`hamburger-btn${menuOpen ? ' open' : ''}`}
          aria-label="Toggle menu"
          aria-expanded={menuOpen}
          onClick={() => setMenuOpen((v) => !v)}
        >
          <span />
          <span />
          <span />
        </button>

        {menuOpen && (
          <nav className="mobile-nav-drawer">
            <NavLink to="/" onClick={closeMenu}>Home</NavLink>
            <NavLink to="/listings" onClick={closeMenu}>Discover</NavLink>
            {isAuthenticated && role === 'BROKER' && <NavLink to="/add-property" onClick={closeMenu}>Add Property</NavLink>}
            {isAuthenticated && <NavLink to="/dashboard" onClick={closeMenu}>Dashboard</NavLink>}
            <hr style={{ margin: '0.3rem 0', border: 'none', borderTop: '1px solid rgba(46,112,181,0.15)' }} />
            {isAuthenticated ? (
              <button type="button" onClick={onConfirmLogout}>Logout</button>
            ) : (
              <>
                <NavLink to="/login" onClick={closeMenu}>Login</NavLink>
                <NavLink to="/signup" onClick={closeMenu}>Sign up</NavLink>
              </>
            )}
          </nav>
        )}

        {isAuthenticated && profileMenuOpen && (
          <>
            <button
              type="button"
              className="profile-menu-backdrop"
              aria-label="Close profile menu"
              onClick={closeProfileMenu}
            />
            <aside className="profile-side-menu" aria-label="Profile side menu">
              <div className="profile-side-header">
                <h3>My Account</h3>
                <button type="button" className="profile-side-close" onClick={closeProfileMenu} aria-label="Close">
                  ×
                </button>
              </div>

              <div className="profile-side-card">
                <div className="profile-side-avatar">
                  {String(displayName || displayEmail || role || 'U').charAt(0).toUpperCase()}
                </div>
                <div className="profile-side-meta">
                  <p className="profile-side-name">{displayName}</p>
                  <p className="profile-side-email">{displayEmail}</p>
                </div>
              </div>

              <div className="profile-side-grid">
                <div>
                  <span>Role</span>
                  <strong>{role || 'N/A'}</strong>
                </div>
                <div>
                  <span>Status</span>
                  <strong>{session?.token ? 'Logged In' : 'Guest'}</strong>
                </div>
                <div>
                  <span>City</span>
                  <strong>{displayCity}</strong>
                </div>
                <div>
                  <span>Phone</span>
                  <strong>{displayMobile}</strong>
                </div>
              </div>

              <button
                type="button"
                className="profile-side-logout"
                onClick={onConfirmLogout}
              >
                Logout
              </button>
            </aside>
          </>
        )}

        </div>
      </header>

      {isAuthenticated && logoutDialogOpen && (
        <>
          <button
            type="button"
            className="logout-confirm-backdrop"
            aria-label="Close logout confirmation"
            onClick={closeLogoutDialog}
          />
          <div className="logout-confirm-modal" role="dialog" aria-modal="true" aria-labelledby="logout-confirm-title">
            <h4 id="logout-confirm-title">Log out?</h4>
            <p>You will need to sign in again to continue.</p>
            <div className="logout-confirm-actions">
              <button type="button" className="cta cta-ghost" onClick={closeLogoutDialog}>
                Cancel
              </button>
              <button type="button" className="logout-danger-btn" onClick={onLogoutApproved}>
                Yes, log out
              </button>
            </div>
          </div>
        </>
      )}
    </>
  )
}

export default Navbar
