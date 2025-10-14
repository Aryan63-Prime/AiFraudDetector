rootProject.name = "ai-fraud-detection"

include(
    "common",
    "services:gateway-service",
    "services:transaction-service",
    "services:fraud-service",
    "services:alert-service",
    "services:admin-service"
)
