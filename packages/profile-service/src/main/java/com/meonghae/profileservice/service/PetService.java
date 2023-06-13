package com.meonghae.profileservice.service;

import com.meonghae.profileservice.client.S3ServiceClient;
import com.meonghae.profileservice.dto.S3.S3RequestDto;
import com.meonghae.profileservice.dto.S3.S3ResponseDto;
import com.meonghae.profileservice.dto.S3.S3UpdateDto;
import com.meonghae.profileservice.dto.pet.PetDetaileResponseDTO;
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
        S3ResponseDto image = s3ServiceClient.viewPetFile(new S3RequestDto(pet.getId(),"PET"));
        resultList.add(new PetInfoResponseDTO(pet,image));
      } else{
        resultList.add(new PetInfoResponseDTO(pet));
      }
    }
    return resultList;
  }

  // 한 마리의 정보 가져오기
  @Transactional
  public PetDetaileResponseDTO getOneOfPet(Long id) {
    Pet pet = petRepository.findById(id).orElseThrow(() -> {
                  throw new NotFoundException(ErrorCode.NOT_FOUND_PET, ErrorCode.NOT_FOUND_PET.getMessage());});
    PetDetaileResponseDTO petDetaileResponseDTO = new PetDetaileResponseDTO(pet);
    if (pet.isHasImage()){
      S3ResponseDto images = s3ServiceClient.viewPetFile(new S3RequestDto(pet.getId(),"PET"));
      petDetaileResponseDTO.setImage(images);
    }
    return petDetaileResponseDTO;
  }

//======================================================================

  @Transactional //펫과 이미지 저장
  public String savePetList (PetInfoRequestDto petDto, String token){
    String userEmail = feignService.getUserEmail(token);
    try {
        Pet pet = new Pet(petDto, userEmail);
        Pet savedPet = petRepository.save(pet);

        if (petDto.getImage() != null) {
          S3RequestDto s3RequestDto = new S3RequestDto(savedPet.getId(),"PET");

          List<MultipartFile> imageList = new ArrayList<>();
          imageList.add(petDto.getImage());

          s3ServiceClient.uploadImages(imageList, s3RequestDto);
          savedPet.setHasImage();
        }

    } catch (NullPointerException e){
      throw new NullPointerException("NULL 예외 발생");
    }
    return "저장완료";
  }

//  @Transactional//한 마리 저장
//  public String savePet(List<MultipartFile> images, PetInfoRequestDto petInfoRequestDto, String token) {
//    String userEmail = feignService.getUserEmail(token);
//    Pet pet = new Pet(petInfoRequestDto,userEmail);
//
//    Pet savedPet = petRepository.save(pet);
//    if (images != null){
//      S3RequestDto s3RequestDto = new S3RequestDto(savedPet.getId(),"PET");
//      s3ServiceClient.uploadImages(images, s3RequestDto);
//      savedPet.setHasImage();
//    }
//    return "저장 완료";
//  }

  //===================
  @Transactional
  public String update(Long id, PetInfoRequestDto petDto) {
    Pet updatedPet = petRepository.findById(id).orElseThrow(() -> {throw new NotFoundException(
            ErrorCode.NOT_FOUND_PET, ErrorCode.NOT_FOUND_PET.getMessage());});
    //기존 엔티티랑 비교해서 업데이트 시키고
    updatedPet.update(petDto);

    //pet이 이미지를 가지고 있지 않고, 들어온 이미지가 null이 아닐때
    if ( !(updatedPet.isHasImage()) && petDto.getImage() != null ){

      S3RequestDto s3RequestDto = new S3RequestDto(updatedPet.getId(),"PET");
      List<MultipartFile> images = new ArrayList<>();
      images.add(petDto.getImage());

      s3ServiceClient.uploadImages(images, s3RequestDto);
      updatedPet.setHasImage();

    }else if (updatedPet.isHasImage() && petDto.getImage() != null){
      //사진을 이미 가지고있고, 바뀔 image가 들릴때 기존 이미지를 삭제하고 새로 업로드
      //사진 받아오기
      S3ResponseDto s3ResponseDto = s3ServiceClient.viewPetFile(new S3RequestDto(updatedPet.getId(),"PET"));
      // 기존 사진 삭제처리
      S3UpdateDto s3UpdateDto = new S3UpdateDto(s3ResponseDto);

      List<MultipartFile> imageList = new ArrayList<>();
      imageList.add(petDto.getImage());
      List<S3UpdateDto> s3UpdateDtoList = new ArrayList<>();
      s3UpdateDtoList.add(s3UpdateDto);

      s3ServiceClient.updateFiles(imageList, s3UpdateDtoList);
    }

    // images == null 일때 서비스 코드 미구현

    return "수정 완료";
  }

  @Transactional
  public String deleteById(Long id) {

    petRepository.deleteById(id);
    return "삭제 완료";
  }
}
