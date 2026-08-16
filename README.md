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
$env:GOOGLE_APPLICATION_CREDENTIALS = "C:\path\to\google-credentials.json"
```

`JWT_SECRET` must be long enough for HS256 signing. Use at least 32 bytes.
`OPENAI_API_KEY` is required only for LLM-based expense analysis.
`GOOGLE_APPLICATION_CREDENTIALS` is required only for OCR features that call
Google Cloud Vision.
