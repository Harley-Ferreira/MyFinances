package com.easyall.myfinances.api.resource;

import com.easyall.myfinances.api.dto.UsuarioDTO;
import com.easyall.myfinances.exception.ErroAutenticacao;
import com.easyall.myfinances.exception.RegraNegocioException;
import com.easyall.myfinances.model.entity.Lancamento;
import com.easyall.myfinances.model.entity.Usuario;
import com.easyall.myfinances.service.LancamentoService;
import com.easyall.myfinances.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc
public class UsuarioControllerTest {

    static final String API = "/api/usuarios";

    @Autowired
    MockMvc mvc;

    @MockBean
    UsuarioService service;

    @MockBean
    LancamentoService lancamentoService;

    @Test
    public void deveAutenticarUmUsuario() throws Exception {
        String email = "usuario@email.com";
        String senha = "123";

        //cenario
        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
        Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();
        Mockito.when(service.autenticar(email, senha)).thenReturn(usuario);
        String json = new ObjectMapper().writeValueAsString(dto);

        //execução e verificação
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/autenticar"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
    }

    @Test
    public void deveRetornarBadRequestAoObterErroDeAutenticacao() throws Exception {
        String email = "usuario@email.com";
        String senha = "123";

        //cenario
        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();

        Mockito.when(service.autenticar(email, senha)).thenThrow(ErroAutenticacao.class);

        String json = new ObjectMapper().writeValueAsString(dto);

        //execução e verificação
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/autenticar"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void deveCriarUmNovoUsuario() throws Exception {
        String email = "usuario@email.com";
        String senha = "123";

        //cenario
        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
        Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();

        Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);
        String json = new ObjectMapper().writeValueAsString(dto);

        //execução e verificação
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
    }

    @Test
    public void deveRetornarBadRequestAoTentarCriarUmUsuarioInvalido() throws Exception {
        String email = "usuario@email.com";
        String senha = "123";

        //cenario
        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();

        Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);
        String json = new ObjectMapper().writeValueAsString(dto);

        //execução e verificação
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}
