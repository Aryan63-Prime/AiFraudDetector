# AI Fraud Admin UI

A Vite + React dashboard that lets fraud analysts sign in, review alerts, and update alert statuses through the `admin-service`.

## Quick start

```powershell
# install dependencies
npm install

# start dev server on http://localhost:5173
npm run dev

# run production build
npm run build
```

Set the API base URL with `VITE_API_BASE_URL`. During local development the Vite dev server proxies `/api` to `http://localhost:8080` (gateway-service) by default.

## What’s included

- Session provider with local-storage persistence and mocked login
- Alerts table backed by `@tanstack/react-query`
- Tailwind CSS for styling
- Proxy setup for gateway-service integration

## Next steps

- Replace the mocked authentication with the real identity provider
- Add alert detail drawer with transaction timeline
- Package UI into CI workflow and deploy to static hosting
