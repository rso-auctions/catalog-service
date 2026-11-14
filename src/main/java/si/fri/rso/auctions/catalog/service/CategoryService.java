package si.fri.rso.auctions.catalog.service;

import java.util.List;
import si.fri.rso.auctions.catalog.lib.dto.Category;

/** Business operations on item categories. */
public interface CategoryService {

  /** Returns all categories sorted by name. */
  List<Category> getAll();
}
