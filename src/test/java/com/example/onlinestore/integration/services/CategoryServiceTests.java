package com.example.onlinestore.integration.services;

import com.example.onlinestore.domain.entities.Category;
import com.example.onlinestore.domain.models.service.CategoryServiceModel;
import com.example.onlinestore.repository.CategoryRepository;
import com.example.onlinestore.services.category.CategoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CategoryServiceTests {

    private static final String CATEGORY_ID = "categoryId";
    private static final String CATEGORY_NAME = "categoryName";

    @Autowired
    private CategoryService categoryService;

    @MockBean
    private CategoryRepository mockCategoryRepository;

    private List<Category> categories;

    @Before
    public void setupTest() {
        this.categories = new ArrayList<>();
        when(this.mockCategoryRepository.findAll())
                .thenReturn(this.categories);
    }

    @Test
    public void addCategory_whenValid_saveCategory() {
        CategoryServiceModel categoryServiceModel = this.categoryService.addCategory(new CategoryServiceModel());
        categoryServiceModel.setId(CATEGORY_ID);
        categoryServiceModel.setName(CATEGORY_NAME);

        verify(this.mockCategoryRepository)
                .save(any());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCategory_whenCategoryIsNull_throwException() {
        CategoryServiceModel categoryServiceModel = this.categoryService.addCategory(null);
        categoryServiceModel.setId(CATEGORY_ID);
        categoryServiceModel.setName(CATEGORY_NAME);

        verify(this.mockCategoryRepository)
                .save(any());
    }

    @Test
    public void findAllCategories_when2Categories_return2Categories() {
        Category category1 = new Category();
        category1.setId(CATEGORY_ID + "1");
        category1.setName(CATEGORY_NAME + "1");

        Category category2 = new Category();
        category2.setId(CATEGORY_ID + "2");
        category2.setName(CATEGORY_NAME + "2");

        this.categories.add(category1);
        this.categories.add(category2);

        List<CategoryServiceModel> actualCategories = this.categoryService.findAllCategories();
        CategoryServiceModel actualCategory1 = actualCategories.get(0);
        CategoryServiceModel actualCategory2 = actualCategories.get(1);

        assertEquals(category1.getId(), actualCategory1.getId());
        assertEquals(category1.getName(), actualCategory1.getName());
        assertEquals(category2.getId(), actualCategory2.getId());
        assertEquals(category2.getName(), actualCategory2.getName());
        assertEquals(this.categories.size(), actualCategories.size());
    }

    @Test
    public void findAllCategories_when0Categories_returnEmptyList() {
        List<CategoryServiceModel> actualCategories = this.categoryService.findAllCategories();

        assertEquals(this.categories.size(), actualCategories.size());
    }

    @Test
    public void findCategoryById_whenValidCategory_returnCategory() {
        Category category = new Category();

        when(this.mockCategoryRepository.findById(any()))
                .thenReturn(Optional.of(category));
    }

    @Test
    public void findCategoryById_whenIdIsNull_throwException() {
        when(this.mockCategoryRepository.findById(null))
                .thenThrow(NullPointerException.class);
    }

    @Test
    public void editCategory_whenSuccessfullyEdited_returnEditedCategory() {
        Category category = new Category();
        category.setId(CATEGORY_ID);
        category.setName(CATEGORY_NAME);

        when(this.mockCategoryRepository.findById(any()))
                .thenReturn(Optional.of(category));

        CategoryServiceModel categoryServiceModel = this.categoryService.editCategory(CATEGORY_ID, new CategoryServiceModel() {{
            setName("editedCategoryName");
        }});

        assertEquals("editedCategoryName", categoryServiceModel.getName());
        verify(this.mockCategoryRepository)
                .save(any());
    }

    //TODO: deleteCategory test

    @Test
    public void deleteCategory_whenSuccessfullyDeleted_verifyDelete() {
        Category category = new Category();
        category.setId(CATEGORY_ID);
        category.setName(CATEGORY_NAME);

        when(this.mockCategoryRepository.findById(any()))
                .thenReturn(Optional.of(category));

        this.categories.add(category);

        List<CategoryServiceModel> actualCategories = this.categoryService.findAllCategories();
        CategoryServiceModel actualCategory = actualCategories.get(0);
        this.categoryService.deleteCategory(actualCategory.getId());

        verify(this.mockCategoryRepository)
                .delete(any());
    }

}
