@app.get("/health")
async def health_check():
    """Health check endpoint for container orchestration"""
    return {
        "status": "healthy",
        "service": "fraud-detection-inference",
        "version": "0.1.0"
    }
