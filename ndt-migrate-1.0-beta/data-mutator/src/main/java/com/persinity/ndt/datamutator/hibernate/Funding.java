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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.persinity.ndt.datamutator.load.EntityBase;

/**
 * @author Ivo Yanakiev
 */
@Entity
@Table
public class Funding extends EntityBase {

    @Override
    public List<Object> getId() {
        return Arrays.<Object>asList(fundingId, costCenterId);
    }

    public void setId(Long funding, Long costCenter) {
        setFundingId(funding);
        setCostCenterId(costCenter);
    }

    public Long getCostCenterId() {
        return costCenterId;
    }

    public void setCostCenterId(final Long costCenterId) {
        this.costCenterId = costCenterId;
    }

    public Long getFundingId() {
        return fundingId;
    }

    public void setFundingId(final Long fundingId) {
        this.fundingId = fundingId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(final Integer amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public void mutate(final long mutateId) {
        this.setName("Mutate name " + mutateId);
        final int mint = Math.abs((int) mutateId);
        this.setAmount(random.nextInt(mint));
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return format("{}({}, {}, {})", formatObj(this), getId(), getName(), getAmount());
    }

    @Id
    private Long fundingId;

    @Id
    private Long costCenterId;

    private Integer amount;

    private String name;

    private static Random random = new Random();
}
