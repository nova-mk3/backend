package org.nova.backend.board.adapter.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.nova.backend.board.application.dto.request.CreatePostRequest;
import org.nova.backend.board.application.dto.response.PostResponse;
import org.nova.backend.board.application.mapper.PostMapper;
import org.nova.backend.board.application.port.in.PostUseCase;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Post API", description = "게시글 생성 및 관련 API 목록")
@RestController
@RequestMapping("/api/v1/posts")
public class CreatePostController {
    private final PostUseCase postUseCase;
    private final PostMapper postMapper;

    public CreatePostController(
            PostUseCase postUseCase,
            PostMapper postMapper
    ) {
        this.postUseCase = postUseCase;
        this.postMapper = postMapper;
    }

    @Operation(summary = "게시글 생성", description = "새로운 게시글을 생성합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "게시글 생성 성공", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(mediaType = "application/json"))
    })
    @PostMapping(consumes = {"multipart/form-data"})
    public ApiResponse<PostResponse> createPost(
            @RequestBody CreatePostRequest request,
            @RequestParam("files") List<MultipartFile> files
    ) {

        var post = postMapper.toEntity(request);
        var savedPost = postUseCase.createPost(post);
        var response = postMapper.toResponse(savedPost);

        return ApiResponse.success(response);
    }
}
