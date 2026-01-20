# POS App (Electron + React)

## Setup
```bash
npm install
cp .env.example .env
# Set VITE_API_BASE_URL to backend LAN URL
npm run dev
```

## Build
```bash
npm run build
```

## Notes
- Barcode scanner is treated as keyboard input; scan into the Barcode field.
- Printing uses browser print with 80mm thermal layout.
- ESC/POS raw printing stub exists in Electron main process.
