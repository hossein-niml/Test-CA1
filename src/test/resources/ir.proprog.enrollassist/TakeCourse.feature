Feature: As a user I want to take a course

  Background:
    Given course of "c1" with "" prerequisites
    And course of "c2" with "c1" prerequisites
    And course of "c3" with "" prerequisites
    And course of "c4" with "c1,c2" prerequisites

    And student of "hossein" that has passed "c1" courses
    And student of "javad" that has passed "c1,c3" courses
    And student of "reza" that has passed "c1,c2" courses

  Scenario:
    When student of "hossein" wants to take course of "c4"
    Then student gets an error with message="c2 is not passed as a prerequisite of c4"