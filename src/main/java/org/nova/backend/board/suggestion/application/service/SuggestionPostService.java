package org.nova.backend.board.suggestion.application.service;

import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.nova.backend.notification.application.port.in.NotificationUseCase;
import org.nova.backend.notification.domain.model.entity.valueobject.EventType;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.common.domain.exception.BoardDomainException;
import org.nova.backend.board.suggestion.application.dto.request.SuggestionPostRequest;
import org.nova.backend.board.suggestion.application.dto.request.SuggestionReplyRequest;
import org.nova.backend.board.suggestion.application.dto.response.SuggestionPostDetailResponse;
import org.nova.backend.board.suggestion.application.dto.response.SuggestionPostSummaryResponse;
import org.nova.backend.board.suggestion.application.dto.response.SuggestionReplyResponse;
import org.nova.backend.board.suggestion.application.mapper.SuggestionPostMapper;
import org.nova.backend.board.suggestion.application.port.in.SuggestionFileUseCase;
import org.nova.backend.board.suggestion.application.port.in.SuggestionPostUseCase;
import org.nova.backend.board.suggestion.application.port.out.SuggestionPostPersistencePort;
import org.nova.backend.board.suggestion.domain.exception.SuggestionDomainException;
import org.nova.backend.board.suggestion.domain.model.entity.SuggestionFile;
import org.nova.backend.board.suggestion.domain.model.entity.SuggestionPost;
import org.nova.backend.member.adapter.repository.MemberRepository;
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
public class SuggestionPostService implements SuggestionPostUseCase {
    private static final Logger logger = LoggerFactory.getLogger(SuggestionPostService.class);

    private final SuggestionPostPersistencePort suggestionPostPersistencePort;
    private final MemberRepository memberRepository;
    private final SuggestionFileUseCase fileUseCase;
    private final SuggestionPostMapper postMapper;
    private final NotificationUseCase notificationUseCase;

    /**
     * 새로운 건의 게시글과 첨부파일 저장
     *
     * @param request  생성할 게시글 객체
     * @param memberId  게시글 작성자
     * @return 저장된 게시글 객체
     */
    @Override
    @Transactional
    public SuggestionPostDetailResponse createPost(
            SuggestionPostRequest request,
            UUID memberId
    ) {
        logger.info("건의 게시글 생성 요청 - 사용자 ID: {}", memberId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    logger.error("건의 게시글 생성 실패 - 사용자 없음 ID: {}", memberId);
                    return new BoardDomainException("사용자를 찾을 수 없습니다.");
                });

        SuggestionPost post = postMapper.toEntity(request, member);
        SuggestionPost savedPost = suggestionPostPersistencePort.save(post);
        logger.info("건의 게시글 생성 완료 - 게시글 ID: {}", savedPost.getId());

        if (request.getFileIds() != null && !request.getFileIds().isEmpty()) {
            List<SuggestionFile> files = fileUseCase.findFilesByIds(request.getFileIds());
            files.forEach(file -> file.setSuggestionPost(savedPost));
            savedPost.addFiles(files);
            logger.info("건의 게시글에 파일 {}개 추가 - 게시글 ID: {}", files.size(), savedPost.getId());
        }

        suggestionPostPersistencePort.save(savedPost);
        return postMapper.toDetailResponse(savedPost);
    }

    /**
     * 모든 게시글 조회 (페이징)
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SuggestionPostSummaryResponse> getAllPosts(
            Pageable pageable,
            UUID currentUserId
    ) {
        logger.info("건의 게시글 전체 조회 요청 - 사용자 ID: {}", currentUserId);
        return suggestionPostPersistencePort.findAll(pageable)
                .map(post -> postMapper.toSummaryResponse(post, currentUserId));
    }

    /**
     * 상세 게시글 조회
     */
    @Override
    @Transactional(readOnly = false)
    public SuggestionPostDetailResponse getPostById(
            UUID postId,
            UUID memberId
    ) {
        logger.info("건의 게시글 조회 요청 - 게시글 ID: {}, 사용자 ID: {}", postId, memberId);

        SuggestionPost post = suggestionPostPersistencePort.findById(postId)
                .orElseThrow(() -> {
                    logger.error("건의 게시글 조회 실패 - 게시글 없음 ID: {}", postId);
                    return new SuggestionDomainException("게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
                });

        if (memberId == null) {
            if (post.isPrivate()) {
                logger.warn("비공개 게시글 조회 차단 - 게시글 ID: {}, 비로그인 사용자", postId);
                throw new SuggestionDomainException("비공개 게시글은 작성자 또는 관리자만 조회할 수 있습니다.", HttpStatus.FORBIDDEN);
            }
        } else {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new BoardDomainException("사용자를 찾을 수 없습니다."));

            if (member.getRole() == Role.ADMINISTRATOR && !post.isAdminRead()) {
                logger.info("관리자가 게시글을 읽음 - 게시글 ID: {}", postId);
                post.setAdminRead(true);
                suggestionPostPersistencePort.save(post);
            }

            if (post.isPrivate() && !post.getMember().getId().equals(memberId) && member.getRole() != Role.ADMINISTRATOR) {
                logger.warn("비공개 게시글 조회 차단 - 게시글 ID: {}, 사용자 ID: {}", postId, memberId);
                throw new SuggestionDomainException("비공개 게시글은 작성자 또는 관리자만 조회할 수 있습니다.", HttpStatus.FORBIDDEN);
            }
        }

        logger.info("건의 게시글 조회 성공 - 게시글 ID: {}", post.getId());
        return postMapper.toDetailResponse(post);
    }

    /**
     * 관리자가 건의게시글에 답변 추가
     */
    @Override
    @Transactional
    public SuggestionReplyResponse addAdminReply(
            UUID postId,
            SuggestionReplyRequest request,
            UUID adminId
    ) {
        logger.info("건의 게시글 답변 추가 요청 - 게시글 ID: {}, 관리자 ID: {}", postId, adminId);

        SuggestionPost post = suggestionPostPersistencePort.findById(postId)
                .orElseThrow(() -> {
                    logger.error("건의 게시글 답변 추가 실패 - 게시글 없음 ID: {}", postId);
                    return new SuggestionDomainException("게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
                });

        Member admin = memberRepository.findById(adminId)
                .orElseThrow(() -> new BoardDomainException("관리자를 찾을 수 없습니다."));

        if (admin.getRole() != Role.ADMINISTRATOR) {
            logger.warn("사용자 {}가 건의 게시글 {}에 답변을 작성하려 했으나 권한이 없습니다.", adminId, postId);
            throw new BoardDomainException("건의 게시글에 답변을 작성할 권한이 없습니다.");
        }

        post.addAdminReply(request.getReply());
        suggestionPostPersistencePort.save(post);
        logger.info("건의 게시글 답변 추가 완료 - 게시글 ID: {}", postId);

        notificationUseCase.create(
                post.getMember().getId(),
                EventType.SUGGESTION_ANSWERED,
                post.getId(),
                PostType.SUGGESTION,
                admin.getName()
        );

        return new SuggestionReplyResponse(post.getAdminReply(),post.getAdminReplyTime());
    }

    /**
     * 건의게시판 검색
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SuggestionPostSummaryResponse> searchPostsByTitle(
            String keyword,
            Pageable pageable
    ) {
        Page<SuggestionPost> posts = suggestionPostPersistencePort.searchByTitle(keyword, pageable);
        return posts.map(post -> postMapper.toSummaryResponse(post, null));
    }
}
