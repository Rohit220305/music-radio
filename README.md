# Music Radio

A retro-inspired online music radio platform where users can explore curated music themes, discover stations, and enjoy music through an interactive radio experience.

## Project Overview

Music Radio provides a simple and engaging music listening experience with curated stations and playlists.

The platform consists of two independent applications:

- **Radio Frontend:** User-facing music streaming interface.
- **Radio Backend:** REST API and application management system.

## Technology Stack

### Frontend
- Next.js
- React
- TypeScript
- Tailwind CSS

### Backend
- Java 21
- Spring Boot 3
- Spring Data JPA
- Spring Security
- Maven

### Database
- PostgreSQL
- Flyway

### Music Playback
- YouTube IFrame Player API

## Project Structure

```text
music-radio/
├── radio-frontend/
├── radio-backend/
├── README.md
└── .gitignore
```

## Local Development

### Frontend

```bash
cd radio-frontend
npm install
npm run dev
```

Frontend: http://localhost:3000

### Backend

```bash
cd radio-backend
./mvnw spring-boot:run
```

Backend: http://localhost:8080

### Database

PostgreSQL database: `music_radio`

Configure database credentials using environment variables.

## Development Progress

- [x] Repository initialization
- [ ] Frontend setup
- [ ] Backend setup
- [ ] PostgreSQL integration
- [ ] Music playback
- [ ] Admin panel
- [ ] Deployment

## License

To be decided.