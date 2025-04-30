package org.nova.backend.mypage.application.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.common.application.port.out.BasePostPersistencePort;
import org.nova.backend.mypage.application.dto.mapper.MyPageMapper;
import org.nova.backend.mypage.application.dto.response.MyPostsResponse;
import org.nova.backend.mypage.application.port.in.MyPageUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService implements MyPageUseCase {
    private final BasePostPersistencePort basePostPersistencePort;

    @Override
    @Transactional(readOnly = true)
    public Page<MyPostsResponse> getMyPosts(UUID memberId, Pageable pageable) {
        return basePostPersistencePort.findAllByMemberId(memberId, pageable)
                .map(MyPageMapper::toMyPostsResponse);
    }
}