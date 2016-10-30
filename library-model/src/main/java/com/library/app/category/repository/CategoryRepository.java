package com.library.app.category.repository;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.library.app.category.model.Category;

@Stateless
public class CategoryRepository {

	EntityManager em;

	public Category add(final Category category) {
		em.persist(category);
		return category;
	}

	public Category findById(final Long id) {
		if (id == null) {
			return null;
		}
		return em.find(Category.class, id);
	}

	public void update(final Category category) {
		em.merge(category);

	}

	@SuppressWarnings("unchecked")
	public List<Category> findAll(final String orderField) {
		// TODO Auto-generated method stub
		return em.createQuery("select e From Category e order by e." + orderField).getResultList();
	}

	public boolean alreadyExists(final Category category) {
		System.out.println("Inside AlreadyExists");
		final StringBuilder jpql = new StringBuilder();
		jpql.append("Select 1 from Category e where e.name = :name");
		if (category.getId() != null) {
			System.out.println("HELLO:" + category);
			jpql.append(" And e.id != :id");
		} else {
			System.out.println("HELLOElse:" + category);
			System.out.println("Print category id is null");
		}

		final Query query = em.createQuery(jpql.toString());
		System.out.println("Inside AlreadyExistsAgain::" + jpql.toString());
		query.setParameter("name", category.getName());
		if (category.getId() != null) {
			query.setParameter("id", category.getId());
		}

		System.out.println("Returned Value is::" + jpql.toString());
		return query.setMaxResults(1).getResultList().size() > 0;
	}

	public boolean existsById(final Long id) {
		return em.createQuery("Select 1 from Category e where e.id = :id")
				.setParameter("id", id)
				.setMaxResults(1)
				.getResultList().size() > 0;
	}

	public int addNumber(final int i, final int j) {
		return (i + j);
	}

}
