package org.nova.backend.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Tag(name = "테스트 API", description = "프로젝트 환경 설정 테스트")
public @interface TestApiDocument {

    @Operation(summary = "ping 테스트", description = "ping 테스트")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface pingApiDoc {
    }

    @Operation(summary = "get 테스트", description = "get 테스트")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "age가 더 많은 사람들 리스트 반환",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface byAgeApiDoc {
    }
}
