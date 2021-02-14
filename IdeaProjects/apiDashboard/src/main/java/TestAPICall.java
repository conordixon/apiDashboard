
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.*;



public class TestAPICall {

    public void TestAPICall(){
        given().
                given().
                contentType("text/json").
                when().
                get("https://swapi.dev/api/people/1").
                then().
                statusCode(401);
    }
}