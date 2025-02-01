package org.nova.backend.board.application.service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.nova.backend.board.application.dto.request.BasePostRequest;
import org.nova.backend.board.application.dto.response.PostResponse;
import org.nova.backend.board.application.mapper.BasePostMapper;
import org.nova.backend.board.application.port.in.BoardUseCase;
import org.nova.backend.board.application.port.in.FileUseCase;
import org.nova.backend.board.application.port.in.PostUseCase;
import org.nova.backend.board.application.port.out.PostPersistencePort;
import org.nova.backend.board.domain.exception.BoardDomainException;
import org.nova.backend.board.domain.model.entity.Board;
import org.nova.backend.board.domain.model.entity.File;
import org.nova.backend.board.domain.model.entity.Post;
import org.nova.backend.board.domain.model.valueobject.BoardCategory;
import org.nova.backend.member.domain.model.entity.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PostService implements PostUseCase {
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    private final PostPersistencePort postPersistencePort;
    private final BoardUseCase boardUseCase;
    private final FileUseCase fileUseCase;
    private final BasePostMapper postMapper;

    public PostService(
            PostPersistencePort postPersistencePort,
            BoardUseCase boardUseCase,
            FileUseCase fileUseCase,
            BasePostMapper postMapper
    ) {
        this.postPersistencePort = postPersistencePort;
        this.boardUseCase = boardUseCase;
        this.fileUseCase = fileUseCase;
        this.postMapper = postMapper;
    }

    /**
     * 새로운 게시글과 첨부파일 저장
     *
     * @param request  생성할 게시글 객체
     * @param member  게시글 작성자
     * @param files 첨부파일 리스트
     * @return 저장된 게시글 객체
     */
    @Override
    @Transactional
    public PostResponse createPost(
            BasePostRequest request,
            Member member,
            List<MultipartFile> files
    ) {
        Board board = boardUseCase.getBoardByCategory(request.getPostType().getCategory());
        Post post = postMapper.toEntity(request, member, board);

        Post savedPost = postPersistencePort.save(post);
        List<File> savedFiles = fileUseCase.saveFiles(files, savedPost);
        savedPost.addFiles(savedFiles);

        return postMapper.toResponse(savedPost);
    }

    /**
     * 특정 카테고리의 모든 게시글 조회 (페이징)
     */
    @Override
    @Transactional
    public Page<PostResponse> getPostsByCategory(BoardCategory category, Pageable pageable) {
        return postPersistencePort.findAllByCategory(category, pageable)
                .map(postMapper::toResponse);
    }

    /**
     * 특정 게시글 조회
     */
    @Override
    @Transactional
    public PostResponse getPostById(UUID postId) {
        postPersistencePort.increaseViewCount(postId);
        Post post = postPersistencePort.findById(postId)
                .orElseThrow(() -> new BoardDomainException("게시글을 찾을 수 없습니다. ID: " + postId));
        return postMapper.toResponse(post);
    }

    @Override
    @Transactional
    public int likePost(UUID postId, Member member) {
        return postPersistencePort.likePost(postId, member);
    }

    @Override
    @Transactional
    public int unlikePost(UUID postId, Member member) {
        return postPersistencePort.unlikePost(postId, member);
    }

    /**
     * 게시글 삭제 (작성자 본인만 가능)
     *
     * @param postId   삭제할 게시글 ID
     * @param memberId 요청한 사용자 ID
     */
    @Override
    @Transactional
    public void deletePost(UUID postId, UUID memberId) {
        logger.info("게시글 삭제 요청 - Post ID: {}, Member ID: {}", postId, memberId);

        Post post = postPersistencePort.findById(postId)
                .orElseThrow(() -> {
                    logger.error("삭제 요청한 게시글이 존재하지 않습니다. ID: {}", postId);
                    return new BoardDomainException("게시글을 찾을 수 없습니다.");
                });

        if (!post.getMember().getId().equals(memberId)) {
            logger.warn("사용자 {}가 게시글 {}를 삭제하려 했으나 권한이 없습니다.", memberId, postId);
            throw new BoardDomainException("게시글 삭제 권한이 없습니다.");
        }

        postPersistencePort.deleteById(postId);
        logger.info("게시글이 성공적으로 삭제되었습니다. Post ID: {}", postId);
    }
}
