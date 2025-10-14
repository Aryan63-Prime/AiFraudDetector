# How to Submit Transactions for Fraud Detection

## Quick Reference

**Transaction Service Endpoint**: `http://localhost:8081/api/v1/transactions`  
**Method**: `POST`  
**Content-Type**: `application/json`

---

## Required Fields

```json
{
  "transactionId": "unique-txn-id",      // Required: Unique transaction ID
  "accountId": "account-123",             // Required: Customer account ID
  "amount": 100.50,                       // Required: Transaction amount (must be > 0)
  "currency": "USD",                      // Required: Currency code
  "merchantCategory": "grocery",          // Required: Merchant category
  "channel": "online",                    // Optional: Transaction channel
  "deviceId": "device-456",               // Optional: Device identifier
  "metadata": {}                          // Optional: Additional data
}
```

---

## Method 1: Using PowerShell (Windows)

### Low-Risk Transaction ($50 - Grocery)
```powershell
$body = @{
    transactionId = "txn-" + (Get-Random -Maximum 99999)
    accountId = "user123"
    amount = 50.00
    currency = "USD"
    merchantCategory = "grocery"
    channel = "online"
    deviceId = "device123"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8081/api/v1/transactions" `
    -Method POST `
    -Body $body `
    -ContentType "application/json" | 
    Select-Object -ExpandProperty Content
```

### High-Risk Transaction ($15,000 - Electronics)
```powershell
$body = @{
    transactionId = "txn-" + (Get-Random -Maximum 99999)
    accountId = "user456"
    amount = 15000.00
    currency = "USD"
    merchantCategory = "electronics"
    channel = "online"
    deviceId = "suspicious-device"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8081/api/v1/transactions" `
    -Method POST `
    -Body $body `
    -ContentType "application/json" | 
    Select-Object -ExpandProperty Content
```

### Medium-Risk Transaction ($800 - Restaurant)
```powershell
$body = @{
    transactionId = "txn-" + (Get-Random -Maximum 99999)
    accountId = "user789"
    amount = 800.00
    currency = "USD"
    merchantCategory = "restaurant"
    channel = "mobile"
    deviceId = "device456"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8081/api/v1/transactions" `
    -Method POST `
    -Body $body `
    -ContentType "application/json" | 
    Select-Object -ExpandProperty Content
```

---

## Method 2: Using curl (Git Bash or WSL)

### Low-Risk Transaction
```bash
curl -X POST http://localhost:8081/api/v1/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "txn-001",
    "accountId": "user123",
    "amount": 50.00,
    "currency": "USD",
    "merchantCategory": "grocery",
    "channel": "online",
    "deviceId": "device123"
  }'
```

### High-Risk Transaction
```bash
curl -X POST http://localhost:8081/api/v1/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "txn-002",
    "accountId": "user456",
    "amount": 15000.00,
    "currency": "USD",
    "merchantCategory": "electronics",
    "channel": "online",
    "deviceId": "suspicious-device"
  }'
```

---

## Method 3: Using Postman or Thunder Client (VS Code)

1. **Create a new POST request**
2. **Set URL**: `http://localhost:8081/api/v1/transactions`
3. **Set Headers**: 
   - `Content-Type: application/json`
4. **Set Body** (raw JSON):
```json
{
  "transactionId": "txn-12345",
  "accountId": "user123",
  "amount": 500.00,
  "currency": "USD",
  "merchantCategory": "electronics",
  "channel": "online",
  "deviceId": "device123"
}
```
5. **Click Send**

---

## Method 4: Using Python Script

Create a file `submit_transaction.py`:

```python
import requests
import random

def submit_transaction(amount, category, account_id="user123"):
    """Submit a transaction for fraud detection"""
    
    transaction = {
        "transactionId": f"txn-{random.randint(10000, 99999)}",
        "accountId": account_id,
        "amount": amount,
        "currency": "USD",
        "merchantCategory": category,
        "channel": "online",
        "deviceId": f"device-{random.randint(100, 999)}"
    }
    
    response = requests.post(
        "http://localhost:8081/api/v1/transactions",
        json=transaction
    )
    
    print(f"Status: {response.status_code}")
    print(f"Response: {response.json()}")
    return response.json()

# Example usage
if __name__ == "__main__":
    # Submit different risk levels
    print("\n=== LOW RISK ===")
    submit_transaction(50.00, "grocery")
    
    print("\n=== MEDIUM RISK ===")
    submit_transaction(800.00, "restaurant")
    
    print("\n=== HIGH RISK ===")
    submit_transaction(15000.00, "electronics")
```

Run with: `python submit_transaction.py`

---

## Method 5: Bulk Transaction Submission (PowerShell)

Create multiple transactions at once:

```powershell
# Define transaction scenarios
$transactions = @(
    @{ amount = 25.50; category = "grocery"; accountId = "user001" },
    @{ amount = 150.00; category = "clothing"; accountId = "user002" },
    @{ amount = 500.00; category = "restaurant"; accountId = "user003" },
    @{ amount = 2500.00; category = "electronics"; accountId = "user004" },
    @{ amount = 10000.00; category = "jewelry"; accountId = "user005" }
)

foreach ($txn in $transactions) {
    $body = @{
        transactionId = "txn-" + (Get-Random -Maximum 99999)
        accountId = $txn.accountId
        amount = $txn.amount
        currency = "USD"
        merchantCategory = $txn.category
        channel = "online"
        deviceId = "device-" + (Get-Random -Maximum 999)
    } | ConvertTo-Json
    
    Write-Host "`nSubmitting $($txn.amount) $($txn.category) transaction..."
    $response = Invoke-WebRequest -Uri "http://localhost:8081/api/v1/transactions" `
        -Method POST `
        -Body $body `
        -ContentType "application/json"
    
    Write-Host "Response: $($response.Content)"
    Start-Sleep -Seconds 1
}
```

---

## View Results

After submitting transactions, check the results:

### View All Alerts
```powershell
Invoke-WebRequest -Uri "http://localhost:8083/api/v1/alerts" | 
    Select-Object -ExpandProperty Content | 
    ConvertFrom-Json | 
    Select-Object -ExpandProperty content | 
    Format-Table transactionId, riskScore, riskLevel, recommendation
```

### View Specific Transaction
```powershell
$txnId = "txn-001"  # Replace with your transaction ID
Invoke-WebRequest -Uri "http://localhost:8081/api/v1/transactions/$txnId" | 
    Select-Object -ExpandProperty Content
```

### View in Kafka UI
Open: http://localhost:8088/
- Navigate to **Topics** → `transactions.raw` to see raw transactions
- Navigate to **Topics** → `fraud.decisions` to see fraud analysis results

### View in Frontend
Open: http://localhost:5173/
- You should see alerts displayed in the dashboard

---

## Risk Score Interpretation

The current heuristic model assigns risk scores based on:

| Amount Range | Risk Score | Risk Level | Recommendation |
|-------------|-----------|-----------|----------------|
| $0 - $100 | 0.15 - 0.30 | LOW | ✅ ALLOW |
| $100 - $500 | 0.30 - 0.50 | LOW-MEDIUM | ⚠️ ALLOW |
| $500 - $1,000 | 0.50 - 0.65 | MEDIUM | ⚠️ REVIEW |
| $1,000 - $5,000 | 0.65 - 0.80 | MEDIUM-HIGH | ⚠️ REVIEW |
| $5,000+ | 0.80 - 0.95 | HIGH | 🚫 BLOCK |

**Note**: The current system uses a heuristic model. For production, replace with a trained ML model.

---

## Merchant Categories

Common categories to test:
- `grocery`
- `restaurant`
- `gas_station`
- `pharmacy`
- `clothing`
- `electronics`
- `jewelry`
- `travel`
- `entertainment`
- `online_services`

---

## Transaction Channels

- `online` - Web purchases
- `mobile` - Mobile app
- `pos` - Point of sale (physical store)
- `atm` - ATM withdrawal
- `phone` - Phone order

---

## Troubleshooting

### Error: 404 Not Found
- ✅ Make sure you're using port **8081** (transaction-service)
- ✅ Check endpoint: `/api/v1/transactions` (not `/api/transactions`)

### Error: 400 Bad Request
- ✅ Ensure all required fields are present
- ✅ Check that `amount` is greater than 0
- ✅ Verify JSON syntax is correct

### No Alert Created
- ✅ Wait 2-3 seconds for processing
- ✅ Check Kafka is running: `docker ps | grep kafka`
- ✅ Verify fraud-service is running on port 8082
- ✅ Check alert-service logs for errors

### Transaction Accepted but No Risk Score
- ✅ Check inference service: `curl http://localhost:9090/health`
- ✅ Check fraud-service logs
- ✅ Verify Kafka topics: `docker exec fraud-platform-kafka kafka-topics --bootstrap-server localhost:9092 --list`

---

## Quick Test Script

Save as `test_fraud_detection.ps1`:

```powershell
Write-Host "`n=== Testing Fraud Detection System ===`n" -ForegroundColor Cyan

function Submit-Transaction($amount, $category, $label) {
    Write-Host "Testing $label..." -NoNewline
    
    $body = @{
        transactionId = "test-txn-" + (Get-Random -Maximum 99999)
        accountId = "test-user"
        amount = $amount
        currency = "USD"
        merchantCategory = $category
        channel = "online"
        deviceId = "test-device"
    } | ConvertTo-Json
    
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8081/api/v1/transactions" `
            -Method POST -Body $body -ContentType "application/json" -ErrorAction Stop
        Write-Host " ✓ SUCCESS" -ForegroundColor Green
        return $true
    } catch {
        Write-Host " ✗ FAILED" -ForegroundColor Red
        return $false
    }
}

# Test different scenarios
Submit-Transaction 45.00 "grocery" "LOW RISK ($45 grocery)"
Start-Sleep -Seconds 2

Submit-Transaction 750.00 "restaurant" "MEDIUM RISK ($750 restaurant)"
Start-Sleep -Seconds 2

Submit-Transaction 12000.00 "electronics" "HIGH RISK ($12,000 electronics)"
Start-Sleep -Seconds 2

Write-Host "`n=== Checking Results ===`n" -ForegroundColor Cyan
Write-Host "Fetching alerts..." -NoNewline

try {
    $alerts = Invoke-WebRequest -Uri "http://localhost:8083/api/v1/alerts" -ErrorAction Stop | 
        ConvertFrom-Json | 
        Select-Object -ExpandProperty content
    
    Write-Host " Found $($alerts.Count) alerts`n" -ForegroundColor Green
    
    $alerts | Select-Object transactionId, riskScore, riskLevel, recommendation | 
        Format-Table -AutoSize
        
} catch {
    Write-Host " ✗ FAILED" -ForegroundColor Red
}

Write-Host "`nView details at:" -ForegroundColor Cyan
Write-Host "  Frontend: http://localhost:5173/"
Write-Host "  Kafka UI: http://localhost:8088/"
```

Run with: `.\test_fraud_detection.ps1`

---

## Next Steps

1. **Test with various amounts** to see how risk scores change
2. **Monitor Kafka UI** at http://localhost:8088/ to see message flow
3. **Check Frontend** at http://localhost:5173/ to see alerts
4. **Experiment with different merchant categories**
5. **Try bulk submissions** to simulate real traffic

Happy testing! 🚀
