package com.library.app.category.repository;

import static com.library.app.commontests.category.CategoryForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.library.app.category.model.Category;
import com.library.app.commontests.util.DBCommand;
import com.library.app.commontests.util.DBCommandTransactionalExecutor;

public class CategoryRepositoryUTest {

	private EntityManagerFactory emf;
	private EntityManager em;
	private CategoryRepository categoryRepository;
	private DBCommandTransactionalExecutor dbCommandTransactionalExecutor;

	@Before
	public void initTestCase() {
		emf = Persistence.createEntityManagerFactory("libraryPU");
		em = emf.createEntityManager();

		categoryRepository = new CategoryRepository();
		categoryRepository.em = em;
		dbCommandTransactionalExecutor = new DBCommandTransactionalExecutor(em);
	}

	@After
	public void closeEntity() {
		em.close();
		emf.close();
	}

	@Test
	public void check() {

	}

	@Test
	public void addCategoryAndFindIt() {

		// final Long categoryAddedId is implemented in java 8 way as shown below

		/*
		 * final Long categoryAddedId = dbCommandTransactionalExecutor.executeCommand(new DBCommand<Long>() {
		 * 
		 * @Override
		 * public Long execute() {
		 * return categoryRepository.add(java()).getId();
		 * }
		 * });
		 */

		final Long categoryAddedId = dbCommandTransactionalExecutor.executeCommand(() -> {
			return categoryRepository.add(java()).getId();
		});

		assertThat(categoryAddedId, is(notNullValue()));
		final Category category = categoryRepository.findById(categoryAddedId);
		assertThat(category, is(notNullValue()));
		assertThat(category.getName(), is(equalTo(java().getName())));
	}

	@Test
	public void findCategoryByIdNotFound() {
		final Category category = categoryRepository.findById(999L);
		assertThat(category, is(nullValue()));
	}

	@Test
	public void findCategoryByIdWithNullId() {
		final Category category = categoryRepository.findById(null);
		assertThat(category, is(nullValue()));
	}

	@Test
	public void updateCategory() {
		final Long categoryAddedId = dbCommandTransactionalExecutor.executeCommand(new DBCommand<Long>() {

			@Override
			public Long execute() {

				return categoryRepository.add(java()).getId();
			}
		});

		final Category categoryAfterAdd = categoryRepository.findById(categoryAddedId);
		assertThat(categoryAfterAdd.getName(), is(equalTo(java().getName())));

		categoryAfterAdd.setName(cleanCode().getName());
		dbCommandTransactionalExecutor.executeCommand(() -> {
			categoryRepository.update(categoryAfterAdd);
			return null;
		});

		final Category categoryAfterUpdate = categoryRepository.findById(categoryAddedId);
		assertThat(categoryAfterUpdate.getName(), is(equalTo(cleanCode().getName())));

	}

	@Test
	public void findAllCategories() {
		dbCommandTransactionalExecutor.executeCommand(() -> {
			// for (final Category category : allCategories()) {
			// System.out.println("Category.name" + category.getName());
			// categoryRepository.add(category);
			// }

			// The above loop in java 8 looks like this
			allCategories().forEach(categoryRepository::add);
			return null;
		});

		final List<Category> categories = categoryRepository.findAll("name");
		System.out.println("List of categories==" + categories);
		assertThat(categories.size(), is(equalTo(4)));

		assertThat(categories.get(0).getName(), is(equalTo(architecture().getName())));
		assertThat(categories.get(1).getName(), is(equalTo(cleanCode().getName())));
		assertThat(categories.get(2).getName(), is(equalTo(java().getName())));
		assertThat(categories.get(3).getName(), is(equalTo(networks().getName())));

	}

	// to check if the category already Exists

	@Test
	public void alreadyExistsForAdd() {
		dbCommandTransactionalExecutor.executeCommand(() -> {
			categoryRepository.add(java());
			return null;
		});
		assertThat(categoryRepository.alreadyExists(java()), is(equalTo(true)));
		assertThat(categoryRepository.alreadyExists(cleanCode()), is(equalTo(false)));
	}

	// sophisticated test case
	@Test
	public void alreadyExistsCategoryWithId() {
		// final Category java = dbCommandTransactionalExecutor.executeCommand(new DBCommand<Category>() {
		// @Override
		// public Category execute() {
		// categoryRepository.add(cleanCode());
		// return categoryRepository.add(java());
		// }
		// });

		final Category java = dbCommandTransactionalExecutor.executeCommand(() -> {
			categoryRepository.add(cleanCode());
			return categoryRepository.add(java());
		});
		System.out.println("JavaId is::" + java.getId());
		assertThat(categoryRepository.alreadyExists(java), is(equalTo(false)));

		java.setName(cleanCode().getName());
		System.out.println("CleanCodeId is::" + java.getId());
		assertThat(categoryRepository.alreadyExists(java), is(equalTo(true)));
		java.setName(networks().getName());
		System.out.println("NetowrkId is::" + java.getId());
		assertThat(categoryRepository.alreadyExists(java), is(equalTo(false)));
	}

	@Test
	public void existsById() {
		final Long categoryAddedId = dbCommandTransactionalExecutor.executeCommand(() -> {
			return categoryRepository.add(java()).getId();
		});
		assertThat(categoryRepository.existsById(categoryAddedId), is(equalTo(true)));
		assertThat(categoryRepository.existsById(999L), is(equalTo(false)));
	}

}
