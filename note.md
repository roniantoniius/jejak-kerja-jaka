# Jaka
Jejak Kerja merupakan situs web yang menawarkan sistem ticketing per jam, per hari, atau per bulan untuk setiap pekerjaan yang disediakan oleh pemberi kerja.

Kategori: #java  #code 

---
# Overall Plan
1. **Bab 1**: System Design
	- Analisis Domain
	- personalisasi user
	- ui design
	- domain modeling
2. **Bab 2**: Design REST API
	- merancang rest api
	- merancang arsitektur aplikasi
3. **Bab 3**: Project Setup
	- menyiapkan dan setup projek
	- menerapkan spring security using *OAuth2* dan *OpenID connect*
4. **Bab 4**: Domain Implementation
	- write code about domain entity object
5. **Bab 5**: Event Creation & Management
	- Event yang akan digunakan untuk logika *ticketing*
6. **Bab 6**: Ticket Sale & Purchase
	- Menjual tiket melalui QR dan mencatat tiket
7. **Bab 7**: Ticket Validation
	- Mengelola tiket
8. **Bab 8**: Sales Reporting

note: shoutout to Devtiro :)
# Bab 1: System Design

## Summary
A web-based platform enabling users to create events, manage ticket sales, and generate QR coded tickets for attendees, streamlining the event management and ticket distribution process.


## Definitions
### Event
A planned gathering or occasion with a specific date, time, and venue that requires ticketing for attendance management.

### Ticket
A digital or physical document that grants the holder access to an event, containing event details and a unique QR code for validation.

### QR Code
A machine-readable code consisting of black and white squares, used to store ticket information and verify authenticity at event entry.

## Stories
### User Stories
- As an event organizer
- I want to create and configure a new event with details like date, venue, and ticket types
- So that I can start selling tickets to attendees
	- Organizer can input event name, date, time, and venue
	-  Organizer can set multiple ticket types with different prices
	- Organizer can specify total available tickets per type
	- Event appears on the platform after creation

### Purchase Event Ticket
- As an event goer (people that went to his holiday)
- I want to purchase the correct ticket for an event
- So that I can attend and experience the event
	- Event goer can search for events
	- Event goer can browse and select different ticket types available each even
	- Event goer can purchase their chosen ticket type

### Manage ticket sales
	- As an event organizer
	- I want to monitor and manage ticket sales
	- So that I can track revenue and attendance
		- • Dashboard displays sales metrics
		- • Organizer can view purchaser details
		- • System prevents overselling of tickets
		- • Sales automatically stop at specified end date

### Validate Tickets
- As an event staff member
- I want to scan attendee QR codes at entry
- So that I can verify ticket authenticity
	- • Staff can scan QR codes using mobile device
	- • System displays ticket validity status instantly
	- • System prevents duplicate ticket use
	- • Staff can manually input ticket numbers if QR scan fails



- System menggunakan 3 tipe user: organizers, hadirin, dan panitia
- platform diharap manage event lifecycle dari membuat sampai validasi suatu app
- Memiliki fitur untuk setup event, penjualan tiket, monitoring, dan validasi QR.

### User Journey
1. **Organizer**
	1. **Event Creation**
		- login
		- create even baru
		- konfigurasi tipe event / kegiatan
		- set harga tiket
		- review detail event
		- publish event
	2. **Sales management**
		- monitor penjualan event dashboard
		- melacak tiket inventori
		- memodif harga tiket
	3. **Event perparation**
		- brief staff on check-process
		- test scan equipment
	4. **Event day**
		- monitor entri analisis
		- handle ticket problems
	5. **Post event**
		- review final report

2. **Hadirin**
	1. **Discovery**
		- mencari events
		- memeriksa harga tiket
		- membagikan share tiket
		- koordinasi kehadiran tiket bersama
	2. **Purchase**
		1. memilih kuantitas tiket
		2. review pajak dan total tiket
		3. complete mobile payment
		4. menerima tiket digital
	3. **Event Perparation**
		1. simpan tiket digital
		2. share ticket dgn teman
		3. get event reminder
	4. **Event day**
		1. tiba di venue
		2. quick qr code scan
		3. masuk event
	5. **Post Event**
		1. share excperience
		2. follow venue / org social media
3. **Panitia**
	1. **Event Preparation**
		1. akses detail dari event
		2. review tiket dan peraturan
		3. briefing staff prosedur
	2. **Event Day**
		1. mekanisme panitia briefing ketika hari-H lainnya
	3. **Post event**
		1. debrief with staff

### Struktur Model

This is a refined model app
![[Pasted image 20250419214152.png]]

note:
- 0..* artinya 0 to many
	- kenapa 0? relasi yang digunakan walaupun memiliki 0 maka tidak terjadi apa-apa atau status quo yang kosong

---





