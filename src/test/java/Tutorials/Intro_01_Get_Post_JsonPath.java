package Tutorials;

import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import io.restassured.path.json.JsonPath;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

public class Intro_01_Get_Post_JsonPath {

    static {
        RestAssured.baseURI = "https://reqres.in";
    }

    @Test
    public void getRequest() {

        String getResponse = RestAssured
            .given()
//                .log()
//                .all()
                .header("Content-Type", "application/json; charset=utf-8")
                .queryParam("page", "2")
            .when()
                .get("/api/users")
            .then()
//                .log()
//                .all()
            .assertThat()
                .statusCode(200)
                .body("page", Matchers.equalTo(2))
                .body("data[0].email", Matchers.equalTo("michael.lawson@reqres.in"))
            .extract()
                .body()
                .asString();

        System.out.println("getResponse --> " + getResponse);
    }

    @Test
    public void postRequest() {

        String postResponse = RestAssured
            .given()
//                .log()
//                .all()
                .queryParam("", "")
                .header("Content-Type", "application/json; charset=utf-8")
                .body("""
                        {
                            "name": "Sachin Kn",
                            "job": "Developer"
                        }
                """)
            .when()
                .post("/api/users")
            .then()
//                .log()
//                .all()
            .assertThat()
                .statusCode(201)
                .body("name", Matchers.equalTo("Sachin Kn"))
                .body("job", Matchers.equalTo("Developer"))
            .extract()
                .asString();

        System.out.println("postResponse --> " + postResponse);
    }

    @Test
    public void jsonPathTest() {

        JsonPath jsonPath = new JsonPath("""
            {
                "dashboard": {
                    "purchaseAmount": 910,
                    "website": "sachinkn.in"
                },
                "courses": [
                    {
                        "title": "Selenium Java",
                        "price": 50,
                        "copies": 6
                    },
                    {
                        "title": "Cypress",
                        "price": 40,
                        "copies": 4
                    },
                    {
                        "title": "Playwright",
                        "price": 45,
                        "copies": 10
                    }
                ]
            }
        """);
        System.out.println("1. Print No of courses returned by API\n>> " + jsonPath.getInt("courses.size()"));
        System.out.println("\n2. Print Purchase Amount\n>> " + jsonPath.getInt("dashboard.purchaseAmount"));
        System.out.println("\n3. Print Title of the first course\n>> " + jsonPath.getString("courses[0].title"));
        System.out.println("\n4. Print All course titles and their respective Prices\n>> " + jsonPath.getString("courses[0].title"));

        System.out.println("\n5. Print no of copies sold by Playwright Course");
        for (int i=0; i<jsonPath.getInt("courses.size()"); i++) {
            if (jsonPath.getString("courses[" + i + "].title").equals("Playwright")) {
                System.out.println(">> " + jsonPath.getInt("courses[" + i + "].price"));
                break;
            }
        }

        System.out.println("\n6. Verify if Sum of all Course prices matches with Purchase Amount");
        int totalSum = 0;
        for (int i=0; i<jsonPath.getInt("courses.size()"); i++) {
            totalSum += jsonPath.getInt("courses[" + i + "].price") * jsonPath.getInt("courses[" + i + "].copies");
        }
        System.out.println(">> purchaseAmount -> " + jsonPath.getInt("dashboard.purchaseAmount"));
        System.out.println(">> totalSum -> " + totalSum);

    }

    @Test
    public void dynamicPathParameterRequest() {

        String postResponse = RestAssured
            .given()
                .pathParam("path1", "api") // Set the path Params
                .header("Content-Type", "application/json; charset=utf-8")
                .queryParam("page", "2")
                .body("")
            .when()
                .get("/{path1}/users")  // Get the path Params
            .then()
                .extract()
                .body()
                .asString();

        System.out.println("postResponse --> " + postResponse);
    }

    @Test(enabled = false) // This test is just for example
    public void sessionFilterExample() {

        SessionFilter session = new SessionFilter();

        RestAssured
            .given()
                .header("Content-Type", "application/json; charset=utf-8")
                .queryParam("page", "2")
                .body("""
                        {
                            "username": "SachinKn",
                            "password": "Sachin@123"
                        }
                        """)
                .filter(session)  // This will store the session cookie after login
            .when()
                .post("/api/users")
            .then()
                .log()
                .status();

        RestAssured
            .given()
                .header("Content-Type", "application/json; charset=utf-8")
                .queryParam("page", "2")
                .filter(session)  // Here it will use the same session and treated as login user.
            .when()
                .get("/api/users")
            .then()
                .log()
                .status();
    }

    @Test(enabled = false) // This test is just for example
    public void allTheMethods() {

        SessionFilter session = new SessionFilter();

        RestAssured
            .given()
                .header("Content-Type", "application/json; charset=utf-8")
                .pathParam("path1", "api")
                .queryParam("page", "2")
                .formParam("", "")
                .body("{ \"name\": \"Sachin\" }")
                .filter(session)
                .log()
                .all()
            .when()
                .post("/api/users")
            .then()
                .log()
                .all()
                .assertThat()
                    .statusCode(200)
                    .body("page", Matchers.equalTo(2))
                .extract()
                    .body()
                    .asString();

    }

}