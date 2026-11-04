package com.hk_restaurant.web;

import com.hk_restaurant.dao.BanAnDAO;
import com.hk_restaurant.model.entity.BanAn;
import com.hk_restaurant.model.enumtype.TableStatus;
import jakarta.persistence.EntityManager;
import java.util.*;

/** Uses the POS command's existing EntityManager and transaction. */
final class TransactionalTableDao implements BanAnDAO {
    private final EntityManager em;
    TransactionalTableDao(EntityManager em) { this.em=em; }
    public List<BanAn> findAll() { return em.createQuery("FROM BanAn b WHERE b.maBanCode NOT LIKE 'DEL_BAN_%' ORDER BY b.id",BanAn.class).getResultList(); }
    public Optional<BanAn> findById(Long id) { return Optional.ofNullable(em.find(BanAn.class,id)); }
    public Optional<BanAn> findByCode(String code) { return em.createQuery("FROM BanAn b WHERE b.maBanCode=:code",BanAn.class).setParameter("code",code).getResultStream().findFirst(); }
    public List<BanAn> findByTrangThai(TableStatus status) { return em.createQuery("FROM BanAn b WHERE b.trangThai=:status AND b.maBanCode NOT LIKE 'DEL_BAN_%' ORDER BY b.id",BanAn.class).setParameter("status",status).getResultList(); }
    public BanAn save(BanAn table) { em.persist(table); return table; }
    public BanAn update(BanAn table) { return em.merge(table); }
    public void deleteById(Long id) { findById(id).ifPresent(em::remove); }
    public boolean existsByCode(String code) { return findByCode(code).isPresent(); }
}
