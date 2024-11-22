package com.library.stepDefinition;
import com.library.pages.BookPage;
import com.library.pages.LoginPage;
import com.library.utilities.BrowserUtil;
import com.library.utilities.ConfigurationReader;
import com.library.utilities.DB_Util;
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
import org.junit.Assert;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    public void pathParamIs(String pathParam, String valua) {
        givenPart.pathParam(pathParam, valua);
        id=valua;
    }


    @And("{string} field should be same with path param")
    public void fieldShouldBeSameWithPathParam(String path) {

        thenPart.body(path,is(id));
    }

    @Then("following fields should not be null:")
    public void followingFieldsShouldNotBeNull(List<String>path) {
        for(String each: path) {
            thenPart.body(each, is(notNullValue()));
        }
    }


    @And("the field value for {string} path should be equal to {string}")
    public void theFieldValueForPathShouldBeEqualTo(String path, String value) {
        thenPart.body(path, is(value));
    }

    @Given("Request Content Type header is {string}")
    public void requestContentTypeHeaderIs(String contentType) {
        givenPart.contentType(contentType);
    }

    Map<String,Object>apiData;
    @Given("I create a random {string} as request body")
    public void iCreateARandomAsRequestBody(String randomData) {

        Map<String,Object> requestBody=new LinkedHashMap<>();

        switch (randomData){
            case "user":
                requestBody=LibraryAPI_Util.getRandomUserMap();
                break;
            case "book":
                requestBody=LibraryAPI_Util.getRandomBookMap();
                break;
            default:
                throw new RuntimeException("Unexepted Data :"+ randomData);
        }
        apiData=requestBody;
        System.out.println("Request body is as following "+ requestBody);
        givenPart.formParams(requestBody);
    }

    @When("I send POST request to {string} endpoint")
    public void iSendPOSTRequestToEndpoint(String endpoint) {
        response = givenPart.when()
                .post(ConfigurationReader.getProperty("library.baseUri") + endpoint)
                .prettyPeek();

        thenPart = response.then();
    }
        @And("{string} field should not be null")
        public void fieldShouldNotBeNull(String path) {
            thenPart.body(path, is(notNullValue()));
    }

    @Then("UI, Database and API created book information must match")
    public void uiDatabaseAndAPICreatedBookInformationMustMatch() {

        //API DATA --> REQUEST DATA
        System.out.println("apiData = " + apiData);

        //RETRIEVE DATA FROM DATABASE WE NEED BOOK_ID
        String bookID = response.path("book_id");
        System.out.println("bookID = " + bookID);


        //OPEN DATABASE CONNECTION --> Add @db into related scenario
        //AFTER RETRIEVING DATA FROM DATABASE, REMOVE ID AND ADDED DATA
        DB_Util.runQuery("select * from books where id=" + bookID);
        Map<String, Object> dbData = DB_Util.getRowMap(1);
        dbData.remove("id");
        dbData.remove("added_date");
        System.out.println("dbData = " + dbData);


        Assert.assertEquals(apiData, dbData);

        //GET BOOK NAME TO SEARCH IN UI
        String bookName =(String) apiData.get("name");
        System.out.println("bookName = " + bookName);

        BookPage bookPage = new BookPage();
        bookPage.search.sendKeys(bookName);
        BrowserUtil.waitFor(3);

        bookPage.editBook(bookName).click();
        BrowserUtil.waitFor(3);


        //GET UI MAP
        Map<String, Object> uiData = new LinkedHashMap<>();
        String uiBookName = bookPage.bookName.getAttribute("value");
        uiData.put("name", uiBookName);

        String uiISBN = bookPage.isbn.getAttribute("value");
        uiData.put("isbn",uiISBN);

        String uiYear= bookPage.year.getAttribute("value");
        uiData.put("year",uiYear);

        String uiAutor = bookPage.author.getAttribute("value");
        uiData.put("author", uiAutor);

        //RETRIEVE BOOK CATEGORY ID
        String selectCategory=BrowserUtil.getSelectedOption(bookPage.categoryDropdown);
        DB_Util.runQuery("select id from book_categories where name = '"+selectCategory+"'");

        String uiCategoryID = DB_Util.getFirstRowFirstColumn();
        uiData.put("book_category_id", uiCategoryID);

        String uiDesc = bookPage.description.getAttribute("value");
        uiData.put("description", uiDesc);
        System.out.println("uiDesc = " + uiData);
        Assert.assertEquals(apiData,uiData);

    }

    @Then("created user information should match with Database")
    public void createdUserInformationShouldMatchWithDatabase() {
        //GET ME USER ID
        String id = response.path("user_id");
        System.out.println("id = " + id);

        //GET ME DATA FROM DATABASE
        String query="select full_name, email, user_group_id, status,start_data, end_data, address from users where id="+id;
        DB_Util.runQuery(query);

        Map<String, Object>dbUser=DB_Util.getRowMap(1);
        System.out.println("------DB DATA------");
        System.out.println("dbUser = " + dbUser);

        //API DATA THAT WE GENERATE / BODY
        System.out.println("-------API----------");
        String password =(String) apiData.remove("password");
        System.out.println("apiData = " + apiData);

        Assert.assertEquals(apiData,dbUser);
        apiData.put("password",password);
    }

    @Then("created user should be able to login Library UI")
    public void createdUserShouldBeAbleToLoginLibraryUI() {

        LoginPage loginPage=new LoginPage();
        // email
        String email = (String) apiData.get("email");
        System.out.println("email = " + email);
        // password
        String password = (String) apiData.get("password");
        System.out.println("password = " + password);
        loginPage.login(email,password);

        BrowserUtil.waitFor(2);

    }

    @And("created user name should appear in Dashboard Page")
    public void createdUserNameShouldAppearInDashboardPage() {

        BookPage bookPage=new BookPage();

        // UI FULLNAME
        String uiFull = bookPage.accountHolderName.getText();
        System.out.println("uiFull = " + uiFull);
        // API DATA THAT WE SEND
        String apiFull = (String) apiData.get("full_name");
        System.out.println("apiFull = " + apiFull);
        Assert.assertEquals(apiFull,uiFull);
    }


    String token;
    @Given("I logged Library api with credentials {string} and {string}")
    public void iLoggedLibraryApiWithCredentialsAnd(String email, String password) {

        token = LibraryAPI_Util.getToken(email, password);
        givenPart= RestAssured.given().log().uri();
    }

    @And("I send token information as request body")
    public void iSendTokenInformationAsRequestBody() {
        givenPart.formParam("token",token);
    }



}
