package si.fri.rso.auctions.catalog.mappers.common;

import org.mapstruct.MapperConfig;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/** Shared MapStruct configuration: CDI beans, and unmapped target properties fail the build. */
@MapperConfig(
    componentModel = MappingConstants.ComponentModel.JAKARTA_CDI,
    unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CommonMapperConfig {}
