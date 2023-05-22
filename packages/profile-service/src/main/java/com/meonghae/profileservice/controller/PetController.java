package com.meonghae.profileservice.controller;

import com.meonghae.profileservice.dto.pet.PetInfoRequestDto;
import com.meonghae.profileservice.dto.pet.PetInfoResponseDTO;
import com.meonghae.profileservice.service.PetService;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/profile")
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
    return petService.getUserPet(id);
  }
  @Operation(summary = "반려동물 추가")
  @PostMapping
  public String add(@RequestBody PetInfoRequestDto petDTO, @RequestHeader("Authorization") String token) {
    return petService.save(petDTO, token);
  }
  @Operation(summary = "반려동물 수정")
  @PutMapping("/{id}")
  public String update(
      @PathVariable Long id, @RequestBody PetInfoRequestDto petDTO) {
    return petService.update(id, petDTO);
  }
  @Operation(summary = "반려동물 삭제")
  @DeleteMapping("/{id}")
  public String deleteById(@PathVariable Long id) {
    return petService.deleteById(id);
  }
}
