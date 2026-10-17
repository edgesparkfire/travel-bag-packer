# Travel Bag Packer (Fullstack Application)

Production-ready repository configured for deployment on **Render** (PostgreSQL + Docker Spring Boot Web Service + Vite Static Site).

---

## 🏛️ Architecture Overview

- **Frontend**: React 19 + Vite SPA (Hosted as a **Render Static Site**)
- **Backend**: Spring Boot 4 / Java 21 (Hosted as a **Render Web Service** via Docker)
- **Database**: PostgreSQL (Managed **Render Database** `travel-bag-db`)
- **Health Check**: `/api/health`

---

## 🚀 Step-by-Step Render Deployment Guide

### Step 1: Deploy Backend & Database

#### Option A: 1-Click via Render Blueprint (Recommended)
1. In the [Render Dashboard](https://dashboard.render.com), click **New +** > **Blueprint**.
2. Connect the backend repository: `https://github.com/edgesparkfire/travel-bag-packer`.
3. Render reads `render.yaml` and will automatically configure:
   - **Database**: `travel-bag-db` (PostgreSQL)
   - **Web Service**: `travel-bag-backend` (Docker multi-stage build, Java 21)
   - **Environment Variables**: `PORT=8080`, `DATABASE_URL` linked from the database, `FRONTEND_URL`.
4. Click **Apply**.
5. Once deployed, copy your backend URL: e.g. `https://travel-bag-backend.onrender.com`.

#### Option B: Manual Setup on Render
1. **Create PostgreSQL Database**:
   - Click **New +** > **PostgreSQL**.
   - Name: `travel-bag-db`
   - Database: `travel_bag_db`
   - User: `bag_user`
   - Plan: **Free**
   - Click **Create Database**.
   - Copy the **Internal Database URL**.
2. **Create Web Service**:
   - Click **New +** > **Web Service**.
   - Connect repository `travel-bag-packer`.
   - Runtime: **Docker** (Root directory: `.`, Dockerfile path: `./Dockerfile`).
   - Health Check Path: `/api/health`
   - Environment Variables:
     - `DATABASE_URL`: *(Paste the Internal Database URL from step 1)*
     - `PORT`: `8080`
     - `FRONTEND_URL`: `https://<your-frontend-subdomain>.onrender.com`
   - Click **Create Web Service**.

---

### Step 2: Deploy Frontend

1. In the [Render Dashboard](https://dashboard.render.com), click **New +** > **Static Site**.
2. Connect your frontend repository: `https://github.com/edgesparkfire/travel-bag-packer-frontend`.
3. Fill in the settings:
   - **Name**: `travel-bag-frontend`
   - **Branch**: `main`
   - **Build Command**: `npm install && npm run build`
   - **Publish Directory**: `dist`
4. Under **Environment Variables**:
   - `VITE_API_BASE_URL`: `https://travel-bag-backend.onrender.com` *(use your actual backend URL from Step 1)*
5. Click **Create Static Site**.
*(SPA routing rewrites are automatically handled by `public/_redirects` which is bundled into `dist/_redirects`).*

---

## 💻 Local Development

### Run Backend
```bash
cd backend/bag
./mvnw spring-boot:run
```
*(If no PostgreSQL DATABASE_URL is set, it automatically runs an in-memory H2 database).*

### Run Frontend
```bash
cd frontend
npm install
npm run dev
```
