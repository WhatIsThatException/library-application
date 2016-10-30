package com.library.app.category.services.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.library.app.category.model.Category;
import com.library.app.category.repository.CategoryRepository;
import com.library.app.category.services.CategoryServices;
import com.library.app.common.exception.CategoryExistentException;
import com.library.app.common.exception.CategoryNotFoundException;
import com.library.app.common.exception.FieldNotValidException;

@Stateless
public class CategoryServicesImpl implements CategoryServices {
	@Inject
	CategoryRepository categoryRepository;
	@Inject
	Validator validator; // The dependency will be managed by container but for unit tests we will have to develop it
							// manually

	@Override
	public Category add(final Category category) {
		validateCategory(category);

		return categoryRepository.add(category);
	}

	@Override
	public void update(final Category category) throws FieldNotValidException {
		validateCategory(category);

		if (!categoryRepository.existsById(category.getId()))
			throw new CategoryNotFoundException();

		categoryRepository.update(category);
	}

	@Override
	public Category findById(final long id) throws CategoryNotFoundException {
		final Category category = categoryRepository.findById(id);
		if (category == null)
			throw new CategoryNotFoundException();
		return categoryRepository.findById(id);
	}

	@Override
	public List<Category> findAll() {
		return categoryRepository.findAll("name");
	}

	private void validateCategory(final Category category) {
		validateCategoryFields(category);

		if (categoryRepository.alreadyExists(category)) {
			throw new CategoryExistentException();
		}
	}

	private void validateCategoryFields(final Category category) {
		final Set<ConstraintViolation<Category>> errors = validator.validate(category);
		final Iterator<ConstraintViolation<Category>> itErrors = errors.iterator();
		// Throws FieldNotValidException if the constraints returns error
		if (itErrors.hasNext()) {
			System.out.println("ItErrors");
			final ConstraintViolation<Category> violation = itErrors.next();
			System.out.println("Error is::" + violation.getMessage());
			throw new FieldNotValidException(violation.getPropertyPath().toString(), violation.getMessage());
		}
	}
}
