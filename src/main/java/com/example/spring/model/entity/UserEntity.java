package com.example.spring.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Table(name = "users")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@NamedEntityGraph(name = "client_entity-graph", attributeNodes = @NamedAttributeNode("accountEntityList"))

public class UserEntity {

    @Id
    private Long id;

    @Column(name = "fullname")
    private String fullName;

    @Column(name = "total_balance")
    private Long totalBalance;

    @OneToMany(mappedBy = "number", cascade = CascadeType.ALL)
    private List<AccountEntity> accountEntityList;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "users_settings",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "setting_id", referencedColumnName = "id"))
    private Set<SettingEntity> settings;


}
