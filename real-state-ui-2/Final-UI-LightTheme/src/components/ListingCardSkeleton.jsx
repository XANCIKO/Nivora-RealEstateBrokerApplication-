function ListingCardSkeleton() {
  return (
    <div className="listing-card skeleton-card" aria-hidden="true">
      <div className="skeleton skeleton-chip" />
      <div className="skeleton skeleton-title" />
      <div className="skeleton skeleton-price" />
      <div className="skeleton-meta-row">
        <div className="skeleton skeleton-meta-item" />
        <div className="skeleton skeleton-meta-item" />
        <div className="skeleton skeleton-meta-item" />
      </div>
      <div className="skeleton skeleton-address" />
    </div>
  )
}

export default ListingCardSkeleton
