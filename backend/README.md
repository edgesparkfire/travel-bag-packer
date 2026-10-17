# Travel Bag Packer Backend

Production URL: **[https://travel-bag-packer.onrender.com](https://travel-bag-packer.onrender.com)**

Live Health Check: **[https://travel-bag-packer.onrender.com/api/health](https://travel-bag-packer.onrender.com/api/health)**

---

## 🚀 Overview

Spring Boot backend for the Travel Bag Packer application, connected to PostgreSQL and deployed on [Render](https://render.com).

- **Framework**: Spring Boot 4 / Java 21
- **Database**: PostgreSQL (via Spring Data JPA & Hibernate)
- **Deployment Platform**: Render
- **Containerization**: Multi-stage Docker build

---

## 🌐 API Endpoints

Base URL: `https://travel-bag-packer.onrender.com`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/health` | Health check endpoint for Render uptime monitoring |
| `GET` | `/api/trips` | Retrieve all trips (sorted newest first) |
| `GET` | `/api/trips/{id}` | Get trip details by ID |
| `POST` | `/api/trips` | Create a new trip with automated packing list & Gemini welcome note |
| `PATCH` | `/api/trips/{id}/tier` | Change budget tier (`BUDGET`, `BALANCED`, `LUXURY`) and regenerate items |
| `PATCH` | `/api/trips/{id}/items/{itemId}` | Toggle packed status of an item |
| `PATCH` | `/api/trips/{id}/complete` | Toggle trip completion status |
| `POST` | `/api/trips/{id}/chat` | Ask Gemini Travel Advisor a question within the trip context |
| `DELETE` | `/api/trips/{id}` | Delete a trip |
| `GET` | `/api/city-guide?city={name}` | Get curated city travel & cultural guide |
| `POST` | `/api/ai/tips` | Get standalone travel recommendations |

---

## 🗄️ PostgreSQL Setup on Render

### Option A: Automatic via Render Blueprint (`render.yaml`)
1. In Render, select **New +** > **Blueprint**.
2. Connect your GitHub repository (`travel-bag-packer`).
3. Render automatically provisions the PostgreSQL database (`travel-bag-db`) and the Web Service (`travel-bag-packer`), injecting `DATABASE_URL`.

### Option B: Manual Setup
1. In the Render Dashboard, create a **PostgreSQL** database:
   - Name: `travel-bag-db`
   - Database: `travel_bag_db`
   - User: `bag_user`
2. Create a **Web Service**:
   - Environment: **Docker**
   - Health Check Path: `/api/health`
3. Under **Environment Variables**, add:
   - `DATABASE_URL`: Paste the PostgreSQL **Internal Database URL** from your Render database.
   - `PORT`: `8080`

The backend includes dynamic parsing in `DatabaseConfig.java` that automatically converts Render's `postgres://` or `postgresql://` connection strings into standard JDBC PostgreSQL format.

---

## 💻 Local Development

Run the backend locally:
```bash
cd bag
./mvnw clean spring-boot:run
```
If no `DATABASE_URL` is set in the environment, the application automatically falls back to an in-memory H2 database in PostgreSQL mode, allowing local testing without needing PostgreSQL installed.
