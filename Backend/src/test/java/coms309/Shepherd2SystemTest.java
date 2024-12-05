package coms309;

import static org.junit.jupiter.api.Assertions.assertEquals;

import coms309.entity.Menu;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.web.server.LocalServerPort;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class Shepherd2SystemTest {
    @LocalServerPort
    int port;

    @Before
    public void setUp(){
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void createMenuTest(){
        Response response = RestAssured.given().header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .body("{\n" +
                        "    \"name\": \"Test1\",\n" +
                        "    \"location\": \"Bakery3\",\n" +
                        "    \"meal\": \"Lunch\",\n" +
                        "    \"date\": \"2024-10-18\",\n" +
                        "    \"foodItems\": []\n" +
                        "}").when().post("/menu");

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        try{
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("Test1", returnObj.get("name"));
            assertEquals("Lunch", returnObj.get("meal"));
            assertEquals("2024-10-18", returnObj.get("date"));
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Test
    public void updateMenu(){
//        Response
    }
}
