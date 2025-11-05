// package com.openclassrooms.starterjwt.models;

// import java.time.LocalDateTime;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;
// import jakarta.persistence.UniqueConstraint;
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// @NoArgsConstructor
// @AllArgsConstructor
// @Data
// @Entity
// @Table(name = "users" ,  uniqueConstraints = @UniqueConstraint(columnNames = "email"))
// public class UserEntity {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @Column(name = "email")
//     private String email;

//      @Column(name = "name")
//     private String name;

//     @Column(name = "password")
//     private String password;

//     @Column(name = "created_at")
//     private LocalDateTime createdAt;

//     @Column(name = "updated_at")
//     private LocalDateTime updatedAt;
// }