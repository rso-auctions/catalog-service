package si.fri.rso.auctions.catalog.mappers;

import java.util.List;
import org.mapstruct.Mapper;
import si.fri.rso.auctions.catalog.lib.dto.Category;
import si.fri.rso.auctions.catalog.mappers.common.CommonMapperConfig;
import si.fri.rso.auctions.catalog.persistence.models.CategoryEntity;

/** Maps categories between the persistence and API representations. */
@Mapper(config = CommonMapperConfig.class)
public interface CategoryMapper {

  Category toDto(CategoryEntity entity);

  List<Category> toDtos(List<CategoryEntity> entities);
}
