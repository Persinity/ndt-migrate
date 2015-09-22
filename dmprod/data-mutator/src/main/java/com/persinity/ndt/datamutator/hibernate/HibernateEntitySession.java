/**
 * Copyright (c) 2015 Persinity Inc.
 */
package com.persinity.ndt.datamutator.hibernate;

import static com.persinity.common.invariant.Invariant.assertState;

import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.persinity.ndt.datamutator.load.EntityBase;
import com.persinity.ndt.datamutator.load.EntitySession;

/**
 * {@link EntitySession} implemented with Hibernate.
 * Here we expect all entities to be in detached mode
 * so we can clear the session safely to cleanup rallback
 * changed entities.
 *
 * @author Ivan Dachev
 */
public class HibernateEntitySession implements EntitySession {
    public HibernateEntitySession(final Session session) {
        this.session = session;
    }

    @Override
    public void insert(final EntityBase entity) {
        session.save(entity);
    }

    @Override
    public void update(final EntityBase entity) {
        session.update(entity);
    }

    @Override
    public List<EntityBase> delete(final EntityBase entity) {
        session.delete(entity);
        return Collections.singletonList(entity);
    }

    @Override
    public void openTransaction() {
        assertState(transaction == null);
        session.clear();
        transaction = session.getTransaction();
        transaction.begin();
    }

    @Override
    public void commitTransaction() {
        assertState(transaction != null);
        session.flush();
        transaction.commit();
        session.clear();
        transaction = null;
    }

    @Override
    public void rollbackTransaction() {
        assertState(transaction != null);
        transaction.rollback();
        session.clear();
        transaction = null;
    }

    @Override
    public void close() {
        session.close();
    }

    private final Session session;

    private Transaction transaction;
}
