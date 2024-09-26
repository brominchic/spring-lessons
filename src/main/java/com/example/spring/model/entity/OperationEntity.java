package com.example.spring.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Table(name = "operations")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OperationEntity {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_account")
    private AccountEntity fromAccount;

    @ManyToOne
    @JoinColumn(name = "to_account")
    private AccountEntity toAccount;

    @Column(name = "sum")
    private Long sum;

    @Column(name = "comment_for_operation")
    private String comment;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "operations_applied_settings",
            joinColumns = @JoinColumn(name = "operation_id"),
            inverseJoinColumns = @JoinColumn(name = "setting_id"))
    private Set<SettingEntity> settings = new HashSet<SettingEntity>();

}
