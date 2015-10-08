/*
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.datamutator.hibernate;

import static com.persinity.common.StringUtils.format;
import static com.persinity.common.StringUtils.formatObj;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.List;

import com.persinity.ndt.datamutator.load.EntityBase;

/**
 * @author Ivo Yanakiev
 */
@Entity
@Table
public class Department extends EntityBase {

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

    //    public List<Employee> getEmployees() {
    //        return employees;
    //    }
    //
    //    public void setEmployees(List<Employee> employees) {
    //        this.employees = employees;
    //    }

    public Funding getFunding() {
        return funding;
    }

    public void setFunding(final Funding funding) {
        this.funding = funding;
    }

    @Override
    public void mutate(final long mutateId) {
        this.setName("Mutate name " + mutateId);
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return format("{}({}, {}, {})", formatObj(this), getId(), getName(), getFunding());
    }

    @Id
    private Long id;

    private String name;

    //TODO problems for now
    //    @OneToMany(mappedBy = "department", cascade = CascadeType.PERSIST)
    //    private List<Employee> employees = new ArrayList<>();
    //
    @OneToOne(cascade = CascadeType.PERSIST)
    private Funding funding;
}