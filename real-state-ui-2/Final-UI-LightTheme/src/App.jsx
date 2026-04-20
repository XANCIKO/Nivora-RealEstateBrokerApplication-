import { Navigate, Route, Routes, useLocation } from 'react-router-dom'
import { AnimatePresence, motion as framerMotion } from 'framer-motion'
import Navbar from './components/Navbar'
import ProtectedRoute from './components/ProtectedRoute'
import AddPropertyPage from './pages/AddPropertyPage'
import DashboardPage from './pages/DashboardPage'
import ForgotPasswordPage from './pages/ForgotPasswordPage'
import HomePage from './pages/HomePage'
import ListingsPage from './pages/ListingsPage'
import LoginPage from './pages/LoginPage'
import ResetPasswordPage from './pages/ResetPasswordPage'
import SignupPage from './pages/SignupPage'
import ScrollToTop from './components/ScrollToTop'
import { ToastProvider } from './context/ToastContext'
import './App.css'

const pageVariants = {
  initial: { opacity: 0, y: 18 },
  animate: { opacity: 1, y: 0, transition: { duration: 0.38, ease: 'easeOut' } },
  exit: { opacity: 0, y: -12, transition: { duration: 0.22, ease: 'easeIn' } },
}

function AnimatedPage({ children }) {
  const MotionDiv = framerMotion.div

  return (
    <MotionDiv variants={pageVariants} initial="initial" animate="animate" exit="exit">
      {children}
    </MotionDiv>
  )
}

function App() {
  const location = useLocation()
  return (
    <ToastProvider>
      <Navbar />
      <ScrollToTop />
      <AnimatePresence mode="wait">
        <Routes location={location} key={location.pathname}>
          <Route path="/" element={<AnimatedPage><HomePage /></AnimatedPage>} />
          <Route path="/listings" element={<AnimatedPage><ListingsPage /></AnimatedPage>} />
          <Route
            path="/add-property"
            element={(
              <ProtectedRoute allowedRoles={['BROKER']}>
                <AnimatedPage><AddPropertyPage /></AnimatedPage>
              </ProtectedRoute>
            )}
          />
          <Route path="/login" element={<AnimatedPage><LoginPage /></AnimatedPage>} />
          <Route path="/forgot-password" element={<AnimatedPage><ForgotPasswordPage /></AnimatedPage>} />
          <Route path="/reset-password" element={<AnimatedPage><ResetPasswordPage /></AnimatedPage>} />
          <Route path="/signup" element={<AnimatedPage><SignupPage /></AnimatedPage>} />
          <Route
            path="/dashboard"
            element={(
              <ProtectedRoute>
                <AnimatedPage><DashboardPage /></AnimatedPage>
              </ProtectedRoute>
            )}
          />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AnimatePresence>
    </ToastProvider>
  )
}

export default App
