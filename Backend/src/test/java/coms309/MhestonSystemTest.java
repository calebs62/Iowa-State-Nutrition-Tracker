package coms309;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class MhestonSystemTest {
    @LocalServerPort
    int port;

    @Before
    public void setUp(){
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    public static int id = 0;

    @Test
    public void runFoodItemTests() {
        createFoodItemTest();
        updateFoodItemTest();
        getFoodItemTest();
        searchFoodItemTest();
        deleteFoodItemTest();
    }



    private void createFoodItemTest(){
        String body = "{\n" +
                "    \"name\": \"FoodItemTest\",\n" +
                "    \"calories\": 450,\n" +
                "    \"totalFat\": 23,\n" +
                "    \"sodium\": 370,\n" +
                "    \"carbohydrate\": 57,\n" +
                "    \"protein\": 5,\n" +
                "    \"servingsize\": \"1 Item\",\n" +
                "    \"description\": \"A test item\"\n" +
                "}";
        Response response = RestAssured.given().header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .body(body).when().post("/item");

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        try{
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("FoodItemTest", returnObj.get("name"));
            id =  (Integer)returnObj.get("id");
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void updateFoodItemTest(){
        String body = "{\n" +
                "    \"name\": \"FoodItemTestUpdate\",\n" +
                "    \"calories\": 47,\n" +
                "    \"totalFat\": 24,\n" +
                "    \"sodium\": 375,\n" +
                "    \"carbohydrate\": 60,\n" +
                "    \"protein\": 1,\n" +
                "    \"servingsize\": \"2 Item\",\n" +
                "    \"description\": \"A test item updated\"\n" +
                "}";

        Response response = RestAssured.given().header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .body(body).when().put("/item/update/" + id);

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        assertTrue(returnString.contains("FoodItemTestUpdate"));
    }

    private void searchFoodItemTest(){
        String body ="{\n" +
                "    \"name\": \"FoodItemTestUpdate\",\n" +
                "    \"calories\": \"47\",\n" +
                "    \"caloriescomp\": \"==\",\n" +
                "    \"carbohydrate\": \"45\",\n" +
                "    \"carbohydratecomp\": \">\",\n" +
                "    \"description\": \"A test item updated\",\n" +
                "    \"sodium\": \"0\",\n" +
                "    \"sodiumcomp\": \">\",\n" +
                "    \"totalfat\": \"0\",\n" +
                "    \"totalfatcomp\": \">\",\n" +
                "    \"protein\": \"7\",\n" +
                "    \"proteincomp\": \"<\"\n" +
                "}";
        System.out.println(body);
        Response response = RestAssured.given().header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .body(body).when().put("/item");

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();

        assertTrue(returnString.contains("FoodItemTestUpdate"));
    }

    private void getFoodItemTest(){

        Response response = RestAssured.given().header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .when().get("/item/" + id);

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();

        try{
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("FoodItemTestUpdate", returnObj.get("name"));
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void deleteFoodItemTest(){
        Response response = RestAssured.given().header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .when().delete("/item/" + id);

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        try{
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("FoodItemTestUpdate", returnObj.get("name"));
        } catch (JSONException e){
            e.printStackTrace();
        }
    }


}
