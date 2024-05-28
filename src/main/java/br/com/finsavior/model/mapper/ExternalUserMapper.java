package br.com.finsavior.model.mapper;

import br.com.finsavior.model.dto.ExternalUserDTO;
import br.com.finsavior.model.entities.ExternalUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ExternalUserMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.userPlan.planId", target = "planId")
    @Mapping(source = "user.userPlan.pla", target = "planStatus")
    ExternalUserDTO toExternalUserDTO(ExternalUser externalUser);
}
