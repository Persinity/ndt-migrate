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