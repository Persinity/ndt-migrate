/*
 * Copyright 2015 Persinity Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.persinity.ndt.datamutator.hibernate;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.StringUtils.formatObj;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.persinity.ndt.datamutator.load.EntityBase;

/**
 * @author Ivo Yanakiev
 */
@Entity
@Table
public class Employee extends EntityBase {

    public List<Object> getId() {
        return Arrays.<Object>asList(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(final Date hireDate) {
        this.hireDate = hireDate;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Employee getManager() {
        return manager;
    }

    public void setManager(final Employee manager) {
        this.manager = manager;
    }

    //    public List<Employee> getEmployees() {
    //        return employees;
    //    }
    //    public void setEmployees(final List<Employee> employees) {
    //        this.employees = employees;
    //    }

    public Position getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(final Position hierarchy) {
        this.hierarchy = hierarchy;
    }

    @Override
    public void mutate(final long mutateId) {
        this.setName("Mutate name " + mutateId);
        this.setHireDate(new Date(System.currentTimeMillis() - random.nextInt(157680000) * 1000L));
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return format("{}({}, {}, {}, {}, {}, {})", formatObj(this), getId(), getName(), getHireDate(), getDepartment(),
                getManager(), getHierarchy());
    }

    @Id
    private Long id;

    private String name;

    private Date hireDate;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Department department;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Employee manager;

    //TODO uncomment when support tables without primary key, hibernate creates
    // a custom table employee_employee to support the List<Employee>
    //    @OneToMany(cascade = CascadeType.PERSIST)
    //    private List<Employee> employees = new ArrayList<>();
    //

    @OneToOne(cascade = CascadeType.PERSIST)
    private Position hierarchy;

    private static Random random = new Random();
}