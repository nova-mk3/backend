package org.nova.backend.member.application.service;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.email.application.service.EmailAuthService;
import org.nova.backend.member.adapter.repository.GraduationRepository;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.application.dto.request.AuthCodeEmailRequest;
import org.nova.backend.member.application.dto.request.CheckAuthCodeRequest;
import org.nova.backend.member.application.dto.request.UpdateGraduationRequest;
import org.nova.backend.member.application.dto.request.UpdateMemberProfileRequest;
import org.nova.backend.member.application.dto.request.UpdateMemberRequest;
import org.nova.backend.member.application.dto.request.UpdatePasswordRequest;
import org.nova.backend.member.application.dto.response.GraduationResponse;
import org.nova.backend.member.application.dto.response.MemberDetailResponse;
import org.nova.backend.member.application.dto.response.MemberForListResponse;
import org.nova.backend.member.application.dto.response.MemberResponse;
import org.nova.backend.member.application.dto.response.MemberSimpleProfileResponse;
import org.nova.backend.member.application.dto.response.MyPageMemberResponse;
import org.nova.backend.member.application.dto.response.ProfilePhotoResponse;
import org.nova.backend.member.application.mapper.GradeSemesterYearMapper;
import org.nova.backend.member.application.mapper.GraduationMapper;
import org.nova.backend.member.application.mapper.MemberMapper;
import org.nova.backend.member.application.mapper.MemberProfilePhotoMapper;
import org.nova.backend.member.domain.exception.MemberDomainException;
import org.nova.backend.member.domain.exception.ProfilePhotoFileDomainException;
import org.nova.backend.member.domain.model.entity.Graduation;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.member.domain.model.entity.ProfilePhoto;
import org.nova.backend.member.domain.model.valueobject.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    @Value("${admin.student.number}")
    private String adminStudentNumber;

    private final MemberRepository memberRepository;
    private final GraduationRepository graduationRepository;

    private final ProfilePhotoFileService profilePhotoFileService;
    private final EmailAuthService emailAuthService;

    private final GradeSemesterYearMapper gradeSemesterYearMapper;
    private final MemberMapper memberMapper;
    private final GraduationMapper graduationMapper;
    private final MemberProfilePhotoMapper memberProfilePhotoMapper;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 모든 회원 리스트 불러오기
     *
     * @return List<MemberForListResponse>
     */
    public List<MemberForListResponse> getAllMemberListResponse() {
        List<Member> memberList = memberRepository.findAllMembers(adminStudentNumber);

        return memberList.stream().map(this::getMemberForListResponseFromMember).toList();
    }

    /**
     * 모든 회원 리스트 불러오기
     *
     * @return List<MemberResponse>
     */
    public List<MemberResponse> getAllMembers() {
        List<Member> memberList = memberRepository.findAllMembers(adminStudentNumber);

        return memberList.stream().map(this::getMemberResponseFromMember).toList();
    }

    /**
     * 특정 학년 회원 리스트 불러오기
     *
     * @return List<Member>
     */
    public List<Member> getAllMembersByGrade(int grade) {
        if (grade == 0) { //졸업생
            return memberRepository.findAllMembersByGraduation(adminStudentNumber);
        }
        if (grade > 4) {  // 초과학기
            return memberRepository.findByGradeGreaterThan(grade, adminStudentNumber);
        }
        //재학생
        return memberRepository.findAllMembersByGrade(grade, adminStudentNumber);
    }

    /**
     * 특정 학년 회원 리스트 불러오기
     *
     * @return List<MemberResponse>
     */
    public List<MemberResponse> getAllMembersResponseByGrade(int grade) {
        List<Member> memberList = getAllMembersByGrade(grade);
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
     * Member List용 간소화된 응답 객체 생성
     *
     * @param member
     * @return MemberForListResponse
     */
    public MemberForListResponse getMemberForListResponseFromMember(Member member) {
        ProfilePhoto profilePhoto = profilePhotoFileService.getProfilePhoto(member.getProfilePhoto());
        return memberMapper.toResponseForList(member, profilePhoto);
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
     * Graduation 응답 객체 생성
     */
    public GraduationResponse getGraduationFromMember(Graduation graduation) {
        return graduationMapper.toResponse(graduation);
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
     * 회원 간단 프로필 조회
     *
     * @param memberId 현재 로그인한 사용자
     * @return 간단 프로필 응답 객체
     */
    public MemberSimpleProfileResponse getSimpleProfile(UUID memberId) {
        Member profileMember = findByMemberId(memberId);

        ProfilePhoto profilePhoto = profilePhotoFileService.getProfilePhoto(profileMember.getProfilePhoto());
        ProfilePhotoResponse profilePhotoResponse = memberProfilePhotoMapper.toResponse(profilePhoto);
        boolean isAdmin = profileMember.getRole() == Role.ADMINISTRATOR;

        return new MemberSimpleProfileResponse(profileMember.getId(), profileMember.getName(), profilePhotoResponse,
                isAdmin);
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

        return new MyPageMemberResponse(isLoginMember, getMemberResponseFromMember(profileMember),
                getGraduationFromMember(profileMember.getGraduation()));
    }

    /**
     * 회원 프로필 정보 조회
     *
     * @param memberId 조회할 프로필 Member id
     */
    public MemberDetailResponse getMemberProfile(UUID memberId) {
        Member profileMember = findByMemberId(memberId);

        Graduation graduation = profileMember.isGraduation() ? profileMember.getGraduation() : null;
        return new MemberDetailResponse(getMemberResponseFromMember(profileMember),
                getGraduationFromMember(graduation));
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
     * 마이페이지 회원 정보 수정
     *
     * @param profileMemberId     조회할 프로필 Member id
     * @param loginMemberId       현재 로그인한 Member id
     * @param updateMemberRequest 회원 정보 수정 요청 객체
     */
    @Transactional
    public MyPageMemberResponse updateMember(UUID profileMemberId, UUID loginMemberId,
                                             UpdateMemberRequest updateMemberRequest) {
        validateMemberAuthorize(profileMemberId, loginMemberId);
        Member member = findByMemberId(loginMemberId);

        //회원 기본 정보 수정
        updateMemberProfile(member, updateMemberRequest.getUpdateMemberProfileRequest());
        //졸업생 정보 수정
        Graduation graduation = handleGraduation(member, updateMemberRequest);

        return new MyPageMemberResponse(true, getMemberResponseFromMember(member),
                getGraduationFromMember(graduation));
    }

    /**
     * 회원 정보 수정
     *
     * @param member                     수정 대상 사용자
     * @param updateMemberProfileRequest 수정 사항 request
     */
    public void updateMemberProfile(Member member, UpdateMemberProfileRequest updateMemberProfileRequest) {
        // 졸업생은 휴학중일 수 없다.
        if (updateMemberProfileRequest.isGraduation() && updateMemberProfileRequest.isAbsence()) {
            throw new MemberDomainException("졸업생은 휴학중일 수 없습니다.", HttpStatus.CONFLICT);
        }

        int grade = gradeSemesterYearMapper.toIntGrade(updateMemberProfileRequest.getGrade());
        int semester = gradeSemesterYearMapper.toIntCompletionSemester(updateMemberProfileRequest.getSemester());

        member.updateProfileInfo(updateMemberProfileRequest, grade, semester);
    }


    // 졸업생 정보 수정
    public Graduation handleGraduation(Member member, UpdateMemberRequest updateMemberRequest) {
        boolean isGraduationMember = updateMemberRequest.getUpdateMemberProfileRequest().isGraduation();

        if (isGraduationMember) {  // 졸업생인 경우 : 프로필 업데이트
            return updateGraduation(member, updateMemberRequest.getUpdateGraduationRequest());
        }
        if (member.isGraduation()) {  // 졸업생이 아닌데 졸업 정보가 있는 사람 : 기존 졸업 정보 삭제
            deleteGraduation(member);
        }
        return null;
    }

    //졸업생 정보 반영
    public Graduation updateGraduation(Member member, UpdateGraduationRequest updateGraduationRequest) {
        int graduateYear = gradeSemesterYearMapper.toIntYear(updateGraduationRequest.getYear());

        if (member.getGraduation() == null) {  //기존에 졸업생 정보가 없으면 새로 생성
            Graduation newGraduation = graduationMapper.toEntity(graduateYear, updateGraduationRequest);
            newGraduation = graduationRepository.save(newGraduation);
            member.updateGraduationInfo(newGraduation);
            return newGraduation;
        } else {   // 졸업생 정보 수정 : 기존에 졸업생 정보가 있음.
            member.getGraduation().updateProfile(graduateYear, updateGraduationRequest);
            return member.getGraduation();
        }
    }

    //졸업생 정보 삭제
    private void deleteGraduation(Member member) {
        Optional<Graduation> graduation = graduationRepository.findById(member.getGraduation().getId());
        if (graduation.isPresent()) {
            graduationRepository.delete(graduation.get());
            member.updateGraduationInfo(null);
        }
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
     * 회원 이메일 조회
     *
     * @param profileMemberId 조회할 프로필 Member id
     * @param loginMemberId   현재 로그인한 Member id
     */
    public String getEmail(UUID profileMemberId, UUID loginMemberId) {
        validateMemberAuthorize(profileMemberId, loginMemberId);
        Member currentMember = findByMemberId(loginMemberId);

        return currentMember.getEmail();
    }

    /**
     * 회원 프로필 사진 변경
     *
     * @param profileMemberId   조회할 프로필 Member id
     * @param loginMemberId     현재 로그인한 Member id
     * @param newProfilePhotoId 바꿀 프로필 사진 pk
     */
    @Transactional
    public ProfilePhotoResponse updateProfilePhoto(UUID profileMemberId, UUID loginMemberId, UUID newProfilePhotoId) {
        validateMemberAuthorize(profileMemberId, loginMemberId);

        Member currentMember = findByMemberId(loginMemberId);
        if (currentMember.getProfilePhoto() != null) {  // 기존에 프로필 사진이 있었으면 삭제
            ProfilePhoto currentProfilePhoto = currentMember.getProfilePhoto();
            profilePhotoFileService.deleteProfilePhotoById(currentProfilePhoto.getId());
        }

        ProfilePhoto newProfilePhoto = profilePhotoFileService.findProfilePhotoById(newProfilePhotoId);
        currentMember.updateProfilePhoto(newProfilePhoto);

        ProfilePhoto profilePhoto = profilePhotoFileService.getProfilePhoto(currentMember.getProfilePhoto());

        return memberProfilePhotoMapper.toResponse(profilePhoto);
    }

    /**
     * 회원 프로필 사진 삭제
     *
     * @param profileMemberId 조회할 프로필 Member id
     * @param loginMemberId   현재 로그인한 Member id
     */
    @Transactional
    public ProfilePhotoResponse deleteProfilePhoto(UUID profileMemberId, UUID loginMemberId) {
        validateMemberAuthorize(profileMemberId, loginMemberId);

        Member currentMember = findByMemberId(loginMemberId);
        ProfilePhoto currentProfilePhoto = currentMember.getProfilePhoto();

        if (currentProfilePhoto != null) {  //프로필 사진 삭제
            profilePhotoFileService.deleteProfilePhotoById(currentProfilePhoto.getId());
            currentMember.updateProfilePhoto(null);
        }

        ProfilePhoto baseProfilePhoto = profilePhotoFileService.findBaseProfilePhoto();
        return memberProfilePhotoMapper.toResponse(baseProfilePhoto);
    }

    /**
     * 회원 프로필 사진 다운로드
     */
    public void downloadProfilePhoto(UUID profileMemberId, HttpServletResponse response, UUID loginMemberId) {
        findByMemberId(loginMemberId);
        Member profileMember = findByMemberId(profileMemberId);

        if (profileMember.getProfilePhoto() == null) {
            throw new ProfilePhotoFileDomainException("프로필 사진이 기본 이미지입니다.", HttpStatus.NOT_FOUND);
        }

        profilePhotoFileService.downloadProfilePhoto(profileMember.getProfilePhoto().getId(), response);
    }

    /**
     * 휴학 여부 변경
     *
     * @param memberId  선택된 회원
     * @param isAbsence 휴학 여부
     */
    @Transactional
    public Member updateAbsence(UUID memberId, boolean isAbsence) {
        Member member = findByMemberId(memberId);
        if (member.isGraduation()) {
            throw new MemberDomainException("졸업생은 휴학 여부를 변경할 수 없습니다.", HttpStatus.CONFLICT);
        }
        member.updateAbsence(isAbsence);
        return member;
    }

    /**
     * 학년 변경
     *
     * @param memberId
     * @param grade
     */
    @Transactional
    public Member updateGrade(UUID memberId, int grade) {
        Member member = findByMemberId(memberId);

        member.updateGrade(grade);  //학년 변경
        member.updateSemester(grade * 2);  //학년 변경에 따른 학기 변경
        return member;
    }

//    /**
//     * 이름으로 검색
//     *
//     * @param name 이름
//     * @return 검색된 member 리스트
//     */
//    public List<MemberResponse> findMembersByName(String name) {
//        List<Member> memberList = memberRepository.findAllMembersByName(adminStudentNumber, name);
//
//        return memberList.stream().map(member -> {
//            ProfilePhoto profilePhoto = profilePhotoFileService.getProfilePhoto(member.getProfilePhoto());
//            return memberMapper.toResponse(member, profilePhoto);
//        }).toList();
//    }
}
