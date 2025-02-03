package org.nova.backend.board.application.service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.nova.backend.auth.UnauthorizedException;
import org.nova.backend.board.application.dto.request.BasePostRequest;
import org.nova.backend.board.application.dto.request.UpdatePostRequest;
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
import org.nova.backend.board.domain.model.valueobject.PostType;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.member.domain.model.valueobject.Role;
import org.nova.backend.shared.security.BoardSecurityChecker;
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
    private final MemberRepository memberRepository;
    private final BoardSecurityChecker boardSecurityChecker;
    private final BoardUseCase boardUseCase;
    private final FileUseCase fileUseCase;
    private final BasePostMapper postMapper;

    public PostService(
            PostPersistencePort postPersistencePort,
            MemberRepository memberRepository,
            BoardSecurityChecker boardSecurityChecker,
            BoardUseCase boardUseCase,
            FileUseCase fileUseCase,
            BasePostMapper postMapper
    ) {
        this.postPersistencePort = postPersistencePort;
        this.memberRepository = memberRepository;
        this.boardSecurityChecker = boardSecurityChecker;
        this.boardUseCase = boardUseCase;
        this.fileUseCase = fileUseCase;
        this.postMapper = postMapper;
    }

    /**
     * 새로운 게시글과 첨부파일 저장
     *
     * @param request  생성할 게시글 객체
     * @param memberId  게시글 작성자
     * @param files 첨부파일 리스트
     * @return 저장된 게시글 객체
     */
    @Override
    @Transactional
    public PostResponse createPost(
            UUID boardId,
            BasePostRequest request,
            UUID memberId,
            List<MultipartFile> files
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BoardDomainException("사용자를 찾을 수 없습니다."));

        if (request.getPostType() == PostType.NOTICE && !boardSecurityChecker.isAdminOrPresident(member)) {
            throw new UnauthorizedException("공지사항은 관리자 또는 회장만 작성할 수 있습니다.");
        }

        Board board = boardUseCase.getBoardById(boardId);
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
     * 게시글 수정
     *
     * @param postId 수정할 게시글 ID
     * @param request 업데이트할 게시글 요청 데이터
     * @param memberId 게시글 작성자 ID
     * @param files 새로 업로드할 파일 리스트
     * @return 수정된 게시글 응답
     */
    @Override
    @Transactional
    public void updatePost(
            UUID boardId,
            UUID postId,
            UpdatePostRequest request,
            UUID memberId,
            List<MultipartFile> files
    ) {
        Post post = postPersistencePort.findById(postId)
                .orElseThrow(() -> new BoardDomainException("게시글을 찾을 수 없습니다. ID: " + postId));

        if (!post.getBoard().getId().equals(boardId)) {
            throw new BoardDomainException("잘못된 게시판 ID입니다.");
        }

        if (!post.getMember().getId().equals(memberId)) {
            throw new BoardDomainException("게시글 수정 권한이 없습니다.");
        }

        if (request.getDeleteFileIds() != null && !request.getDeleteFileIds().isEmpty()) {
            List<File> filesToDelete = fileUseCase.findFilesByIds(request.getDeleteFileIds());

            if (filesToDelete.isEmpty()) {
                logger.warn("삭제할 파일을 찾을 수 없습니다. ID 목록: {}", request.getDeleteFileIds());
                throw new BoardDomainException("삭제할 파일이 존재하지 않습니다.");
            }

            fileUseCase.deleteFiles(request.getDeleteFileIds());
            post.removeFiles(filesToDelete);
        }

        List<File> newFiles = fileUseCase.saveFiles(files, post);
        post.addFiles(newFiles);

        post.updatePost(request.getTitle(), request.getContent());
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

        Post post = postPersistencePort.findById(postId)
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

        postPersistencePort.deleteById(postId);
        logger.info("게시글이 성공적으로 삭제되었습니다. Board ID: {}, Post ID: {}", boardId, postId);
    }
}
