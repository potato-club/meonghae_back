package com.meonghae.profileservice.service;

import com.meonghae.profileservice.dto.pet.PetInfoRequestDto;
import com.meonghae.profileservice.dto.pet.PetInfoResponseDTO;
import com.meonghae.profileservice.entity.Pet;
import com.meonghae.profileservice.error.ErrorCode;
import com.meonghae.profileservice.error.exception.NotFoundException;
import com.meonghae.profileservice.repository.PetRepository;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PetService {
  private final PetRepository petRepository;
  private final FeignService feignService;
  @Transactional
  public List<PetInfoResponseDTO> getUserPetList(String token) {
    String userEmail = feignService.getUserEmail(token);

    List<Pet> petList = petRepository.findByUserEmailOrderById(userEmail);
    return petList.stream().map(PetInfoResponseDTO::new).collect(Collectors.toList());
  }
  // 한 마리의 정보
  @Transactional
  public PetInfoResponseDTO getUserPet(Long id) {

    Pet pet =
        petRepository
            .findById(id)
            .orElseThrow(
                () -> {
                  throw new NotFoundException(
                      ErrorCode.NOT_FOUND_PET, ErrorCode.NOT_FOUND_PET.getMessage());
                });
    return new PetInfoResponseDTO(pet);
  }
  @Transactional
  public String save(PetInfoRequestDto petInfoRequestDto, String token) {
    String userEmail = feignService.getUserEmail(token);

    Pet pet =
        new Pet()
            .builder()
            .userEmail(userEmail)
            .petType(petInfoRequestDto.getPetType())
            .petName(petInfoRequestDto.getPetName())
            .petGender(petInfoRequestDto.getPetGender())
            .petBirth(petInfoRequestDto.getPetBirth())
            .petSpecies(petInfoRequestDto.getPetSpecies())
            .build();
    petRepository.save(pet);
    return "저장 완료";
  }
  @Transactional
  public String update(Long id, PetInfoRequestDto petDTO) {
    Pet pet =
        petRepository
            .findById(id)
            .orElseThrow(
                () -> {
                  throw new NotFoundException(
                      ErrorCode.NOT_FOUND_PET, ErrorCode.NOT_FOUND_PET.getMessage());
                });

    pet
        .builder()
        .petSpecies(petDTO.getPetSpecies())
        .petGender(petDTO.getPetGender())
        .petBirth(petDTO.getPetBirth())
        .petName(petDTO.getPetName())
        .build();
    petRepository.save(pet);
    return "수정 완료";
  }
  @Transactional
  public String deleteById(Long id) {
    // 인증 로직

    petRepository.deleteById(id);
    return "삭제 완료";
  }
}
