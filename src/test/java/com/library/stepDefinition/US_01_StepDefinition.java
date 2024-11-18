package com.library.stepDefinition;
import com.library.utilities.ConfigurationReader;
import com.library.utilities.LibraryAPI_Util;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class US_01_StepDefinition {
    RequestSpecification givenPart;
    Response response;
    ValidatableResponse thenPart;


    @Given("I logged Library api as a {string}")
    public void i_logged_library_api_as_a(String userType) {
        givenPart= RestAssured.given().log().uri()
                .header("x-library-token", LibraryAPI_Util.getToken(userType));
    }
    @Given("Accept header is {string}")
    public void accept_header_is(String accept) {
        givenPart.accept(accept);

    }
    @When("I send GET request to {string} endpoint")
    public void i_send_get_request_to_endpoint(String endpoint) {
        response = givenPart.when()
                .get(ConfigurationReader.getProperty("library.baseUri") + endpoint)
                .prettyPeek();

        thenPart = response.then();

    }
    @Then("status code should be {int}")
    public void status_code_should_be(Integer statusCode) {

        thenPart.statusCode(statusCode);
    }
    @Then("Response Content type is {string}")
    public void response_content_type_is(String contentType) {
        thenPart.contentType(contentType);
    }
    @Then("Each {string} field should not be null")
    public void each_field_should_not_be_null(String path) {
        thenPart.body(path, everyItem(notNullValue()));
    }


    String id;
    @And("Path param {string} is {string}")
    public void pathParamIs(String id, String valua) {
        givenPart.pathParam(id, valua);
        id=valua;
    }


    @And("{string} field should be same with path param")
    public void fieldShouldBeSameWithPathParam(String path) {

        thenPart.body(path,is(id));
    }

    @And("following fields should not be null")
    public void followingFieldsShouldNotBeNull(List<String>path) {
        for(String each: path){
            thenPart.body(each, is(notNullValue()));
        }
    }
}
