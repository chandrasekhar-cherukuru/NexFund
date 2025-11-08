import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  base: '/', // or keep '/' unless you want subpath
  server: {
    port: 3000,
    open: true
  }
})
