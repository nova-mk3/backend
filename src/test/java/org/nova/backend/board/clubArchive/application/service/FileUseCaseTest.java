package org.nova.backend.board.clubArchive.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nova.backend.board.common.application.dto.response.FileResponse;
import org.nova.backend.board.common.application.port.out.FilePersistencePort;
import org.nova.backend.board.common.application.service.FileService;
import org.nova.backend.board.common.domain.model.entity.File;
import org.nova.backend.board.common.domain.model.valueobject.PostType;
import org.nova.backend.board.common.domain.exception.FileDomainException;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.nova.backend.member.domain.exception.MemberDomainException;
import org.nova.backend.member.domain.model.entity.Member;
import org.nova.backend.member.helper.MemberFixture;
import org.nova.backend.annotation.SlowTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SlowTest
@ExtendWith(MockitoExtension.class)
public class FileUseCaseTest {

    @Mock
    private FilePersistencePort filePersistencePort;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private FileService fileService;

    private Member testMember;
    private UUID fileId;
    private File testFile;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        testMember = MemberFixture.createStudent();

        fileId = UUID.randomUUID();
        testFile = new File(fileId, "test.txt", "/files/test.txt", null, 0);
        mockFile = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());

        ReflectionTestUtils.setField(fileService, "baseFileStoragePath", "/tmp/uploads");
    }

    @Test
    void 파일_업로드_성공() {
        UUID memberId = testMember.getId();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(testMember));
        when(filePersistencePort.save(any(File.class))).thenReturn(testFile);

        List<FileResponse> uploadedFiles = fileService.uploadFiles(
                List.of(mockFile), memberId, PostType.FREE
        );

        assertThat(uploadedFiles).hasSize(1);
        assertThat(uploadedFiles.get(0).getOriginalFileName()).isEqualTo("test.txt");
        verify(filePersistencePort, times(1)).save(any(File.class));
    }

    @Test
    void 파일_ID로_조회() {
        when(filePersistencePort.findFileById(fileId)).thenReturn(Optional.of(testFile));

        Optional<File> foundFile = fileService.findFileById(fileId);

        assertThat(foundFile).isPresent();
        assertThat(foundFile.get().getId()).isEqualTo(fileId);
    }

    @Test
    void 존재하지_않는_파일_조회_예외발생() {
        UUID nonExistentFileId = UUID.randomUUID();
        when(filePersistencePort.findFileById(nonExistentFileId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fileService.findFileById(nonExistentFileId))
                .isInstanceOf(FileDomainException.class)
                .hasMessage("파일을 찾을 수 없습니다. ID: " + nonExistentFileId);
    }

    @Test
    void 파일_삭제_성공() {
        when(filePersistencePort.findFileById(fileId)).thenReturn(Optional.of(testFile));

        fileService.deleteFileById(fileId, testMember.getId());

        verify(filePersistencePort, times(1)).deleteFileById(fileId);
    }

    @Test
    void 존재하지_않는_파일_삭제_예외발생() {
        UUID nonExistentFileId = UUID.randomUUID();
        when(filePersistencePort.findFileById(nonExistentFileId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fileService.deleteFileById(nonExistentFileId, testMember.getId()))
                .isInstanceOf(FileDomainException.class)
                .hasMessage("파일을 찾을 수 없습니다.");
    }

    @Test
    void 파일_다운로드_예외_발생() {
        when(memberRepository.findById(any(UUID.class))).thenReturn(Optional.of(testMember));

        UUID nonExistentFileId = UUID.randomUUID();
        when(filePersistencePort.findFileById(nonExistentFileId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fileService.downloadFile(nonExistentFileId, null, testMember.getId()))
                .isInstanceOf(FileDomainException.class)
                .hasMessage("파일을 찾을 수 없습니다.");

        verify(filePersistencePort, never()).save(any(File.class));
    }

    @Test
    void 로그인_안한_사용자의_파일_업로드_예외발생() {
        UUID nonExistentUserId = UUID.randomUUID();

        assertThatThrownBy(() -> fileService.uploadFiles(
                List.of(mockFile), nonExistentUserId, PostType.FREE
        )).isInstanceOf(MemberDomainException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    void 로그인_안한_사용자의_파일_다운로드_예외발생() {
        UUID nonExistentUserId = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();

        assertThatThrownBy(() -> fileService.downloadFile(fileId, null, nonExistentUserId))
                .isInstanceOf(MemberDomainException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }
}
