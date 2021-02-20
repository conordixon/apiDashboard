
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.*;
import org.junit.Test;




public class TestAPICall {

    @Test
    public void TestAPICall(){
                given().
                contentType("text/json").
                when().
                get("http://www.google.com").
                then().
                statusCode(200);
    }
}