# Real Estate Capstone Project

Full-stack real estate platform with a Spring Boot backend and a React + Vite frontend.

## Stack

- Java 17
- Spring Boot 3.2
- Spring Security + JWT
- Spring Data JPA + Hibernate
- MySQL
- React + Vite

## Project Layout

- `src/main/java/com/capstone/realestate`: backend API
- `src/main/resources`: backend configuration
- `uploads/property-images`: local property image storage and committed demo SVGs
- `real-state-ui-2/Final-UI-LightTheme`: main frontend

## Quick Start

### 1. Backend

Create a local secrets file by copying `src/main/resources/application-secrets.example.properties` to `src/main/resources/application-secrets.properties`.

At minimum, configure your MySQL username and password either in environment variables or by editing `application.properties` defaults.

Recommended environment variables:

```properties
DB_URL=jdbc:mysql://localhost:3306/realestate_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=root
JWT_SECRET=replace-this-in-production
APP_FRONTEND_URL=http://localhost:5173
APP_DEMO_SEED_ENABLED=true
```

Run the backend:

```bash
mvn spring-boot:run
```

Run the backend against the separate demo database (single switch method):

```bash
DB_URL=jdbc:mysql://localhost:3306/realestate_demo?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true mvn spring-boot:run
```

PowerShell equivalent:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/realestate_demo?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true"
mvn spring-boot:run
```

If you prefer an IDE, set the environment variable `DB_URL` to `realestate_demo` for that run configuration.

### 2. Frontend

In `real-state-ui-2/Final-UI-LightTheme`, copy `.env.example` to `.env`.

Default frontend env:

```properties
VITE_API_BASE_URL=http://localhost:8080
```

Run the frontend:

```bash
npm install
npm run dev
```

## Demo Data

When `APP_DEMO_SEED_ENABLED=true`, the backend creates:

- 1 demo broker account
- 1 demo customer account
- 6 demo properties
- local demo SVG property images committed in the repo

Use `DB_URL` to switch the backend between your main and demo databases. No extra Spring profile is required.

Demo credentials:

- Broker: `broker.demo@nivora.local` / `Demo@123`
- Customer: `customer.demo@nivora.local` / `Demo@123`

## Image Handling

- New uploads are saved locally on the machine running the backend.
- Demo listings use committed SVGs under `uploads/property-images` so the UI is not empty after cloning.
- Random runtime uploads are gitignored to keep the repository clean.

## Reviewer Notes

- The project is reviewable without your personal local data.
- Password reset email requires mail credentials in `application-secrets.properties`.
- Stripe payment requires `STRIPE_SECRET_KEY`. Without it, the rest of the app still works.

## GitHub Ready Checklist

### Keep and Push

- Backend source: `src/main/java/**`
- Backend config templates: `src/main/resources/application.properties`, `src/main/resources/application-secrets.example.properties`
- Frontend source: `real-state-ui-2/Final-UI-LightTheme/src/**`
- Frontend env template: `real-state-ui-2/Final-UI-LightTheme/.env.example`
- Demo SVG images only: `uploads/property-images/demo-*.svg`
- Documentation: `README.md`, `real-state-ui-2/Final-UI-LightTheme/README.md`
- Build files: `pom.xml`, `real-state-ui-2/Final-UI-LightTheme/package.json`, lock files

### Do Not Push

- `src/main/resources/application-secrets.properties`
- `real-state-ui-2/Final-UI-LightTheme/.env`
- Any file containing real keys (`sk_test`, `pk_test`, mail passwords)
- Local runtime uploads (non-demo files in `uploads/property-images`)
- `target/`, `dist/`, `node_modules/`, IDE metadata

## Stripe Setup (Optional for Evaluators)

For full payment flow testing:

- Backend local secret: `STRIPE_SECRET_KEY=sk_test_...`
- Frontend local env: `VITE_STRIPE_PUBLISHABLE_KEY=pk_test_...`

If these are not set, the rest of the app works and payment actions will show Stripe configuration guidance.

## Final Push Steps

1. Verify local secret files are ignored and not staged.
2. Verify only demo SVG images are staged from `uploads/property-images`.
3. Run backend and frontend once with demo DB (`DB_URL=...realestate_demo...`).
4. Commit and push.

## Core Features

- JWT login and signup for broker and customer roles
- property listing, search, and detail modal
- broker property management with image upload
- customer booking and payment initiation flow
- OTP-based forgot password flow

## API Highlights

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/forgot-password`
- `POST /api/auth/verify-reset-otp`
- `POST /api/auth/reset-password`
- `GET /api/properties`
- `GET /api/properties/search`
- `GET /api/properties/{id}`
- `GET /api/properties/{id}/broker-contact`

## Tests

```bash
mvn test
```
