package org.nova.backend.board.clubArchive.application.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.auth.UnauthorizedException;
import org.nova.backend.board.clubArchive.application.dto.request.PicturePostRequest;
import org.nova.backend.board.clubArchive.application.dto.request.UpdatePicturePostRequest;
import org.nova.backend.board.clubArchive.application.dto.response.PicturePostDetailResponse;
import org.nova.backend.board.clubArchive.application.mapper.PicturePostMapper;
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
import org.nova.backend.board.common.domain.model.valueobject.BoardCategory;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.nova.backend.board.util.SecurityUtil;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.exception.MemberDomainException;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.member.domain.model.valueobject.Role;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PicturePostService implements PicturePostUseCase {
    private final BasePostPersistencePort basePostPersistencePort;
    private final PostLikePersistencePort postLikePersistencePort;
    private final FileUseCase fileUseCase;
    private final BoardUseCase boardUseCase;
    private final MemberRepository memberRepository;
    private final SecurityUtil securityUtil;
    private final PicturePostMapper picturePostMapper;

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

        if (board.getCategory() != BoardCategory.CLUB_ARCHIVE) {
            throw new BoardDomainException("사진 게시글은 'CLUB_ARCHIVE' 게시판에서만 작성할 수 있습니다.");
        }

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
        return picturePostMapper.toDetailResponse(savedPost, false);
    }

    /**
     * 사진 게시글 수정
     */
    @Override
    @Transactional
    public PicturePostDetailResponse updatePost(
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

        boolean isLiked = postLikePersistencePort.findByPostIdAndMemberId(postId, memberId).isPresent();
        return picturePostMapper.toDetailResponse(post, isLiked);
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
    @Transactional(readOnly = false)
    public PicturePostDetailResponse getPostById(
            UUID boardId,
            UUID postId
    ) {
        basePostPersistencePort.increaseViewCount(postId);
        Post post = basePostPersistencePort.findByBoardIdAndPostId(boardId, postId)
                .orElseThrow(() -> new BoardDomainException("게시글을 찾을 수 없습니다."));

        UUID memberId = securityUtil.getOptionalCurrentMemberId().orElse(null);
        boolean isLiked = (memberId != null) && postLikePersistencePort.findByPostIdAndMemberId(postId, memberId).isPresent();

        return picturePostMapper.toDetailResponse(post, isLiked);
    }
}
