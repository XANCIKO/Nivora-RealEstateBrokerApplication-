# Nivora Realty Frontend

Main React + Vite frontend for the capstone project.

## Setup

Create `.env` from `.env.example`.

```properties
VITE_API_BASE_URL=http://localhost:8080
VITE_STRIPE_PUBLISHABLE_KEY=
```

Install dependencies and start the dev server:

```bash
npm install
npm run dev
```

## Notes

- This frontend expects the Spring Boot backend to be running on port `8080` by default.
- Property image URLs are resolved against `VITE_API_BASE_URL`.
- If the backend demo seeder is enabled, the UI will show demo listings and demo property images immediately after startup.
- Do not commit `.env` with real API keys; commit only `.env.example`.

## Payment Setup (Optional)

- Set `VITE_STRIPE_PUBLISHABLE_KEY=pk_test_...` in local `.env`.
- Backend must set `STRIPE_SECRET_KEY=sk_test_...` in local secrets/env.
- If Stripe keys are missing, purchase confirmation remains blocked but non-payment features still work.

## Demo Accounts

- Broker: `broker.demo@nivora.local` / `Demo@123`
- Customer: `customer.demo@nivora.local` / `Demo@123`

## Scripts

- `npm run dev`
- `npm run build`
- `npm run preview`
- `npm run lint`
