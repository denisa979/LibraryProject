package com.library.stepDefinition;

import com.library.pages.BookPage;
import com.library.pages.LoginPage;
import com.library.utilities.BrowserUtil;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class UI_LoginStepDefinition {
    LoginPage loginPage = new LoginPage();
    BookPage bookPage = new BookPage();

    @Given("I logged in Library UI as {string}")
    public void i_logged_in_library_ui_as(String userType) {
        loginPage.login(userType);
        BrowserUtil.waitFor(3);

    }

    @Given("I navigate to {string} page")
    public void i_navigate_to_page(String moduleName) {
        bookPage.navigateModule(moduleName);
        BrowserUtil.waitFor(3);
    }


}



