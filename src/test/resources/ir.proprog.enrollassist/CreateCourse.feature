Feature: As an user I want to add a new course

  Scenario: Empty title
    When user adds a new course with empty title
    Then user gets an error with message="Course must have a name."

  Scenario: Invalid graduate level
    When user adds a new course with invalid graduate level
    Then user gets an error with message="Graduate level is not valid."


  Scenario Outline: Credits is less than 0 or more than 4
    When user adds a new course with credits=<invalidCredits>
    Then user gets an error with message="Credit must be one of the following values: 0, 1, 2, 3, 4."

    Examples:
      | invalidCredits |
      | -5             |
      | -1             |
      | 5              |
      | 12             |


  Scenario Outline: Invalid course number
    When user adds a new course with number=<invalidCourseNumber>
    Then user gets an error with message=<errorMessage>

    Examples:
      | invalidCourseNumber | errorMessage                            |
      | ""                  | "Course number cannot be empty."        |
      | "s1"                | "Course number must be number."         |
      | "12345678"          | "Course number must contain 7 numbers." |

