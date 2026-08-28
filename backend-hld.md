# Multi-Store Billing & WhatsApp Campaign System
## High-Level Backend Design

**Backend:** Java Spring Boot  
**Database:** MySQL  
**Frontend:** React JS  
**External Integration:** WhatsApp Business API

---

## 1. High-Level Architecture

```text
React Admin Application
        |
        v
Spring Boot Backend
        |
        +------------------+
        |                  |
        v                  v
      MySQL        WhatsApp Business API
        |                  |
        |                  v
        |          Invoice / Campaign Messages
        |
        v
Dashboard & Analytics
```

The Spring Boot backend acts as the central system for all 5 stores.

---

## 2. Main Backend Modules

```text
Authentication
Stores
Products
Customers
Invoices
Feedback
Campaigns
Dashboard / Analytics
WhatsApp Integration
```

---

## 3. Core Entities

### Store
Represents each of the 5 physical stores.

### Product
Stores the items available for billing.

### Customer
Stores customer details such as:

- Name
- Mobile number
- Birthday
- Marital status
- Spouse name
- Anniversary date
- WhatsApp consent

### Invoice
Stores the main invoice information:

- Store
- Customer
- Invoice number
- Date
- Total amount
- Payment status

### Invoice Item
Stores the products included in each invoice.

### Feedback
Stores customer rating received from the invoice page.

### Campaign
Represents:

- Birthday campaign
- Anniversary campaign
- Festival campaign
- Custom campaign

### Campaign Recipient
Stores which customers are included in each campaign.

### WhatsApp Message
Tracks invoice and campaign messages sent through WhatsApp.

---

# 4. Main Business Flow

```text
Customer visits Store
        |
        v
Staff selects / enters customer
        |
        v
Search and add products
        |
        v
Create Invoice
        |
        v
Payment collected outside application
        |
        v
Staff marks invoice as PAID
        |
        v
Backend sends invoice through WhatsApp
        |
        v
Customer opens invoice page
```

---

# 5. Customer Invoice Flow

```text
WhatsApp Message
      |
      v
View Invoice
      |
      +-----------------------+
      |          |            |
      v          v            v
   Invoice     History      Profile
```

### Invoice
Shows current invoice details.

### History
Shows all previous invoices for the same customer.

### Profile
Customer can add/update:

```text
Name
Birthday
Marital Status
Spouse Name
Anniversary Date
```

This data is later used for campaigns.

---

# 6. Feedback & Google Review Flow

```text
Customer opens invoice
        |
        v
Clicks "Rate Experience"
        |
        v
Selects 1 - 5 Stars
        |
        v
Backend saves rating
        |
        +----------------+
        |                |
      4 - 5            1 - 3
        |                |
        v                v
Google Review        Stored internally
Redirect             No redirect
```

Each store can have its own Google Review link.

---

# 7. WhatsApp Invoice Flow

```text
Invoice Created
      |
      v
Payment Confirmed
      |
      v
Backend creates WhatsApp message
      |
      v
WhatsApp Business API
      |
      v
Customer receives invoice
```

Important:

```text
Invoice creation must not fail
if WhatsApp sending fails.
```

If WhatsApp fails, the backend stores the failed status and allows retry.

---

# 8. Campaign Flow

```text
Admin creates campaign
        |
        v
Select campaign type
        |
        +--------------------------+
        |        |        |        |
        v        v        v        v
   Birthday Anniversary Festival Custom
        |
        v
Backend selects eligible customers
        |
        v
Create recipient list
        |
        v
Send WhatsApp messages
        |
        v
Track Sent / Delivered / Failed
```

---

# 9. Birthday Campaign

```text
Daily Scheduler
      |
      v
Find customers whose birthday is today
      |
      v
Send approved WhatsApp birthday message
```

---

# 10. Anniversary Campaign

```text
Daily Scheduler
      |
      v
Find customers whose anniversary is today
      |
      v
Send WhatsApp anniversary message
```

---

# 11. Festival Campaign

Festival campaigns are created manually by the admin.

Example:

```text
Diwali Campaign
      |
      v
Select customers / all customers
      |
      v
Schedule Date & Time
      |
      v
WhatsApp messages sent
```

---

# 12. Dashboard Flow

The backend calculates aggregated data from MySQL.

```text
Invoices
Customers
Feedback
Stores
     |
     v
Backend Aggregation
     |
     v
Dashboard
```

Dashboard shows:

```text
Total Sales
Number of Bills
Revenue
Average Order Value
Sales Trend
Feedback Count
Average Rating
New Customers
Repeat Customers
Customer Frequency
Store-wise Sales
```

---

# 13. Analytics Flow

```text
User selects:
Date Range
Store / All Stores
        |
        v
Spring Boot
        |
        v
MySQL Aggregation
        |
        v
Analytics Result
```

Main analytics sections:

```text
Store Summary
Daily Report
Sales Transactions
Monthly Trend
```

---

# 14. Main Database Relationships

```text
Store
  |
  +---- Invoices
          |
          +---- Invoice Items
          |
          +---- Customer
          |
          +---- Feedback


Customer
  |
  +---- Invoices
  |
  +---- Campaign Recipients
  |
  +---- WhatsApp Messages


Campaign
  |
  +---- Campaign Recipients
```

---

# 15. Recommended Main Tables

```text
stores
users
products
customers
invoices
invoice_items
feedback
campaigns
campaign_recipients
whatsapp_messages
```

These tables are enough for the first version.

---

# 16. Important Backend Rules

1. Every invoice belongs to one store.

2. Customer should primarily be identified by mobile number.

3. Payment happens outside the application.

4. Invoice is sent on WhatsApp only after payment is confirmed.

5. WhatsApp failure must not affect the invoice.

6. Customer ratings are always stored first.

7. Ratings 4-5 redirect to Google Review.

8. Ratings 1-3 stay internal.

9. Birthday and anniversary data comes from the customer profile.

10. Dashboard and analytics calculations should be done in the backend.

11. Store access should be controlled by logged-in user role.

12. Promotional WhatsApp campaigns should be sent only to eligible/opted-in customers.

---

# 17. Overall HLD

```text
                         React Application
                                |
                                v
                      Spring Boot Backend
                                |
          +---------------------+--------------------+
          |                     |                    |
          v                     v                    v
       MySQL              WhatsApp API         Scheduler
          |                     |                    |
          |                     |                    |
          v                     v                    v
Invoices / Customers     Invoice Messages     Birthday
Products / Feedback      Campaign Messages    Anniversary
Campaigns / Stores                            Festival
          |
          v
Dashboard & Analytics
```

---

## Suggested MVP

```text
Stores
Products
Customers
Invoice Creation
Payment Confirmation
WhatsApp Invoice
Customer Invoice History
Customer Profile
Feedback
Google Review Redirect
Dashboard
Basic Analytics
Campaigns
```

This keeps the backend simple, modular and sufficient for all 5 stores without introducing unnecessary microservices or complex infrastructure.
