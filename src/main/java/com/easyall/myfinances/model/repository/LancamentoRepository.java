package com.easyall.myfinances.model.repository;

import com.easyall.myfinances.model.entity.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
}
