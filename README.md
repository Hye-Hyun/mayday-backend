# mayday-backend

## Local configuration

Do not commit real database passwords, JWT secrets, API keys, or Google service
account JSON files.

Copy `.env.example` for your local values, or set these environment variables
before running the app:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/mayday"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-local-db-password"
$env:JWT_SECRET = "replace-with-at-least-32-byte-secret"
$env:JWT_ACCESS_TOKEN_EXPIRATION = "3600000"
$env:OPENAI_API_KEY = "your-openai-api-key"
$env:GOOGLE_VISION_API_KEY = "your-google-vision-api-key"
$env:GOOGLE_APPLICATION_CREDENTIALS = "C:\path\to\google-credentials.json"
```

`JWT_SECRET` must be long enough for HS256 signing. Use at least 32 bytes.
`OPENAI_API_KEY` is required only for LLM-based expense analysis.
OCR requires either `GOOGLE_VISION_API_KEY` or `GOOGLE_APPLICATION_CREDENTIALS`.

## Temporary Render deployment

Use this only as a frontend integration/test server.

1. Create a Render PostgreSQL database.
2. Create a Render Web Service from this repository's `integrate` branch.
3. Use these commands:

```bash
./gradlew clean bootJar -x test
java -Dserver.port=$PORT -jar build/libs/*.jar
```

4. Set environment variables:

```text
DB_URL=jdbc:postgresql://<host>:<port>/<database>
DB_USERNAME=<render-db-user>
DB_PASSWORD=<render-db-password>
DB_DRIVER=org.postgresql.Driver
JWT_SECRET=<at-least-32-byte-secret>
JWT_ACCESS_TOKEN_EXPIRATION=3600000
OPENAI_API_KEY=<openai-api-key>
CORS_ALLOWED_ORIGINS=<frontend-url-or-*>
```

OCR can be skipped for temporary API testing. To enable OCR on Render, set one
of these credential options:

```text
GOOGLE_VISION_API_KEY=<google-cloud-vision-api-key>
```

or add the Google service account JSON as a Render Secret File and set:

```text
GOOGLE_APPLICATION_CREDENTIALS=/etc/secrets/<secret-file-name>.json
```

If `/expenses/ocr` returns `OCR 외부 서비스 인증 또는 설정을 확인해주세요`,
check that the Cloud Vision API is enabled, billing is active, and the API key
or service account is allowed to call Cloud Vision from the backend.
