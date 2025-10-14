# Contributing to AI Fraud Detection Platform

Thank you for your interest in contributing! This document provides guidelines and instructions for contributing to this project.

## Code of Conduct

- Be respectful and inclusive
- Welcome newcomers and help them learn
- Focus on constructive feedback
- Assume good intentions

## How Can I Contribute?

### Reporting Bugs

Before submitting a bug report:
- Check if the issue has already been reported
- Verify the bug exists in the latest version
- Collect relevant information (logs, screenshots, steps to reproduce)

Bug reports should include:
- Clear, descriptive title
- Detailed description of the issue
- Steps to reproduce
- Expected vs actual behavior
- Environment details (OS, Java version, etc.)

### Suggesting Enhancements

Enhancement suggestions are welcome! Please:
- Use a clear, descriptive title
- Explain the current behavior and why it's inadequate
- Describe the proposed enhancement and its benefits
- Provide examples if applicable

### Pull Requests

1. **Fork the repository** and create your branch from `main`
2. **Make your changes** following the code style guidelines
3. **Add tests** for any new functionality
4. **Update documentation** if needed
5. **Ensure all tests pass** locally
6. **Commit your changes** with clear, descriptive messages
7. **Submit a pull request** with a clear description

## Development Setup

### Prerequisites

- JDK 21
- Node.js 20+
- Python 3.11+
- Docker Desktop

### Backend Setup

```bash
cd backend
./gradlew build
./gradlew test
```

### Frontend Setup

```bash
cd ui/frontend
npm install
npm run lint
npm run build
```

### ML/Inference Setup

```bash
cd ml/src/inference
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
pip install -r requirements.txt
pytest
```

## Code Style Guidelines

### Java/Kotlin

- Follow standard Kotlin conventions
- Use meaningful variable and function names
- Keep functions small and focused
- Add comments for complex logic
- Use dependency injection via Spring
- Write unit tests for all business logic

### TypeScript/React

- Use functional components with hooks
- Follow React best practices
- Use TypeScript for type safety
- Keep components small and reusable
- Use meaningful component and variable names

### Python

- Follow PEP 8 style guide
- Use type hints where appropriate
- Write docstrings for functions and classes
- Keep functions focused and testable

## Testing Guidelines

- Write tests for new features
- Maintain or improve test coverage
- Test edge cases and error conditions
- Use descriptive test names
- Mock external dependencies

### Running Tests

```bash
# Backend
cd backend
./gradlew test

# Frontend
cd ui/frontend
npm test

# Python
cd ml/src/inference
pytest
```

## Commit Message Guidelines

Use clear, descriptive commit messages:

```
feat: Add transaction validation middleware
fix: Resolve kafka connection timeout
docs: Update README with deployment instructions
refactor: Extract feature engineering to common module
test: Add integration tests for fraud service
chore: Update dependencies to latest versions
```

Prefixes:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks
- `perf`: Performance improvements

## Pull Request Process

1. Update README.md if needed
2. Update documentation for API changes
3. Ensure all tests pass
4. Request review from maintainers
5. Address review feedback
6. Squash commits if requested
7. Maintainers will merge when approved

## Project Structure

```
backend/services/          # Spring Boot microservices
ml/src/                   # Python ML code
ui/frontend/              # React frontend
infrastructure/           # Docker, K8s, Terraform
docs/                     # Documentation
```

## Questions?

Feel free to open an issue for questions or clarifications.

Thank you for contributing! 🎉
