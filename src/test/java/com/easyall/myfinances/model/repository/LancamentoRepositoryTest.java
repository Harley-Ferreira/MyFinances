package com.easyall.myfinances.model.repository;

import com.easyall.myfinances.model.entity.Lancamento;
import com.easyall.myfinances.model.enums.StatusLancamento;
import com.easyall.myfinances.model.enums.TipoLancamento;
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

    @Autowired
    LancamentoRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void deveSalvarUmLancamento() {
        Lancamento lancamento = criarLancamento();

        lancamento = repository.save(lancamento);

        assertThat(lancamento.getId()).isNotNull();
    }

    @Test
    public void deveDeletarUmLancamento() {
        Lancamento lancamento = criarEPersistirUmLancamento();

        repository.delete(lancamento);

        Lancamento lancamentoExistente = entityManager.find(Lancamento.class, lancamento.getId());
        assertThat(lancamentoExistente).isNull();
    }


    @Test
    public void deveAtualizarUmLancamento() {
        Lancamento lancamento = criarEPersistirUmLancamento();

        lancamento.setAno(2018);
        lancamento.setDescricao("Teste Atualizar");
        lancamento.setStatus(StatusLancamento.CANCELADO);

        repository.save(lancamento);

        Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());

        assertThat(lancamentoAtualizado.getAno()).isEqualTo(2018);
        assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Teste Atualizar");
        assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);

    }

    @Test
    public void deveBuscarUmLancamentoPorId() {
        Lancamento lancamento = criarEPersistirUmLancamento();

        Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());

        assertThat(lancamentoEncontrado.isPresent()).isTrue();
    }

    private Lancamento criarEPersistirUmLancamento() {
        Lancamento lancamento = criarLancamento();
        entityManager.persist(lancamento);
        return lancamento;
    }

    public static Lancamento criarLancamento() {
        return Lancamento.builder().ano(2019).mes(1).descricao("qualquer um")
                .valor(BigDecimal.valueOf(10)).tipo(TipoLancamento.RECEITA).status(StatusLancamento.PENDENTE).
                dataCadastro(LocalDate.now()).build();
    }
}
