package si.fri.rso.auctions.catalog.api;

import java.util.List;
import si.fri.rso.auctions.catalog.api.specs.CategoryApiSpec;
import si.fri.rso.auctions.catalog.lib.dto.Category;
import si.fri.rso.auctions.catalog.service.CategoryService;

/** REST resource implementing {@link CategoryApiSpec}. */
public class CategoryResource implements CategoryApiSpec {

  private final CategoryService categoryService;

  public CategoryResource(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @Override
  public List<Category> getAll() {
    return categoryService.getAll();
  }
}
