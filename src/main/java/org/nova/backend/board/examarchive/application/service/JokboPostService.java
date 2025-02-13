package org.nova.backend.board.examarchive.application.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.common.application.port.in.BoardUseCase;
import org.nova.backend.board.common.application.port.in.FileUseCase;
import org.nova.backend.board.common.application.port.out.BasePostPersistencePort;
import org.nova.backend.board.common.domain.exception.BoardDomainException;
import org.nova.backend.board.common.domain.model.entity.Board;
import org.nova.backend.board.common.domain.model.entity.File;
import org.nova.backend.board.common.domain.model.entity.Post;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.nova.backend.board.examarchive.application.dto.request.JokboPostRequest;
import org.nova.backend.board.examarchive.application.dto.request.UpdateJokboPostRequest;
import org.nova.backend.board.examarchive.application.dto.response.JokboPostDetailResponse;
import org.nova.backend.board.examarchive.application.dto.response.JokboPostSummaryResponse;
import org.nova.backend.board.examarchive.application.mapper.JokboPostMapper;
import org.nova.backend.board.examarchive.application.port.in.JokboPostUseCase;
import org.nova.backend.board.examarchive.application.port.out.JokboPostPersistencePort;
import org.nova.backend.board.examarchive.domain.model.entity.JokboPost;
import org.nova.backend.board.examarchive.domain.model.valueobject.Semester;
import org.nova.backend.board.persistence.repository.PostRepository;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.exception.MemberDomainException;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.member.domain.model.valueobject.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JokboPostService implements JokboPostUseCase {
    private static final Logger logger = LoggerFactory.getLogger(JokboPostService.class);

    private final JokboPostPersistencePort jokboPostPersistencePort;
    private final BasePostPersistencePort basePostPersistencePort;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final BoardUseCase boardUseCase;
    private final FileUseCase fileUseCase;
    private final JokboPostMapper jokboPostMapper;

    /**
     * 족보 게시글 생성
     */
    @Override
    @Transactional
    public JokboPostDetailResponse createPost(
            UUID boardId,
            JokboPostRequest request,
            UUID memberId
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberDomainException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        Board board = boardUseCase.getBoardById(boardId);

        Post post =  new Post(
                UUID.randomUUID(),
                member,
                board,
                PostType.EXAM_ARCHIVE,
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

        JokboPost jokboPost = jokboPostMapper.toEntity(request, savedPost);
        jokboPostPersistencePort.save(jokboPost);

        if (request.getFileIds() != null && !request.getFileIds().isEmpty()) {
            List<File> files = fileUseCase.findFilesByIds(request.getFileIds());
            files.forEach(file -> file.setPost(savedPost));
            savedPost.addFiles(files);
            basePostPersistencePort.save(savedPost);
        }
        return jokboPostMapper.toDetailResponse(jokboPost);
    }

    /**
     * 족보 게시글 수정
     */
    @Override
    @Transactional
    public void updatePost(
            UUID boardId,
            UUID postId,
            UpdateJokboPostRequest request,
            UUID memberId
    ) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BoardDomainException("족보 게시글을 찾을 수 없습니다."));

        JokboPost jokboPost = jokboPostPersistencePort.findByPost(post)
                .orElseThrow(() -> new BoardDomainException("족보 게시글을 찾을 수 없습니다."));

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

        jokboPost.updateJokbo(request.getProfessorName(), request.getYear(), request.getSemester(), request.getSubject());
        jokboPostPersistencePort.save(jokboPost);
    }

    /**
     * 족보 게시글 삭제 (작성자 본인 또는 관리자)
     */
    @Override
    @Transactional
    public void deletePost(
            UUID boardId,
            UUID postId,
            UUID memberId
    ) {
        Post post = postRepository.findByBoardIdAndPostId(boardId, postId)
                .orElseThrow(() -> new BoardDomainException("게시글을 찾을 수 없습니다."));

        JokboPost jokboPost = jokboPostPersistencePort.findByPost(post)
                .orElseThrow(() -> new BoardDomainException("족보 게시글을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BoardDomainException("사용자를 찾을 수 없습니다."));

        if (!post.getMember().getId().equals(memberId) && member.getRole() != Role.ADMINISTRATOR) {
            logger.warn("사용자 {}가 게시글 {}를 삭제하려 했으나 권한이 없습니다.", memberId, postId);
            throw new BoardDomainException("게시글 삭제 권한이 없습니다.");
        }

        List<UUID> fileIds = jokboPost.getPost().getFiles().stream().map(File::getId).toList();
        fileUseCase.deleteFiles(fileIds);

        jokboPostPersistencePort.deleteByPost(post);
        basePostPersistencePort.deleteById(post.getId());
        logger.info("게시글이 성공적으로 삭제되었습니다. Board ID: {}, Post ID: {}", boardId, postId);
    }

    /**
     * 특정 족보 게시글 조회
     */
    @Override
    @Transactional
    public JokboPostDetailResponse getPostById(UUID boardId, UUID postId) {
        Post post = postRepository.findByBoardIdAndPostId(boardId, postId)
                .orElseThrow(() -> new BoardDomainException("족보 게시글을 찾을 수 없습니다."));

        return jokboPostMapper.toDetailResponseFromPost(post);
    }

    /**
     * 족보 게시글 필터링 조회 (교수명, 학년, 학기)
     */
    @Override
    @Transactional
    public Page<JokboPostSummaryResponse> getPostsByFilter(
            UUID boardId,
            String professorName,
            Integer year,
            Semester semester,
            Pageable pageable
    ) {
        return jokboPostPersistencePort.findPostsByFilter(boardId, professorName, year, semester, pageable)
                .map(jokboPostMapper::toSummaryResponse);
    }
}
