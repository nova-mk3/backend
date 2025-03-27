package org.nova.backend.board.common.application.service;

import org.nova.backend.board.clubArchive.application.mapper.PicturePostMapper;
import org.nova.backend.board.common.application.dto.response.AllPostSummaryResponse;
import org.nova.backend.board.common.application.mapper.AllPostMapper;
import org.nova.backend.board.util.SecurityUtil;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.auth.UnauthorizedException;
import org.nova.backend.board.common.application.dto.request.BasePostRequest;
import org.nova.backend.board.common.application.dto.request.UpdateBasePostRequest;
import org.nova.backend.board.common.application.dto.response.BasePostDetailResponse;
import org.nova.backend.board.common.application.dto.response.BasePostSummaryResponse;
import org.nova.backend.board.common.application.mapper.BasePostMapper;
import org.nova.backend.board.common.application.port.in.BoardUseCase;
import org.nova.backend.board.common.application.port.in.FileUseCase;
import org.nova.backend.board.common.application.port.in.BasePostUseCase;
import org.nova.backend.board.common.application.port.out.BasePostPersistencePort;
import org.nova.backend.board.common.application.port.out.PostLikePersistencePort;
import org.nova.backend.board.common.domain.exception.BoardDomainException;
import org.nova.backend.board.common.domain.model.entity.Board;
import org.nova.backend.board.common.domain.model.entity.File;
import org.nova.backend.board.common.domain.model.entity.Post;
import org.nova.backend.board.common.domain.model.entity.PostLike;
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
    private final PostLikePersistencePort postLikePersistencePort;
    private final MemberRepository memberRepository;
    private final BoardSecurityChecker boardSecurityChecker;
    private final BoardUseCase boardUseCase;
    private final FileUseCase fileUseCase;
    private final SecurityUtil securityUtil;
    private final BasePostMapper postMapper;
    private final PicturePostMapper picturePostMapper;
    private final AllPostMapper allPostMapper;

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

        if (!PostType.isValidPostType(board.getCategory(), request.getPostType())) {
            throw new BoardDomainException(
                    String.format("게시판 [%s]에는 [%s] 타입의 게시글을 저장할 수 없습니다.",
                            board.getCategory().getDisplayName(),
                            request.getPostType().getDisplayName())
            );
        }

        Post post = postMapper.toEntity(request, member, board);
        Post savedPost = basePostPersistencePort.save(post);

        if(request.getFileIds() != null && !request.getFileIds().isEmpty()) {
            List<File> files = fileUseCase.findFilesByIds(request.getFileIds());
            files.forEach(file -> file.setPost(savedPost));
            savedPost.addFiles(files);
        }
        basePostPersistencePort.save(savedPost);
        return postMapper.toDetailResponse(savedPost, false);
    }

    /**
     * 모든 게시글 조회 (페이징)
     */
    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Page<?> getPostsByCategory(
            UUID boardId,
            PostType postType,
            Pageable pageable
    ) {
        Page<Post> posts = basePostPersistencePort.findAllByBoardAndCategory(boardId, postType, pageable);

        return switch (postType) {
            case EXAM_ARCHIVE -> posts.map(post -> new JokboPostSummaryResponse(
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
            case PICTURES -> posts.map(picturePostMapper::toSummaryResponse);
            default -> posts.map(postMapper::toSummaryResponse);
        };
    }

    /**
     * 특정 카테고리의 모든 게시글 검색 (페이징)
     */
    @Override
    @Transactional(readOnly = true)
    public Page<?> searchPostsByCategory(
            UUID boardId,
            PostType postType,
            String keyword,
            String searchType,
            Pageable pageable
    ) {
        Page<Post> posts;

        if (keyword == null || keyword.trim().isEmpty()) {
            return getPostsByCategory(boardId, postType, pageable);
        }

        posts = switch (searchType.toUpperCase()) {
            case "TITLE" -> basePostPersistencePort.searchByTitle(boardId, postType, keyword, pageable);
            case "CONTENT" -> basePostPersistencePort.searchByContent(boardId, postType, keyword, pageable);
            default -> basePostPersistencePort.searchByTitleOrContent(boardId, postType, keyword, pageable);
        };

        return switch (postType) {
            case EXAM_ARCHIVE -> posts.map(post -> new JokboPostSummaryResponse(
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
            case PICTURES -> posts.map(picturePostMapper::toSummaryResponse);
            default -> posts.map(postMapper::toSummaryResponse);
        };
    }

    /**
     * 통합 게시판(INTEGRATED)의 모든 게시글 검색 (제목, 내용, 제목+내용)
     */
    @Override
    @Transactional(readOnly = true)
    public Page<BasePostSummaryResponse> searchAllPosts(
            UUID boardId,
            String keyword,
            String searchType,
            Pageable pageable
    ) {
        Page<Post> posts = basePostPersistencePort.searchAllByBoardId(boardId, keyword, searchType, pageable);
        return posts.map(postMapper::toSummaryResponse);
    }

    /**
     * 게시글 상세 조회
     */
    @Override
    @Transactional(readOnly = false)
    public BasePostDetailResponse getPostById(
            UUID boardId,
            UUID postId
    ) {

        basePostPersistencePort.increaseViewCount(postId);
        Post post = basePostPersistencePort.findByBoardIdAndPostId(boardId, postId)
                .orElseThrow(() -> new BoardDomainException("게시글을 찾을 수 없습니다."));

        UUID memberId = securityUtil.getOptionalCurrentMemberId().orElse(null);
        boolean isLiked = (memberId != null) && postLikePersistencePort.findByPostIdAndMemberId(postId, memberId).isPresent();

        return postMapper.toDetailResponse(post, isLiked);
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
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BoardDomainException("사용자를 찾을 수 없습니다."));

        Post post = basePostPersistencePort.findById(postId)
                .orElseThrow(() -> new BoardDomainException("게시글을 찾을 수 없습니다."));

        if (postLikePersistencePort.findByPostIdAndMemberId(postId, memberId).isPresent()) {
            throw new BoardDomainException("이미 좋아요를 눌렀습니다.");
        }

        postLikePersistencePort.save(new PostLike(post, member));
        basePostPersistencePort.increaseLikeCount(postId);

        return basePostPersistencePort.getLikeCount(postId);
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
        if (postLikePersistencePort.findByPostIdAndMemberId(postId, memberId).isEmpty()) {
            throw new BoardDomainException("좋아요를 누르지 않은 게시글입니다.");
        }

        postLikePersistencePort.deleteByPostIdAndMemberId(postId, memberId);
        basePostPersistencePort.decreaseLikeCount(postId);

        return basePostPersistencePort.getLikeCount(postId);
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
    public BasePostDetailResponse updatePost(
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

        boolean isLiked = postLikePersistencePort.findByPostIdAndMemberId(postId, memberId).isPresent();
        return postMapper.toDetailResponse(post, isLiked);
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
    @Transactional(readOnly = true)
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

    @Override
    @Transactional(readOnly = true)
    public Page<AllPostSummaryResponse> getAllPostsFromAllBoards(Pageable pageable) {
        return basePostPersistencePort.findAll(pageable)
                .map(allPostMapper::toSummaryResponse);
    }
}
