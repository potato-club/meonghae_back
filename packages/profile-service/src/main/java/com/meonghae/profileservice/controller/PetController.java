package com.meonghae.profileservice.controller;

import com.meonghae.profileservice.dto.pet.PetInfoRequestDto;
import com.meonghae.profileservice.dto.pet.PetInfoResponseDTO;
import com.meonghae.profileservice.service.PetService;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/profile")
@Api(value = "Pet Controller", tags = "반려동물 관련 서비스 API")
@RequiredArgsConstructor
public class PetController {
  private final PetService petService;
  @Operation(summary = "유저의 반려동물 리스트")
  @GetMapping // uset의 반려동물 리스트
  public List<PetInfoResponseDTO> getUserPetList(@RequestHeader("Authorization") String token) {
    return petService.getUserPetList(token);
  }
  @Operation(summary = "한 마리의 반려동물 정보")
  @GetMapping("/{id}") // 하나의 반려동물
  public PetInfoResponseDTO getUserPet(@PathVariable Long id) {
    return petService.getOneOfPet(id);
  }
  @Operation(summary = "반려동물 추가")
  @PostMapping
  public String add(
          @RequestPart List<MultipartFile> images,
          @RequestPart PetInfoRequestDto petDTO,
          @RequestHeader("Authorization") String token) {

    return petService.savePet(images, petDTO, token);
  }
  @PostMapping("/test")
  public String addPetList(@RequestPart List<MultipartFile> images,
                           @RequestPart List<PetInfoRequestDto> petListDto,
                           @RequestHeader("Authorization") String token){

    return petService.savePetList(images, petListDto, token);
  }

  @Operation(summary = "반려동물 수정")
  @PutMapping("/{id}")
  public String update(
      @PathVariable Long id,
      @RequestPart MultipartFile image,
      @RequestPart PetInfoRequestDto petDto) {

    return petService.update(id, image, petDto);
  }
  @Operation(summary = "반려동물 삭제")
  @DeleteMapping("/{id}")
  public String deleteById(@PathVariable Long id) {
    return petService.deleteById(id);
  }


}
