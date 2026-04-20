import { useCallback, useState } from 'react'
import { AnimatePresence as FramerAnimatePresence, motion as framerMotion } from 'framer-motion'
import { ToastContext } from './toast-context'

let nextId = 0

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([])
  const MotionDiv = framerMotion.div

  const addToast = useCallback((message, type = 'info') => {
    const id = nextId++
    setToasts((prev) => [...prev, { id, message, type }])
    setTimeout(() => setToasts((prev) => prev.filter((t) => t.id !== id)), 3800)
  }, [])

  const removeToast = useCallback((id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id))
  }, [])

  return (
    <ToastContext.Provider value={addToast}>
      {children}
      <div className="toast-container" aria-live="polite">
        <FramerAnimatePresence>
          {toasts.map((toast) => (
            <MotionDiv
              key={toast.id}
              className={`toast toast-${toast.type}`}
              initial={{ opacity: 0, y: 40, scale: 0.92 }}
              animate={{ opacity: 1, y: 0, scale: 1 }}
              exit={{ opacity: 0, y: 20, scale: 0.9 }}
              transition={{ duration: 0.28 }}
            >
              <span>{toast.message}</span>
              <button
                type="button"
                className="toast-close"
                onClick={() => removeToast(toast.id)}
                aria-label="Dismiss"
              >
                ×
              </button>
            </MotionDiv>
          ))}
        </FramerAnimatePresence>
      </div>
    </ToastContext.Provider>
  )
}
