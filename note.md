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

# Bab 2: Design REST API

### Server Flow
1. Authentication using Keycloak

### Organizer flow
1. **Create Event**
	- POST Endpoint: '/api/v1/events'
	- Request Body: Event java object
2. **Get tiket data for dashboard atau report penjualan tiket**
3. **Daftar Event**
	- GET Endpoint: '/api/v1/events'
4. **Get Event by id**
	- GET Endpoint: '/api/v1/events/{event_id}'
5. **Update Event**
	- PUT Endpoint: '/api/v1/events/{event_id}'
	- Request Body: Event
6. **Delete Event**
	- DELETE Endpoint: '/api/v1/events/{event_id}'
7. **Get Daftar Tiket Penjualan Berdasarkan Event**
	- GET Endpoint: '/api/v1/events/{event_id}/tikets'
8. **Partial Update Penjualan Tiket Untuk Batalin**: 
	- PARTIAL Endpoint: '/api/v1/events/{event_id}/tikets/tiket/{tiket_id}'
	- Menggunakan Partial Tiket Objek
9. **Get Penjualan Tiket By Id**
	- GET Endpoint: '/api/v1/events/{event_id}/tikets/{tiket_id}'
10. **Get Daftar Tipe Tiket**
	- GET Endpoint: '/api/v1/events/{event_id}/tikets/tipe-tikets'
11. **Get Daftar Tipe Tiket Berdasarkan Id**
	- GET Endpoint: '/api/v1/events/{event_id}/tikets/tipe-tikets/{tipe_tiket_id}'
12. **Partial Update Tipe Tiket**
	- PARTIAL Endpoint: '/api/v1/events/{event_id}/tikets/tipe-tikets/{tipe_tiket_id}'
	- Request Body: Partial TipeTiket
13. **Delete Tipe Tiket**
	- Delete Endpoint: '/api/v1/events/{event_id}/tikets/tipe-tikets/{tipe_tiket_id}'
14. d


### Hadirin Flow
1. **Search Event Terpublish**
	- GET Endpoint: '/api/v1/event-terpublish'
2. **Get Event Terpublish berdasarkan id**
	- GET Endpoint: '/api/v1/event-terpublish/{event_terpublish_id}'
3. **Post Beli Tiket**
	- POST Endpoint : '/api/v1/event-terpublish/{event_terpublish_id}/tipe-tikets/{tipe_tiket_id}'
4. **Daftar Tiket untuk Hadirin**
	- GET Endpoint: '/api/v1/tikets'
5. **Get Tiket untuk Hadirin berdasarkan id**
	- GET Endpoint: '/api/v1/tikets/{tiket_id}'
6. **Get Tiket QR Code untuk hadirin**
	- GET Endpoint: /api/v1/tikets/{tiket_id}/qr-code
### Panitia Flow
1. **Get List of Events** (endpoint same as the Organizer flow)
2. **Validasi Tiket**
	- POST Endpoint: '/api/v1/events/{event_id}/tikets/tiket_validasi'
3. **mengambil Daftar Validasi Tiket**
	- GET Endpoint: '/api/v1/events/{event_id}/tikets/tiket_validasi'

---
# Bab 3: Project Setup

## ToDo
- [x] membuat arsitektur projek
- [x] membuat spring boot baru
- [x] pakai postgre
- [x] pakai keycloak buat OAuth2
- [ ] pakai MapStruct untuk streamline object mapping atau bantu dalam Mapper

## Projek Arsitektur
1. Front End: React
2. Back End: Spring Boot 3
3. Db: PostgreSQL cuz support fuzzy search capabilites
4. Authorization Server: Keycloak through Docker


## Using Postgre
You can set the db in pgadmin to be ready and configure in `application.properties` like this:
```java
spring.datasource.url=jdbc:postgresql://localhost:5432/bacasekarang
spring.datasource.username=postgres
spring.datasource.password=###
spring.datasource.platform=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Konfigurasi
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

## Keycloak for Auth server
For handling authentication untuk membuat initial realm untuk client dan user dengan konfigurasi yang dibutuhkan.

```yaml
services:  
  keycloak:  
    image: quay.io/keycloak/keycloak:latest  
    ports:  
      - "9090:8080"  
    environment:  
      KEYCLOAK_ADMIN: admin  
      KEYCLOAK_ADMIN_PASSWORD: admin  
    volumes: # kalau pakai ini datanya ga hilang walaupun project spring direstart  
      - keycloak-data:/opt/keycloak/data  
    command:  
      - start-dev  
      - --db=dev-file  
          
volumes:  
  keycloak-data:  
    driver: local
```

run `docker-compose up`. About 1 GB

### Set up key cloak `Realm`
1. Masuk ke http:localhost:9090 dan sign dengan admin, admin
2. Buat realm (), masuk ke Manage Realms > Create Realms
3. Masukan nama realm seperti *kerja-tiket-app*
	- Realm adalah tempat terisolasi yang manages kumpulan users, credentioal, roles, dan group. Jadi satu user itu milik dan telah terdaftar dalam suatu realm. Jadi realm itu hanya bisa manage user yang ada didalamnya
- Buat Client, masuk ke Clients > Create client
- Masukkan `Client ID` seperti *kerja-tiket-app-client* sama juga untuk `Name` > Next
- unable the `client authentication` dan `authorization`, dan enable `Standard Flow` > Next
- masukkan `Home-URL` seperti 'http://localhost:5173' dan juga sama `Valid redirect URLs` begitupun `Valid post logout` dan siasnya empty > Save
- Buat User, masuk ke User > Create User
- untuk field `Username` masukkan contoh nama aktor `organizer`
- field `email` bebas, `first name` = user > Create
- Masuk ke user itu dan ke Credential
- Set password dengan isi bebas dulu aja dan unable `Temporary`
- Simpan setipa nama
- set di `.properties`
```yaml
  
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:9090/realms/kerja-tiket-app
```

## Mapstruct
1. Pada `application.properties` setting si versi dari mapstruct dan lombok