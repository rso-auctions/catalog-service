package si.fri.rso.auctions.catalog.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import si.fri.rso.auctions.catalog.lib.dto.Category;
import si.fri.rso.auctions.catalog.mappers.CategoryMapper;
import si.fri.rso.auctions.catalog.persistence.CategoryRepository;
import si.fri.rso.auctions.catalog.service.CategoryService;

/**
 * Default implementation of {@link CategoryService}. Transactional, so it also works when called outside a REST
 * request (NATS consumers, gRPC).
 */
@ApplicationScoped
@Transactional
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
    this.categoryRepository = categoryRepository;
    this.categoryMapper = categoryMapper;
  }

  @Override
  public List<Category> getAll() {
    return categoryMapper.toDtos(categoryRepository.listAllSortedByName());
  }
}
