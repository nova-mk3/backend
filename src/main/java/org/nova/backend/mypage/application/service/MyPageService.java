package org.nova.backend.mypage.application.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.common.application.port.out.BasePostPersistencePort;
import org.nova.backend.board.suggestion.application.port.out.SuggestionPostPersistencePort;
import org.nova.backend.mypage.application.mapper.MyPageMapper;
import org.nova.backend.mypage.application.dto.response.MyPostsResponse;
import org.nova.backend.mypage.application.dto.response.MySuggestionPostResponse;
import org.nova.backend.mypage.application.port.in.MyPageUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService implements MyPageUseCase {
    private final BasePostPersistencePort basePostPersistencePort;
    private final SuggestionPostPersistencePort suggestionPostPersistencePort;


    @Override
    @Transactional(readOnly = true)
    public Page<MyPostsResponse> getMyPosts(UUID memberId, Pageable pageable) {
        return basePostPersistencePort.findAllByMemberId(memberId, pageable)
                .map(MyPageMapper::toMyPostsResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MySuggestionPostResponse> getMySuggestionPosts(UUID memberId, Pageable pageable) {
        return suggestionPostPersistencePort.findAllByMemberId(memberId, pageable)
                .map(MyPageMapper::toMySuggestionPostResponse);
    }
}