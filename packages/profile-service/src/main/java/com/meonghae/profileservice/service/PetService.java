package com.meonghae.profileservice.service;

import com.meonghae.profileservice.client.S3ServiceClient;
import com.meonghae.profileservice.dto.S3.S3RequestDto;
import com.meonghae.profileservice.dto.S3.S3ResponseDto;
import com.meonghae.profileservice.dto.pet.PetInfoRequestDto;
import com.meonghae.profileservice.dto.pet.PetInfoResponseDTO;
import com.meonghae.profileservice.entity.Pet;
import com.meonghae.profileservice.error.ErrorCode;
import com.meonghae.profileservice.error.exception.NotFoundException;
import com.meonghae.profileservice.repository.PetRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PetService {
  private final PetRepository petRepository;
  private final FeignService feignService;
  private final S3ServiceClient s3ServiceClient;

  // 여러마리 정보 가져오기
  @Transactional
  public List<PetInfoResponseDTO> getUserPetList(String token) {
    String userEmail = feignService.getUserEmail(token);
    List<Pet> petList = petRepository.findByUserEmail(userEmail);
    List<PetInfoResponseDTO> resultList = new ArrayList<>();

    for (Pet pet : petList){
      if ( pet.isHasImage() ){
        List<S3ResponseDto> image = s3ServiceClient.getImages(new S3RequestDto(pet.getId(),"PET"));
        resultList.add(new PetInfoResponseDTO(pet,image.get(0)));
      }
    }
    return resultList;
  }

  // 한 마리의 정보 가져오기
  @Transactional
  public PetInfoResponseDTO getOneOfPet(Long id) {
    Pet pet = petRepository.findById(id).orElseThrow(() -> {
                  throw new NotFoundException(ErrorCode.NOT_FOUND_PET, ErrorCode.NOT_FOUND_PET.getMessage());});
    PetInfoResponseDTO petInfoResponseDTO = new PetInfoResponseDTO(pet);
    if (pet.isHasImage()){
      List<S3ResponseDto> images = s3ServiceClient.getImages(new S3RequestDto(pet.getId(),"PET"));
      petInfoResponseDTO.setImage(images.get(0));
    }
    return  petInfoResponseDTO;
  }

//======================================================================

  @Transactional //다수 펫과 다수 이미지 저장
  public String savePetList (List<MultipartFile> imageList, List<PetInfoRequestDto> petDtoList, String token){
    String userEmail = feignService.getUserEmail(token);
    try {
      for (int i = 0; i < petDtoList.size(); i++) {
        Pet pet = new Pet(petDtoList.get(i), userEmail);
        Pet savedPet = petRepository.save(pet);

        if (imageList.get(i) != null) {
          S3RequestDto s3RequestDto = new S3RequestDto(savedPet.getId(),"PET");

          //현호형 list 말고 이미지 하나 받는거 만들어줘...
          List<MultipartFile> image = new ArrayList<>();
          image.add(imageList.get(i));

          s3ServiceClient.uploadImage(image, s3RequestDto);
          savedPet.setHasImage();
        }
      }
    } catch (NullPointerException e){
      throw new NullPointerException("NULL 예외 발생");
    }
    return "저장완료";
  }

  @Transactional//한 마리 저장
  public String savePet(List<MultipartFile> images, PetInfoRequestDto petInfoRequestDto, String token) {
    String userEmail = feignService.getUserEmail(token);
    Pet pet = new Pet(petInfoRequestDto,userEmail);

    Pet savedPet = petRepository.save(pet);
    if (images != null){
      S3RequestDto s3RequestDto = new S3RequestDto(savedPet.getId(),"PET");
      s3ServiceClient.uploadImage(images, s3RequestDto);
      savedPet.setHasImage();
    }
    return "저장 완료";
  }

  //===================
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
