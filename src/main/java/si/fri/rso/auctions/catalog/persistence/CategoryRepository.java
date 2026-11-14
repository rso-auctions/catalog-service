package si.fri.rso.auctions.catalog.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;
import si.fri.rso.auctions.catalog.persistence.models.CategoryEntity;

/** Repository for {@link CategoryEntity}. */
@ApplicationScoped
public class CategoryRepository implements PanacheRepositoryBase<CategoryEntity, UUID> {

  /** Returns all categories sorted by name. */
  public List<CategoryEntity> listAllSortedByName() {
    return listAll(Sort.by("name"));
  }
}
