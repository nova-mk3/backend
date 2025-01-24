package org.nova.backend.board.application.service;

import java.util.List;
import java.util.UUID;
import org.nova.backend.board.application.port.in.PostUseCase;
import org.nova.backend.board.application.port.out.PostPersistencePort;
import org.nova.backend.board.domain.exception.BoardDomainException;
import org.nova.backend.board.domain.model.entity.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PostService implements PostUseCase {
    private static final Logger logger = LoggerFactory.getLogger(PostService.class); // Logger 추가

    private final PostPersistencePort postPersistencePort;

    public PostService(PostPersistencePort postPersistencePort) {
        this.postPersistencePort = postPersistencePort;
    }

    /**
     * 모든 게시글 조회
     *
     * @return 게시글 리스트
     */
    @Override
    public List<Post> getAllPosts() {
        logger.info("모든 게시글 조회 요청을 처리 중입니다.");
        List<Post> posts = postPersistencePort.findAllPosts();
        logger.info("총 {}개의 게시글을 조회했습니다.", posts.size());
        return posts;
    }

    /**
     * 새로운 게시글 생성
     *
     * @param post 생성할 게시글 객체
     * @return 저장된 게시글 객체
     */
    @Override
    public Post createPost(Post post) {
        logger.info("게시글 생성 요청을 처리 중입니다. 제목: {}", post.getTitle().getTitle());
        Post savedPost = postPersistencePort.save(post);
        logger.info("게시글이 성공적으로 생성되었습니다. ID: {}", savedPost.getPost_id());
        return savedPost;
    }

    /**
     * ID를 기반으로 특정 게시글 조회
     *
     * @param postId 게시글 ID
     * @return 게시글 객체
     * @throws BoardDomainException 게시글이 존재하지 않을 경우 예외 발생
     */
    @Override
    public Post getPostById(UUID postId) {
        logger.info("ID {}에 대한 게시글 조회 요청을 처리 중입니다.", postId);
        return postPersistencePort.findById(postId)
                .orElseThrow(() -> {
                    logger.error("ID {}에 해당하는 게시글이 존재하지 않습니다.", postId);
                    return new BoardDomainException("Post not found for ID: " + postId);
                });
    }
}
