/*
 * Copyright (c) 2015 Persinity Inc.
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
