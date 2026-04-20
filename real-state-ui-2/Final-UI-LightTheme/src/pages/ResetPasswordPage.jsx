import { useState } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { resetPassword } from '../services/authApi'

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
      <path d="M1.5 12s3.8-6.5 10.5-6.5S22.5 12 22.5 12s-3.8 6.5-10.5 6.5S1.5 12 1.5 12Z" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" />
      <circle cx="12" cy="12" r="3.2" fill="none" stroke="currentColor" strokeWidth="1.7" />
    </svg>
  )
}

function EyeOffIcon() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className="password-toggle-icon">
      <path d="M3 3l18 18" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" />
      <path d="M10.7 5.7A11.5 11.5 0 0 1 12 5.5c6.7 0 10.5 6.5 10.5 6.5a18.4 18.4 0 0 1-4.4 4.9" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" />
      <path d="M6.1 6.1A18.8 18.8 0 0 0 1.5 12S5.3 18.5 12 18.5c1.7 0 3.1-.4 4.4-1" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  )
}

function ResetPasswordPage() {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const token = searchParams.get('token') || ''
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  const passwordRequirements = getPasswordRequirements(password)
  const allRequirementsMet = Object.values(passwordRequirements).every(Boolean)
  const passwordStrength = getPasswordStrength(password)

  async function onSubmit(event) {
    event.preventDefault()
    setError('')
    setMessage('')

    if (!token) {
      setError('Reset token is missing or invalid.')
      return
    }

    if (!allRequirementsMet) {
      setError('Password does not meet all requirements.')
      return
    }

    if (password !== confirmPassword) {
      setError('Passwords do not match.')
      return
    }

    setLoading(true)

    try {
      await resetPassword({ token, password, confirmPassword })
      setMessage('Password reset successful. Redirecting to login...')
      setTimeout(() => navigate('/login'), 900)
    } catch (submitError) {
      setError(submitError.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="page-shell auth-page">
      <section className="auth-card">
        <h1>Set new password</h1>
        <p className="auth-note">Choose a strong password for your broker or customer account.</p>

        <form className="stack-form" onSubmit={onSubmit}>
          <div className="password-field-wrap">
            <input
              type={showPassword ? 'text' : 'password'}
              name="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              placeholder="New password"
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

          {password && (
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
                <div className={`requirement ${passwordRequirements.minLength ? 'met' : ''}`}><span className="requirement-check">{passwordRequirements.minLength ? '✓' : '○'}</span><span>At least 8 characters</span></div>
                <div className={`requirement ${passwordRequirements.hasUppercase ? 'met' : ''}`}><span className="requirement-check">{passwordRequirements.hasUppercase ? '✓' : '○'}</span><span>Uppercase letter (A-Z)</span></div>
                <div className={`requirement ${passwordRequirements.hasLowercase ? 'met' : ''}`}><span className="requirement-check">{passwordRequirements.hasLowercase ? '✓' : '○'}</span><span>Lowercase letter (a-z)</span></div>
                <div className={`requirement ${passwordRequirements.hasNumber ? 'met' : ''}`}><span className="requirement-check">{passwordRequirements.hasNumber ? '✓' : '○'}</span><span>Number (0-9)</span></div>
                <div className={`requirement ${passwordRequirements.hasSpecial ? 'met' : ''}`}><span className="requirement-check">{passwordRequirements.hasSpecial ? '✓' : '○'}</span><span>Special character (!@#$%^&*)</span></div>
              </div>
            </div>
          )}

          <div className="password-field-wrap">
            <input
              type={showConfirmPassword ? 'text' : 'password'}
              name="confirmPassword"
              value={confirmPassword}
              onChange={(event) => setConfirmPassword(event.target.value)}
              placeholder="Confirm new password"
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

          <button type="submit" className="cta cta-primary" disabled={loading || !allRequirementsMet}>
            {loading ? 'Resetting password...' : 'Reset password'}
          </button>
        </form>

        {error && <p className="status-message">{error}</p>}
        {message && <p className="status-tag status-ok">{message}</p>}

        <p className="muted-text">
          Back to <Link to="/login">Login</Link>
        </p>
      </section>
    </div>
  )
}

export default ResetPasswordPage