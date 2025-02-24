package org.nova.backend.auth.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nova.backend.auth.application.dto.request.SignUpRequest;
import org.nova.backend.auth.application.service.SignUpService;
import org.nova.backend.member.application.dto.response.PendingMemberResponse;
import org.nova.backend.member.application.dto.response.ProfilePhotoResponse;
import org.nova.backend.member.application.mapper.PendingMemberMapper;
import org.nova.backend.member.application.service.PendingMemberService;
import org.nova.backend.member.application.service.ProfilePhotoFileService;
import org.nova.backend.member.domain.model.entity.PendingMember;
import org.nova.backend.member.domain.model.entity.ProfilePhoto;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Sign Up API", description = "회원가입 API 목록")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class SignUpController {

    private final SignUpService signUpService;
    private final ProfilePhotoFileService profilePhotoFileService;
    private final PendingMemberService pendingMemberService;

    /**
     * 회원가입
     *
     * @param signUpRequest 회원가입 요청
     * @return 생성된 PendingMember response
     */
    @PostMapping()
    @AuthApiDocument.SignUpApiDoc
    public ApiResponse<PendingMemberResponse> signUp(@RequestBody SignUpRequest signUpRequest) {

        PendingMember savedPendingMember = signUpService.signUp(signUpRequest);

        PendingMemberResponse response = pendingMemberService.getPendingMemberResponseFromPendingMember(savedPendingMember);

        return ApiResponse.success(response);
    }

    /**
     * 회원가입시 프로필 사진 업로드
     */
    @RequestMapping(value = "/profile-photo", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @AuthApiDocument.UploadProfilePhotoApiDoc
    public ResponseEntity<ApiResponse<ProfilePhotoResponse>> uploadProfilePhoto(
            @RequestParam("profilePhoto") MultipartFile profilePhoto) {

        ProfilePhotoResponse profilePhotoResponse = profilePhotoFileService.uploadProfilePhoto(profilePhoto);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(profilePhotoResponse));
    }
}
