package com.library.app.category.services.impl;

import static com.library.app.commontests.category.CategoryForTestsRepository.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;

import com.library.app.category.model.Category;
import com.library.app.category.repository.CategoryRepository;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.exception.CategoryExistentException;
import com.library.app.common.exception.CategoryNotFoundException;
import com.library.app.common.exception.FieldNotValidException;

public class CategoryServicesUTest {
	private CategoryServices categoryServices;
	private CategoryRepository categoryRepository;
	private Validator validator;

	@Before
	public void initTestCase() {

		categoryRepository = mock(CategoryRepository.class);
		validator = Validation.buildDefaultValidatorFactory().getValidator();
		categoryServices = new CategoryServicesImpl();
		((CategoryServicesImpl) categoryServices).validator = validator;
		((CategoryServicesImpl) categoryServices).categoryRepository = categoryRepository;

	}

	// Since we are giving null name, it should fail in catch block, but it's not going to catch block.
	// So fail is kept. The reason of addCategoryWithInvalidName() testing is to invoke FieldNotValidException.
	// If fail message is executed it means FieldNotValidException will not be thrown
	@Test
	public void addCategoryWithNullName() {
		addCategoryWithInvalidName(null);
	}

	// addCategoryWithShortName() and addCategoryWithLongName() is not failing, any string inserted passes.
	// It works because when adding the category, FieldInvalidException occurs before fail().
	@Test
	public void addCategoryWithShortName() {
		addCategoryWithInvalidName("A");
	}

	@Test
	public void addCategoryWithLongName() {

		addCategoryWithInvalidName("This is a test for throwing exception");
	}

	private void addCategoryWithInvalidName(final String name) {
		final Category category = new Category(name);
		try {
			//
			// final Set<ConstraintViolation<Category>> errors = validator.validate(category);
			// final Iterator<ConstraintViolation<Category>> itErrors = errors.iterator();
			// if (itErrors.hasNext()) {
			// System.out.println("ConstraintViolation Error");
			// final ConstraintViolation<Category> violation = itErrors.next();
			// assertEquals(1, errors.size());
			// } else {
			// System.out.println("No ConstraintViolation Error");
			// }
			categoryServices.add(category);
			fail("An error should have been thrown");
		} catch (final FieldNotValidException e) {
			System.out.println("FieldNotvalidException Thrown. FieldName is::" + e.getFieldName());
			assertThat(e.getFieldName(), is(equalTo("name")));
		}
	}

	@Test(expected = CategoryExistentException.class)
	public void addCategoryWithExistentName() {
		when(categoryRepository.alreadyExists(java())).thenReturn(true);
		categoryServices.add(java());
	}

	@Test
	public void addValidCategory() {
		when(categoryRepository.alreadyExists(java())).thenReturn(false);
		when(categoryRepository.add(java())).thenReturn(categoryWithId(java(), 1L));

		final Category categoryAddedId = categoryServices.add(java());
		assertThat(categoryAddedId.getId(), is(equalTo(1L)));
	}

	public void updateCategoryWithInvalidName(final String name) {
		try {
			categoryServices.update(new Category(name));
			fail("An error should have been thrown");
		} catch (final FieldNotValidException e) {
			assertThat(e.getFieldName(), is(equalTo("name")));
		}
	}

	@Test
	public void updateWithNullName() {
		updateCategoryWithInvalidName(null);
	}

	@Test
	public void updateWithShortName() {
		updateCategoryWithInvalidName("A");
	}

	@Test
	public void updateWithLongName() {
		updateCategoryWithInvalidName("LongNameis kept so that exception could be thrown");
	}

	@Test(expected = CategoryExistentException.class)
	public void updateCategoryWithExistentName() {
		when(categoryRepository.alreadyExists(categoryWithId(java(), 1L))).thenReturn(true);
		categoryServices.update(categoryWithId(java(), 1L));

	}

	@Test(expected = CategoryNotFoundException.class)
	public void updateCategoryNotFound() {
		when(categoryRepository.alreadyExists(categoryWithId(java(), 1L))).thenReturn(false);
		when(categoryRepository.existsById(1L)).thenReturn(false);

		categoryServices.update(categoryWithId(java(), 1L));
	}

	@Test
	public void updateValidCategory() {
		when(categoryRepository.alreadyExists(categoryWithId(java(), 1L))).thenReturn(false);
		when(categoryRepository.existsById(1L)).thenReturn(true);

		categoryServices.update(categoryWithId(java(), 1L));
		verify(categoryRepository).update(categoryWithId(java(), 1L));

	}

	@Test
	public void findCategoryById() {
		when(categoryRepository.findById(1L)).thenReturn(categoryWithId(java(), 1L));
		final Category category = categoryServices.findById(1L);
		assertThat(category, is(notNullValue()));
		assertThat(category.getId(), is(equalTo(1L)));
		assertThat(category.getName(), is(equalTo(java().getName())));
	}

	@Test(expected = CategoryNotFoundException.class)
	public void findCategoryByIdNotFound() {
		when(categoryRepository.findById(1L)).thenReturn(null);
		categoryServices.findById(1L);
	}

	@Test
	public void findAllNoCategories() {
		when(categoryRepository.findAll("name")).thenReturn(new ArrayList<>());
		final List<Category> categories = categoryServices.findAll();
		assertThat(categories.isEmpty(), is(equalTo(true)));
	}

	@Test
	public void findAllCategories() {
		when(categoryRepository.findAll("name")).thenReturn(
				Arrays.asList(categoryWithId(java(), 1L), categoryWithId(networks(), 1L)));

		final List<Category> categories = categoryServices.findAll();
		assertThat(categories.size(), is(equalTo(2)));
		assertThat(categories.get(0).getName(), is(equalTo(java().getName())));
		assertThat(categories.get(1).getName(), is(equalTo(networks().getName())));
	}
}
