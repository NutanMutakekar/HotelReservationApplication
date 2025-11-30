 # Hotel Reservation System
 
A Java-based hotel reservation console application built using Object-Oriented Programming (OOP) principles.
The system allows users to search rooms, make reservations, view bookings, and manage customer accounts with proper date validation and conflict-free booking logic.

## 🚀 Features
### 🔹 User Features

Search rooms based on check-in/check-out dates

Book available rooms with full conflict checking

View your own reservations

Create a customer account with email validation

Alternative date recommendations when selected rooms are unavailable

### 🔹 Admin Features

Add new rooms (Single/Double or Free/Paid)

View all registered customers

View all hotel rooms

View all reservations

Load pre-configured test data (rooms + users + sample reservation)

## ⚙️ Technical Highlights

Pure Java implementation using:

OOP concepts (Encapsulation, Inheritance, Polymorphism)

Interfaces (IRoom)

Abstracted services (CustomerService, ReservationService)

Full input validation, including:

Date format (yyyy-MM-dd)

Valid month/day handling

No past-date reservations

No overlapping bookings

Collections Framework

HashMap → rooms & customers

ArrayList → reservations

Additional Logic

Recommended date engine suggesting next available 7+ days

Clean modular architecture (UI, model, service, resource layers)

### 📂 Project Structure
src/
 ├── api/               → HotelResource, AdminResource
 ├── model/             → Room, FreeRoom, Customer, Reservation, RoomType
 ├── service/           → CustomerService, ReservationService
 ├── ui/                → MainMenu, AdminMenu (Console UI)
 └── HotelReservation   → Main entry point

## 🧪 How to Run

Clone the repository

Compile and run HotelReservation.java

Use the console menu to explore features

## 🎯 Learning Outcomes

Understanding of Java OOP

Implementing clean service-layer design

Working with Collections, iterators, and search logic

Building console-based workflows

Handling date validation and business rules
