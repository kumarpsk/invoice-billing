# Supermarket POS Monorepo

This monorepo contains:
- **backend**: Spring Boot 3 (Java 17) API with PostgreSQL, Flyway, JWT auth.
- **pos-app**: Electron + React + Vite + TypeScript POS client.

## Local network setup (single store)
- Run the backend on a local server PC (e.g. `http://192.168.1.10:8080`).
- Configure each billing counter machine to point the POS app to that IP.

## Backend quick start
```bash
cd backend
# Update database credentials if needed
mvn spring-boot:run
```

## POS app quick start
```bash
cd pos-app
cp .env.example .env
# Update VITE_API_BASE_URL to your server IP
npm install
npm run dev
```

## Billing flow
1. Login on the POS app with the cashier account.
2. Select terminal: `COUNTER-1` or `COUNTER-2`.
3. Open a shift with opening cash.
4. Start scanning products and complete sales.
5. Close the shift at end of day.
