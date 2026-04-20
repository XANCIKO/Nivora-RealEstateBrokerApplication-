import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { AnimatePresence, motion } from 'framer-motion'
import ListingCard from '../components/ListingCard'
import { useAuth } from '../context/useAuth'
import { fallbackListings } from '../data/listings'
import { searchProperties } from '../services/propertyApi'
import { normalizeProperty } from '../utils/propertyMapper'

const highlightCards = [
  {
    icon: '△',
    title: 'Build Quality',
    copy: 'Strong execution and practical planning across every project.',
  },
  {
    icon: '◎',
    title: 'Modern Design',
    copy: 'Clean architecture made for current city lifestyles.',
  },
  {
    icon: '◈',
    title: 'Smart Efficiency',
    copy: 'Energy-aware materials and future-ready planning.',
  },
]

const trustMetrics = [
  { value: '100%', label: 'Verified listings' },
  { value: '15+', label: 'Top cities covered' },
  { value: '24/7', label: 'Platform access' },
  { value: 'Fast', label: 'Deal confirmation flow' },
]

const projectTiles = [
  {
    title: 'Corporate Tower',
    image:
      'https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=1200&q=80',
  },
  {
    title: 'Futuristic Pavilion',
    image:
      'https://images.unsplash.com/photo-1512918728675-ed5a9ecdebfd?auto=format&fit=crop&w=1200&q=80',
  },
  {
    title: 'Conceptual Residence',
    image:
      'https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?auto=format&fit=crop&w=1200&q=80',
  },
]

const heroSlides = [
  {
    image:
      'https://images.unsplash.com/photo-1600607687644-aac4c3eac7f4?auto=format&fit=crop&w=2000&q=80',
    title: 'Advancing Architectural\nExcellence Across India',
    copy: 'Premium homes and high-value projects across India\'s fastest growing cities.',
  },
  {
    image:
      'https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=2000&q=80',
    title: 'Timeless Homes.\nFuture-Ready Communities.',
    copy: 'Explore homes built with strong design, smart layouts, and long-term value.',
  },
  {
    image:
      'https://images.unsplash.com/photo-1600573472592-401b489a3cdc?auto=format&fit=crop&w=2000&q=80',
    title: 'From Metro Skylines\nTo Elite Residences',
    copy: 'From city towers to villas, find verified options quickly and confidently.',
  },
]

const rollingCities = [
  'Delhi NCR',
  'Mumbai',
  'Bengaluru',
  'Hyderabad',
  'Chennai',
  'Pune',
  'Kolkata',
  'Ahmedabad',
  'Jaipur',
]

function HomePage() {
  const [featured, setFeatured] = useState(fallbackListings)
  const [activeSlide, setActiveSlide] = useState(0)
  const { isAuthenticated } = useAuth()

  const MotionDiv = motion.div
  const MotionSection = motion.section
  const MotionH1 = motion.h1
  const MotionP = motion.p

  useEffect(() => {
    async function loadFeatured() {
      try {
        const properties = await searchProperties({})
        setFeatured(properties.slice(0, 3).map(normalizeProperty))
      } catch {
        setFeatured(fallbackListings)
      }
    }

    loadFeatured()
  }, [])

  useEffect(() => {
    const timer = setInterval(() => {
      setActiveSlide((prev) => (prev + 1) % heroSlides.length)
    }, 4500)

    return () => clearInterval(timer)
  }, [])

  const currentSlide = heroSlides[activeSlide]

  function onPrevSlide() {
    setActiveSlide((prev) => (prev - 1 + heroSlides.length) % heroSlides.length)
  }

  function onNextSlide() {
    setActiveSlide((prev) => (prev + 1) % heroSlides.length)
  }

  return (
    <div className="page-shell lux-home">
      <section className="lux-hero">
        <AnimatePresence mode="wait">
          <MotionDiv
            key={activeSlide}
            className="lux-hero-media"
            style={{ backgroundImage: `url(${currentSlide.image})` }}
            initial={{ opacity: 0, scale: 1.06 }}
            animate={{ opacity: 1, scale: 1.02 }}
            exit={{ opacity: 0.12 }}
            transition={{ duration: 0.75, ease: 'easeOut' }}
          />
        </AnimatePresence>
        <div className="lux-hero-overlay" />
        <div className="lux-hero-glow" />

        <MotionDiv
          className="lux-hero-content"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
        >
          <p className="lux-brand">NIVORA</p>
          <MotionH1
            key={currentSlide.title}
            initial={{ opacity: 0, y: 18 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1, duration: 0.55 }}
          >
            {currentSlide.title.split('\n')[0]}
            <br />
            {currentSlide.title.split('\n')[1]}
          </MotionH1>
          <MotionP
            key={currentSlide.copy}
            className="lux-subcopy"
            initial={{ opacity: 0, y: 18 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2, duration: 0.55 }}
          >
            {currentSlide.copy}
          </MotionP>
          <MotionDiv
            className="lux-actions"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3, duration: 0.5 }}
          >
            <Link className="cta cta-primary" to="/listings">
              Explore Listings
            </Link>
            {isAuthenticated ? (
              <Link className="cta cta-ghost lux-ghost" to="/dashboard">
                Go to Dashboard
              </Link>
            ) : (
              <Link className="cta cta-ghost lux-ghost" to="/signup">
                Join Nivora
              </Link>
            )}
          </MotionDiv>

          <div className="lux-slide-controls">
            <button type="button" className="slide-btn" onClick={onPrevSlide} aria-label="Previous slide">
              {'<'}
            </button>
            <div className="slide-dots" role="tablist" aria-label="Hero slides">
              {heroSlides.map((slide, index) => (
                <button
                  key={slide.title}
                  type="button"
                  className={`slide-dot ${index === activeSlide ? 'active' : ''}`}
                  onClick={() => setActiveSlide(index)}
                  aria-label={`Go to slide ${index + 1}`}
                />
              ))}
            </div>
            <button type="button" className="slide-btn" onClick={onNextSlide} aria-label="Next slide">
              {'>'}
            </button>
          </div>
        </MotionDiv>
      </section>

      <div className="lux-roll-strip" aria-label="Cities we operate in">
        <div className="lux-roll-track">
          {[...rollingCities, ...rollingCities].map((city, index) => (
            <span key={`${city}-${index}`} className="roll-chip">
              * {city}
            </span>
          ))}
        </div>
      </div>

      <section className="lux-trust-strip" aria-label="Nivora highlights">
        {trustMetrics.map((metric) => (
          <div key={metric.label} className="lux-trust-item">
            <p className="lux-trust-value">{metric.value}</p>
            <p className="lux-trust-label">{metric.label}</p>
          </div>
        ))}
      </section>

      <MotionSection
        className="lux-pillars"
        initial={{ opacity: 0, y: 30 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true }}
        transition={{ duration: 0.55 }}
      >
        {highlightCards.map((card, index) => (
          <MotionDiv
            key={card.title}
            className="lux-pillar"
            initial={{ opacity: 0, y: 30 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ delay: 0.08 * index, duration: 0.5 }}
          >
            <span className="lux-pillar-icon">{card.icon}</span>
            <h3>{card.title}</h3>
            <p>{card.copy}</p>
          </MotionDiv>
        ))}
      </MotionSection>

      <MotionSection
        className="lux-projects"
        initial={{ opacity: 0, y: 28 }}
        whileInView={{ opacity: 1, y: 0 }}
        viewport={{ once: true }}
        transition={{ duration: 0.55 }}
      >
        <div className="section-head section-head-dark">
          <h2>Signature Projects</h2>
          <p>Selected premium concepts from our network.</p>
        </div>

        <div className="lux-project-grid">
          {projectTiles.map((tile, index) => (
            <MotionDiv
              key={tile.title}
              className="lux-project-card"
              initial={{ opacity: 0, y: 26 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ delay: 0.1 * index, duration: 0.45 }}
            >
              <div
                className="lux-project-image"
                style={{ backgroundImage: `url(${tile.image})` }}
              />
              <div className="lux-project-copy">
                <h3>{tile.title}</h3>
                <p>Premium concept for high-value development.</p>
              </div>
            </MotionDiv>
          ))}
        </div>
      </MotionSection>

      <section className="listings-section lux-listing-block">
        <div className="section-head section-head-dark">
          <h2>Featured Listings</h2>
        </div>
        <div className="listing-grid">
          {featured.map((item, index) => (
            <ListingCard key={item.id} item={item} index={index} />
          ))}
        </div>
      </section>
    </div>
  )
}

export default HomePage
