package coms309;

import coms309.entity.Menu;
import coms309.repository.MenuRepository;
import io.restassured.RestAssured;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class ShepherdSystemTest {
    @LocalServerPort
    int port;

    @Autowired
    private MockMvc controller;

    @MockBean
    MenuRepository menuRepo;

    @Before
    public void setUp(){
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhsot";
    }

    @Test
    public void getMenuByIdTest() {
        when(menuRepo.findById(1)).thenReturn(Optional.of(new Menu("name", "location", "dinner", "date")));

        controller.perform(post())
    }
}
