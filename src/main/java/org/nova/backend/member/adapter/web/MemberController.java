package org.nova.backend.member.adapter.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.auth.adapter.web.AuthApiDocument;
import org.nova.backend.board.util.SecurityUtil;
import org.nova.backend.member.application.dto.request.AuthCodeEmailRequest;
import org.nova.backend.member.application.dto.request.CheckAuthCodeRequest;
import org.nova.backend.member.application.dto.request.UpdateMemberRequest;
import org.nova.backend.member.application.dto.request.UpdatePasswordRequest;
import org.nova.backend.member.application.dto.response.MemberSimpleProfileResponse;
import org.nova.backend.member.application.dto.response.MyPageMemberResponse;
import org.nova.backend.member.application.dto.response.ProfilePhotoResponse;
import org.nova.backend.member.application.service.MemberService;
import org.nova.backend.member.application.service.ProfilePhotoFileService;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Member API", description = "회원 정보를 관리합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;
    private final ProfilePhotoFileService profilePhotoFileService;

    private final SecurityUtil securityUtil;

    /**
     * 현재 로그인한 회원의 pk 조회, 로그인 되어있지 않으면 null 보냄
     */
    @GetMapping("")
    @MemberProfileApiDocument.GetMemberPKApiDoc
    public ResponseEntity<ApiResponse<UUID>> getMemberPK() {
        Optional<UUID> memberId = securityUtil.getOptionalCurrentMemberId();

        if(memberId.isEmpty()){  //로그인한 회원이 없으면 null 반환
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
        }

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(memberId.get()));
    }

    /**
     * 현재 로그인한 회원의 간단 프로필 조회, 로그인 되어있지 않으면 null보냄
     */
    @GetMapping("/simple-profile")
    @MemberProfileApiDocument.GetMemberSimpleProfileApiDoc
    public ResponseEntity<ApiResponse<MemberSimpleProfileResponse>> getSimpleProfile() {
        Optional<UUID> memberId = securityUtil.getOptionalCurrentMemberId();
        if(memberId.isEmpty()){  //로그인한 회원이 없으면 null 반환
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
        }

        MemberSimpleProfileResponse response = memberService.getSimpleProfile(memberId.get());

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }


    /**
     * 회원 정보 조회
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{profileMemberId}")
    @MemberProfileApiDocument.GetMemberProfileApiDoc
    public ResponseEntity<ApiResponse<MyPageMemberResponse>> getMemberProfile(@PathVariable UUID profileMemberId) {
        UUID loginMemberId = securityUtil.getCurrentMemberId();
        MyPageMemberResponse response = memberService.getMemberProfile(profileMemberId, loginMemberId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    /**
     * 회원 탈퇴
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{profileMemberId}")
    @AuthApiDocument.WithdrawalApiDoc
    public ResponseEntity<ApiResponse<String>> withdrawal(@PathVariable UUID profileMemberId,
                                                          HttpServletResponse response) {
        UUID loginMemberId = securityUtil.getCurrentMemberId();
        memberService.deleteMember(profileMemberId, loginMemberId);

        // auth token 담은 쿠키 제거
        Cookie cookie = new Cookie("AUTH_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("회원 탈퇴 성공"));
    }

    /**
     * 회원 본인 정보 수정
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{profileMemberId}")
    @MemberProfileApiDocument.UpdateMemberProfileApiDoc
    public ResponseEntity<ApiResponse<MyPageMemberResponse>> updateMemberProfile(@PathVariable UUID profileMemberId,
                                                                                 @RequestBody
                                                                                 UpdateMemberRequest updateMemberRequest) {
        UUID loginMemberId = securityUtil.getCurrentMemberId();
        MyPageMemberResponse response = memberService.updateMember(profileMemberId, loginMemberId, updateMemberRequest);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }


    /**
     * 회원 비밀번호 변경
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{profileMemberId}/password")
    @MemberProfileApiDocument.UpdatePasswordApiDoc
    public ResponseEntity<ApiResponse<String>> updatePassword(@PathVariable UUID profileMemberId,
                                                              @RequestBody UpdatePasswordRequest updatePasswordRequest,
                                                              HttpServletResponse response) {
        UUID loginMemberId = securityUtil.getCurrentMemberId();
        memberService.updatePassword(profileMemberId, loginMemberId, updatePasswordRequest);

        //세션 로그아웃 핸들러 설정해야하는건가?

        // auth token 담은 쿠키 제거
        Cookie cookie = new Cookie("AUTH_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("비밀번호 변경 완료. 다시 로그인해주세요."));
    }

    /**
     * 회원 이메일 변경을 위한 인증코드 전송
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{profileMemberId}/email/send")
    @MemberProfileApiDocument.SendEmailAuthCode
    public ResponseEntity<ApiResponse<String>> sendEmailAuthCode(@PathVariable UUID profileMemberId,
                                                                 @RequestBody AuthCodeEmailRequest authCodeEmailRequest) {
        UUID loginMemberId = securityUtil.getCurrentMemberId();
        memberService.sendEmailAuthCode(profileMemberId, loginMemberId, authCodeEmailRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(authCodeEmailRequest.getEmail() + "로 인증 코드를 전송했습니다."));
    }

    /**
     * 회원 이메일 조회
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{profileMemberId}/email")
    @MemberProfileApiDocument.GetEmail
    public ResponseEntity<ApiResponse<String>> getEmail(@PathVariable UUID profileMemberId) {
        UUID loginMemberId = securityUtil.getCurrentMemberId();
        String email = memberService.getEmail(profileMemberId, loginMemberId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(email));
    }

    /**
     * 회원 이메일 인증 코드 확인
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{profileMemberId}/email/check")
    @MemberProfileApiDocument.CheckEmailAuthCode
    public ResponseEntity<ApiResponse<String>> checkEmailAuthCode(@PathVariable UUID profileMemberId,
                                                                  @RequestBody CheckAuthCodeRequest checkAuthCodeRequest) {
        UUID loginMemberId = securityUtil.getCurrentMemberId();
        memberService.checkEmailAuthCode(profileMemberId, loginMemberId, checkAuthCodeRequest);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("인증 코드 확인 완료"));
    }

    /**
     * 회원 이메일 변경
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{profileMemberId}/email")
    @MemberProfileApiDocument.UpdateEmail
    public ResponseEntity<ApiResponse<String>> updateEmail(@PathVariable UUID profileMemberId,
                                                           @RequestBody String email) {
        UUID loginMemberId = securityUtil.getCurrentMemberId();
        memberService.updateEmail(profileMemberId, loginMemberId, email);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("이메일 변경 완료"));
    }

    /**
     * 회원 프로필 사진 업로드
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{profileMemberId}/profile-photo", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @MemberProfileApiDocument.UploadProfilePhoto
    public ResponseEntity<ApiResponse<ProfilePhotoResponse>> uploadProfilePhoto(@PathVariable UUID profileMemberId,
                                                                                @RequestParam("profilePhoto") MultipartFile profilePhoto) {
        ProfilePhotoResponse profilePhotoResponse = profilePhotoFileService.uploadProfilePhoto(profilePhoto);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(profilePhotoResponse));
    }

    /**
     * 회원 프로필 사진 변경
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{profileMemberId}/profile-photo")
    @MemberProfileApiDocument.UpdateProfilePhoto
    public ResponseEntity<ApiResponse<ProfilePhotoResponse>> updateProfilePhoto(@PathVariable UUID profileMemberId,
                                                                                @RequestBody UUID profilePhoto) {
        UUID loginMemberId = securityUtil.getCurrentMemberId();
        ProfilePhotoResponse response = memberService.updateProfilePhoto(profileMemberId, loginMemberId, profilePhoto);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    /**
     * 회원 프로필 사진 삭제
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{profileMemberId}/profile-photo")
    @MemberProfileApiDocument.DeleteProfilePhoto
    public ResponseEntity<ApiResponse<ProfilePhotoResponse>> deleteProfilePhoto(@PathVariable UUID profileMemberId) {
        UUID loginMemberId = securityUtil.getCurrentMemberId();
        ProfilePhotoResponse profilePhotoResponse = memberService.deleteProfilePhoto(profileMemberId, loginMemberId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(profilePhotoResponse));
    }

    /**
     * 회원 프로필 사진 다운로드
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{profileMemberId}/profile-photo/download")
    @MemberProfileApiDocument.DownloadProfilePhoto
    public void downloadProfilePhoto(@PathVariable UUID profileMemberId, HttpServletResponse response) {
        UUID loginMemberId = securityUtil.getCurrentMemberId();
        memberService.downloadProfilePhoto(profileMemberId, response, loginMemberId);
    }

}
