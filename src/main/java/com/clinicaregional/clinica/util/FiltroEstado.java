package com.clinicaregional.clinica.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;

public abstract class FiltroEstado {
    @PersistenceContext
    protected EntityManager entityManager;

    public void activarFiltroEstado(boolean estado) {
        entityManager.unwrap(Session.class).enableFilter("estadoActivo").setParameter("estado", estado);
    }
}
