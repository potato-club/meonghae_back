package com.meonghae.profileservice.service;

import com.meonghae.profileservice.client.S3ServiceClient;
import com.meonghae.profileservice.dto.S3.S3RequestDto;
import com.meonghae.profileservice.dto.S3.S3ResponseDto;
import com.meonghae.profileservice.dto.S3.S3UpdateDto;
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
      S3ResponseDto images = s3ServiceClient.viewPetFile(new S3RequestDto(pet.getId(),"PET"));
      petInfoResponseDTO.setImage(images);
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

          List<MultipartFile> image = new ArrayList<>();
          image.add(imageList.get(i));

          s3ServiceClient.uploadImages(image, s3RequestDto);
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
      s3ServiceClient.uploadImages(images, s3RequestDto);
      savedPet.setHasImage();
    }
    return "저장 완료";
  }

  //===================
  @Transactional
  public String update(Long id, MultipartFile image,PetInfoRequestDto petDTO) {
    Pet updatedPet = petRepository.findById(id).orElseThrow(() -> {throw new NotFoundException(
            ErrorCode.NOT_FOUND_PET, ErrorCode.NOT_FOUND_PET.getMessage());});
    //기존 엔티티랑 비교해서 업데이트 시키고
    updatedPet.update(petDTO);

    //pet이 이미지를 가지고 있지 않고, 들어온 이미지가 null이 아닐때
    if ( !(updatedPet.isHasImage()) && image != null ){

      S3RequestDto s3RequestDto = new S3RequestDto(updatedPet.getId(),"PET");
      List<MultipartFile> images = new ArrayList<>();
      images.add(image);

      s3ServiceClient.uploadImages(images, s3RequestDto);
      updatedPet.setHasImage();

    }else if (updatedPet.isHasImage() && image != null){//이미지가 true면 위에 image와 기존 이미지를 바꾸고
      //사진 받아오기
      S3ResponseDto s3ResponseDto = s3ServiceClient.viewPetFile(new S3RequestDto(updatedPet.getId(),"PET"));

      S3UpdateDto s3UpdateDto = new S3UpdateDto(s3ResponseDto);

      List<MultipartFile> images = new ArrayList<>();
      images.add(image);
      List<S3UpdateDto> s3UpdateDtoList = new ArrayList<>();
      s3UpdateDtoList.add(s3UpdateDto);

      s3ServiceClient.updateFiles(images,s3UpdateDtoList);
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
