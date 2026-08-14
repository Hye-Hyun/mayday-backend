# mayday-backend

## Local configuration

Set these environment variables before running the app:

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-local-db-password"
$env:JWT_SECRET = "replace-with-at-least-32-byte-secret"
$env:JWT_ACCESS_TOKEN_EXPIRATION = "3600000"
```

Do not commit real database passwords or JWT secrets.
