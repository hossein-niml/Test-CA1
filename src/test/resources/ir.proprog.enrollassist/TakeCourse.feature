Feature: As a user I want to take a course

  Background:
    Given course of "c1" with "" prerequisites
    And course of "c2" with "c1" prerequisites
    And course of "c3" with "" prerequisites
    And course of "c4" with "c3" prerequisites

  Scenario: Student has passed course of c1
    When student wants to take course of "c4"
    Then student gets PrerequisiteNotTaken error

  Scenario: Student has passed course of c1
    When student has passed "c2" course
    Then student gets no error