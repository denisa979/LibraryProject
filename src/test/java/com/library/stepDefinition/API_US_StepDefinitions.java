package com.library.stepDefinition;

import com.github.javafaker.Faker;
import com.library.utilities.Driver;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Assert;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;

public class API_US_StepDefinitions {

    String userId;
    String username;
    // String user;
    String token;
    String expires;
    Map<String,String> user;

    @Given("A Test user is created")
    public void a_test_user_is_created() {

        Faker faker = new Faker();
        username = faker.name().username();

//        user = "{\n" +
//                "  \"userName\": \""+username+"\",\n" +
//                "  \"password\": \"Test1234!\"\n" +
//                "}";
        user = new HashMap<>();
        user.put("userName",username);
        user.put("password","Test1234!");
        Response response = RestAssured.given().accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(user)
                .when().post("https://demoqa.com/Account/v1/User");
        userId = response.jsonPath().getString("userID");
        System.out.println("user = " + user);
        System.out.println("userId = " + userId);

    }
    @And("A token is generated for the Test user")
    public void a_token_is_generated_for_the_test_user() {
        Response response = RestAssured.given().accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(user)
                .when().post("https://demoqa.com/Account/v1/GenerateToken");
        token = response.jsonPath().getString("token");
        expires = response.jsonPath().getString("expires");
        System.out.println("token = " + token);
        System.out.println("expires = " + expires);


    }
    @When("A book is added to Test user profile")
    public void a_book_is_added_to_test_user_profile() {

        String addBook = "{\n" +
                "  \"userId\": \""+userId+"\",\n" +
                "  \"collectionOfIsbns\": [\n" +
                "    {\n" +
                "      \"isbn\": \"9781449331818\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        String bearerToken = "Bearer "+token;
        Response response = RestAssured.given().accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .headers("Authorization",bearerToken)
                .body(addBook)
                .when().post("https://demoqa.com/BookStore/v1/Books");
        Assert.assertEquals(201,response.statusCode());
        response.prettyPrint();
    }
    @And("At frontend-UI page of the application user and profile can be verified")
    public void at_frontend_ui_page_of_the_application_user_and_profile_can_be_verified()throws InterruptedException {

        WebDriver driver = Driver.getDriver();
        // provide cookies to login with API request
        Map<String,String> cookies = new HashMap<>();
        cookies.put("userID",userId);
        cookies.put("username",username);
        cookies.put("token",token);
        cookies.put("expires",expires);

        driver.get("https://demoqa.com/");
        // add the cookies to the website browser
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            // addCookie method from Selenium
            Cookie cookie = new Cookie(entry.getKey(), entry.getValue());
            driver.manage().addCookie(cookie);
        }

        driver.get("https://demoqa.com/profile");

        Thread.sleep(5000);

    }
    @And("Test user is deleted from system")
    public void test_user_is_deleted_from_system() {
        String bearerToken = "Bearer "+token;
        Response response = RestAssured.given().accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .headers("Authorization",bearerToken)
                .when().delete("https://demoqa.com/Account/v1/User/"+userId);
        Assert.assertEquals(204,response.statusCode());


    }
}
