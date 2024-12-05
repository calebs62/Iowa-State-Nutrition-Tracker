package coms309;

import coms309.entity.Menu;
import coms309.repository.MenuRepository;

// Import Java libraries
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

// import junit/spring tests
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.http.MediaType;

// import mockito related
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@WebMvcTest({Menu.class})
public class ShepherdSystemTest {
    @Autowired
    private MockMvc controller;

    @MockBean
    MenuRepository menuRepo;

//    @Test
//    public void getMenuByIdTest() throws Exception{
//        Menu menu = new Menu("menu1", "UDD", "din", "2024-12-04");
//        menu.setId(5);
//
//        when(menuRepo.findById(menu.getId())).thenReturn(Optional.of(menu));
//
//        controller.perform(get("/menu/" + menu.getId()))
////                .andExpect(status().isOk());
//                .andExpect(((Menu) content()).getId());
//    }
//
//    @Test
//    public void createMenuTest() throws Exception{
//        List<Menu> list = new ArrayList<Menu>();
//        when(menuRepo.findAll()).thenReturn(list);
//        when(menuRepo.save((Menu) any(Menu.class)))
//                .thenAnswer(x -> {
//                    Menu m = x.getArgument(1);
//                    list.add(m);
//                    return null;
//                });
//
//        controller.perform(post("/menu").contentType(MediaType.APPLICATION_JSON)
//                .content("{\n" +
//                        "    \"name\": \"Test1\",\n" +
//                        "    \"location\": \"UDCC\",\n" +
//                        "    \"meal\": \"Dinner\",\n" +
//                        "    \"date\": \"2024-10-18\",\n" +
//                        "    \"foodItems\": []\n" +
//                        "}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].data", isA(Menu.class)));
//    }
}
