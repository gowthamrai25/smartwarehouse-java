# SmartWarehouse™ — E-Commerce Warehouse & Storage Management System

A professional **Java-based warehouse management system** with a full web dashboard, built as an industrial-grade academic project.

## 🌐 Live Demo

**→ [https://smartwarehouse-java.netlify.app](https://smartwarehouse-java.netlify.app)**

| Email | Password | Role |
|---|---|---|
| `arjun.sharma@smartwarehouse.com` | `Admin@2024` | Admin |
| `priya.patel@smartwarehouse.com` | `Staff@2024` | Warehouse Staff |
| `vikram.singh@smartwarehouse.com` | `Deliver@2024` | Delivery Manager |

---

## 📋 Features

- **Product Management** — Add, remove, update, search inventory
- **Smart Rack Allocation** — Auto-assigns storage rack by category (Enum-based)
- **Shipment Management** — Incoming stock (Vector log) + outgoing dispatch (Queue)
- **Low Stock Alerts** — Custom `LowStockException` when qty < 5
- **Billing Estimate** — Base price + 18% GST + storage fee + shipping
- **Employee Access System** — Admin / Warehouse Staff / Delivery Manager roles
- **Sign Up / Sign In / Forgot Password** — Full auth flow with localStorage

---

## 🗂 Project Structure

```
project/
├── run.bat                    ← Compile & run (double-click)
├── ecommerce/
│   ├── exceptions/            ← LowStockException, ProductNotFoundException, InsufficientStockException
│   ├── interfaces/            ← Trackable, Manageable
│   ├── products/              ← Category (Enum), Product, ProductManager (ArrayList)
│   ├── warehouse/             ← Rack (Array), StorageManager (Generics)
│   ├── shipment/              ← Shipment (implements Trackable), ShipmentManager (Vector + Queue)
│   └── main/                  ← EmployeeRole (Enum), Employee, Main
└── web/                       ← Web dashboard (HTML + CSS + JS)
    ├── index.html
    ├── style.css
    └── app.js
```

---

## ▶ How to Run (Console)

**Requirements:** Java 8+

```bat
cd "path\to\project"
run.bat
```

**Or manually:**
```cmd
javac -d out ecommerce\exceptions\*.java ecommerce\interfaces\*.java ecommerce\products\*.java ecommerce\warehouse\*.java ecommerce\shipment\*.java ecommerce\main\*.java
java -cp out ecommerce.main.Main
```

---

## ☕ Java Concepts Used

| Concept | Implementation |
|---|---|
| Classes & Objects | Product, Shipment, Rack, Employee |
| Array | `String[] shelves` in Rack |
| ArrayList | `ArrayList<Product>` in ProductManager |
| Vector | `Vector<Shipment>` — thread-safe shipment log |
| Queue | `Queue<Shipment>` — FIFO dispatch processing |
| Generics | All collections use type parameters |
| Custom Exceptions | LowStockException, InsufficientStockException, ProductNotFoundException |
| Interfaces | Trackable (Shipment), Manageable (ProductManager, StorageManager) |
| Enums | Category, EmployeeRole, ShipmentType, ShipmentStatus |
| Packages | 6 modular packages |
| String Methods | `.contains()`, `.startsWith()` in search |

---

## 🚀 Deployment

Web dashboard deployed on **Netlify** via CLI:

```bash
npm install -g netlify-cli
netlify login
netlify deploy --dir ./web --no-build --prod --site smartwarehouse-java
```

---

*Built with ❤ as a Java academic project — Smart E-Commerce Warehouse & Storage Management System*
