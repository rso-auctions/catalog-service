package si.fri.rso.auctions.catalog.persistence.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import si.fri.rso.auctions.catalog.persistence.models.common.BaseEntity;

/** Item category, e.g. vehicles or electronics. */
@Entity
@Table(name = "categories")
public class CategoryEntity extends BaseEntity {

  @NotBlank
  @Size(max = 64)
  @Column(name = "code", nullable = false, unique = true, length = 64)
  private String code;

  @NotBlank
  @Size(max = 128)
  @Column(name = "name", nullable = false, length = 128)
  private String name;

  @Size(max = 512)
  @Column(name = "description", length = 512)
  private String description;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
