package co.medily;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DeviceApiIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void listaDispositivosEnJson() throws Exception {
        mockMvc.perform(get("/api/devices").param("brand", "Apple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("iPhone 15 Pro")));
    }

    @Test
    void crudCompletoProtegidoPorSesion() throws Exception {
        MvcResult login = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"identity\":\"admin\",\"password\":\"Medily2026*\"}"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("medily_session"))
                .andReturn();

        String payload = "{" +
                "\"name\":\"Producto de prueba\",\"brand\":\"Medily\"," +
                "\"price\":999000,\"image\":\"https://example.com/device.jpg\"," +
                "\"description\":\"Registro creado desde prueba de integracion\"," +
                "\"releaseDate\":\"2026-01-01\",\"category\":\"Gama media\"," +
                "\"features\":{\"storage\":\"256 GB\"}}";

        MvcResult created = mockMvc.perform(post("/api/devices")
                        .cookie(login.getResponse().getCookie("medily_session"))
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Producto de prueba")))
                .andReturn();

        String id = com.jayway.jsonpath.JsonPath.read(created.getResponse().getContentAsString(), "$.id").toString();
        String updated = payload.replace("Producto de prueba", "Producto actualizado");
        mockMvc.perform(put("/api/devices/" + id)
                        .cookie(login.getResponse().getCookie("medily_session"))
                        .contentType("application/json")
                        .content(updated))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Producto actualizado")));

        mockMvc.perform(delete("/api/devices/" + id)
                        .cookie(login.getResponse().getCookie("medily_session")))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/devices/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Dispositivo no encontrado")));
    }

    @Test
    void protegeLasOperacionesDeEscritura() throws Exception {
        mockMvc.perform(post("/api/devices")
                        .contentType("application/json")
                        .content("{\"name\":\"Prueba\",\"brand\":\"Medily\",\"price\":1,\"category\":\"Gama media\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)));
    }

    @Test
    void controlaLaCaidaDeLaApiExterna() throws Exception {
        mockMvc.perform(get("/api/external/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fallback", is(true)))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }
}
