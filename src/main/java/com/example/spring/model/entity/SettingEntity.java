package com.example.spring.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Table(name = "settings")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettingEntity {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "users_settings",
            joinColumns = @JoinColumn(name = "setting_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<UserEntity> users = new HashSet<UserEntity>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "operations_applied_settings",
            joinColumns = @JoinColumn(name = "setting_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "operation_id", referencedColumnName = "id"))
    private Set<OperationEntity> operations = new HashSet<OperationEntity>();

    public SettingEntity(String name, Long id) {
        this.name = name;
        this.id = id;
    }
}