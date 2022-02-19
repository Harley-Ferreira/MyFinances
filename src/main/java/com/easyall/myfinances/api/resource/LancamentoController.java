package com.easyall.myfinances.api.resource;

import com.easyall.myfinances.api.dto.AtualizaStatusDTO;
import com.easyall.myfinances.api.dto.LancamentoDTO;
import com.easyall.myfinances.exception.RegraNegocioException;
import com.easyall.myfinances.model.entity.Lancamento;
import com.easyall.myfinances.model.entity.Usuario;
import com.easyall.myfinances.model.enums.StatusLancamento;
import com.easyall.myfinances.model.enums.TipoLancamento;
import com.easyall.myfinances.service.LancamentoService;
import com.easyall.myfinances.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {

    // Não há necessidade de autowired pois o proprio spring injeta a instancia. desde que adicione no construtor.
    // Aqui não tem construtor porque usamos o @RequiredArgsConstructor e declaramos como final os services. (BEM MAIS PRATICO NÉ)
    private final LancamentoService lancamentoService;
    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity buscar(
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam(value = "usuario") Long idUsuario) {

        //Obs: Caso não queira passar varios RequestParam, basta usar um map. (Utilizei assim para fins didaticos)
        //Exemplo: @RequestParam Map<String, String> params -> Seria um mapa com o valor e a variavel
        //Obs: quando se usa mapa todos os parâmetros se tornam opcionais

        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);

        Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
        if (!usuario.isPresent())
            return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado para o id informado.");
        else
            lancamentoFiltro.setUsuario(usuario.get());

        List<Lancamento> lancamentos = lancamentoService.buscar(lancamentoFiltro);
        return ResponseEntity.ok(lancamentos);
    }

    @PostMapping //Post e para criar um recurso no servidor que ainda não foi criado
    public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
        try {
            Lancamento lancamento = converter(dto);
            lancamento = lancamentoService.salvar(lancamento);
//            return ResponseEntity.ok(entidade); //Igual a linha de baixo
            return new ResponseEntity(lancamento, HttpStatus.CREATED);

        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}") //Put e para atualizar um recurso que está no servido
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
        return lancamentoService.obterPorId(id).map(entity -> {
            try {
                Lancamento lancamento = converter(dto);
                lancamento.setId(entity.getId());
                lancamentoService.atualizar(lancamento);
                return ResponseEntity.ok(lancamento);
            } catch (RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(() ->
                new ResponseEntity("Lançamento não encontrado na base de Dados.", HttpStatus.BAD_REQUEST));
    }

    @PutMapping("{id}/atualiza-status")
    public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto) {
        return lancamentoService.obterPorId(id).map(entity -> {
            StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
            if (statusSelecionado == null)
                return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamento, envie um status válido");
            try {
                entity.setStatus(statusSelecionado);
                lancamentoService.atualizar(entity);
                return ResponseEntity.ok(entity);
            } catch (RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(() ->
                new ResponseEntity("Lancamento não encontrado na base de Dados.", HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("{id}")
    public ResponseEntity deletar(@PathVariable("id") Long id) {
        return lancamentoService.obterPorId(id).map(entity -> {
            lancamentoService.deletar(entity);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet(() ->
                new ResponseEntity("Lancamento não encontrado na base de Dados.", HttpStatus.BAD_REQUEST));
    }


    private Lancamento converter(LancamentoDTO dto) {
        Lancamento lancamento = new Lancamento();
        lancamento.setId(dto.getId());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());

        Usuario usuario = usuarioService.obterPorId(dto.getUsuario())
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o id informado."));

        lancamento.setUsuario(usuario);

        if (dto.getTipo() != null)
            lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));

        if (dto.getStatus() != null)
            lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));

        return lancamento;
    }
}