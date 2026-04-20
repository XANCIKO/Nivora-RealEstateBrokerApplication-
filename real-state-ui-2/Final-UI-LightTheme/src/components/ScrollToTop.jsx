import { useEffect, useState } from 'react'
import { motion as framerMotion, AnimatePresence as FramerAnimatePresence } from 'framer-motion'

function ScrollToTop() {
  const [visible, setVisible] = useState(false)
  const MotionButton = framerMotion.button

  useEffect(() => {
    function onScroll() {
      setVisible(window.scrollY > 320)
    }
    window.addEventListener('scroll', onScroll, { passive: true })
    return () => window.removeEventListener('scroll', onScroll)
  }, [])

  function scrollUp() {
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  return (
    <FramerAnimatePresence>
      {visible && (
        <MotionButton
          className="scroll-to-top"
          onClick={scrollUp}
          aria-label="Scroll to top"
          initial={{ opacity: 0, scale: 0.7, y: 20 }}
          animate={{ opacity: 1, scale: 1, y: 0 }}
          exit={{ opacity: 0, scale: 0.7, y: 20 }}
          transition={{ duration: 0.25 }}
          whileHover={{ scale: 1.1 }}
          whileTap={{ scale: 0.95 }}
        >
          ↑
        </MotionButton>
      )}
    </FramerAnimatePresence>
  )
}

export default ScrollToTop
