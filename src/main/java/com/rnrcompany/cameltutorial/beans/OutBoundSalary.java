package com.rnrcompany.cameltutorial.beans;

import lombok.Data;

@Data
public class OutBoundSalary {

    private String salary;

    public OutBoundSalary(String salary) {
        this.salary = salary;
    }
}
