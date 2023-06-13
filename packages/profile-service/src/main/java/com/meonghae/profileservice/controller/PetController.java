package com.meonghae.profileservice.controller;

import com.meonghae.profileservice.dto.pet.PetDetaileResponseDTO;
import com.meonghae.profileservice.dto.pet.PetInfoRequestDto;
import com.meonghae.profileservice.dto.pet.PetInfoResponseDTO;
import com.meonghae.profileservice.service.PetService;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;


@RestController
@RequestMapping("/profile")
@Api(value = "Pet Controller", tags = "반려동물 관련 서비스 API")
@RequiredArgsConstructor
@Slf4j
public class PetController {
  private final PetService petService;
  @Operation(summary = "유저의 반려동물 리스트")
  @GetMapping // user의 반려동물 리스트
  public List<PetInfoResponseDTO> getUserPetList(@ApiParam(value = "사용자 토큰", required = true) @RequestHeader("Authorization") String token) {
    return petService.getUserPetList(token);
  }
  @Operation(summary = "한 마리의 반려동물 정보")
  @GetMapping("/{id}") // 하나의 반려동물
  public PetDetaileResponseDTO getUserPet(@ApiParam(value = "반려동물 id", required = true) @PathVariable Long id) {
    return petService.getOneOfPet(id);
  }

  @Operation(summary = "반려동물 리스트 추가 [ 3마리까지만 테스트 부탁 ]")
  @PostMapping("")
  public String addPetList( PetInfoRequestDto petDto,
                           @ApiParam(value = "사용자 토큰", required = true) @RequestHeader("Authorization") String token){

    return petService.savePetList(petDto, token);
  }

  @Operation(summary = "반려동물 수정")
  @PutMapping("/{id}")
  public String update(
          @ApiParam(value = "반려동물 id", required = true)@PathVariable Long id,
          PetInfoRequestDto petDto) {

    return petService.update(id, petDto);
  }
  @Operation(summary = "반려동물 삭제")
  @DeleteMapping("/{id}")
  public String deleteById(@PathVariable Long id) {
    return petService.deleteById(id);
  }


}
