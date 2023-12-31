package ru.school.retailanalitycs_web_java.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.school.retailanalitycs_web_java.dto.viewDto.GroupViewDto;
import ru.school.retailanalitycs_web_java.entities.views.groupView.GroupView;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, uses = {CustomerMapper.class, SkuGroupMapper.class})
public abstract class GroupViewMapper {

    @Mapping(target = "skuGroup", source = "id.skuGroup")
    @Mapping(target = "customer", source = "id.customer")
    public abstract GroupViewDto toDto(GroupView groupView);

}