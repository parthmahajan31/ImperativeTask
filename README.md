# Android Transactions App

## Project Overview

This is an Android application designed to manage and display financial transactions. It features a
modern UI with secure authentication and offline capabilities, making it suitable for tracking
transactions on the go.

### Key Features

- User authentication with encrypted credentials
- Transaction list with category-based search
- Offline support using Room database
- Responsive Material Design UI
- REST API integration with Retrofit

## Setup Instructions

1. **Prerequisites**:
    - Android Studio (latest version recommended)
    - JDK 11 or higher
    - Android SDK with API level 21+ (Lollipop or higher)

2. **Clone the Repository**:
   ```bash
   git clone https://github.com/parthmahajan31/ImperativeTask.git
   cd ImperativeTask

ImperativeTask/
app/
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/com/imperative/task/
│ │ │ │ ├── TransactionsActivity.java
│ │ │ │ ├── adapter/
│ │ │ │ │ └── TransactionAdapter.java
│ │ │ │ ├── data/
│ │ │ │ │ └── response/
│ │ │ │ │ └── Transaction.java
│ │ │ │ ├── retrofit/
│ │ │ │ │ └── ApiService.java
│ │ │ │ └── room/
│ │ │ │ ├── AppDatabase.java
│ │ │ │ └── TransactionDao.java
│ │ │ ├── res/
│ │ │ │ ├── layout/
│ │ │ │ │ ├── activity_transactions.xml
│ │ │ │ │ ├── activity_main.xml (login screen)
│ │ │ │ │ └── transaction_item.xml
│ │ │ │ └── drawable/
│ │ │ │ └── gradient_background.xml
│ │ └── build.gradle
├── .gitignore
└── README.md