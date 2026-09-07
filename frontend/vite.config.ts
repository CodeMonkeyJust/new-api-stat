import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  return {
    base: env.VITE_BASE_PATH || '/new-api-stat/',
    plugins: [vue()],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      }
    },
    build: {
      outDir: 'new-api-stat',
      emptyOutDir: true
    },
    server: {
      port: Number(env.VITE_DEV_PORT || 3001),
      proxy: {
        '/new-api-stat-api': {
          target: env.VITE_API_PROXY_TARGET || 'http://localhost:8082',
          changeOrigin: true
        }
      }
    }
  }
})
