package com.transi.flex.billings.mapper;

import com.transi.flex.billings.dto.SubscriptionDTO;
import com.transi.flex.billings.dto.SubscriptionPlanDTO;
import com.transi.flex.billings.model.Subscription;
import jakarta.persistence.SqlResultSetMappings;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface SubscriptionMapper {

    Subscription toModel(SubscriptionDTO dto);

    @Mapping(target = "planPrice", source = "plan.price")
    @Mapping(target = "planName", source = "plan.name")
    @Mapping(target = "companyName", source = "company.name")
    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "planId", source = "plan.id")
    SubscriptionDTO toDto(Subscription model);

    List<SubscriptionDTO> toDtos(List<Subscription> models);
}
