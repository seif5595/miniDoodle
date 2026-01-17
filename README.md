# 📅 MiniDoodle

A high-performance meeting scheduling platform built with **Spring Boot** and **Java**. MiniDoodle allows users to manage their time slots, schedule meetings, and view their calendar availability.

---

## 🚀 Features

### Time Slot Management
- Create available time slots with configurable duration
- Modify or delete existing time slots
- Mark time slots as **busy** or **free**

### Meeting Scheduling
- Convert available slots into meetings
- Add meeting details: **title**, **description**, and **participants**
- Query free or busy slots within a selected time frame

### Personal Calendar
- Each user has a personal calendar to manage their time
- Aggregated view of availability for scheduling

---

## 🛠️ Tech Stack

- **Java 21+**
- **Spring Boot 4.0.1**
- **Spring Data JPA**
- **H2 / PostgreSQL**
- **Maven** or **Gradle**

---

## 📋 Prerequisites

Before running the application, ensure you have:

- [Java 17+](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) installed
- [Maven](https://maven.apache.org/download.cgi) or [Gradle](https://gradle.org/install/) installed
- A database (H2 for development, PostgreSQL/MySQL for production)

---

## ⚙️ Installation & Setup

### 1. Clone the repository

```bash
git clone https://github.com/seif5595/miniDoodle.git
cd miniDoodle
