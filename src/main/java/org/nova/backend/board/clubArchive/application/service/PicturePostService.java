package org.nova.backend.board.clubArchive.application.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.auth.UnauthorizedException;
import org.nova.backend.board.clubArchive.application.dto.request.PicturePostRequest;
import org.nova.backend.board.clubArchive.application.dto.request.UpdatePicturePostRequest;
import org.nova.backend.board.clubArchive.application.dto.response.ImageResponse;
import org.nova.backend.board.clubArchive.application.dto.response.PicturePostDetailResponse;
import org.nova.backend.board.clubArchive.application.port.in.PicturePostUseCase;
import org.nova.backend.board.clubArchive.domain.exception.PictureDomainException;
import org.nova.backend.board.common.application.port.in.BoardUseCase;
import org.nova.backend.board.common.application.port.in.FileUseCase;
import org.nova.backend.board.common.application.port.out.BasePostPersistencePort;
import org.nova.backend.board.common.application.port.out.PostLikePersistencePort;
import org.nova.backend.board.common.domain.exception.BoardDomainException;
import org.nova.backend.board.common.domain.model.entity.Board;
import org.nova.backend.board.common.domain.model.entity.File;
import org.nova.backend.board.common.domain.model.entity.Post;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.exception.MemberDomainException;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.member.domain.model.valueobject.Role;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PicturePostService implements PicturePostUseCase {
    private final BasePostPersistencePort basePostPersistencePort;
    private final PostLikePersistencePort postLikePersistencePort;
    private final FileUseCase fileUseCase;
    private final BoardUseCase boardUseCase;
    private final ImageFileService imageFileService;
    private final MemberRepository memberRepository;

    /**
     * 사진 게시글 생성
     */
    @Override
    @Transactional
    public PicturePostDetailResponse createPost(
            UUID boardId,
            PicturePostRequest request,
            UUID memberId
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberDomainException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        Board board = boardUseCase.getBoardById(boardId);

        Post post = new Post(
                UUID.randomUUID(),
                member,
                board,
                PostType.PICTURES,
                request.getTitle(),
                request.getContent(),
                0,
                0,
                0,
                0,
                new ArrayList<>(),
                new ArrayList<>(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Post savedPost = basePostPersistencePort.save(post);

        if(request.getImageFileIds() != null && !request.getImageFileIds().isEmpty()) {
            List<File> files = fileUseCase.findFilesByIds(request.getImageFileIds());
            files.forEach(file -> file.setPost(savedPost));
            savedPost.addFiles(files);
        }

        basePostPersistencePort.save(savedPost);
        return toDetailResponse(savedPost, false);
    }

    /**
     * 사진 게시글 수정
     */
    @Override
    @Transactional
    public void updatePost(
            UUID boardId,
            UUID postId,
            UpdatePicturePostRequest request,
            UUID memberId
    ) {
        Post post = basePostPersistencePort.findById(postId)
                .orElseThrow(() -> new PictureDomainException("게시글을 찾을 수 없습니다. ID: " + postId, HttpStatus.NOT_FOUND));

        if (!post.getBoard().getId().equals(boardId)) {
            throw new BoardDomainException("잘못된 게시판 ID입니다.");
        }

        if (!post.getMember().getId().equals(memberId)) {
            throw new UnauthorizedException("게시글 수정 권한이 없습니다.");
        }

        if (request.getDeleteImageFileIds() != null && !request.getDeleteImageFileIds().isEmpty()) {
            fileUseCase.deleteFiles(request.getDeleteImageFileIds());
            post.removeFilesByIds(request.getDeleteImageFileIds());
        }

        if (request.getImageFileIds() != null && !request.getImageFileIds().isEmpty()) {
            List<File> newFiles = fileUseCase.findFilesByIds(request.getImageFileIds());
            newFiles.forEach(file -> file.setPost(post));
            post.addFiles(newFiles);
        }

        post.updatePost(request.getTitle(), request.getContent());
        basePostPersistencePort.save(post);
    }

    /**
     * 사진 게시글 삭제
     */
    @Override
    @Transactional
    public void deletePost(
            UUID boardId,
            UUID postId,
            UUID memberId
    ) {
        Post post = basePostPersistencePort.findById(postId)
                .orElseThrow(() -> new BoardDomainException("게시글을 찾을 수 없습니다. ID: " + postId));

        if (!post.getBoard().getId().equals(boardId)) {
            throw new BoardDomainException("잘못된 게시판 ID입니다.");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberDomainException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        if (!post.getMember().getId().equals(memberId) && member.getRole() != Role.ADMINISTRATOR) {
            throw new UnauthorizedException("게시글 삭제 권한이 없습니다.");
        }

        List<UUID> fileIds = post.getFiles().stream().map(File::getId).toList();
        fileUseCase.deleteFiles(fileIds);

        basePostPersistencePort.deleteById(postId);
    }

    /**
     * 사진 게시글 조회
     */
    @Override
    @Transactional
    public PicturePostDetailResponse getPostById(
            UUID boardId,
            UUID postId
    ) {
        basePostPersistencePort.increaseViewCount(postId);
        Post post = basePostPersistencePort.findByBoardIdAndPostId(boardId, postId)
                .orElseThrow(() -> new BoardDomainException("게시글을 찾을 수 없습니다."));

        UUID memberId = getCurrentMemberId().orElse(null);
        boolean isLiked = (memberId != null) && postLikePersistencePort.findByPostIdAndMemberId(postId, memberId).isPresent();

        return toDetailResponse(post, isLiked);
    }

    /**
     * 현재 로그인한 사용자의 UUID 가져오기 (비로그인 사용자는 Optional.empty() 반환)
     */
    private Optional<UUID> getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return Optional.empty();
        }

        String studentNumber = authentication.getName();

        return memberRepository.findByStudentNumber(studentNumber)
                .map(Member::getId);
    }

    /**
     * 사진 게시글 상세 응답 변환
     */
    private PicturePostDetailResponse toDetailResponse(
            Post post,
            boolean isLiked
    ) {
        List<ImageResponse> images = post.getFiles().stream()
                .map(imageFileService::createImageResponse)
                .toList();

        return new PicturePostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getLikeCount(),
                post.getCreatedTime(),
                post.getModifiedTime(),
                post.getMember().getName(),
                images,
                isLiked
        );
    }
}
