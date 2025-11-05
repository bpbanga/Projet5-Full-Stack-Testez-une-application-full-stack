# 🧘‍♀️ Yoga App — Fullstack Booking Platform

![Coverage](https://img.shields.io/badge/Coverage-80%25-brightgreen)
![Backend](https://img.shields.io/badge/Backend-Spring%20Boot%203.5-blue)
![Frontend](https://img.shields.io/badge/Frontend-Angular%2014-DD0031)
![Java](https://img.shields.io/badge/Java-17-orange)
![Node](https://img.shields.io/badge/Node-16-green)

> Une application **Fullstack Angular + Spring Boot** pour la **gestion et la réservation de séances de yoga**.  
> Authentification sécurisée via **JWT**, gestion des rôles (`USER` / `ADMIN`) et couverture de tests élevée.

---

## 🧾 Sommaire


- [Technologies](#-technologies)
- [Installation](#️-installation)
- [Tests et couverture](#-tests-et-couverture)
- [Auteur](#-auteur)
  
---

## ⚙️ Technologies


| Couche | Technologie | Version |
|--------|--------------|----------|
| **Frontend** | Angular CLI | 14.2.1 |
|  | TypeScript | 4.7.4 |
|  | Angular Material / Flex Layout | 14.x |
| **Backend** | Spring Boot | 3.5.3 |
|  | Java | 17 |
|  | Maven | 3.9.10 |
|  | ORM | Spring Data JPA + Hibernate |
|  | Sécurité | Spring Security + JWT |
|  | Mapping | MapStruct 1.5.5.Final |
|  | Lombok | 1.18.36 |
| **Database** | MySQL | 8+ |
| **Tests** | Jest / Cypress / JUnit 5 / Mockito / JaCoCo |

---


## 🛠️ Installation


### ▶️ Backend

#### ⚙️ Prérequis

- Java 17+
- Maven 3.9+
- MySQL installé et configuré

#### ⚙️ Configuration (`application.properties`)

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/yoga_app
spring.datasource.username=root
spring.datasource.password=your_password
jwt.secret=your_secret_key
```

#### ⚙️ Lancer le backend 

```properties
cd back
mvn clean spring-boot:run
```
Backend disponible sur :
`👉 http://localhost:8080`


### ▶️ Frontend

#### ⚙️ Prérequis

- Node.js 16+
- Angular CLI 14+

#### ▶️ Lancer le frontend 

```properties
cd front
npm install
npm start
```
Frontend disponible sur :
`👉 http://localhost:4200`

#### 🔒 Authentification & Connexion

L’application utilise JWT (JSON Web Token) pour sécuriser les endpoints API.
Les rôles utilisateurs déterminent les accès :
- `USER`	Peut consulter et réserver des séances.
- `ADMIN`	Peut créer, modifier et supprimer des séances.
##### Login
Pour se connecter a l'application utiliser :
- email: `yoga@studio.com`
- password: `test!1234`
##### 🪪 Exemple de header :
`Authorization: Bearer <token>`

##### 🌐 API REST
- POST	 /api/auth/login	 Authentifie un utilisateur
- POST	 /api/auth/register	 Crée un compte utilisateur
- GET	 /api/user/{id}	 Récupère un utilisateur
- DELETE	 /api/user/{id}	 Supprime un utilisateur
- GET	 /api/session	 Liste toutes les sessions
- GET  /api/session/{id}   Les détails d'une session
- POST	 /api/session	 Crée une session (teacher only)
- POST   /api/session/{id}/participate/{id}  Ajoute un utilisateur a une session (teacher only)
- PUT    /api/session/{id} Modifie les détails d'une session (teacher only)
- DELETE	 /api/session/{id}	 Supprime une session
- DELETE	 /api/session/{id}/participate/{id}	 Supprime un utilisateur a une session (teacher only)
- POST	 /api/session/{id}/book	 Réserve une session


---


##  Tests et couverture


### 🧰 Backend

#### 📚 Technologies
- JUnit 5, Mockito, Spring Boot Test
- Base H2 (en mémoire)
- Couverture : JaCoCo

### IMPORTANT ❗
Si vous avez besoin de clean les tests:
`mvn clean verify`
`clean`
- Supprimer le dossier `target/.
- Assurez vous que vous partez d’une base propre..
`verify`
- Exécute l’ensemble du cycle de build Maven jusqu’à la phase `verify`.
- Cela inclut :
  - `compile:` compilez le code source Java.
  - `test:` exécutez les tests unitaires.
  - `package:`mettez le code dans un fichier JAR ou WAR (inclut également la vérification de la couverture de code via JaCoCo).
    
#### ▶️ Lancer les tests
```properties
cd back
mvn clean verify
```
#### 🎯 Lancer uniquement les tests d’intégration
```properties
mvn verify -Dgroups=integration
```
#### 📊 Rapport JaCoCo
Ouvre :
`back/target/site/jacoco/index.html`


### 🎨 Frontend

#### ✅ Tests unitaires (Jest)
```properties
cd front
npm run test
npm run test:coverage
```
#### 🌐 Tests end-to-end (Cypress)
```properties
npm run cypress:open
# ou en mode CI
npm run cypress:run
```
#### Rapport E2E :
`http://localhost:4300`


### 🧑‍💻 Auteur

Pagès Tchana Chrétien
🎓 Concepteur Développeur Logiciel
💻 Passionné par le développement fullstack, les architectures REST et les applications Angular/Spring Boot performantes.
📧 pagestchana@gmail.com
