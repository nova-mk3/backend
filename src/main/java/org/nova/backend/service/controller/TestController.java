package org.nova.backend.service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.nova.backend.service.entity.Test;
import org.nova.backend.service.service.TestService;
import org.nova.backend.shared.exception.CustomException;
import org.nova.backend.shared.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "테스트 API", description = "프로젝트 환경 설정 테스트")
@RestController
@RequestMapping("/service")
public class TestController {

    private final TestService testService;

    @Autowired
    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/ping")
    @TestApiDocument.pingApiDoc
    public ApiResponse<String> pingService() {
        return ApiResponse.success("nova server is working");
    }

    @GetMapping("/error")
    public ApiResponse<Void> throwError() {
        throw new CustomException("권한이 없습니다.", HttpStatus.FORBIDDEN);
    }

    @GetMapping("/by-age")
    @TestApiDocument.byAgeApiDoc
    public ApiResponse<List<Test>> getTestsByAge(@RequestParam int age) {
        List<Test> tests = testService.getTestsByAge(age);
        return ApiResponse.success(tests);
    }
}
