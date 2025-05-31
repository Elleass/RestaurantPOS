# RestaurantPOS - Java 2024/2025 Project
## Opis projektu:
Aplikacja Point of Sale (POS) dla restauracji, umożliwiająca zarządzanie zamówieniami, użtykownikami i menu. Projekt spełnia zasady SOLID i programowania obiektowego.
### Fukcje aplikacji:
- Zarządzanie użytkownikami i rolami
- Zarządzanie klientami
- Zarządzanie Menu
- Obsługa zamówień
- Płatności 
### Możliwości rozwoju:
- Raporty i analizy
- Faktury i paragony
- Moduł rezerwacji
- Integracja z systemem lojalnościowym

## Technologie
- Maven
- Spring Boot
- Spring Security
- Spring Web
- Hibernate
- PostgreSQL
- Flyway
- Docker + Docker Compose
- Swagger UI
- Junit + JaCoCO

## Struktura Plików:
```
restaurantpos/
├── src/
│   ├── main/
│   │   ├── java/org/example/restaurantpos/
│   │   │   ├── config
│   │   │   ├── controller/        
│   │   │   ├── service/           
│   │   │   ├── repository/        
│   │   │   ├── entity/            
│   │   │   └── security/          
│   ├── resources/
│   │   └── db/migration/          
├── Dockerfile                     
├── docker-compose.yml           
├── pom.xml                      
└── README.md   
```
## Uruchomienie
### Uruchamianie z Docker
`docker-compose up --build`
### Swagger UI
Po uruchieniu aplikacji:
`http://localhost:8080/swagger-ui.html`
![SwaggerUI_img1](https://github.com/user-attachments/assets/d75b4326-feb8-4acb-a9d0-056fcbc66094)


## Zarządzanie użytkownikami i bezpieczeństwo
- Role:
  - USER
  - ADMIN
- Dostęp ograniczony przez Spring Security
- Dane logowania testowe:
  - `testadmin`
  - `testpassword`
schemat działania:
```
[ Klient HTTP ]
      |
[ Login + Hasło ]
      |
[ CustomUserDetailsService ]
      |
[ UserDetails / BCrypt ]
      |
[ Sprawdzenie ról (ROLE_USER, ROLE_ADMIN) ]
      |
[ HttpSecurity → authenticated() ]
      |
[ Dostęp do zasobów lub błąd 401/403 ]
```
    ![Security_img](https://github.com/user-attachments/assets/5623fd11-a76b-4a6e-93f2-8618119c8760)

## Baza danych 
### ERD Diagram
![obraz](https://github.com/user-attachments/assets/ec50592d-ea5e-4113-8a09-d8859ed8f80a)

### Migracja Flyway
Pliki migracyjne znajdują się w:
`src/main/resources/db/migration`
## Docker
### Pliki:
- `Dockerfile` – buduje aplikację
- `docker-compose.yml` – uruchamia aplikację i bazę danych PostgreSQL razem



## REST API(Swagger UI)
Dokumentacji API dostępna jest pod:
`http://localhost:8080/swagger-ui.html`
## Testy
- Pokrycie 80%
- ![img.png](img.png)

## Zastosowane wzorce projektowe i poliformizm
W projekcie zastosowano architektoniczny wzorzec **Service Layer**, który oddziela logikę biznesową od warstwy prezentacji (kontrolerów).
Dodatkowo w kodzie występuje **polimorfizm interfejsowy** – obiekty są traktowane przez ich interfejs a nie implementację, co pozwala na łatwe podstawianie różnych wariantów logiki np. w testach lub rozszerzeniach.
