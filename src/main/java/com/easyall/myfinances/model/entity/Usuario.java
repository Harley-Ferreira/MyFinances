package com.easyall.myfinances.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "usuario", schema = "finances")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome")
    @Setter
    private String nome;

    @Column(name = "email")
    private String email;

    @Column(name = "senha")
    @JsonIgnore
    private String senha;

}
