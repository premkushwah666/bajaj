package com.bajaj.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service

public class SQLSolutionService {

    /**
     * Solves the SQL problem based on whether the registration number is odd or even
     *
     * @param isOdd true if registration number is odd, false if even
     * @return the final SQL query solution
     */
    public String solveSQL(boolean isOdd) {
        //log.info("Solving SQL problem for registration number with odd={}", isOdd);

        if (isOdd) {
            // SQL Solution for Question 1 (Odd registration number)
            /*
             * Question 1: Find the highest salary that was credited to an employee, but only for transactions
             * that were not made on the 1st day of any month. Along with the salary, you are also required
             * to extract the employee data like name (combine first name and last name into one column),
             * age and department who received this salary.
             */
            return "SELECT " +
                    "    p.AMOUNT AS SALARY, " +
                    "    CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
                    "    TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, " +
                    "    d.DEPARTMENT_NAME " +
                    "FROM " +
                    "    PAYMENTS p " +
                    "JOIN " +
                    "    EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
                    "JOIN " +
                    "    DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                    "WHERE " +
                    "    DAY(p.PAYMENT_TIME) != 1 " +
                    "ORDER BY " +
                    "    p.AMOUNT DESC " +
                    "LIMIT 1";
        } else {
            // SQL Solution for Question 2 (Even registration number)
            /*
             * Question 2: Calculate the number of employees who are younger than each
             * employee, grouped by their respective departments. For each employee, return the
             * count of employees in the same department whose age is less than theirs.
             */
            return "SELECT " +
                    "    e1.EMP_ID, " +
                    "    e1.FIRST_NAME, " +
                    "    e1.LAST_NAME, " +
                    "    d.DEPARTMENT_NAME, " +
                    "    COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT " +
                    "FROM " +
                    "    EMPLOYEE e1 " +
                    "JOIN " +
                    "    DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID " +
                    "LEFT JOIN " +
                    "    EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT AND e1.DOB > e2.DOB " +
                    "GROUP BY " +
                    "    e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME " +
                    "ORDER BY " +
                    "    e1.EMP_ID DESC";
        }
    }
}