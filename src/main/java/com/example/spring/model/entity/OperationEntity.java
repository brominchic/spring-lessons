package com.example.spring.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Table(name = "operations")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationEntity {
    @Id
    private Long id;

    @Column(name = "from_account")
    private Long fromAccount;

    @Column(name = "to_account")
    private Long toAccount;

    @Column(name = "sum")
    private Long sum;

    @Column(name = "comment_for_operation")
    private String comment;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "operations_applied_settings",
            joinColumns = @JoinColumn(name = "operation_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "setting_id", referencedColumnName = "id"))
    private Set<SettingEntity> settings = new HashSet<SettingEntity>();

}
