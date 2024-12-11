package coms309;

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
public class ShepherdSystemTest {
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
    public void readMenuTest(){
        Response response = RestAssured.given().get("/menu/34");

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
    public void updateMenuTest(){
        Response response = RestAssured.given().header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .body("{\n" +
                        "    \"name\": \"Test2\",\n" +
                        "    \"location\": \"Bakery6\",\n" +
                        "    \"meal\": \"Dinner\",\n" +
                        "    \"date\": \"2024-10-10\",\n" +
                        "    \"foodItems\": []\n" +
                        "}").when().put("/menu/update/33");

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        try{
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("Test2", returnObj.get("name"));
            assertEquals("Dinner", returnObj.get("meal"));
            assertEquals("2024-10-10", returnObj.get("date"));
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Test
    public void deleteMenuTest(){
        Response createResponse = RestAssured.given().header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .body("{\n" +
                        "    \"name\": \"Test1\",\n" +
                        "    \"location\": \"Bakery3\",\n" +
                        "    \"meal\": \"Lunch\",\n" +
                        "    \"date\": \"2024-10-18\",\n" +
                        "    \"foodItems\": []\n" +
                        "}").when().post("/menu");

        assertEquals(200, createResponse.getStatusCode(), "Create failed");
        try {
            JSONObject createObject = new JSONObject(createResponse.getBody().asString());

            Response response = RestAssured.given().delete("/menu/"+ createObject.get("id"));
            assertEquals(200, response.getStatusCode());

            String returnString = response.getBody().asString();
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals(createObject.get("id"), returnObj.get("id"), "Create and Delete have different IDs");
            assertEquals("Test1", returnObj.get("name"));
            assertEquals("Lunch", returnObj.get("meal"));
            assertEquals("2024-10-18", returnObj.get("date"));

        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Test
    public void createFoodPlanTest(){
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .body("{\n" +
                        "    \"name\": \"restTest\",\n" +
                        "    \"calories\": 300,\n" +
                        "    \"totalFat\": 400,\n" +
                        "    \"sodium\": 500,\n" +
                        "    \"carbohydrate\" : 600,\n" +
                        "    \"protein\": 700\n" +
                        "}").when().post("/plan");

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        try{
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("restTest", returnObj.get("name"));
            assertEquals(400, returnObj.get("totalFat"));
            assertEquals(700, returnObj.get("protein"));
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Test
    public void deleteFoodPlanTest(){
        Response createResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .body("{\n" +
                        "    \"name\": \"restTest\",\n" +
                        "    \"calories\": 300,\n" +
                        "    \"totalFat\": 400,\n" +
                        "    \"sodium\": 500,\n" +
                        "    \"carbohydrate\" : 600,\n" +
                        "    \"protein\": 700\n" +
                        "}").when().post("/plan");

        assertEquals(200, createResponse.getStatusCode(), "Create failed");
        try {
            JSONObject createObject = new JSONObject(createResponse.getBody().asString());

            Response response = RestAssured.given().delete("/plan/"+ createObject.get("id"));
            Response badResponse = RestAssured.given().delete("/plan/" + -1);

            assertEquals(200, badResponse.getStatusCode(), "Bad response");

            String badReturn = response.getBody().asString();
            JSONObject badObj = new JSONObject(badReturn);
            assertEquals(500, badObj.get("status"), "Bad delete request");

            String returnString = response.getBody().asString();
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals(createObject.get("id"), returnObj.get("id"), "Create and Delete have different IDs");
            assertEquals("restTest", returnObj.get("name"));
            assertEquals(400, returnObj.get("totalFat"));
            assertEquals(702, returnObj.get("protein"));

        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Test
    public void planUpdateTest() {
        Response createResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .body("{\n" +
                        "    \"name\": \"restTest\",\n" +
                        "    \"calories\": 300,\n" +
                        "    \"totalFat\": 400,\n" +
                        "    \"sodium\": 500,\n" +
                        "    \"carbohydrate\" : 600,\n" +
                        "    \"protein\": 700\n" +
                        "}").when().post("/plan");

        assertEquals(200, createResponse.getStatusCode(), "Create failed");
        try {
            JSONObject createObject = new JSONObject(createResponse.getBody().asString());

            Response preUpdate = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .header("charset", "utf-8")
                    .body("{\n" +
                            "    \"name\": \"newName\",\n" +
                            "    \"calories\": 888,\n" +
                            "    \"totalFat\": 222,\n" +
                            "    \"sodium\": 111,\n" +
                            "    \"carbohydrate\" : 777,\n" +
                            "    \"protein\": 666\n" +
                            "}").when().put("/plan/update/"+createObject.get("id"));

            assertEquals(200, preUpdate.getStatusCode(), "Update failed");

            Response response = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .header("charset", "utf-8")
                    .body("{\n" +
                            "    \"test\": \"Empty\"\n" +
                            "}").when().put("/plan/update/"+createObject.get("id"));

            assertEquals(200, response.getStatusCode(), "Update failed");

            String returnString = response.getBody().asString();

            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("newName", returnObj.get("name"));
            assertEquals(888, returnObj.get("calories"));
            assertEquals(777, returnObj.get("carbohydrate"));

            Response deleteResponse = RestAssured.given().delete("/plan/"+ createObject.get("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getPlanTest(){
        Response response = RestAssured.given().get("/plan/21");

        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        try{
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("restTest", returnObj.get("name"));
            assertEquals(600, returnObj.get("carbohydrate"));
            assertEquals(700, returnObj.get("protein"));
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Test
    public void createDeleteGroupTest(){
        Response createResponse = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("charset", "utf-8")
                .body("{\n" +
                        "    \"groupName\": \"restTest\",\n" +
                        "    \"ownerId\": 1,\n" +
                        "    \"planId\": 3\n" +
                        "}").when().post("/group");

        assertEquals(200, createResponse.getStatusCode(), "Create failed");
        try {
            JSONObject createObject = new JSONObject(createResponse.getBody().asString());

            Response response = RestAssured.given()
                    .param("sessionToken", "1:2:1")
                    .delete("/group/"+ createObject.get("id"));
            Response badResponse = RestAssured.given()
                    .param("sessionToken", "1:2:1")
                    .delete("/group/" + -1);

            assertEquals(200, badResponse.getStatusCode(), "Bad response");

            String badReturn = response.getBody().asString();
            JSONObject badObj = new JSONObject(badReturn);
            assertEquals(500, badObj.get("status"), "Bad delete request");

            String returnString = response.getBody().asString();
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals(createObject.get("id"), returnObj.get("id"), "Create and Delete have different IDs");
            assertEquals("restTest", returnObj.get("name"));
            assertEquals(3, returnObj.get("plan.id"));
            assertEquals(1, returnObj.get("ownerId"));

        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Test
    public void getGroupTest(){
        Response getGroupResponse = RestAssured.given().get("/group/1");
        assertEquals(200, getGroupResponse.getStatusCode());
        String getGroupStr = getGroupResponse.getBody().asString();
        Response getOwnerResponse = RestAssured.given().get("/group/1/getOwner");
        assertEquals(200, getOwnerResponse.getStatusCode());
        String getOwnerStr = getOwnerResponse.getBody().asString();
        Response getPlanResponse = RestAssured.given().get("/group/1/getPlan");
        assertEquals(200, getPlanResponse.getStatusCode());
        String getPlanStr = getPlanResponse.getBody().asString();

        try{
            JSONObject getGroupObj = new JSONObject(getGroupStr);
            assertEquals("Lose_Weight", getGroupObj.get("groupName"));
            assertEquals(56, getGroupObj.get("ownerId"));
            assertEquals(1, getGroupObj.getJSONObject("plan").get("id"));

            JSONObject getOwnerObj = new JSONObject(getOwnerStr);
            assertEquals(56, getOwnerObj.get("uid"));
            assertEquals("John", getOwnerObj.get("fname"));
            assertEquals("CONTRIBUTOR", getOwnerObj.get("accounttype"));

            JSONObject getPlanObj = new JSONObject(getPlanStr);
            assertEquals("Lose_Weight", getPlanObj.get("name"));
            assertEquals(500, getPlanObj.get("sodium"));
            assertEquals(400, getPlanObj.get("totalFat"));


        } catch (JSONException e){
            e.printStackTrace();
        }
    }

}
