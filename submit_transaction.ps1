# Quick Transaction Submission Script
# Usage: .\submit_transaction.ps1

Write-Host "`n╔════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║   AI FRAUD DETECTION - Transaction Tester   ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════╝`n" -ForegroundColor Cyan

function Submit-FraudTransaction {
    param(
        [Parameter(Mandatory=$true)]
        [double]$Amount,
        
        [Parameter(Mandatory=$true)]
        [string]$Category,
        
        [string]$AccountId = "user-" + (Get-Random -Maximum 999),
        [string]$Channel = "online"
    )
    
    $txnId = "txn-" + (Get-Date -Format "yyyyMMdd-HHmmss") + "-" + (Get-Random -Maximum 9999)
    
    $body = @{
        transactionId = $txnId
        accountId = $AccountId
        amount = $Amount
        currency = "USD"
        merchantCategory = $Category
        channel = $Channel
        deviceId = "device-" + (Get-Random -Maximum 999)
    } | ConvertTo-Json
    
    try {
        $response = Invoke-WebRequest `
            -Uri "http://localhost:8081/api/v1/transactions" `
            -Method POST `
            -Body $body `
            -ContentType "application/json" `
            -ErrorAction Stop
        
        $result = $response.Content | ConvertFrom-Json
        
        # Determine risk level based on amount
        $riskLevel = if ($Amount -lt 100) { "LOW" } 
                     elseif ($Amount -lt 1000) { "MEDIUM" } 
                     else { "HIGH" }
        
        $color = switch ($riskLevel) {
            "LOW" { "Green" }
            "MEDIUM" { "Yellow" }
            "HIGH" { "Red" }
        }
        
        Write-Host "✓ " -NoNewline -ForegroundColor Green
        Write-Host "Transaction " -NoNewline
        Write-Host "$txnId " -NoNewline -ForegroundColor Cyan
        Write-Host "submitted - " -NoNewline
        Write-Host "$" -NoNewline
        Write-Host "$Amount " -NoNewline -ForegroundColor White
        Write-Host "($Category) " -NoNewline
        Write-Host "[$riskLevel RISK]" -ForegroundColor $color
        
        return $txnId
    }
    catch {
        Write-Host "✗ Failed to submit transaction: $($_.Exception.Message)" -ForegroundColor Red
        return $null
    }
}

function Show-RecentAlerts {
    Write-Host "`n" + ("─" * 80) -ForegroundColor Gray
    Write-Host "RECENT FRAUD ALERTS" -ForegroundColor Cyan
    Write-Host ("─" * 80) -ForegroundColor Gray
    
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8083/api/v1/alerts?page=0&size=5"
        $alerts = $response.Content | ConvertFrom-Json
        
        if ($alerts.content.Count -eq 0) {
            Write-Host "No alerts found. Submit some transactions first!" -ForegroundColor Yellow
            return
        }
        
        $alerts.content | ForEach-Object {
            $color = switch ($_.riskLevel) {
                "LOW" { "Green" }
                "MEDIUM" { "Yellow" }
                "HIGH" { "Red" }
            }
            
            $icon = switch ($_.recommendation) {
                "ALLOW" { "✓" }
                "REVIEW" { "⚠" }
                "BLOCK" { "✗" }
            }
            
            Write-Host "$icon " -NoNewline -ForegroundColor $color
            Write-Host "$($_.transactionId) " -NoNewline -ForegroundColor Cyan
            Write-Host "Risk: " -NoNewline
            Write-Host "$($_.riskScore) " -NoNewline -ForegroundColor $color
            Write-Host "→ " -NoNewline
            Write-Host "$($_.riskLevel)" -NoNewline -ForegroundColor $color
            Write-Host " → " -NoNewline
            Write-Host "$($_.recommendation)" -ForegroundColor $color
        }
        
        Write-Host "`nTotal Alerts: $($alerts.totalElements)" -ForegroundColor Gray
    }
    catch {
        Write-Host "Failed to fetch alerts: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Menu
while ($true) {
    Write-Host "`n" + ("═" * 50) -ForegroundColor Cyan
    Write-Host "TRANSACTION MENU" -ForegroundColor Cyan
    Write-Host ("═" * 50) -ForegroundColor Cyan
    Write-Host "1. Submit LOW RISK transaction (< $100)"
    Write-Host "2. Submit MEDIUM RISK transaction ($100-$1000)"
    Write-Host "3. Submit HIGH RISK transaction (> $5000)"
    Write-Host "4. Custom transaction"
    Write-Host "5. Bulk test (10 random transactions)"
    Write-Host "6. View recent alerts"
    Write-Host "7. View all services status"
    Write-Host "8. Open Frontend UI"
    Write-Host "9. Open Kafka UI"
    Write-Host "0. Exit"
    Write-Host ("═" * 50) -ForegroundColor Cyan
    
    $choice = Read-Host "`nEnter choice"
    
    switch ($choice) {
        "1" {
            $categories = @("grocery", "gas_station", "pharmacy")
            $amount = Get-Random -Minimum 10 -Maximum 99
            $category = $categories | Get-Random
            Submit-FraudTransaction -Amount $amount -Category $category
        }
        "2" {
            $categories = @("restaurant", "clothing", "entertainment")
            $amount = Get-Random -Minimum 100 -Maximum 999
            $category = $categories | Get-Random
            Submit-FraudTransaction -Amount $amount -Category $category
        }
        "3" {
            $categories = @("electronics", "jewelry", "travel")
            $amount = Get-Random -Minimum 5000 -Maximum 15000
            $category = $categories | Get-Random
            Submit-FraudTransaction -Amount $amount -Category $category
        }
        "4" {
            Write-Host "`n--- Custom Transaction ---" -ForegroundColor Yellow
            $amount = Read-Host "Enter amount"
            Write-Host "Categories: grocery, restaurant, electronics, jewelry, clothing, travel"
            $category = Read-Host "Enter category"
            $accountId = Read-Host "Enter account ID (or press Enter for random)"
            
            if ([string]::IsNullOrWhiteSpace($accountId)) {
                Submit-FraudTransaction -Amount $amount -Category $category
            } else {
                Submit-FraudTransaction -Amount $amount -Category $category -AccountId $accountId
            }
        }
        "5" {
            Write-Host "`nSubmitting 10 random transactions..." -ForegroundColor Yellow
            $categories = @("grocery", "restaurant", "electronics", "jewelry", "clothing", "travel", "gas_station")
            
            for ($i = 1; $i -le 10; $i++) {
                $amount = switch (Get-Random -Minimum 1 -Maximum 4) {
                    1 { Get-Random -Minimum 10 -Maximum 99 }
                    2 { Get-Random -Minimum 100 -Maximum 999 }
                    3 { Get-Random -Minimum 1000 -Maximum 4999 }
                    default { Get-Random -Minimum 5000 -Maximum 15000 }
                }
                $category = $categories | Get-Random
                Submit-FraudTransaction -Amount $amount -Category $category
                Start-Sleep -Milliseconds 500
            }
            
            Write-Host "`nWaiting for fraud analysis..." -ForegroundColor Yellow
            Start-Sleep -Seconds 3
            Show-RecentAlerts
        }
        "6" {
            Show-RecentAlerts
        }
        "7" {
            Write-Host "`n" + ("─" * 50) -ForegroundColor Gray
            Write-Host "SERVICE HEALTH CHECK" -ForegroundColor Cyan
            Write-Host ("─" * 50) -ForegroundColor Gray
            
            @(
                @{Name="Transaction Service"; Port=8081; Path="/actuator/health"},
                @{Name="Fraud Service"; Port=8082; Path="/actuator/health"},
                @{Name="Alert Service"; Port=8083; Path="/actuator/health"},
                @{Name="Admin Service"; Port=8084; Path="/actuator/health"},
                @{Name="Gateway Service"; Port=8080; Path="/actuator/health"},
                @{Name="Inference Service"; Port=9090; Path="/health"}
            ) | ForEach-Object {
                Write-Host "$($_.Name): " -NoNewline
                try {
                    $response = Invoke-WebRequest -Uri "http://localhost:$($_.Port)$($_.Path)" -TimeoutSec 2 -ErrorAction Stop
                    Write-Host "✓ HEALTHY" -ForegroundColor Green
                } catch {
                    Write-Host "✗ DOWN" -ForegroundColor Red
                }
            }
        }
        "8" {
            Write-Host "`nOpening Frontend UI..." -ForegroundColor Cyan
            Start-Process "http://localhost:5173"
        }
        "9" {
            Write-Host "`nOpening Kafka UI..." -ForegroundColor Cyan
            Start-Process "http://localhost:8088"
        }
        "0" {
            Write-Host "`nExiting... Goodbye! 👋" -ForegroundColor Cyan
            break
        }
        default {
            Write-Host "Invalid choice. Please try again." -ForegroundColor Red
        }
    }
}
