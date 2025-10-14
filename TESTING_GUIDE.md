# Complete System Testing Guide

This guide will help you test the entire fraud detection system end-to-end.

## Prerequisites Checklist

Before starting, ensure you have:
- [x] Docker Desktop running
- [x] JDK 21 installed
- [x] Node.js 20+ installed
- [x] Git configured and working
- [x] All code committed to Git

## Step 1: Start Infrastructure (5 minutes)

### 1.1 Start Docker Services

Open a **new PowerShell terminal** and run:

```powershell
cd C:\Users\patel\AiFraudDetection\infrastructure\docker
docker compose up --build -d
```

### 1.2 Verify Infrastructure is Running

```powershell
docker compose ps
```

You should see:
- ✅ `fraud-platform-postgres` - **healthy**
- ✅ `fraud-platform-kafka` - **healthy**
- ✅ `fraud-platform-zookeeper` - **healthy**
- ✅ `fraud-platform-inference` - **healthy**
- ✅ `fraud-platform-kafka-ui` - **Up**

### 1.3 Test Infrastructure Endpoints

```powershell
# Test inference API
Invoke-WebRequest http://localhost:9090/docs

# Check Kafka UI
Start-Process "http://localhost:8088"
```

**Expected Results:**
- Inference API docs should load (FastAPI Swagger UI)
- Kafka UI should show topics list

---

## Step 2: Start Backend Services (10 minutes)

Open **5 separate PowerShell terminals** (one for each service).

### Terminal 1: Transaction Service (Port 8081)

```powershell
cd C:\Users\patel\AiFraudDetection\backend
.\gradlew.bat :services:transaction-service:bootRun
```

**Wait for:** `Started TransactionServiceApplication`

### Terminal 2: Fraud Service (Port 8082)

```powershell
cd C:\Users\patel\AiFraudDetection\backend
$env:FRAUD_SCORING_HTTP_ENABLED = "true"
.\gradlew.bat :services:fraud-service:bootRun
```

**Wait for:** `Started FraudServiceApplication`

### Terminal 3: Alert Service (Port 8083)

```powershell
cd C:\Users\patel\AiFraudDetection\backend
.\gradlew.bat :services:alert-service:bootRun
```

**Wait for:** `Started AlertServiceApplication`

### Terminal 4: Admin Service (Port 8084)

```powershell
cd C:\Users\patel\AiFraudDetection\backend
.\gradlew.bat :services:admin-service:bootRun
```

**Wait for:** `Started AdminServiceApplication`

### Terminal 5: Gateway Service (Port 8080)

```powershell
cd C:\Users\patel\AiFraudDetection\backend
.\gradlew.bat :services:gateway-service:bootRun
```

**Wait for:** `Started GatewayServiceApplication`

### Verify All Services are Running

In a new PowerShell window:

```powershell
# Check all health endpoints
Invoke-WebRequest http://localhost:8081/actuator/health | Select-Object -ExpandProperty Content
Invoke-WebRequest http://localhost:8082/actuator/health | Select-Object -ExpandProperty Content
Invoke-WebRequest http://localhost:8083/actuator/health | Select-Object -ExpandProperty Content
Invoke-WebRequest http://localhost:8084/actuator/health | Select-Object -ExpandProperty Content
Invoke-WebRequest http://localhost:8080/actuator/health | Select-Object -ExpandProperty Content
```

**All should return:** `{"status":"UP"}`

---

## Step 3: Start Frontend (2 minutes)

Open **one more PowerShell terminal**:

```powershell
cd C:\Users\patel\AiFraudDetection\ui\frontend
npm run dev
```

**Wait for:** `Local: http://localhost:5173/`

Open browser to: **http://localhost:5173**

---

## Step 4: End-to-End Testing (15 minutes)

### Test 1: Submit a Low-Risk Transaction

```powershell
# Low amount, has device ID
$lowRiskTx = @{
    transactionId = "test-tx-001"
    accountId = "acct-test-001"
    amount = 50.00
    currency = "USD"
    merchantCategory = "groceries"
    channel = "POS"
    deviceId = "device-12345"
    metadata = @{
        ipAddress = "192.168.1.100"
        location = "New York, USA"
    }
} | ConvertTo-Json -Depth 5

Invoke-RestMethod -Uri 'http://localhost:8081/api/v1/transactions' `
    -Method Post -Body $lowRiskTx -ContentType 'application/json'
```

**Expected:**
- Status: `ACCEPTED`
- Message: "Transaction queued for fraud analysis"

### Test 2: Submit a High-Risk Transaction

```powershell
# High amount, no device ID
$highRiskTx = @{
    transactionId = "test-tx-002"
    accountId = "acct-test-002"
    amount = 9500.00
    currency = "USD"
    merchantCategory = "electronics"
    channel = "ONLINE"
    deviceId = $null
    metadata = @{
        ipAddress = "203.0.113.45"
        location = "Unknown"
    }
} | ConvertTo-Json -Depth 5

Invoke-RestMethod -Uri 'http://localhost:8081/api/v1/transactions' `
    -Method Post -Body $highRiskTx -ContentType 'application/json'
```

### Test 3: Submit Medium-Risk Transaction

```powershell
$mediumRiskTx = @{
    transactionId = "test-tx-003"
    accountId = "acct-test-003"
    amount = 1200.00
    currency = "USD"
    merchantCategory = "travel"
    channel = "ONLINE"
    deviceId = "device-99999"
    metadata = @{
        ipAddress = "45.76.23.11"
        previousDeviceId = "device-88888"
    }
} | ConvertTo-Json -Depth 5

Invoke-RestMethod -Uri 'http://localhost:8081/api/v1/transactions' `
    -Method Post -Body $mediumRiskTx -ContentType 'application/json'
```

---

## Step 5: Verify Fraud Detection Pipeline

### 5.1 Check Kafka Topics

Open Kafka UI: **http://localhost:8088**

Navigate to Topics:
- ✅ `transactions.raw` - Should have 3 messages
- ✅ `fraud.decisions` - Should have 3 messages

### 5.2 Check Database

```powershell
# Connect to Postgres and check alerts
docker exec -it fraud-platform-postgres psql -U postgres -d alerts -c "SELECT transaction_id, risk_score, risk_level, recommendation, status FROM fraud_alerts ORDER BY created_at DESC LIMIT 5;"
```

**Expected Output:**
```
transaction_id | risk_score | risk_level | recommendation | status
---------------+------------+------------+----------------+--------
test-tx-002    |     0.95   | HIGH       | BLOCK          | OPEN
test-tx-003    |     0.75   | MEDIUM     | REVIEW         | OPEN
test-tx-001    |     0.25   | LOW        | ALLOW          | OPEN
```

### 5.3 Test Admin API

```powershell
# Create admin session
$session = Invoke-RestMethod -Uri 'http://localhost:8084/api/v1/admin/session' `
    -Method Post `
    -Body (@{ username='admin'; password='changeme' } | ConvertTo-Json) `
    -ContentType 'application/json'

$token = $session.token

# Get all alerts
Invoke-RestMethod -Uri 'http://localhost:8084/api/v1/admin/alerts?page=0&size=10' `
    -Headers @{ Authorization = "Bearer $token" }
```

**Expected:** List of 3 alerts with risk scores

---

## Step 6: Test Frontend UI

### 6.1 Login
1. Open **http://localhost:5173** in browser
2. Login with:
   - Username: `admin`
   - Password: `changeme`

### 6.2 View Alerts Dashboard
- Should see 3 alerts
- Check risk levels: HIGH, MEDIUM, LOW
- Verify transaction IDs match

### 6.3 Update Alert Status
1. Click on a HIGH risk alert
2. Change status to "IN_PROGRESS" or "RESOLVED"
3. Verify status updates in UI

---

## Step 7: Performance Testing (Optional)

### Submit Multiple Transactions

```powershell
# Submit 10 transactions rapidly
1..10 | ForEach-Object {
    $tx = @{
        transactionId = "perf-test-$_"
        accountId = "acct-perf-001"
        amount = Get-Random -Minimum 10 -Maximum 10000
        currency = "USD"
        merchantCategory = "online"
        channel = "ONLINE"
        deviceId = "device-perf"
    } | ConvertTo-Json

    Invoke-RestMethod -Uri 'http://localhost:8081/api/v1/transactions' `
        -Method Post -Body $tx -ContentType 'application/json'
    
    Write-Host "Submitted transaction $_"
    Start-Sleep -Milliseconds 100
}
```

Monitor:
- Kafka lag in Kafka UI
- Service logs for errors
- Alert creation rate

---

## Troubleshooting

### Issue: Docker containers not starting
```powershell
# Check logs
docker compose logs inference
docker compose logs postgres
docker compose logs kafka

# Restart
docker compose down -v
docker compose up --build -d
```

### Issue: Spring Boot service won't start
```powershell
# Check port availability
netstat -ano | findstr :8081
netstat -ano | findstr :8082

# Kill process if needed
taskkill /PID <process_id> /F
```

### Issue: Gradle build fails
```powershell
# Clean and rebuild
cd backend
.\gradlew.bat clean build --refresh-dependencies
```

### Issue: Frontend won't start
```powershell
cd ui/frontend
Remove-Item -Recurse -Force node_modules
Remove-Item package-lock.json
npm install
npm run dev
```

---

## Success Criteria

✅ **Infrastructure:** All 5 Docker containers running and healthy  
✅ **Backend:** All 5 Spring services started without errors  
✅ **Frontend:** React app accessible at localhost:5173  
✅ **Transactions:** Accepted and stored in database  
✅ **Kafka:** Messages flowing through topics  
✅ **Fraud Detection:** Risk scores calculated correctly  
✅ **Alerts:** Created and visible in admin UI  
✅ **UI:** Can login and view/update alerts  

---

## Next Steps After Testing

If everything works:
1. ✅ Document any issues encountered
2. ✅ Consider adding real ML models
3. ✅ Set up monitoring and logging
4. ✅ Deploy to cloud environment
5. ✅ Add more comprehensive tests

If issues found:
1. Check logs in each terminal
2. Verify environment variables
3. Check database connectivity
4. Verify Kafka topics exist
5. Review this guide step-by-step

---

## Quick Commands Reference

```powershell
# Start everything
cd infrastructure/docker && docker compose up -d

# Stop everything
docker compose down -v

# View logs
docker compose logs -f inference
.\gradlew.bat :services:fraud-service:bootRun --info

# Health checks
curl http://localhost:8081/actuator/health
curl http://localhost:9090/health

# Database query
docker exec -it fraud-platform-postgres psql -U postgres -d alerts -c "SELECT COUNT(*) FROM fraud_alerts;"

# Kafka topics
docker exec -it fraud-platform-kafka kafka-topics --bootstrap-server localhost:9092 --list
```
