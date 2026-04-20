import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { registerUser } from '../services/authApi'
import { INDIAN_CITIES } from '../data/indianCities'

const initialForm = {
  email: '',
  password: '',
  confirmPassword: '',
  role: 'CUSTOMER',
  name: '',
  mobile: '',
  city: '',
}

const ROLE_OPTIONS = ['CUSTOMER', 'BROKER']

function getPasswordStrength(pwd) {
  let strength = 0
  if (pwd.length >= 8) strength += 1
  if (/[A-Z]/.test(pwd)) strength += 1
  if (/[a-z]/.test(pwd)) strength += 1
  if (/\d/.test(pwd)) strength += 1
  if (/[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]/.test(pwd)) strength += 1
  
  if (strength <= 2) return { label: 'Weak', color: '#ef4444' }
  if (strength <= 3) return { label: 'Fair', color: '#f97316' }
  if (strength <= 4) return { label: 'Good', color: '#eab308' }
  return { label: 'Strong', color: '#22c55e' }
}

function getPasswordRequirements(pwd) {
  return {
    minLength: pwd.length >= 8,
    hasUppercase: /[A-Z]/.test(pwd),
    hasLowercase: /[a-z]/.test(pwd),
    hasNumber: /\d/.test(pwd),
    hasSpecial: /[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]/.test(pwd),
  }
}

function EyeIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="password-toggle-icon">
      <path
        d="M1.5 12s3.8-6.5 10.5-6.5S22.5 12 22.5 12s-3.8 6.5-10.5 6.5S1.5 12 1.5 12Z"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.7"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <circle cx="12" cy="12" r="3.2" fill="none" stroke="currentColor" strokeWidth="1.7" />
    </svg>
  )
}

function EyeOffIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="password-toggle-icon">
      <path
        d="M3 3l18 18"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.7"
        strokeLinecap="round"
      />
      <path
        d="M10.7 5.7A11.5 11.5 0 0 1 12 5.5c6.7 0 10.5 6.5 10.5 6.5a18.4 18.4 0 0 1-4.4 4.9"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.7"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
      <path
        d="M6.1 6.1A18.8 18.8 0 0 0 1.5 12S5.3 18.5 12 18.5c1.7 0 3.1-.4 4.4-1"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.7"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  )
}

function SignupPage() {
  const navigate = useNavigate()
  const [form, setForm] = useState(initialForm)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)
  const [mismatchPulse, setMismatchPulse] = useState(0)
  const [roleMenuOpen, setRoleMenuOpen] = useState(false)
  const [cityInputFocused, setCityInputFocused] = useState(false)
  const [roleActiveIndex, setRoleActiveIndex] = useState(-1)
  const [cityActiveIndex, setCityActiveIndex] = useState(-1)

  const hasPasswordMismatch = error === 'Passwords do not match.'
  const passwordRequirements = getPasswordRequirements(form.password)
  const allRequirementsMet = Object.values(passwordRequirements).every(Boolean)
  const passwordStrength = getPasswordStrength(form.password)
  const normalizedCityQuery = String(form.city || '').trim().toLowerCase()
  const cityPrefixOptions = normalizedCityQuery
    ? INDIAN_CITIES.filter((city) => city.toLowerCase().startsWith(normalizedCityQuery)).slice(0, 10)
    : []
  const showCitySuggestions = cityInputFocused && normalizedCityQuery.length > 0 && cityPrefixOptions.length > 0

  function onChange(event) {
    const { name, value } = event.target
    const nextValue = name === 'mobile' ? value.replace(/\D/g, '').slice(0, 10) : value
    setForm((prev) => ({ ...prev, [name]: nextValue }))

    if (hasPasswordMismatch && (name === 'password' || name === 'confirmPassword')) {
      setError('')
    }
  }

  function onPickRole(role) {
    setForm((prev) => ({ ...prev, role }))
    setRoleMenuOpen(false)
    setRoleActiveIndex(-1)
  }

  function onPickCity(city) {
    setForm((prev) => ({ ...prev, city }))
    setCityInputFocused(false)
    setCityActiveIndex(-1)
  }

  function onRoleTriggerKeyDown(event) {
    if (event.key === 'ArrowDown') {
      event.preventDefault()
      if (!roleMenuOpen) {
        setRoleMenuOpen(true)
        setRoleActiveIndex(0)
        return
      }
      setRoleActiveIndex((prev) => (prev + 1) % ROLE_OPTIONS.length)
      return
    }

    if (event.key === 'ArrowUp') {
      event.preventDefault()
      if (!roleMenuOpen) {
        setRoleMenuOpen(true)
        setRoleActiveIndex(ROLE_OPTIONS.length - 1)
        return
      }
      setRoleActiveIndex((prev) => (prev <= 0 ? ROLE_OPTIONS.length - 1 : prev - 1))
      return
    }

    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault()
      if (!roleMenuOpen) {
        setRoleMenuOpen(true)
        setRoleActiveIndex(0)
        return
      }
      const index = roleActiveIndex >= 0 ? roleActiveIndex : 0
      onPickRole(ROLE_OPTIONS[index])
      return
    }

    if (event.key === 'Escape') {
      setRoleMenuOpen(false)
      setRoleActiveIndex(-1)
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
    setError('')
    setMessage('')

    if (!allRequirementsMet) {
      setError('Password does not meet all requirements.')
      return
    }

    if (form.password !== form.confirmPassword) {
      setMismatchPulse((prev) => prev + 1)
      setError('Passwords do not match.')
      return
    }

    if (!/^\d{10}$/.test(form.mobile)) {
      setError('Mobile number must be exactly 10 digits.')
      return
    }

    setLoading(true)

    try {
      const payload = {
        email: form.email,
        password: form.password,
        role: form.role,
        name: form.name,
        mobile: form.mobile,
        city: form.city,
      }
      await registerUser(payload)
      setMessage('Registration successful. Please login.')
      setTimeout(() => {
        navigate('/login')
      }, 700)
    } catch (submitError) {
      setError(submitError.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page-shell auth-page">
      <section className="auth-card">
        <h1>Create account</h1>
        <p>Start as customer or broker.</p>

        <form className="stack-form" onSubmit={onSubmit}>
          <input name="name" value={form.name} onChange={onChange} placeholder="Full name" required />
          <input name="email" value={form.email} onChange={onChange} placeholder="Email" required />
          <div
            key={`password-${mismatchPulse}`}
            className={`password-field-wrap ${hasPasswordMismatch ? 'password-field-wrap-mismatch' : ''}`}
          >
            <input
              type={showPassword ? 'text' : 'password'}
              name="password"
              value={form.password}
              onChange={onChange}
              placeholder="Password"
              className={hasPasswordMismatch ? 'mismatch-input' : ''}
              required
            />
            <button
              type="button"
              className="password-toggle-btn"
              onClick={() => setShowPassword((prev) => !prev)}
              aria-label={showPassword ? 'Hide password' : 'Show password'}
            >
              {showPassword ? <EyeOffIcon /> : <EyeIcon />}
            </button>
          </div>

          {form.password && (
            <div className="password-validation">
              <div className="strength-bar">
                <div
                  className="strength-fill"
                  style={{
                    width: `${(Object.values(passwordRequirements).filter(Boolean).length / 5) * 100}%`,
                    backgroundColor: passwordStrength.color,
                  }}
                />
              </div>
              <p className="strength-label" style={{ color: passwordStrength.color }}>
                Strength: {passwordStrength.label}
              </p>

              <div className="requirements-list">
                <div className={`requirement ${passwordRequirements.minLength ? 'met' : ''}`}>
                  <span className="requirement-check">{passwordRequirements.minLength ? '✓' : '○'}</span>
                  <span>At least 8 characters</span>
                </div>
                <div className={`requirement ${passwordRequirements.hasUppercase ? 'met' : ''}`}>
                  <span className="requirement-check">{passwordRequirements.hasUppercase ? '✓' : '○'}</span>
                  <span>Uppercase letter (A-Z)</span>
                </div>
                <div className={`requirement ${passwordRequirements.hasLowercase ? 'met' : ''}`}>
                  <span className="requirement-check">{passwordRequirements.hasLowercase ? '✓' : '○'}</span>
                  <span>Lowercase letter (a-z)</span>
                </div>
                <div className={`requirement ${passwordRequirements.hasNumber ? 'met' : ''}`}>
                  <span className="requirement-check">{passwordRequirements.hasNumber ? '✓' : '○'}</span>
                  <span>Number (0-9)</span>
                </div>
                <div className={`requirement ${passwordRequirements.hasSpecial ? 'met' : ''}`}>
                  <span className="requirement-check">{passwordRequirements.hasSpecial ? '✓' : '○'}</span>
                  <span>Special character (!@#$%^&*)</span>
                </div>
              </div>
            </div>
          )}

          <div
            key={`confirm-password-${mismatchPulse}`}
            className={`password-field-wrap ${hasPasswordMismatch ? 'password-field-wrap-mismatch' : ''}`}
          >
            <input
              type={showConfirmPassword ? 'text' : 'password'}
              name="confirmPassword"
              value={form.confirmPassword}
              onChange={onChange}
              placeholder="Confirm password"
              className={hasPasswordMismatch ? 'mismatch-input' : ''}
              required
            />
            <button
              type="button"
              className="password-toggle-btn"
              onClick={() => setShowConfirmPassword((prev) => !prev)}
              aria-label={showConfirmPassword ? 'Hide confirm password' : 'Show confirm password'}
            >
              {showConfirmPassword ? <EyeOffIcon /> : <EyeIcon />}
            </button>
          </div>
          <div className="city-autocomplete-wrap role-picker-wrap">
            <button
              type="button"
              className="role-picker-trigger"
              onClick={() => setRoleMenuOpen((prev) => !prev)}
              onKeyDown={onRoleTriggerKeyDown}
              onBlur={() => setTimeout(() => {
                setRoleMenuOpen(false)
                setRoleActiveIndex(-1)
              }, 120)}
              aria-haspopup="listbox"
              aria-expanded={roleMenuOpen}
            >
              <span>{form.role}</span>
              <span className="role-picker-caret">▾</span>
            </button>
            {roleMenuOpen && (
              <div className="city-autocomplete-list" role="listbox" aria-label="Role options">
                {ROLE_OPTIONS.map((role, index) => (
                  <button
                    key={role}
                    type="button"
                    className={`city-autocomplete-item ${roleActiveIndex === index ? 'city-autocomplete-item-active' : ''}`}
                    onMouseDown={() => onPickRole(role)}
                    onMouseEnter={() => setRoleActiveIndex(index)}
                  >
                    {role}
                  </button>
                ))}
              </div>
            )}
          </div>
          <input
            name="mobile"
            value={form.mobile}
            onChange={onChange}
            placeholder="Mobile"
            inputMode="numeric"
            pattern="[0-9]{10}"
            minLength={10}
            maxLength={10}
            title="Enter a valid 10-digit mobile number"
            required
          />
          <div className="city-autocomplete-wrap">
            <input
              name="city"
              value={form.city}
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
              placeholder="Start typing city"
              autoComplete="off"
              required
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
          <button type="submit" className="cta cta-primary" disabled={loading || !allRequirementsMet}>
            {loading ? 'Creating account...' : 'Sign up'}
          </button>
        </form>

        {error && (
          <p
            className={error === 'Passwords do not match.' ? 'status-message status-pop' : 'status-message'}
            aria-live="polite"
          >
            {error}
          </p>
        )}
        {message && <p className="status-tag">{message}</p>}

        <p className="muted-text">
          Already registered? <Link to="/login">Login</Link>
        </p>
      </section>
    </div>
  )
}

export default SignupPage
