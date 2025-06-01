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
![obraz](https://github.com/user-attachments/assets/b35794d1-9b22-4c76-b8c2-c8790c82b866)

![obraz](https://github.com/user-attachments/assets/0c9db722-931f-49ed-90c8-cf76ba6582b2)
![obraz](https://github.com/user-attachments/assets/ffc58a98-efe0-493f-a54b-6b0a33eeddca)
![obraz](https://github.com/user-attachments/assets/8ce62c7f-3058-495b-89b6-0cffaf9a726a)
![obraz](https://github.com/user-attachments/assets/114f5f3e-2a5f-48cc-8a81-eaf972cdc7e2)
![obraz](https://github.com/user-attachments/assets/c153bb2c-cfbf-46f9-84bd-00cce40e93ef)
![obraz](https://github.com/user-attachments/assets/e822ee59-d5a2-47c3-a304-f8957ed497e0)
![obraz](https://github.com/user-attachments/assets/427df463-f218-4820-837c-38689a8b7320)





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

```
[ Klient HTTP ]
      |
[ Kontroler (Controller) ]
      |
[ Wywołanie metody w Service Layer (np. OrderService.createOrder()) ]
      |
[ Service Layer ]
      |
[ Repozytorium (Repository/DAO) ]
      |
[ JPA / EntityManager 
    – Generowanie SQL 
    – Połączenie z Bazą Danych 
]
      |
[ Baza Danych (np. PostgreSQL) ]
      |
[ Repozytorium zwraca encję/dane ]
      |
[ Service Layer ]
      |
[ Kontroler przygotowuje ResponseEntity (np. JSON) ]
      |
[ Klient HTTP otrzymuje odpowiedź (200/4xx/5xx) ]
```

![obraz](https://github.com/user-attachments/assets/bb6e8b91-12cd-4687-b200-e023dfef2b1f)
![obraz](https://github.com/user-attachments/assets/f53364b0-a0ea-496b-ab20-f884216927bd)
![obraz](https://github.com/user-attachments/assets/f75e385f-cadc-44e6-a786-c276fdce5b5b)



