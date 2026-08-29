# Himcharm Stores — Admin Frontend

React administration UI for the Himcharm Stores Spring Boot API.

## Run locally

```bash
npm install
npm run dev
```

The frontend uses `http://localhost:8080` by default. Override it when needed:

```bash
VITE_API_BASE_URL=https://api.example.com npm run dev
```

## Structure

- `src/api` — Axios client and backend services
- `src/auth` — JWT authentication state
- `src/components` — shared UI and dashboard layout
- `src/pages` — feature pages for users, stores, and invoices
