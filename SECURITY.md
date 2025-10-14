# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 0.1.x   | :white_check_mark: |

## Reporting a Vulnerability

If you discover a security vulnerability in this project, please report it by emailing the maintainers directly. Please do not open a public issue.

### What to Include

- Description of the vulnerability
- Steps to reproduce the issue
- Potential impact
- Suggested fix (if any)

### Response Time

- We will acknowledge receipt of your vulnerability report within 48 hours
- We will provide a more detailed response within 7 days
- We will work with you to understand and resolve the issue

## Security Best Practices

### For Development

1. **Never commit credentials** - Use environment variables for all sensitive data
2. **Use `.env` files locally** - Copy `.env.example` to `.env` and customize
3. **Keep dependencies updated** - Regularly run security audits
4. **Enable security scanning** - Use tools like Dependabot, Snyk, or GitHub Advanced Security

### For Production

1. **Change default passwords** - Replace all default credentials before deployment
2. **Use strong authentication** - Implement proper authentication and authorization
3. **Enable HTTPS** - Use TLS/SSL for all external communication
4. **Limit database access** - Use principle of least privilege for database users
5. **Enable audit logging** - Track all security-relevant events
6. **Regular backups** - Implement automated backup strategies
7. **Network security** - Use firewalls and security groups to limit access

## Known Security Considerations

### Default Credentials

This repository contains **example credentials** for development purposes only:
- PostgreSQL: `postgres/postgres`
- Database users: `transaction_user`, `alert_user` with password `change_me`
- Admin user: `admin/changeme`

**⚠️ These MUST be changed before deploying to any production or shared environment.**

### Dependency Vulnerabilities

We use automated tools to scan for known vulnerabilities:
- Backend: Gradle dependency scanning
- Frontend: npm audit
- Python: pip-audit or safety

Run security checks:
```bash
# Backend
cd backend && ./gradlew dependencyCheckAnalyze

# Frontend
cd ui/frontend && npm audit

# Python
cd ml/src/inference && pip-audit
```

## Security Improvements Roadmap

- [ ] Implement proper secret management (HashiCorp Vault, AWS Secrets Manager)
- [ ] Add rate limiting and DDoS protection
- [ ] Implement JWT authentication with refresh tokens
- [ ] Add API input validation and sanitization
- [ ] Enable OWASP dependency checking in CI/CD
- [ ] Implement database encryption at rest
- [ ] Add security headers (CSP, HSTS, etc.)
- [ ] Implement audit logging for all sensitive operations
