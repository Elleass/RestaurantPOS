package org.example.restaurantpos.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role") // 🔧 Matches SQL column name
    private Long id;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    private String description;
}
