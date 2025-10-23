package com.transi.flex.billings.mapper;

import com.transi.flex.billings.dto.CreateInvoiceDTO;
import com.transi.flex.billings.dto.InvoiceDTO;
import com.transi.flex.billings.model.Invoice;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, builder = @Builder(disableBuilder = true))
public interface InvoiceMapper {

    Invoice toModel(InvoiceDTO dto);

    Invoice toModel(CreateInvoiceDTO dto);

    @Mapping(target = "companyName", source = "company.name")
    @Mapping(target = "companyId", source = "company.id")
    InvoiceDTO toDto(Invoice model);

    List<InvoiceDTO> toDtos(List<Invoice> models);
}
