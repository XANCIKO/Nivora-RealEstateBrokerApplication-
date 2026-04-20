import { useMemo, useState } from 'react'
import { Elements, PaymentElement, useElements, useStripe } from '@stripe/react-stripe-js'
import { loadStripe } from '@stripe/stripe-js'

function PaymentForm({ amountInInr, paymentLabel, onClose, onSuccess }) {
  const stripe = useStripe()
  const elements = useElements()
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState('')

  async function onSubmit(event) {
    event.preventDefault()
    if (!stripe || !elements) return

    setSubmitting(true)
    setError('')

    const result = await stripe.confirmPayment({
      elements,
      redirect: 'if_required',
    })

    if (result.error) {
      setError(result.error.message || 'Payment failed. Please try again.')
      setSubmitting(false)
      return
    }

    if (result.paymentIntent?.status === 'succeeded') {
      onSuccess(result.paymentIntent.id)
      return
    }

    setError('Payment could not be completed. Please try again.')
    setSubmitting(false)
  }

  return (
    <div className="deal-modal-overlay" role="dialog" aria-modal="true" aria-label="Stripe payment">
      <div className="deal-modal">
        <div className="deal-success-head">
          <h3>Complete Payment</h3>
        </div>

        <p className="deal-success-time">{paymentLabel}: Rs {Number(amountInInr || 0).toLocaleString('en-IN')}</p>

        <form className="stripe-form" onSubmit={onSubmit}>
          <div className="stripe-element-wrap">
            <PaymentElement />
          </div>

          {error && <p className="status-message">{error}</p>}

          <div className="deal-modal-actions">
            <button type="submit" className="cta cta-primary" disabled={submitting || !stripe || !elements}>
              {submitting ? 'Processing...' : 'Pay Now'}
            </button>
            <button type="button" className="cta cta-ghost" disabled={submitting} onClick={onClose}>
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

function StripePaymentModal({ clientSecret, publishableKey, amountInInr, paymentLabel = 'Amount', onClose, onSuccess }) {
  const stripePromise = useMemo(() => {
    if (!publishableKey) return null
    return loadStripe(publishableKey)
  }, [publishableKey])

  if (!clientSecret || !stripePromise) return null

  return (
    <Elements stripe={stripePromise} options={{ clientSecret }}>
      <PaymentForm amountInInr={amountInInr} paymentLabel={paymentLabel} onClose={onClose} onSuccess={onSuccess} />
    </Elements>
  )
}

export default StripePaymentModal
