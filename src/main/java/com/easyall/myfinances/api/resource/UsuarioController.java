package com.easyall.myfinances.api.resource;


import com.easyall.myfinances.api.dto.TokenTDO;
import com.easyall.myfinances.api.dto.UsuarioDTO;
import com.easyall.myfinances.exception.ErroAutenticacao;
import com.easyall.myfinances.exception.RegraNegocioException;
import com.easyall.myfinances.model.entity.Usuario;
import com.easyall.myfinances.service.JwtService;
import com.easyall.myfinances.service.LancamentoService;
import com.easyall.myfinances.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

// Estamos usando essa annotition @RestController porque estamos trabalhando com arquitetura rest
// @RestController é a associação entre a annotition @Controller (esteriotico padrão) e @ ReponseBody.
// @RequestMapping("/api/usuarios") todas as requisições que começar com essa url passada, vão entrar nesse controller
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    // Não há necessidade de autowired pois o proprio spring injeta a instancia. desde que adicione no construtor.
    // Aqui não tem construtor porque usamos o @RequiredArgsConstructor e declaramos como final os services. (BEM MAIS PRATICO NÉ)
    private final UsuarioService usuarioService;
    private final LancamentoService lancamentoService;
    private final JwtService jwtService;

    @PostMapping("/autenticar")
    public ResponseEntity<?> autenticar(@RequestBody UsuarioDTO dto) {
       try {
           Usuario usuarioAutententicado = usuarioService.autenticar(dto.getEmail(), dto.getSenha());
           String token = jwtService.gerarToken(usuarioAutententicado);
           TokenTDO tokenDTO = new TokenTDO(usuarioAutententicado.getNome(), token);
           return ResponseEntity.ok(tokenDTO );
       } catch (ErroAutenticacao e) {
           return ResponseEntity.badRequest().body(e.getMessage());
       }
    }

    @PostMapping //Tipo da requisição que vai ser feita
    public ResponseEntity salvar(@RequestBody UsuarioDTO dto) {
        Usuario usuario = Usuario.builder().nome(dto.getNome()).email(dto.getEmail()).senha(dto.getSenha()).build();

        try {
            Usuario usuariosalvo = usuarioService.salvarUsuario(usuario);
            return new ResponseEntity(usuariosalvo, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("{id}/saldo")
    public ResponseEntity obterSaldo(@PathVariable("id") Long id) {
        Optional<Usuario> usuario = usuarioService.obterPorId(id);

        if (!usuario.isPresent())
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
        return ResponseEntity.ok(saldo);
    }
}
