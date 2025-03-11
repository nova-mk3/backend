package org.nova.backend.member.application.service;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.email.application.service.EmailAuthService;
import org.nova.backend.member.adapter.repository.GraduationRepository;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.application.dto.request.AuthCodeEmailRequest;
import org.nova.backend.member.application.dto.request.CheckAuthCodeRequest;
import org.nova.backend.member.application.dto.request.UpdateMemberRequest;
import org.nova.backend.member.application.dto.request.UpdatePasswordRequest;
import org.nova.backend.member.application.dto.response.MemberResponse;
import org.nova.backend.member.application.dto.response.MyPageMemberResponse;
import org.nova.backend.member.application.mapper.GraduationMapper;
import org.nova.backend.member.application.mapper.MemberMapper;
import org.nova.backend.member.domain.exception.MemberDomainException;
import org.nova.backend.member.domain.model.entity.Graduation;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.member.domain.model.entity.ProfilePhoto;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final GraduationRepository graduationRepository;

    private final ProfilePhotoFileService profilePhotoFileService;
    private final EmailAuthService emailAuthService;

    private final MemberMapper memberMapper;
    private final GraduationMapper graduationMapper;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 모든 회원 리스트 불러오기
     *
     * @return List<MemberResponse>
     */
    public List<MemberResponse> getAllMembers() {
        List<Member> memberList = memberRepository.findAllMembers();

        return memberList.stream().map(this::getMemberResponseFromMember).toList();
    }

    /**
     * id로 회원 찾기
     */
    public Member findByMemberId(UUID memberId) {
        return memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new MemberDomainException("member not found.", HttpStatus.NOT_FOUND));
    }

    /**
     * Member 응답 객체 생성
     *
     * @param member
     * @return MemberResponse
     */
    public MemberResponse getMemberResponseFromMember(Member member) {
        ProfilePhoto profilePhoto = profilePhotoFileService.getProfilePhoto(member.getProfilePhoto());
        return memberMapper.toResponse(member, profilePhoto);
    }

    /**
     * 로그인한 Member가 본인의 프로필을 조회하는지 로그인한 Member가 본인의 프로필을 조회하지 않는 경우 loginMember의 유효성 검증.
     */
    private boolean isLoginMember(UUID profileMemberId, UUID loginMemberId) {
        return loginMemberId.equals(profileMemberId);
    }

    /**
     * 회원 정보 접근 권한 확인
     */
    private void validateMemberAuthorize(UUID profileMemberId, UUID loginMemberId) {
        if (!isLoginMember(profileMemberId, loginMemberId)) {
            throw new MemberDomainException("회원 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
    }

    /**
     * 회원 프로필 정보 조회
     *
     * @param profileMemberId 조회할 프로필 Member id
     * @param loginMemberId   현재 로그인한 Member id
     */
    public MyPageMemberResponse getMemberProfile(UUID profileMemberId, UUID loginMemberId) {
        Member profileMember = findByMemberId(profileMemberId);
        boolean isLoginMember = isLoginMember(profileMemberId, loginMemberId);
        if (!isLoginMember) {
            findByMemberId(loginMemberId);
        }

        return new MyPageMemberResponse(isLoginMember, getMemberResponseFromMember(profileMember));
    }

    /**
     * 회원 탈퇴
     *
     * @param profileMemberId 조회할 프로필 Member id
     * @param loginMemberId   현재 로그인한 Member id
     */
    @Transactional
    public void deleteMember(UUID profileMemberId, UUID loginMemberId) {
        validateMemberAuthorize(profileMemberId, loginMemberId);

        Member member = findByMemberId(loginMemberId);
        member.setDeleted();
    }

    /**
     * 회원 정보 수정
     *
     * @param profileMemberId 조회할 프로필 Member id
     * @param loginMemberId   현재 로그인한 Member id
     */
    @Transactional
    public MyPageMemberResponse updateMember(UUID profileMemberId, UUID loginMemberId,
                                             UpdateMemberRequest updateMemberRequest) {
        validateMemberAuthorize(profileMemberId, loginMemberId);
        Member member = findByMemberId(loginMemberId);

        // 졸업생 정보 수정
        if (updateMemberRequest.getUpdateMemberProfileRequest().isGraduation()) {
            if (member.isGraduation()) {  // 졸업생 정보 수정 : 기존에 졸업생 정보가 있음.
                member.getGraduation().updateProfile(updateMemberRequest.getUpdateGraduationRequest());
            } else {  //졸업생 정보 생성
                Graduation newGraduation = graduationMapper.toEntity(updateMemberRequest.getUpdateGraduationRequest());
                graduationRepository.save(newGraduation);
                member.updateGraduationInfo(newGraduation);
            }
        }

        // 기본 회원 정보 수정
        member.updateProfileInfo(updateMemberRequest.getUpdateMemberProfileRequest());

        return new MyPageMemberResponse(true, getMemberResponseFromMember(member));
    }

    /**
     * 기존 비밀번호 확인
     */
    private void checkPassword(Member member, String password) {
        if (!bCryptPasswordEncoder.matches(password, member.getPassword())) {
            throw new MemberDomainException("비밀번호를 확인해주세요.", HttpStatus.CONFLICT);
        }
    }

    /**
     * 비밀번호 변경
     *
     * @param profileMemberId       조회할 프로필 Member id
     * @param loginMemberId         현재 로그인한 Member id
     * @param updatePasswordRequest 비밀번호 요청 객체
     */
    @Transactional
    public void updatePassword(UUID profileMemberId, UUID loginMemberId, UpdatePasswordRequest updatePasswordRequest) {
        validateMemberAuthorize(profileMemberId, loginMemberId);

        Member currendMember = findByMemberId(loginMemberId);
        checkPassword(currendMember, updatePasswordRequest.getCurrentPassword());

        if (!updatePasswordRequest.getNewPassword().equals(updatePasswordRequest.getCheckNewPassword())) {
            throw new MemberDomainException("새 비밀번호와 비밀번호 확인이 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        currendMember.updatePassword(bCryptPasswordEncoder.encode(updatePasswordRequest.getNewPassword()));
    }

    /**
     * 회원 이메일로 이메일 변경을 위한 인증코드 전송
     *
     * @param profileMemberId      조회할 프로필 Member id
     * @param loginMemberId        현재 로그인한 Member id
     * @param authCodeEmailRequest 이메일 인증코드 요청 객체
     */
    //@Transactional이 필요한가?
    public void sendEmailAuthCode(UUID profileMemberId, UUID loginMemberId, AuthCodeEmailRequest authCodeEmailRequest) {
        validateMemberAuthorize(profileMemberId, loginMemberId);
        findByMemberId(loginMemberId);
        emailAuthService.sendAuthCodeEmail(authCodeEmailRequest.getEmail());
    }

    /**
     * 이메일 인증코드 확인
     *
     * @param profileMemberId      조회할 프로필 Member id
     * @param loginMemberId        현재 로그인한 Member id
     * @param checkAuthCodeRequest 이메일 인증코드 확인 요청 객체
     */
    @Transactional
    public void checkEmailAuthCode(UUID profileMemberId, UUID loginMemberId,
                                   CheckAuthCodeRequest checkAuthCodeRequest) {
        validateMemberAuthorize(profileMemberId, loginMemberId);
        findByMemberId(loginMemberId);

        String newEmail = checkAuthCodeRequest.getEmail();
        emailAuthService.checkAuthCode(newEmail, checkAuthCodeRequest.getAuthCode());
    }

    /**
     * 회원 이메일 변경
     *
     * @param profileMemberId 조회할 프로필 Member id
     * @param loginMemberId   현재 로그인한 Member id
     * @param newEmail        새 이메일
     */
    @Transactional
    public void updateEmail(UUID profileMemberId, UUID loginMemberId, String newEmail) {
        validateMemberAuthorize(profileMemberId, loginMemberId);
        Member currentMember = findByMemberId(loginMemberId);

        currentMember.updateEmail(newEmail);
    }

    /**
     * 회원 프로필 사진 변경
     *
     * @param profileMemberId   조회할 프로필 Member id
     * @param loginMemberId     현재 로그인한 Member id
     * @param newProfilePhotoId 바꿀 프로필 사진 pk
     */
    @Transactional
    public void updateProfilePhoto(UUID profileMemberId, UUID loginMemberId, UUID newProfilePhotoId) {
        validateMemberAuthorize(profileMemberId, loginMemberId);

        Member currentMember = findByMemberId(loginMemberId);
        ProfilePhoto currentProfilePhoto = currentMember.getProfilePhoto();

        ProfilePhoto newProfilePhoto = profilePhotoFileService.findProfilePhotoById(newProfilePhotoId);
        currentMember.updateProfilePhoto(newProfilePhoto);

        profilePhotoFileService.deleteProfilePhotoById(currentProfilePhoto.getId());
    }
}
