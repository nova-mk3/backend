package org.nova.backend.board.common.application.service;

import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.auth.UnauthorizedException;
import org.nova.backend.board.clubArchive.application.dto.response.ImageResponse;
import org.nova.backend.board.clubArchive.application.dto.response.PicturePostSummaryResponse;
import org.nova.backend.board.clubArchive.application.service.ImageFileService;
import org.nova.backend.board.common.application.dto.request.BasePostRequest;
import org.nova.backend.board.common.application.dto.request.UpdateBasePostRequest;
import org.nova.backend.board.common.application.dto.response.BasePostDetailResponse;
import org.nova.backend.board.common.application.dto.response.BasePostSummaryResponse;
import org.nova.backend.board.common.application.mapper.BasePostMapper;
import org.nova.backend.board.common.application.port.in.BoardUseCase;
import org.nova.backend.board.common.application.port.in.FileUseCase;
import org.nova.backend.board.common.application.port.in.BasePostUseCase;
import org.nova.backend.board.common.application.port.out.BasePostPersistencePort;
import org.nova.backend.board.common.domain.exception.BoardDomainException;
import org.nova.backend.board.common.domain.model.entity.Board;
import org.nova.backend.board.common.domain.model.entity.File;
import org.nova.backend.board.common.domain.model.entity.Post;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.nova.backend.board.clubArchive.application.dto.response.JokboPostSummaryResponse;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.member.domain.model.valueobject.Role;
import org.nova.backend.shared.security.BoardSecurityChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasePostService implements BasePostUseCase {
    private static final Logger logger = LoggerFactory.getLogger(BasePostService.class);

    private final BasePostPersistencePort basePostPersistencePort;
    private final MemberRepository memberRepository;
    private final BoardSecurityChecker boardSecurityChecker;
    private final BoardUseCase boardUseCase;
    private final FileUseCase fileUseCase;
    private final ImageFileService imageFileService;
    private final BasePostMapper postMapper;

    /**
     * 새로운 게시글과 첨부파일 저장
     *
     * @param request  생성할 게시글 객체
     * @param memberId  게시글 작성자
     * @return 저장된 게시글 객체
     */
    @Override
    @Transactional
    public BasePostDetailResponse createPost(
            UUID boardId,
            BasePostRequest request,
            UUID memberId
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BoardDomainException("사용자를 찾을 수 없습니다."));

        if (request.getPostType() == PostType.NOTICE && !boardSecurityChecker.isAdminOrPresident(member)) {
            throw new UnauthorizedException("공지사항은 관리자 또는 회장만 작성할 수 있습니다.");
        }

        Board board = boardUseCase.getBoardById(boardId);
        Post post = postMapper.toEntity(request, member, board);
        Post savedPost = basePostPersistencePort.save(post);

        if(request.getFileIds() != null && !request.getFileIds().isEmpty()) {
            List<File> files = fileUseCase.findFilesByIds(request.getFileIds());
            files.forEach(file -> file.setPost(savedPost));
            savedPost.addFiles(files);
        }
        basePostPersistencePort.save(savedPost);
        return postMapper.toDetailResponse(savedPost);
    }

    /**
     * 모든 게시글 조회 (페이징)
     */
    @Override
    @Transactional
    public Page<BasePostSummaryResponse> getAllPosts(
            UUID boardId,
            Pageable pageable
    ) {
        return basePostPersistencePort.findAllByBoard(boardId, pageable)
                .map(postMapper::toSummaryResponse);
    }

    /**
     * 특정 카테고리의 모든 게시글 조회 (페이징)
     */
    @Override
    @Transactional
    public Page<?> getPostsByCategory(
            UUID boardId,
            PostType postType,
            Pageable pageable
    ) {
        Page<Post> posts = basePostPersistencePort.findAllByBoardAndCategory(boardId, postType, pageable);

        if (postType == PostType.EXAM_ARCHIVE) {
            return posts.map(post -> new JokboPostSummaryResponse(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    post.getViewCount(),
                    post.getLikeCount(),
                    post.getCreatedTime(),
                    post.getModifiedTime(),
                    post.getMember().getName(),
                    post.getTotalDownloadCount(),
                    post.getFiles().size()
            ));
        }
        else if (postType == PostType.PICTURES) {
            return posts.map(post -> {
                List<UUID> fileIds = post.getFiles().stream().map(File::getId).toList();
                ImageResponse thumbnail = imageFileService.getThumbnail(fileIds);

                return new PicturePostSummaryResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getViewCount(),
                        post.getLikeCount(),
                        post.getCreatedTime(),
                        post.getModifiedTime(),
                        post.getMember().getName(),
                        post.getFiles().size(),
                        thumbnail != null ? thumbnail.getId() : null,
                        thumbnail != null ? thumbnail.getDownloadUrl() : null,
                        thumbnail != null ? thumbnail.getWidth() : 0,
                        thumbnail != null ? thumbnail.getHeight() : 0
                );
            });
        }
        else {
            return posts.map(postMapper::toSummaryResponse);
        }
    }

    /**
     * 특정 게시글 조회
     */
    @Override
    @Transactional
    public BasePostDetailResponse getPostById(
            UUID boardId,
            UUID postId
    ) {
        basePostPersistencePort.increaseViewCount(postId);
        Post post = basePostPersistencePort.findByBoardIdAndPostId(boardId, postId)
                .orElseThrow(() -> new BoardDomainException("게시글을 찾을 수 없습니다. Board ID: " + boardId + ", Post ID: " + postId));
        return postMapper.toDetailResponse(post);
    }



    /**
     * 특정 게시글 좋아요
     */
    @Override
    @Transactional
    public int likePost(
            UUID postId,
            UUID memberId
    ) {
        return basePostPersistencePort.likePost(postId, memberId);
    }

    /**
     * 특정 게시글 좋아요 취소
     */
    @Override
    @Transactional
    public int unlikePost(
            UUID postId,
            UUID memberId
    ) {
        return basePostPersistencePort.unlikePost(postId, memberId);
    }

    /**
     * 게시글 수정
     *
     * @param postId 수정할 게시글 ID
     * @param request 업데이트할 게시글 요청 데이터
     * @param memberId 게시글 작성자 ID
     */
    @Override
    @Transactional
    public void updatePost(
            UUID boardId,
            UUID postId,
            UpdateBasePostRequest request,
            UUID memberId
    ) {
        Post post = basePostPersistencePort.findById(postId)
                .orElseThrow(() -> new BoardDomainException("게시글을 찾을 수 없습니다. ID: " + postId));

        if (!post.getBoard().getId().equals(boardId)) {
            throw new BoardDomainException("잘못된 게시판 ID입니다.");
        }

        if (!post.getMember().getId().equals(memberId)) {
            throw new BoardDomainException("게시글 수정 권한이 없습니다.");
        }

        if (request.getDeleteFileIds() != null && !request.getDeleteFileIds().isEmpty()) {
            fileUseCase.deleteFiles(request.getDeleteFileIds());
            post.removeFilesByIds(request.getDeleteFileIds());
        }

        if (request.getFileIds() != null && !request.getFileIds().isEmpty()) {
            List<File> newFiles = fileUseCase.findFilesByIds(request.getFileIds());
            newFiles.forEach(file -> file.setPost(post));
            post.addFiles(newFiles);
        }
        post.updatePost(request.getTitle(), request.getContent());
        basePostPersistencePort.save(post);
    }

    /**
     * 게시글 삭제 (작성자 본인 또는 관리자 가능)
     *
     * @param boardId  게시판 ID
     * @param postId   삭제할 게시글 ID
     * @param memberId   요청한 사용자
     */
    @Override
    @Transactional
    public void deletePost(
            UUID boardId,
            UUID postId,
            UUID memberId
    ) {
        logger.info("게시글 삭제 요청 - Board ID: {}, Post ID: {}, Member ID: {}", boardId, postId, memberId);

        Post post = basePostPersistencePort.findById(postId)
                .orElseThrow(() -> {
                    logger.error("삭제 요청한 게시글이 존재하지 않습니다. ID: {}", postId);
                    return new BoardDomainException("게시글을 찾을 수 없습니다.");
                });

        if (!post.getBoard().getId().equals(boardId)) {
            throw new BoardDomainException("잘못된 게시판 ID입니다. 게시글이 해당 게시판에 존재하지 않습니다.");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BoardDomainException("사용자를 찾을 수 없습니다."));

        if (!post.getMember().getId().equals(memberId) && member.getRole() != Role.ADMINISTRATOR) {
            logger.warn("사용자 {}가 게시글 {}를 삭제하려 했으나 권한이 없습니다.", memberId, postId);
            throw new BoardDomainException("게시글 삭제 권한이 없습니다.");
        }

        List<UUID> fileIds = post.getFiles().stream().map(File::getId).toList();
        fileUseCase.deleteFiles(fileIds);

        basePostPersistencePort.deleteById(postId);
        logger.info("게시글이 성공적으로 삭제되었습니다. Board ID: {}, Post ID: {}", boardId, postId);
    }

    /**
     *
     * @param boardId 게시판 ID
     * @return 긱 카테고리별 게시판 리스트
     */
    @Override
    @Transactional
    public Map<PostType, List<BasePostSummaryResponse>> getLatestPostsByType(UUID boardId) {

        List<PostType> allowedPostTypes = List.of(
                PostType.QNA,
                PostType.FREE,
                PostType.INTRODUCTION,
                PostType.NOTICE
        );

        Map<PostType, List<BasePostSummaryResponse>> groupedPosts = new HashMap<>();

        for (PostType postType : allowedPostTypes) {
            List<Post> posts = basePostPersistencePort.findLatestPostsByType(boardId, postType, 6);
            List<BasePostSummaryResponse> postResponses = posts.stream()
                    .map(postMapper::toSummaryResponse)
                    .toList();

            groupedPosts.put(postType, postResponses);
        }

        return groupedPosts;
    }
}
