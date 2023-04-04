package com.meonghae.profileservice.service;

import com.meonghae.profileservice.dto.PetInfoRequestDto;
import com.meonghae.profileservice.dto.PetInfoResponseDTO;
import com.meonghae.profileservice.entity.PetEntity;
import com.meonghae.profileservice.error.ErrorCode;
import com.meonghae.profileservice.error.exception.NotFoundException;
import com.meonghae.profileservice.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PetService {
    private final PetRepository petRepository;

    public List<PetInfoResponseDTO> getUserPet(HttpServletRequest request){
        //user at에서 네임 빼오기
        String userName = "조금주";
        List<PetEntity> petEntities = petRepository.findByUserName(userName);
        return petEntities.stream().map(PetInfoResponseDTO::new).collect(Collectors.toList());
    }
    //하나 가져오는건 없어도 됌 해당 동물 캘린더 필요

    public String save(PetInfoRequestDto petInfoRequestDto, HttpServletRequest request) {
        PetEntity petEntity = new PetEntity(petInfoRequestDto);
        petRepository.save(petEntity);
        return "저장 완료";
    }

    public String update(Long id, PetInfoRequestDto petDTO,HttpServletRequest request) {
       PetEntity petEntity = petRepository.findById(id).orElseThrow(()->{throw new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION,"E0004");});
       petEntity.builder()
               .petSpecies(petDTO.getPetSpecies())
               .petGender(petDTO.getPetGender())
               .petBirth(petDTO.getPetBirth())
               .petName(petDTO.getPetName())
               .build();
       petRepository.save(petEntity);
       return "수정 완료";
    }

    public String deleteById(Long id) {
        petRepository.deleteById(id);
        return "삭제 완료";
    }

}
