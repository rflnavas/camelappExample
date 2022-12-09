package com.rnrcompany.cameltutorial.beans;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Data;

/*
    @Data brings us the constructor, getters, setters and toString
    As eluded in the doc it is equivalent as declaring all of
    these annotations: @Getter @Setter
    @RequiredArgsConstructor @ToString @EqualsAndHashCode
*/
@Entity
@Data
@Table(name = "employee_salary")
@NamedQuery(name="findAll", query = "Select X from EmployeeSalary X")
public class EmployeeSalary implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String department;

    private String departmentName;

    private String division;

    private String gender;

    private String salary;

    private String overtimePay;

    private String longetivityPay;

    private String grade;
}
