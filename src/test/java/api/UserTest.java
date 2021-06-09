package api;

import com.ivanwidyan.module.api.request.CreateUserRequest;
import com.ivanwidyan.module.api.response.CreateUserResponse;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UserTest {

    @Test
    @Parameters({"id"})
    public void getUser(String id) {
        Response response = given().
                header("Accept", "application/json").
                when().
                get("https://reqres.in/api/users/" + id);

        response.then().log().everything();

        // assert status code
        assertThat(response.statusCode(), equalTo(200));
        response.then().statusCode(200);
    }

    @DataProvider(name = "CreateUserDataProvider")
    public Object[][] createUserData() {
        Object[][] data = {{"ivan", "qa"}};
        return data;
    }

    @Test(dataProvider = "CreateUserDataProvider")
    public void createUser(String name, String job) {
        CreateUserRequest request = new CreateUserRequest();
        request.setName(name);
        request.setJob(job);
        Response response = given().
                header("Content-Type", "application/json").
                header("Accept", "application/json").
                body(request).
                when().
                post("https://reqres.in/api/users");

        response.then().log().everything();

        // assert status code
        assertThat(response.statusCode(), equalTo(201));
        response.then().statusCode(201);

        // assert response param
        CreateUserResponse createUserResponse = response.
                getBody().as(CreateUserResponse.class);
        assertThat(createUserResponse.getName(), equalTo(name));
        assertThat(createUserResponse.getJob(), equalTo(job));

        response.
                then().
                body("name", equalTo(name)).
                body("job", equalTo(job));
    }
}
