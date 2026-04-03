# ── Stage 1: Build Spring Boot JAR ──────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn clean package -DskipTests -q


# ── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-jammy

# ── System deps: Node.js 20, Chrome deps for Puppeteer ───────────────────────
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    ca-certificates \
    gnupg \
    libnss3 \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    libcups2 \
    libdrm2 \
    libxkbcommon0 \
    libxcomposite1 \
    libxdamage1 \
    libxrandr2 \
    libgbm1 \
    libasound2 \
    libpangocairo-1.0-0 \
    libpango-1.0-0 \
    libgtk-3-0 \
    libx11-xcb1 \
    libxcb-dri3-0 \
    fonts-liberation \
    xdg-utils \
    && curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get install -y nodejs \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# ── Copy Spring Boot JAR ──────────────────────────────────────────────────────
COPY --from=build /app/target/*.jar app.jar

# ── Copy Node.js automation service ──────────────────────────────────────────
COPY puppeteer-automation/ ./puppeteer-automation/

# Install Node deps and download Puppeteer's bundled Chromium
WORKDIR /app/puppeteer-automation
RUN npm ci --omit=dev

ENV PUPPETEER_CACHE_DIR=/app/puppeteer-automation/.cache/puppeteer
RUN npx puppeteer browsers install chrome

WORKDIR /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]