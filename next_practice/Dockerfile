# Base stage: common Node.js runtime configuration shared by all stages
FROM node:20-bookworm-slim AS base

WORKDIR /app

ENV CI=true \
    NEXT_TELEMETRY_DISABLED=1

# Dependencies stage: install node_modules with exact versions for reproducible builds
FROM base AS deps

COPY package*.json ./
RUN npm ci

# Builder stage: produce optimized Next.js artifacts
FROM base AS builder

COPY --from=deps /app/node_modules ./node_modules
COPY . .
RUN npm run build

# Runner stage: lightweight image containing only production runtime assets
FROM base AS runner

ENV NODE_ENV=production \
    PORT=3000

COPY package*.json ./
RUN npm ci --omit=dev

COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public

EXPOSE 3000

CMD ["npm", "run", "start"]
