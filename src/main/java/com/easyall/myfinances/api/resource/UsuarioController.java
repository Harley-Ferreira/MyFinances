package com.easyall.myfinances.api.resource;


import com.easyall.myfinances.api.dto.UsuarioDTO;
import com.easyall.myfinances.exception.ErroAutenticacao;
import com.easyall.myfinances.exception.RegraNegocioException;
import com.easyall.myfinances.model.entity.Usuario;
import com.easyall.myfinances.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Estamos usando essa annotition @RestController porque estamos trabalhando com arquitetura rest
// @RestController é a associação entre a annotition @Controller (esteriotico padrão) e @ ReponseBody.
// @RequestMapping("/api/usuarios") todas as requisições que começar com essa url passada, vão entrar nesse controller
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private UsuarioService usuarioService; // Não precisa do @Autowired porque o @RestController faz  isso

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/autenticar")
    public ResponseEntity autenticar(@RequestBody UsuarioDTO dto) {
       try {
           Usuario usuarioAutententicado = usuarioService.autenticar(dto.getEmail(), dto.getSenha());
           return ResponseEntity.ok(usuarioAutententicado);
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
}
