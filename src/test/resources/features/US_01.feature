@wip
Feature: As a librarian, I want to retrieve all users


  Scenario: Retrieve all users from the API endpoint

    Given I logged Library api as a "librarian"
    And Accept header is "application/json"
    When I send GET request to "/get_all_users" endpoint
    Then status code should be 200
    And Response Content type is "application/json; charset=utf-8"
    And Each "id" field should not be null
    And Each "name" field should not be null

  Scenario: Retrieve single user
    Given I logged Library api as a "librarian"
    And Accept header is "application/json"
    And Path param "id" is "1"
    When I send GET request to "/get_user_by_id/{id}" endpoint
    Then status code should be 200
    And Response Content type is "application/json; charset=utf-8"
    And "id" field should be same with path param
    Then following fields should not be null:
      | full_name |
      | email     |
      | password  |

  Scenario: Create a new book API
    Given I logged Library api as a "librarian"
    And Accept header is "application/json"
    And Request Content Type header is "application/x-www-form-urlencoded"
    And I create a random "book" as request body
    When I send POST request to "/add_book" endpoint
    And Response Content type is "application/json; charset=utf-8"
    And the field value for "message" path should be equal to "The book has been created."
    And "book_id" field should not be null


  @db @ui
  Scenario: Create a new book all layers
    Given I logged Library api as a "librarian"
    And Accept header is "application/json"
    And Request Content Type header is "application/x-www-form-urlencoded"
    And I create a random "book" as request body
    And I logged in Library UI as "librarian"
    And I navigate to "Books" page
    When I send POST request to "/add_book" endpoint
    Then status code should be 200
    And Response Content type is "application/json; charset=utf-8"
    And the field value for "message" path should be equal to "The book has been created."
    And "book_id" field should not be null
    And UI, Database and API created book information must match

  Scenario: Create a new user API
    Given I logged Library api as a "librarian"
    And Accept header is "application/json"
    And Request Content Type header is "application/x-www-form-urlencoded"
    And I create a random "user" as request body
    When I send POST request to "/add_user" endpoint
    Then status code should be 200
    And Response Content type is "application/json; charset=utf-8"
    And the field value for "message" path should be equal to "The user has been created."
    And "user_id" field should not be null
    # And Currently generated user should be deleted



  @db @ui
  Scenario: Create a new user all layers
    Given I logged Library api as a "librarian"
    And Accept header is "application/json"
    And Request Content Type header is "application/x-www-form-urlencoded"
    And I create a random "user" as request body
    When I send POST request to "/add_user" endpoint
    Then status code should be 200
    And Response Content type is "application/json; charset=utf-8"
    And the field value for "message" path should be equal to "The user has been created."
    And "user_id" field should not be null
    And created user information should match with Database
    And created user should be able to login Library UI
    And created user name should appear in Dashboard Page

  Scenario Outline: Decode User
    Given I logged Library api with credentials "<email>" and "<password>"
    And Accept header is "application/json"
    And Request Content Type header is "application/x-www-form-urlencoded"
    And I send token information as request body
    When I send POST request to "/decode" endpoint
    Then status code should be 200
    And Response Content type is "application/json; charset=utf-8"
    And the field value for "user_group_id" path should be equal to "<user_group_id>"
    And the field value for "email" path should be equal to "<email>"
    And "full_name" field should not be null
    And "id" field should not be null


    Examples:
      | email                | password    | user_group_id |
      | student5@library     | libraryUser | 3             |
      | librarian10@library  | libraryUser | 2             |
      | student10@library    | libraryUser | 3             |
      | librarian13@library  | libraryUser | 2             |