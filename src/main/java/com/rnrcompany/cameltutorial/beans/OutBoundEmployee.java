package com.rnrcompany.cameltutorial.beans;

import lombok.Data;

@Data
public class OutBoundEmployee {

    String department;
    String salary;

    public OutBoundEmployee(String department, String salary){
        this.department = department;
        this.salary = salary;
    }
}
