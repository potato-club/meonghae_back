package com.meonghae.profileservice.service;

import com.meonghae.profileservice.dto.PetInfoRequestDto;
import com.meonghae.profileservice.dto.PetInfoResponseDTO;
import com.meonghae.profileservice.entity.PetEntity;
import com.meonghae.profileservice.error.ErrorCode;
import com.meonghae.profileservice.error.exception.NotFoundException;
import com.meonghae.profileservice.repository.PetRepository;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PetService {
  private final PetRepository petRepository;
  private final RedisService redisService;

  public List<PetInfoResponseDTO> getUserPetList(HttpServletRequest request) {
    String userEmail = redisService.getUserEmail(request);

    List<PetEntity> petEntityList = petRepository.findByUserEmailOrderById(userEmail);
    return petEntityList.stream().map(PetInfoResponseDTO::new).collect(Collectors.toList());
  }
  // 한 마리의 정보
  public PetInfoResponseDTO getUserPet(Long id) {

    PetEntity petEntity =
        petRepository
            .findById(id)
            .orElseThrow(
                () -> {
                  throw new NotFoundException(
                      ErrorCode.NOT_FOUND_PET, ErrorCode.NOT_FOUND_PET.getMessage());
                });
    return new PetInfoResponseDTO(petEntity);
  }

  public String save(PetInfoRequestDto petInfoRequestDto, HttpServletRequest request) {
    String userEmail = redisService.getUserEmail(request);

    PetEntity petEntity =
        new PetEntity()
            .builder()
            .userEmail(userEmail)
            .petType(petInfoRequestDto.getPetType())
            .petName(petInfoRequestDto.getPetName())
            .petGender(petInfoRequestDto.getPetGender())
            .petBirth(petInfoRequestDto.getPetBirth())
            .petSpecies(petInfoRequestDto.getPetSpecies())
            .build();
    petRepository.save(petEntity);
    return "저장 완료";
  }

  public String update(Long id, PetInfoRequestDto petDTO, HttpServletRequest request) {
    PetEntity petEntity =
        petRepository
            .findById(id)
            .orElseThrow(
                () -> {
                  throw new NotFoundException(
                      ErrorCode.NOT_FOUND_PET, ErrorCode.NOT_FOUND_PET.getMessage());
                });

    petEntity
        .builder()
        .petSpecies(petDTO.getPetSpecies())
        .petGender(petDTO.getPetGender())
        .petBirth(petDTO.getPetBirth())
        .petName(petDTO.getPetName())
        .build();
    petRepository.save(petEntity);
    return "수정 완료";
  }

  public String deleteById(Long id, HttpServletRequest request) {
    // 인증 로직

    petRepository.deleteById(id);
    return "삭제 완료";
  }
}
