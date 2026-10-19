# 🧳 Travel Bag Packer — Fullstack Application

> **Smart packing assistant with automated checklists, weather integration, and AI travel guidance.**

### 🔗 Live Production Links
- 🌐 **Live Web Application (Frontend)**: [https://travel-bag-frontend.onrender.com](https://travel-bag-frontend.onrender.com)
- ⚙️ **Live Backend API**: [https://travel-bag-packer.onrender.com](https://travel-bag-packer.onrender.com)
- 🏥 **API Health Check**: [https://travel-bag-packer.onrender.com/api/health](https://travel-bag-packer.onrender.com/api/health)
- 📦 **Frontend GitHub Repository**: [https://github.com/edgesparkfire/travel-bag-packer-frontend](https://github.com/edgesparkfire/travel-bag-packer-frontend)

---

## 🏛️ Architecture Overview
- **Frontend**: React 19 + Vite SPA (Hosted as a **Render Static Site**)
- **Backend**: Spring Boot 4 / Java 21 (Hosted as a **Render Web Service** via Docker container)
- **Database**: PostgreSQL (Managed **Render Database** `travel-bag-packer_Database`)
- **Health Check**: `/api/health`

---

## 🌐 API Endpoints

Base URL: `https://travel-bag-packer.onrender.com`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/health` | Service uptime and health check |
| `GET` | `/api/trips` | Retrieve all trips |
| `GET` | `/api/trips/{id}` | Get specific trip details |
| `POST` | `/api/trips` | Create trip with auto-generated packing list & AI welcome |
| `PATCH` | `/api/trips/{id}/tier` | Update budget tier (`BUDGET`, `BALANCED`, `LUXURY`) |
| `PATCH` | `/api/trips/{id}/items/{itemId}` | Toggle packed item status |
| `PATCH` | `/api/trips/{id}/complete` | Toggle trip completion status |
| `POST` | `/api/trips/{id}/chat` | Ask Gemini Travel Advisor contextual question |
| `DELETE` | `/api/trips/{id}` | Delete a trip |
| `GET` | `/api/city-guide?city={name}` | Get curated cultural & city guide |
| `POST` | `/api/ai/tips` | Standalone AI travel recommendations |

---

## 💻 Local Development

### Backend
```bash
cd backend/bag
./mvnw spring-boot:run
```
*(If no PostgreSQL DATABASE_URL is set, it automatically falls back to an in-memory H2 database).*

### Frontend
```bash
cd frontend
npm install
npm run dev
```
