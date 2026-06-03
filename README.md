# 📚 BookVerse — Backend

> *Where every story finds its shelf.*

BookVerse is a full-stack web application for book lovers. Discover new books, build your personal library, track your
reading progress, and share reviews with other readers.

This repository contains the **backend** of the application, built with Spring Boot.

🔗 **Frontend repository:** [bookverse-frontend](https://github.com/GiorgiaFormicola/bookverse-frontend)

---

## ✨ Features

- 🔐 **JWT Authentication** — Secure stateless authentication with role-based access control
- 📚 **Book Management** — Search via Google Books API with automatic fallback to local database
- 📖 **Personal Library** — Save books, track reading status and visibility
- ⭐ **Reviews** — Create, update and delete book reviews
- 👤 **User Management** — Profile updates, password reset via email
- 🛡️ **Admin Panel** — Manage users and books with full CRUD operations
- 📧 **Email Notifications** — Registration, password reset and account reactivation via Mailgun
- 🖼️ **Image Upload** — Profile pictures and book covers via Cloudinary

---

## 🛠️ Tech Stack

| Technology       | Description                    |
|------------------|--------------------------------|
| Java             | Programming language           |
| Spring Boot      | Application framework          |
| Spring Security  | Authentication & authorization |
| Spring Data JPA  | Database access                |
| PostgreSQL       | Relational database            |
| Cloudinary       | Image upload & storage         |
| Mailgun          | Email delivery                 |
| Google Books API | Book search                    |
| JWT              | Token-based authentication     |

---

## 🚀 Getting Started

### Prerequisites

- [Java](https://adoptium.net/)
- [Maven](https://maven.apache.org/)
- [PostgreSQL](https://www.postgresql.org/)
- A [Cloudinary](https://cloudinary.com/) account
- A [Mailgun](https://www.mailgun.com/) account (sandbox mode supported)
- A [Google Books API](https://developers.google.com/books) key

### Installation

1. **Clone the repository**

```bash
git clone https://github.com/GiorgiaFormicola/bookverse-backend.git
cd bookverse-backend
```

2. **Create a PostgreSQL database**

```sql
CREATE
DATABASE your_database_name;
```

3. **Create an `env.properties` file** in the root of the project

```properties
## SERVER ##
SERVER_PORT=<your_server_port>
SERVER_ADDRESS=<your_server_address>
## DATABASE ##
DB_PORT=<your_db_port>
DB_NAME=<your_db_name>
DB_USERNAME=<your_db_username>
DB_PASSWORD=<your_db_password>
## JWT ##
JWT_SECRET=<your_jwt_secret>
## ORIGINS ##
LOCALHOST_PORT=<your_frontend_port>
## CLOUDINARY ##
CLOUDINARY_NAME=<your_cloudinary_name>
CLOUDINARY_API_KEY=<your_cloudinary_api_key>
CLOUDINARY_API_SECRET=<your_cloudinary_api_secret>
## SERVLET ##
MAX_FILE_SIZE=<max_file_size>
## GOOGLE API ##
GOOGLE_API_KEY=<your_google_api_key>
## MAILGUN ##
MAILGUN_DOMAIN_NAME=<your_mailgun_domain>
MAILGUN_API_KEY=<your_mailgun_api_key>
MAILGUN_ADMIN_EMAIL=<your_admin_email>
```

4. **Run the application**

```bash
mvn spring-boot:run
```

The server will start at `http://localhost:<SERVER_PORT>`

> ⚠️ **Mailgun sandbox mode:** In sandbox mode, emails can only be sent to verified recipients. To test email features (
> registration, password reset, account reactivation), you must first register with an email address that has been
> verified in your Mailgun dashboard under **Sending → Sandbox domain → Authorized Recipients**. Emails sent to unverified
> addresses will be silently ignored.
>
> 📬 **Admin email:** The `MAILGUN_ADMIN_EMAIL` variable can be set to any working email address — it is used to receive
> account reactivation requests sent by disabled users. Make sure it is also verified in Mailgun sandbox if you want to
> test this feature.

---

## 📁 Project Structure

```
src/main/java/giorgiaformicola/capstone/
├── clients/          # External API clients (Google Books)
├── configurations/   # Spring configurations
├── controllers/      # REST controllers
├── entities/         # JPA entities
├── enums/            # Enumerations
├── exceptions/       # Custom exceptions and error handler
├── payloads/         # DTOs
├── repositories/     # JPA repositories
├── security/         # JWT and security configuration
├── services/         # Business logic
├── specifications/   # JPA specifications for filtering
└── tools/            # Utilities (email sender, book mapper)
```

---

## 🔐 Environment Variables

| Variable                | Description                   |
|-------------------------|-------------------------------|
| `SERVER_PORT`           | Port the server runs on       |
| `SERVER_ADDRESS`        | Server address                |
| `DB_PORT`               | PostgreSQL port               |
| `DB_NAME`               | Database name                 |
| `DB_USERNAME`           | Database username             |
| `DB_PASSWORD`           | Database password             |
| `JWT_SECRET`            | Secret key for JWT signing    |
| `LOCALHOST_PORT`        | Frontend port (for CORS)      |
| `CLOUDINARY_NAME`       | Cloudinary cloud name         |
| `CLOUDINARY_API_KEY`    | Cloudinary API key            |
| `CLOUDINARY_API_SECRET` | Cloudinary API secret         |
| `MAX_FILE_SIZE`         | Maximum upload file size      |
| `GOOGLE_API_KEY`        | Google Books API key          |
| `MAILGUN_DOMAIN_NAME`   | Mailgun domain name           |
| `MAILGUN_API_KEY`       | Mailgun API key               |
| `MAILGUN_ADMIN_EMAIL`   | Admin email for notifications |

---

## 👩‍💻 Author

**Giorgia Formicola**

- GitHub: [@GiorgiaFormicola](https://github.com/GiorgiaFormicola)
