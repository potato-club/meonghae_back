package com.meonghae.profileservice.service;

import com.meonghae.profileservice.dto.PetInfoRequestDto;
import com.meonghae.profileservice.dto.PetInfoResponseDTO;
import com.meonghae.profileservice.entity.PetEntity;
import com.meonghae.profileservice.error.ErrorCode;
import com.meonghae.profileservice.error.exception.NotFoundException;
import com.meonghae.profileservice.repository.PetRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PetService {
    private final PetRepository petRepository;

    public List<PetInfoResponseDTO> getUserPetList(HttpServletRequest request){
        //인증 로직

        List<PetEntity> petEntityList = petRepository.findByUserNameOrderById("명재");
        return petEntityList.stream().map(PetInfoResponseDTO::new).collect(Collectors.toList());
    }
    // 한 마리의 정보
    public PetInfoResponseDTO getUserPet(Long id, HttpServletRequest request){
        //인증 로직

        PetEntity petEntity = petRepository.findById(id).orElseThrow(()->{throw new NotFoundException(ErrorCode.NOT_FOUND_PET,ErrorCode.NOT_FOUND_PET.getMessage());});
        return new PetInfoResponseDTO(petEntity);
    }
    public String save(PetInfoRequestDto petInfoRequestDto, HttpServletRequest request) {
        //인증 로직

        PetEntity petEntity = new PetEntity().builder()
                .userName("명재")
                .petType(petInfoRequestDto.getPetType())
                .petName(petInfoRequestDto.getPetName())
                .petGender(petInfoRequestDto.getPetGender())
                .petBirth(petInfoRequestDto.getPetBirth())
                .petSpecies(petInfoRequestDto.getPetSpecies())
                .build();
        petRepository.save(petEntity);
        return "저장 완료";
    }

    public String update(Long id, PetInfoRequestDto petDTO,HttpServletRequest request) {
        //인증 로직

        PetEntity petEntity = petRepository.findById(id).orElseThrow(()->{throw new NotFoundException(ErrorCode.NOT_FOUND_PET,ErrorCode.NOT_FOUND_PET.getMessage());});
       petEntity.builder()
               .petSpecies(petDTO.getPetSpecies())
               .petGender(petDTO.getPetGender())
               .petBirth(petDTO.getPetBirth())
               .petName(petDTO.getPetName())
               .build();
       petRepository.save(petEntity);
       return "수정 완료";
    }

    public String deleteById(Long id,HttpServletRequest request) {
        //인증 로직

        petRepository.deleteById(id);
        return "삭제 완료";
    }

}
