# Multi-Store Billing, Invoice & WhatsApp Campaign System
## Backend + Database Design
**Tech Stack:** Java 21 / Spring Boot / Spring Security / Spring Data JPA / MySQL / React frontend / WhatsApp Business API

---

## 1. Objective

Build a backend system for managing 5 retail stores with:

- Store-wise billing and invoices
- Customer management
- Product/item management
- WhatsApp invoice delivery
- Customer invoice history
- Customer profile enrichment
- Customer feedback / rating
- Google Review redirection for high ratings
- Birthday campaigns
- Anniversary campaigns
- Festival campaigns
- Custom WhatsApp campaigns
- Dashboard metrics
- Store-wise analytics
- Sales reports
- Customer repeat-frequency analytics

Payments are collected outside this application.  
The application only records the payment status/mode/reference if required and sends the invoice after staff confirms payment.

---

# 2. High-Level Architecture

```text
React Frontend
      |
      | HTTPS / REST APIs
      v
Spring Boot Backend
      |
      |-----------------------------
      |              |             |
      v              v             v
   MySQL        WhatsApp API   Google Review
 Database       Integration       Link
      |
      v
Scheduled Jobs
(Birthday / Anniversary /
Festival Campaigns)
```

Recommended backend modules:

```text
com.company.storeapp
|
|-- auth
|-- user
|-- store
|-- product
|-- customer
|-- invoice
|-- feedback
|-- campaign
|-- analytics
|-- whatsapp
|-- notification
|-- common
|-- security
```

---

# 3. Core Domain Entities

## 3.1 Store

Represents one physical store.

### Table: `stores`

| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK | Auto increment |
| store_code | VARCHAR(30) UNIQUE | Example: STORE-01 |
| name | VARCHAR(150) | Store name |
| phone | VARCHAR(20) | Optional |
| address | VARCHAR(500) | Store address |
| google_review_url | VARCHAR(1000) | Store-specific Google review URL |
| active | BOOLEAN | Active/inactive |
| created_at | DATETIME | Audit |
| updated_at | DATETIME | Audit |

### Important

Each invoice belongs to exactly one store.

Google Review URL should preferably be store-specific because the customer should review the store from which they purchased.

---

## 3.2 Application User

Admin/cashier/store employee who logs into the system.

### Table: `users`

| Column | Type |
|---|---|
| id | BIGINT PK |
| name | VARCHAR(150) |
| email | VARCHAR(150) UNIQUE |
| phone | VARCHAR(20) |
| password_hash | VARCHAR(255) |
| role | VARCHAR(50) |
| active | BOOLEAN |
| created_at | DATETIME |
| updated_at | DATETIME |

Possible roles:

```text
SUPER_ADMIN
ADMIN
STORE_MANAGER
CASHIER
```

---

## 3.3 User Store Mapping

If a user can access selected stores only.

### Table: `user_stores`

| Column | Type |
|---|---|
| id | BIGINT PK |
| user_id | BIGINT FK |
| store_id | BIGINT FK |

Unique constraint:

```text
(user_id, store_id)
```

SUPER_ADMIN may access all stores.

---

# 4. Product / Item Management

Items are stored before invoice creation so the billing screen can provide suggestions.

## 4.1 Product

### Table: `products`

| Column | Type |
|---|---|
| id | BIGINT PK |
| sku | VARCHAR(100) UNIQUE |
| name | VARCHAR(255) |
| brand | VARCHAR(150) |
| category | VARCHAR(150) |
| default_price | DECIMAL(12,2) |
| tax_percentage | DECIMAL(5,2) |
| active | BOOLEAN |
| created_at | DATETIME |
| updated_at | DATETIME |

Example:

```text
SKU: SHIRT-001
Name: Men's Cotton Shirt
Brand: Citrus
Category: Shirt
Price: 1299
```

---

## 4.2 Store Product

Use this table if product pricing/availability differs by store.

### Table: `store_products`

| Column | Type |
|---|---|
| id | BIGINT PK |
| store_id | BIGINT FK |
| product_id | BIGINT FK |
| selling_price | DECIMAL(12,2) |
| stock_quantity | INT |
| active | BOOLEAN |

Unique:

```text
(store_id, product_id)
```

If inventory tracking is not required initially, `stock_quantity` can be skipped.

---

# 5. Customer Management

Phone number should be the main customer identifier because WhatsApp messaging depends on it.

## 5.1 Customer

### Table: `customers`

| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK | |
| name | VARCHAR(150) | |
| phone | VARCHAR(20) UNIQUE | Main identity |
| email | VARCHAR(150) | Optional |
| date_of_birth | DATE | For birthday campaigns |
| marital_status | VARCHAR(30) | SINGLE / MARRIED / OTHER |
| spouse_name | VARCHAR(150) | Nullable |
| anniversary_date | DATE | Nullable |
| created_at | DATETIME | |
| updated_at | DATETIME | |

### Why phone should be unique

When staff enters a mobile number during billing:

```text
Enter phone
     |
     v
Search customer
     |
  Found?
  /   \
Yes   No
 |     |
Reuse  Create customer
```

This prevents duplicate customers and gives correct invoice history and repeat-customer analytics.

---

# 6. Invoice Domain

Invoice should be separated into header and line items.

---

## 6.1 Invoice

### Table: `invoices`

| Column | Type |
|---|---|
| id | BIGINT PK |
| invoice_number | VARCHAR(80) UNIQUE |
| store_id | BIGINT FK |
| customer_id | BIGINT FK |
| invoice_date | DATETIME |
| subtotal | DECIMAL(12,2) |
| discount_amount | DECIMAL(12,2) |
| total_amount | DECIMAL(12,2) |
| payment_mode | VARCHAR(30) | ==> CREDIT, DEBIT, UPI
| whatsapp_status | VARCHAR(30) | => SENT, NOT_SENT, DELIVERED
| paid_at | DATETIME |
| created_at | DATETIME |
| updated_at | DATETIME |


## 6.2 Invoice Item

### Table: `invoice_items`

| Column | Type |
|---|---|
| id | BIGINT PK |
| invoice_id | BIGINT FK |
| product_id | BIGINT FK nullable |
| item_name | VARCHAR(255) |
| quantity | INT |
| unit_price | DECIMAL(12,2) |
| discount_amount | DECIMAL(12,2) |
| line_total | DECIMAL(12,2) |

Important: copy product description and price into invoice item.

Do **not** depend only on the current Product row.

Reason:

```text
Product price today = ₹1,299
Product price after 6 months = ₹1,499
```

Old invoice should still show ₹1,299.

---

# 7. Invoice Number Generation

Recommended format:

```text
STORE01/2026/000001
STORE01/2026/000002
STORE02/2026/000001
```

Alternative:

```text
INV-S01-20260828-000123
```

Maintain sequence safely using a database-backed sequence table.

### Table: `invoice_sequences`

| Column | Type |
|---|---|
| id | BIGINT PK |
| store_id | BIGINT |
| financial_year | VARCHAR(20) |
| current_value | BIGINT |

Unique:

```text
(store_id, financial_year)
```

Generate invoice number inside a database transaction.

---

# 8. Complete Invoice Creation Flow

```text
Staff opens New Invoice
        |
        v
Select Store
        |
        v
Enter Customer Mobile
        |
        +--------------------+
        |                    |
 Customer Exists         New Customer
        |                    |
        +---------+----------+
                  |
                  v
          Search/Add Products
                  |
                  v
            Calculate Total
                  |
                  v
            Save Invoice
                  |
                  v
         Payment collected outside
                  |
                  v
       Staff clicks "Mark as Paid"
                  |
                  v
         Backend marks invoice PAID
                  |
                  v
       Create WhatsApp notification
                  |
                  v
        Send Invoice WhatsApp
                  |
                  v
       Customer opens invoice link
```

---

# 9. Recommended Invoice API Design

Base:

```text
/api/v1/invoices
```

## Create Invoice

```http
POST /api/v1/invoices
```

Request:

```json
{
  "storeId": 1,
  "customer": {
    "phone": "9876543210",
    "name": "Rahul Sharma"
  },
  "items": [
    {
      "productId": 101,
      "quantity": 2,
      "unitPrice": 850
    },
    {
      "productId": 202,
      "quantity": 1,
      "unitPrice": 1600
    }
  ],
  "discountAmount": 100
}
```

Backend:

```text
1. Validate store
2. Find/create customer
3. Validate products
4. Calculate subtotal
5. Calculate discount
6. Calculate tax if applicable
7. Calculate total
8. Generate invoice number
9. Generate public access token
10. Insert invoice
11. Insert invoice items
12. Return invoice
```

---

## Get Invoice

```http
GET /api/v1/invoices/{invoiceId}
```

---

## Search Invoices

```http
GET /api/v1/invoices
```

Query parameters:

```text
storeId
customerId
customerPhone
fromDate
toDate
paymentStatus
invoiceStatus
page
size
sort
```

Example:

```http
GET /api/v1/invoices?storeId=2&fromDate=2026-08-01&toDate=2026-08-31&page=0&size=20
```

---

## Mark Invoice Paid

```http
PATCH /api/v1/invoices/{invoiceId}/payment
```

Request:

```json
{
  "paymentStatus": "PAID",
  "paymentMode": "UPI",
  "paymentReference": "STORE-UPI-REF-123"
}
```

Backend should:

```text
Invoice -> PAID
paid_at -> now()
Publish/send invoice notification
```

---

## Resend WhatsApp Invoice

```http
POST /api/v1/invoices/{invoiceId}/send-whatsapp
```

Useful when original delivery failed.

---

# 10. Public Customer Invoice Portal

Customer receives WhatsApp:

```text
Thank you for shopping with us.

Invoice: INV-1256
Amount: ₹2,850

View Invoice:
https://billing.company.com/i/{publicToken}
```

Do not expose internal database invoice IDs publicly.

Use:

```text
public_token
```

Example:

```text
3fa85f64-5717-4562-b3fc-2c963f66afa6
```

or a cryptographically secure random token.

---

## Public APIs

These APIs do not require employee JWT login.

They validate the secure invoice/customer token.

```http
GET /public/invoices/{token}
GET /public/invoices/{token}/history
GET /public/invoices/{token}/profile
PUT /public/invoices/{token}/profile
POST /public/invoices/{token}/feedback
```

Rate-limit public APIs.

---

# 11. Customer Invoice Screen

Tabs:

```text
Invoice
History
Profile
```

---

## Invoice Tab

Returns:

```text
Invoice Number
Date
Store
Items
Quantity
Unit price
Total
Payment status
```

API:

```http
GET /public/invoices/{token}
```

---

## Invoice History

API:

```http
GET /public/invoices/{token}/history
```

Backend flow:

```text
public token
    |
    v
Find invoice
    |
    v
Find customer_id
    |
    v
Fetch all invoices of same customer
ORDER BY invoice_date DESC
```

Do not return invoices belonging to another phone/customer.

---

# 12. Customer Profile Update

API:

```http
PUT /public/invoices/{token}/profile
```

Request:

```json
{
  "name": "Rahul Sharma",
  "dateOfBirth": "1995-09-20",
  "maritalStatus": "MARRIED",
  "spouseName": "Priya Sharma",
  "anniversaryDate": "2021-02-12"
}
```

This profile data drives future WhatsApp campaigns.

---

# 13. Feedback / Rating System

## Feedback Entity

### Table: `feedback`

| Column | Type |
|---|---|
| id | BIGINT PK |
| invoice_id | BIGINT FK |
| customer_id | BIGINT FK |
| store_id | BIGINT FK |
| rating | TINYINT |
| comments | VARCHAR(1000) |
| redirected_to_google | BOOLEAN |
| created_at | DATETIME |

Constraint:

```text
rating BETWEEN 1 AND 5
```

Recommended unique constraint:

```text
(invoice_id)
```

One feedback per invoice.

---

## Feedback Flow

```text
Customer clicks Rate Experience
        |
        v
Select 1 - 5 Stars
        |
        v
POST Feedback
        |
        +-----------------------+
        |                       |
Rating >= 4                Rating <= 3
        |                       |
Save in DB                Save in DB
        |                       |
Return Google URL         No redirect
        |
        v
Frontend redirects
to Google Review
```

API:

```http
POST /public/invoices/{token}/feedback
```

Request:

```json
{
  "rating": 5,
  "comments": "Good experience"
}
```

Response for 4-5:

```json
{
  "saved": true,
  "redirectToGoogle": true,
  "googleReviewUrl": "https://..."
}
```

Response for 1-3:

```json
{
  "saved": true,
  "redirectToGoogle": false
}
```

### Important backend rule

Do not let the frontend decide alone whether Google Review should be shown.

Backend should make the decision using:

```java
rating >= 4
```

and return the correct store's Google Review URL.

---

# 14. WhatsApp Integration

Create an abstraction:

```java
public interface WhatsAppService {

    void sendInvoice(Invoice invoice);

    void sendCampaignMessage(
        Customer customer,
        Campaign campaign
    );
}
```

Possible implementation:

```text
Meta WhatsApp Cloud API
```

Keep WhatsApp provider-specific code isolated.

---

## WhatsApp Message Log

### Table: `whatsapp_messages`

| Column | Type |
|---|---|
| id | BIGINT PK |
| customer_id | BIGINT FK |
| invoice_id | BIGINT FK nullable |
| campaign_id | BIGINT FK nullable |
| phone | VARCHAR(20) |
| template_name | VARCHAR(150) |
| provider_message_id | VARCHAR(255) |
| message_type | VARCHAR(50) |
| status | VARCHAR(50) |
| failure_reason | VARCHAR(1000) |
| sent_at | DATETIME |
| delivered_at | DATETIME |
| read_at | DATETIME |
| created_at | DATETIME |

Message type:

```text
INVOICE
BIRTHDAY
ANNIVERSARY
FESTIVAL
CUSTOM
```

Status:

```text
QUEUED
SENT
DELIVERED
READ
FAILED
```

WhatsApp webhook updates delivery status.

---

# 15. WhatsApp Webhook

Expose:

```http
GET  /api/webhooks/whatsapp
POST /api/webhooks/whatsapp
```

POST receives events such as:

```text
sent
delivered
read
failed
```

Backend updates:

```text
whatsapp_messages.status
```

and optionally:

```text
invoices.whatsapp_status
```

---

# 16. Campaign Domain

## Campaign

### Table: `campaigns`

| Column | Type |
|---|---|
| id | BIGINT PK |
| name | VARCHAR(200) |
| type | VARCHAR(50) |
| store_id | BIGINT nullable |
| template_name | VARCHAR(150) |
| message_text | TEXT |
| scheduled_at | DATETIME |
| status | VARCHAR(50) |
| created_by | BIGINT |
| created_at | DATETIME |
| updated_at | DATETIME |

Campaign types:

```text
BIRTHDAY
ANNIVERSARY
FESTIVAL
CUSTOM
```

Campaign statuses:

```text
DRAFT
SCHEDULED
RUNNING
COMPLETED
CANCELLED
FAILED
```

`store_id = NULL` can mean all stores.

---

# 17. Campaign Recipients

### Table: `campaign_recipients`

| Column | Type |
|---|---|
| id | BIGINT PK |
| campaign_id | BIGINT FK |
| customer_id | BIGINT FK |
| phone | VARCHAR(20) |
| status | VARCHAR(50) |
| provider_message_id | VARCHAR(255) |
| sent_at | DATETIME |
| delivered_at | DATETIME |
| read_at | DATETIME |
| failure_reason | VARCHAR(1000) |

Unique:

```text
(campaign_id, customer_id)
```

---

# 18. Birthday Campaign Flow

Example scheduled job runs once daily:

```text
Every day 08:00
      |
      v
Find customers:
MONTH(date_of_birth) = current month
DAY(date_of_birth) = current day
      |
      v
whatsapp_opt_in = true
      |
      v
Create campaign recipients
      |
      v
Send approved WhatsApp template
      |
      v
Store delivery status
```

Query concept:

```sql
SELECT *
FROM customers
WHERE MONTH(date_of_birth) = MONTH(CURDATE())
AND DAY(date_of_birth) = DAY(CURDATE())
AND whatsapp_opt_in = TRUE
AND active = TRUE;
```

---

# 19. Anniversary Campaign Flow

```text
Scheduled Job
    |
    v
Find anniversary_date = today's month/day
    |
    v
Check opt-in
    |
    v
Send anniversary template
```

---

# 20. Festival Campaign Flow

Festival campaign is generally manually configured.

Example:

```text
Campaign Name: Diwali 2026
Type: FESTIVAL
Scheduled At: 2026-11-08 09:00
Audience: All Customers
Template: diwali_offer_2026
```

Flow:

```text
Admin creates campaign
       |
       v
Select audience
       |
       v
Schedule date/time
       |
       v
Campaign Scheduler
       |
       v
Build recipient list
       |
       v
Send messages
       |
       v
Track delivery/read
```

---

# 21. Custom Campaign

Admin can define rules such as:

```text
All customers
Customers from Store 1
Customers who purchased in last 30 days
Repeat customers
Customers with no purchase in last 90 days
Customers above a spend threshold
```

Do not build an overly generic audience-builder in Version 1.

Start with:

```text
ALL_CUSTOMERS
STORE_CUSTOMERS
REPEAT_CUSTOMERS
INACTIVE_CUSTOMERS
```

---

# 22. Campaign APIs

Base:

```text
/api/v1/campaigns
```

---

## Create Campaign

```http
POST /api/v1/campaigns
```

Example:

```json
{
  "name": "Diwali 2026",
  "type": "FESTIVAL",
  "storeId": null,
  "templateName": "diwali_offer_2026",
  "scheduledAt": "2026-11-08T09:00:00",
  "audience": {
    "type": "ALL_CUSTOMERS"
  }
}
```

---

## Get Campaigns

```http
GET /api/v1/campaigns
```

Filters:

```text
type
status
storeId
fromDate
toDate
page
size
```

---

## Campaign Details

```http
GET /api/v1/campaigns/{id}
```

---

## Schedule Campaign

```http
POST /api/v1/campaigns/{id}/schedule
```

---

## Cancel Campaign

```http
POST /api/v1/campaigns/{id}/cancel
```

---

## Campaign Performance

```http
GET /api/v1/campaigns/{id}/analytics
```

Response:

```json
{
  "totalRecipients": 1250,
  "sent": 1220,
  "delivered": 1180,
  "read": 910,
  "failed": 30,
  "deliveryRate": 96.72,
  "readRate": 74.59
}
```

---

# 23. Dashboard APIs

The Dashboard should not fetch raw invoices and calculate everything in React.

Backend should return aggregated data.

Base:

```text
/api/v1/dashboard
```

---

## Dashboard Summary

```http
GET /api/v1/dashboard/summary
```

Parameters:

```text
storeId
fromDate
toDate
```

Response:

```json
{
  "totalSales": 1245678,
  "totalInvoices": 2345,
  "revenue": 1245678,
  "averageOrderValue": 531.20,
  "totalCustomers": 7090,
  "newCustomers": 2458,
  "repeatCustomers": 4632,
  "repeatCustomerPercentage": 65.33,
  "averageCustomerFrequency": 2.3,
  "feedbackCount": 1256,
  "averageRating": 4.2
}
```

---

## Sales Trend

```http
GET /api/v1/dashboard/sales-trend
```

Response:

```json
[
  {
    "date": "2026-08-01",
    "sales": 42000,
    "invoiceCount": 82
  },
  {
    "date": "2026-08-02",
    "sales": 51000,
    "invoiceCount": 96
  }
]
```

---

## Store Distribution

```http
GET /api/v1/dashboard/store-distribution
```

---

## Customer Distribution

```http
GET /api/v1/dashboard/customer-distribution
```

Example:

```json
{
  "newCustomers": 2458,
  "repeatCustomers": 4632
}
```

---

## Feedback Summary

```http
GET /api/v1/dashboard/feedback-summary
```

Response:

```json
{
  "averageRating": 4.2,
  "totalFeedback": 1256,
  "ratingDistribution": {
    "1": 22,
    "2": 26,
    "3": 75,
    "4": 276,
    "5": 857
  }
}
```

---

# 24. Analytics Module

Frontend tabs:

```text
Store Summary
Daily Report
Sales Transactions
Monthly Trend
```

Each API accepts:

```text
storeId = optional
fromDate
toDate
```

No `storeId` means all stores.

---

# 25. Store Summary API

```http
GET /api/v1/analytics/store-summary
```

Response:

```json
[
  {
    "storeId": 1,
    "storeName": "Store 1",
    "sales": 512450,
    "invoiceCount": 945,
    "customerCount": 2650,
    "averageOrderValue": 542.27,
    "growthPercentage": 12.5
  }
]
```

---

# 26. Daily Report API

```http
GET /api/v1/analytics/daily-report
```

Response:

```json
[
  {
    "date": "2026-08-28",
    "sales": 85000,
    "invoiceCount": 120,
    "uniqueCustomers": 103,
    "averageOrderValue": 708.33
  }
]
```

---

# 27. Sales Transactions

```http
GET /api/v1/analytics/sales-transactions
```

Use pagination.

Parameters:

```text
storeId
fromDate
toDate
customerPhone
invoiceNumber
minAmount
maxAmount
page
size
sort
```

Response contains invoices, not individual item lines unless requested.

---

# 28. Monthly Trend

```http
GET /api/v1/analytics/monthly-trend
```

Example:

```json
[
  {
    "month": "2026-01",
    "sales": 380000,
    "invoiceCount": 680,
    "averageOrderValue": 558.82
  }
]
```

---

# 29. Customer Frequency Definition

Recommended:

```text
Purchase Frequency =
Total invoices / unique customers
```

For repeat/new:

### New Customer

Customer's first-ever invoice falls within selected date range.

### Repeat Customer

Customer has at least one invoice before the selected range or more than one purchase overall, depending on metric definition.

Recommended backend definition for dashboard:

```text
NEW:
first purchase date is inside selected date range

REPEAT:
customer purchased in selected date range
AND had a purchase before this range
```

Keep this definition consistent across reports.

---

# 30. Customer APIs

Base:

```text
/api/v1/customers
```

Endpoints:

```http
GET    /api/v1/customers
GET    /api/v1/customers/{id}
POST   /api/v1/customers
PUT    /api/v1/customers/{id}
GET    /api/v1/customers/{id}/invoices
GET    /api/v1/customers/{id}/summary
```

Search:

```http
GET /api/v1/customers?search=9876543210
```

or:

```http
GET /api/v1/customers?search=Rahul
```

---

# 31. Product APIs

Base:

```text
/api/v1/products
```

Endpoints:

```http
GET    /api/v1/products
POST   /api/v1/products
GET    /api/v1/products/{id}
PUT    /api/v1/products/{id}
DELETE /api/v1/products/{id}
```

Instead of hard delete, usually set:

```text
active = false
```

Search/suggestions:

```http
GET /api/v1/products/suggestions?storeId=1&q=shirt
```

Response:

```json
[
  {
    "id": 101,
    "sku": "SHIRT-001",
    "name": "Men Cotton Shirt",
    "price": 850
  }
]
```

---

# 32. Store APIs

```http
GET    /api/v1/stores
POST   /api/v1/stores
GET    /api/v1/stores/{id}
PUT    /api/v1/stores/{id}
PATCH  /api/v1/stores/{id}/status
```

---

# 33. Suggested Spring Boot Entity Relationships

```text
Store
 |
 | 1
 |------< Invoice >------1 Customer
 |           |
 |           |
 |           +------< InvoiceItem >------ Product
 |
 +------< Feedback

Customer
 |
 +------< Invoice
 |
 +------< Feedback
 |
 +------< CampaignRecipient
 |
 +------< WhatsAppMessage

Campaign
 |
 +------< CampaignRecipient
 |
 +------< WhatsAppMessage
```

---

# 34. Simplified ER Diagram

```text
users
  |
  | 1:N
  v
invoices -------- stores
   |
   | N:1
   v
customers
   |
   | 1:N
   v
feedback

invoices
   |
   | 1:N
   v
invoice_items
   |
   | N:1
   v
products


campaigns
   |
   | 1:N
   v
campaign_recipients
   |
   | N:1
   v
customers


customers
   |
   | 1:N
   v
whatsapp_messages
```

---

# 35. Recommended Java Entities

```text
User
Store
UserStore
Product
StoreProduct
Customer
Invoice
InvoiceItem
Feedback
Campaign
CampaignRecipient
WhatsAppMessage
InvoiceSequence
```

Optional:

```text
AuditLog
WhatsAppTemplate
CampaignAudienceRule
```

---

# 36. Recommended Repository Layer

```java
StoreRepository
ProductRepository
StoreProductRepository
CustomerRepository
InvoiceRepository
InvoiceItemRepository
FeedbackRepository
CampaignRepository
CampaignRecipientRepository
WhatsAppMessageRepository
UserRepository
```

For analytics, do not force everything through standard JPA entity loading.

Use:

```text
JPQL projections
native SQL queries
JdbcTemplate
```

for complex aggregation queries.

---

# 37. Recommended Service Layer

```text
AuthService
StoreService
ProductService
CustomerService
InvoiceService
InvoicePaymentService
InvoiceNotificationService
FeedbackService
CampaignService
CampaignExecutionService
WhatsAppService
AnalyticsService
DashboardService
```

Avoid one giant:

```text
StoreService
```

for all features.

---

# 38. Controller Structure

```text
AuthController
StoreController
ProductController
CustomerController
InvoiceController
CampaignController
AnalyticsController
DashboardController
PublicInvoiceController
WhatsAppWebhookController
```

---

# 39. Invoice Transaction Boundary

Invoice creation should be atomic.

Use:

```java
@Transactional
public InvoiceResponse createInvoice(CreateInvoiceRequest request) {
    ...
}
```

Everything below should either succeed together or fail:

```text
Customer lookup/create
Invoice sequence increment
Invoice creation
Invoice item creation
Totals calculation
```

Do not call external WhatsApp API inside the same DB transaction.

Recommended:

```text
DB transaction commits
       |
       v
Create notification/message job
       |
       v
Call WhatsApp separately
```

---

# 40. Recommended Reliable WhatsApp Sending

For Version 1:

```text
Invoice marked PAID
      |
      v
Insert whatsapp_messages row with QUEUED
      |
      v
Async worker / scheduler picks QUEUED messages
      |
      v
Send to WhatsApp
      |
      +---------+
      |         |
   Success    Failure
      |         |
    SENT      FAILED
```

This is safer than directly calling WhatsApp and assuming success.

Later you can introduce:

```text
RabbitMQ
Kafka
AWS SQS
```

but not necessary initially for this project size.

A DB-backed queue is sufficient for Version 1.

---

# 41. Scheduled Message Worker

Example Spring scheduler:

```text
Every 30-60 seconds
```

Find:

```text
whatsapp_messages.status = QUEUED
```

Process a small batch.

Example:

```sql
SELECT *
FROM whatsapp_messages
WHERE status = 'QUEUED'
ORDER BY created_at
LIMIT 50;
```

Use retry count if desired.

Recommended additional fields:

```text
retry_count
next_retry_at
```

---

# 42. Database Indexes

Indexes matter heavily for dashboard/reporting.

## `invoices`

Recommended:

```sql
INDEX idx_invoice_store_date (store_id, invoice_date)
INDEX idx_invoice_customer_date (customer_id, invoice_date)
INDEX idx_invoice_payment_status (payment_status)
INDEX idx_invoice_number (invoice_number)
INDEX idx_invoice_public_token (public_token)
```

---

## `customers`

```sql
UNIQUE INDEX uq_customer_phone (phone)
INDEX idx_customer_dob (date_of_birth)
INDEX idx_customer_anniversary (anniversary_date)
```

---

## `feedback`

```sql
INDEX idx_feedback_store_date (store_id, created_at)
INDEX idx_feedback_customer (customer_id)
```

---

## `campaign_recipients`

```sql
INDEX idx_campaign_recipient_campaign_status
(campaign_id, status)
```

---

## `whatsapp_messages`

```sql
INDEX idx_whatsapp_status_created
(status, created_at)

INDEX idx_whatsapp_provider_message
(provider_message_id)
```

---

# 43. Important Database Constraints

Use DB constraints in addition to Java validation.

Examples:

```text
invoice.total_amount >= 0
rating between 1 and 5
quantity > 0
unit_price >= 0
```

Use foreign keys.

Avoid storing money as:

```text
FLOAT
DOUBLE
```

Use:

```text
DECIMAL(12,2)
```

In Java use:

```java
BigDecimal
```

---

# 44. Soft Delete

For business records, avoid deleting historical data.

Use:

```text
active
deleted_at
```

for:

```text
products
stores
customers
users
```

Never delete a product if it appears in historical invoices.

---

# 45. Audit Columns

Recommended for most main tables:

```text
created_at
created_by
updated_at
updated_by
```

Use Spring Data JPA auditing.

---

# 46. Authentication

Employee/admin application:

```text
POST /api/v1/auth/login
POST /api/v1/auth/logout
GET  /api/v1/auth/me
```

Use JWT or secure server session.

Recommended role rules:

```text
SUPER_ADMIN
    all stores + config

ADMIN
    all business modules

STORE_MANAGER
    assigned store(s)

CASHIER
    customer search
    product search
    invoice create
    invoice view
    payment update
```

---

# 47. Store-Level Authorization

Do not rely on frontend filtering.

Example:

```text
Cashier belongs to Store 2
```

They should not be allowed to call:

```http
GET /api/v1/invoices?storeId=5
```

unless authorized.

Backend must verify store access.

---

# 48. Public Portal Security

The invoice portal is public-facing.

Recommended:

```text
Secure random token
Rate limiting
No sequential IDs
No customer search endpoint
No raw phone number in URL
No JWT needed for customer if secure token is used
```

Optional stronger version:

```text
Token + OTP
```

but probably unnecessary for Version 1 unless invoices contain sensitive data.

---

# 49. API Response Convention

Recommended:

```json
{
  "success": true,
  "data": {},
  "message": "Invoice created successfully",
  "timestamp": "2026-08-28T10:35:00"
}
```

For pagination:

```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 150,
  "totalPages": 8
}
```

---

# 50. Global Error Handling

Use:

```java
@RestControllerAdvice
```

Exceptions:

```text
ResourceNotFoundException
ValidationException
DuplicateCustomerException
InvalidInvoiceStateException
UnauthorizedStoreAccessException
WhatsAppException
CampaignException
```

Example:

```json
{
  "success": false,
  "code": "INVOICE_NOT_FOUND",
  "message": "Invoice not found"
}
```

---

# 51. Recommended Invoice State Rules

Example rules:

```text
DRAFT
  -> CONFIRMED

CONFIRMED + PENDING
  -> PAID

PAID
  -> send WhatsApp

CANCELLED
  -> cannot mark paid
```

Do not allow:

```text
CANCELLED -> PAID
```

without a controlled business action.

---

# 52. Analytics Query Strategy

For only 5 stores and moderate bill volume, calculate analytics directly from MySQL initially.

Example total sales:

```sql
SELECT
    SUM(total_amount)
FROM invoices
WHERE payment_status = 'PAID'
AND invoice_date BETWEEN :fromDate AND :toDate
AND (:storeId IS NULL OR store_id = :storeId);
```

Average order value:

```sql
SELECT
    AVG(total_amount)
FROM invoices
WHERE payment_status = 'PAID'
AND invoice_date BETWEEN :fromDate AND :toDate;
```

Invoices per store:

```sql
SELECT
    s.id,
    s.name,
    COUNT(i.id),
    SUM(i.total_amount)
FROM stores s
LEFT JOIN invoices i
    ON i.store_id = s.id
WHERE i.invoice_date BETWEEN :fromDate AND :toDate
GROUP BY s.id, s.name;
```

---

# 53. Dashboard Performance

Do not prematurely build a separate analytics database.

For this project:

```text
MySQL
+
correct indexes
+
aggregate queries
```

should be enough.

If reports later become slow:

```text
Add daily aggregate table
```

Example:

### `daily_store_metrics`

```text
id
metric_date
store_id
invoice_count
sales_amount
unique_customers
new_customers
repeat_customers
feedback_count
average_rating
```

Then dashboard can read precomputed data.

---

# 54. Campaign Scheduler Design

Use:

```java
@Scheduled
```

for small scale.

Possible jobs:

```text
CampaignScheduler
BirthdayCampaignScheduler
AnniversaryCampaignScheduler
WhatsAppRetryScheduler
```

But avoid creating duplicate birthday campaigns every time the scheduler runs.

Use idempotency.

Example unique business key:

```text
BIRTHDAY + customer_id + campaign_date
```

---

# 55. Campaign Consent

You should store:

```text
whatsapp_opt_in
```

before sending promotional campaigns.

Invoice/transactional WhatsApp and marketing/promotional WhatsApp may have different WhatsApp Business template/policy requirements.

Keep opt-in timestamp optionally:

```text
whatsapp_opt_in_at
whatsapp_opt_in_source
```

---

# 56. WhatsApp Templates

Recommended table:

### `whatsapp_templates`

| Column | Type |
|---|---|
| id | BIGINT |
| template_name | VARCHAR(150) |
| template_type | VARCHAR(50) |
| language_code | VARCHAR(20) |
| provider_template_id | VARCHAR(255) |
| active | BOOLEAN |
| created_at | DATETIME |

Examples:

```text
invoice_ready
birthday_wish
anniversary_wish
diwali_offer
custom_offer
```

---

# 57. Recommended API Groups Summary

```text
/api/v1/auth/*
/api/v1/stores/*
/api/v1/products/*
/api/v1/customers/*
/api/v1/invoices/*
/api/v1/campaigns/*
/api/v1/dashboard/*
/api/v1/analytics/*

/public/invoices/*

/api/webhooks/whatsapp/*
```

---

# 58. Suggested MySQL Tables Summary

Version 1:

```text
1. users
2. stores
3. user_stores
4. products
5. store_products
6. customers
7. invoices
8. invoice_items
9. invoice_sequences
10. feedback
11. campaigns
12. campaign_recipients
13. whatsapp_messages
14. whatsapp_templates
```

Optional later:

```text
15. audit_logs
16. daily_store_metrics
17. customer_segments
18. campaign_audience_rules
```

---

# 59. Recommended MVP Scope

## Phase 1

Build:

```text
Authentication
Stores
Products
Customers
Invoice create/view
Payment confirmation
WhatsApp invoice sending
Customer public invoice
Invoice history
Customer profile update
Feedback
Google Review redirect
Basic dashboard
```

---

## Phase 2

Add:

```text
Birthday campaigns
Anniversary campaigns
Festival campaigns
Custom campaigns
WhatsApp delivery tracking
Campaign analytics
Advanced store analytics
```

---

## Phase 3

Optional:

```text
Inventory
Returns/refunds
Advanced customer segmentation
Offers/coupons
Automated inactive customer campaigns
Detailed profit analytics
Export to Excel/PDF
Accounting integration
```

---

# 60. End-to-End Business Flow

```text
PRODUCTS PRELOADED
      |
      v
CUSTOMER VISITS STORE
      |
      v
CASHIER ENTERS MOBILE
      |
      +----------------+
      |                |
EXISTING CUSTOMER   NEW CUSTOMER
      |                |
      +-------+--------+
              |
              v
        ADD PRODUCTS
              |
              v
        CREATE INVOICE
              |
              v
   PAYMENT OUTSIDE SYSTEM
              |
              v
      MARK INVOICE PAID
              |
              v
  QUEUE WHATSAPP INVOICE
              |
              v
     CUSTOMER RECEIVES
              |
              v
        VIEW INVOICE
       /      |       \
      /       |        \
 Invoice   History    Profile
                         |
                         v
                Birthday / Anniversary
                       data
                         |
                         v
                     Campaigns


Customer Feedback
      |
      v
   1-5 Rating
      |
      +-------------------+
      |                   |
     4-5                 1-3
      |                   |
Save Feedback        Save Feedback
      |                   |
Google Review         Stop Here
```

---

# 61. Recommended Backend Flow in Spring Boot

```text
Controller
   |
   v
Request DTO
   |
   v
Validation
   |
   v
Service
   |
   +----------> Authorization
   |
   +----------> Business Validation
   |
   +----------> Repository / MySQL
   |
   +----------> Domain Events / Queue
   |
   v
Response DTO
```

Avoid returning JPA entities directly from controllers.

Use:

```text
CreateInvoiceRequest
InvoiceResponse
CustomerResponse
CampaignResponse
DashboardSummaryResponse
```

---

# 62. Important Design Decision

Do **not** make Invoice dependent on WhatsApp availability.

Correct flow:

```text
Invoice saved successfully
        |
        v
Payment marked successfully
        |
        v
WhatsApp queued
```

If WhatsApp fails:

```text
Invoice remains valid
WhatsApp status = FAILED
Admin can retry
```

This prevents external API failures from breaking billing.

---

# 63. Final Suggested Backend Modules

```text
auth
    AuthController
    AuthService

stores
    Store
    StoreRepository
    StoreService
    StoreController

products
    Product
    StoreProduct
    ProductRepository
    ProductService
    ProductController

customers
    Customer
    CustomerRepository
    CustomerService
    CustomerController

invoices
    Invoice
    InvoiceItem
    InvoiceSequence
    InvoiceRepository
    InvoiceService
    InvoiceController
    PublicInvoiceController

feedback
    Feedback
    FeedbackRepository
    FeedbackService
    FeedbackController

campaigns
    Campaign
    CampaignRecipient
    CampaignRepository
    CampaignService
    CampaignExecutionService
    CampaignController
    CampaignScheduler

whatsapp
    WhatsAppService
    MetaWhatsAppService
    WhatsAppMessage
    WhatsAppMessageRepository
    WhatsAppWebhookController
    WhatsAppMessageWorker

analytics
    AnalyticsRepository
    AnalyticsService
    AnalyticsController
    DashboardService
    DashboardController
```

---

# 64. Recommended Starting Database Relationships

For the first implementation, keep the core simple:

```text
Store
Product
Customer
Invoice
InvoiceItem
Feedback
Campaign
CampaignRecipient
WhatsAppMessage
```

These entities cover almost the complete business requirement without making the system unnecessarily complex.

---

# 65. Key Rules to Keep in Backend

1. A customer is primarily identified using a normalized mobile number.
2. Every invoice must belong to one store.
3. Invoice item prices are copied into invoice lines and never recalculated from current product prices later.
4. Payment is external, but payment confirmation/status is stored.
5. WhatsApp sending starts only after payment confirmation.
6. WhatsApp failure must never roll back invoice creation/payment confirmation.
7. Feedback is saved before any Google Review redirect.
8. Rating >= 4 returns the store's Google Review link.
9. Rating <= 3 stays internal.
10. Customer birthday/anniversary data drives campaign targeting.
11. Promotional campaigns should honor WhatsApp opt-in.
12. Dashboard and analytics calculations belong in the backend, not React.
13. Store-level authorization must be enforced in Spring Boot.
14. Public invoice URLs should use secure random tokens, not invoice IDs.
15. All financial calculations should use `BigDecimal` / MySQL `DECIMAL`.

---

## Final Architecture

```text
                       +----------------+
                       |   React Admin  |
                       +-------+--------+
                               |
                               | REST
                               v
                    +----------------------+
                    |    Spring Boot API   |
                    +----------+-----------+
                               |
              +----------------+----------------+
              |                |                |
              v                v                v
          MySQL DB      WhatsApp Business   Public Invoice
                             API               Portal
              |                |
              |                v
              |         Delivery Webhooks
              |
              v
    Dashboard / Analytics
         Aggregations


Spring Scheduler / Worker
       |
       +--> Birthday Campaigns
       +--> Anniversary Campaigns
       +--> Scheduled Festivals
       +--> WhatsApp Retry Queue
```

This design is suitable for the initial 5-store application and can scale significantly before any microservices split is required.
