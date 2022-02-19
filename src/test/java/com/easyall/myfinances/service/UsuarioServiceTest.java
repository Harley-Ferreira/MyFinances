package com.easyall.myfinances.service;

import com.easyall.myfinances.exception.ErroAutenticacao;
import com.easyall.myfinances.exception.RegraNegocioException;
import com.easyall.myfinances.model.entity.Usuario;
import com.easyall.myfinances.model.repository.UsuarioRepository;
import com.easyall.myfinances.service.impl.UsuarioServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @SpyBean
    UsuarioServiceImpl service;

    @MockBean
    UsuarioRepository repository;


    @Test(expected = Test.None.class)
    public void deveSalvarUmUsuario(){
        //cenário
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuario = Usuario.builder().id(1l).nome("nome").email("usuario@email.com").senha("senha").build();
        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

        //acao
        Usuario usuarioSalvo = service.salvarUsuario(new Usuario());

        //verificacação
        Assertions.assertThat(usuarioSalvo).isNotNull();
        Assertions.assertThat(usuarioSalvo.getId().equals(1l));
        Assertions.assertThat(usuarioSalvo.getNome().equals("nome"));
        Assertions.assertThat(usuarioSalvo.getEmail().equals("usuario@email.com"));
        Assertions.assertThat(usuarioSalvo.getSenha().equals("senha"));
    }

    @Test(expected = RegraNegocioException.class)
    public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
        //cenário
        String email = "usuario@email.com";
        Usuario usuario = Usuario.builder().email("usuario@email.com").build();
        Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);

        //acao
        service.salvarUsuario(usuario);

        //verificação
        Mockito.verify(repository, Mockito.never()).save(usuario);


    }

    @Test(expected = Test.None.class)
    public void deveAutenticarUmUsuarioComSucesso() {
        String email = "usuario@email.com";
        String senha = "senha";

        Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

        //acao
        Usuario result = service.autenticar(email, senha);

        //verificacao
        Assertions.assertThat(result).isNotNull();


    }

    @Test
    public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado(){

        //cenário
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        //acao
        Throwable exception = Assertions.catchThrowable(() -> service.autenticar("usuario@email.com", "senha"));

        //Verificação
        Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuário não encontrado para o email informado");
    }

    @Test
    public void deveLancarErroQuandoSenhaNaoBater() {
        //cenario
        String senha = "senha";
        Usuario usuario = Usuario.builder().email("usuario@email.com").senha(senha).build();
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

        //acao
        Throwable exception = Assertions.catchThrowable(() -> service.autenticar("usuario@email.com", "123"));
        Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inválida");
    }

    @Test(expected = Test.None.class)
    public void deveValidarEmail() {
        //cenario
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

        //acao
        service.validarEmail("usuario@email.com");

    }

    @Test(expected = RegraNegocioException.class)
    public void deveLancarErroAoValidarQuandoExistirEmailCadstrado() {
        //cenário
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

        //acao
        service.validarEmail("usuario@email.com");
    }


}
