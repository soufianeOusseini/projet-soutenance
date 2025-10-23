package com.transi.flex.billings.mapper;

import com.transi.flex.billings.dto.CreateSubscriptionPlanDTO;
import com.transi.flex.billings.dto.SubscriptionPlanDTO;
import com.transi.flex.billings.model.SubscriptionPlan;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface SubscriptionPlanMapper {

    SubscriptionPlan toModel(SubscriptionPlanDTO dto);

    SubscriptionPlan toModel(CreateSubscriptionPlanDTO dto);

    SubscriptionPlanDTO toDto(SubscriptionPlan model);

    List<SubscriptionPlanDTO> toDtos(List<SubscriptionPlan> models);
}
