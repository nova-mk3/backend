package org.nova.backend.board.suggestion.application.service;

import jakarta.servlet.http.HttpServletResponse;
import org.nova.backend.board.util.FileStorageUtil;
import org.nova.backend.shared.constants.FilePathConstants;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nova.backend.board.common.domain.exception.BoardDomainException;
import org.nova.backend.board.suggestion.application.dto.response.SuggestionFileResponse;
import org.nova.backend.board.suggestion.application.port.in.SuggestionFileUseCase;
import org.nova.backend.board.suggestion.application.port.out.SuggestionFilePersistencePort;
import org.nova.backend.board.suggestion.domain.exception.SuggestionFileDomainException;
import org.nova.backend.board.suggestion.domain.model.entity.SuggestionFile;
import org.nova.backend.board.util.FileUtil;
import org.nova.backend.member.adapter.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SuggestionFileService implements SuggestionFileUseCase {
    private static final Logger logger = LoggerFactory.getLogger(SuggestionFileService.class);

    private final SuggestionFilePersistencePort filePersistencePort;
    private final MemberRepository memberRepository;

    @Value("${file.storage.path}")
    private String baseFileStoragePath;

    /**
     * 파일 업로드
     */
    @Transactional
    @Override
    public List<SuggestionFileResponse> uploadFiles(
            List<MultipartFile> files,
            UUID postId
    ) {
        FileUtil.validateFileList(files);

        for (MultipartFile file : files) {
            FileUtil.validateFileExtension(file);
            FileUtil.validateFileSize(file);
        }

        String storagePath = getStoragePath();

        return files.stream()
                .map(file -> processFileUpload(file, storagePath))
                .collect(Collectors.toList());
    }

    private SuggestionFileResponse processFileUpload(
            MultipartFile file,
            String storagePath
    ) {
        String savedFilePath = FileStorageUtil.saveFileToLocal(file, storagePath, FilePathConstants.PROTECTED_FOLDER);
        SuggestionFile savedFile = new SuggestionFile(null, file.getOriginalFilename(), savedFilePath, null);
        savedFile = filePersistencePort.save(savedFile);

        return new SuggestionFileResponse(
                savedFile.getId(),
                savedFile.getOriginalFilename(),
                "/api/v1/suggestion-files/" + savedFile.getId() + "/download"
        );
    }

    /**
     * 파일 저장 경로 결정
     */
    private String getStoragePath() {
        return baseFileStoragePath;
    }

    /**
     * 파일 list로 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<SuggestionFile> findFilesByIds(List<UUID> fileIds) {
        List<SuggestionFile> files = filePersistencePort.findFilesByIds(fileIds);

        if (files.isEmpty()) {
            logger.warn("파일이 존재하지 않습니다. ID 목록: {}", fileIds);
        } else {
            logger.info("파일 조회 완료: {}", files);
        }

        return files;
    }

    /**
     * 파일 다운로드 (로그인한 사람만 가능)
     */
    @Override
    public void downloadFile(
            UUID fileId,
            HttpServletResponse response,
            UUID memberId
    ) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new BoardDomainException("사용자를 찾을 수 없습니다."));

        SuggestionFile file = filePersistencePort.findFileById(fileId)
                .orElseThrow(() -> new SuggestionFileDomainException("파일을 찾을 수 없습니다.",HttpStatus.NOT_FOUND));

        FileStorageUtil.processFileDownload(file.getFilePath(), file.getOriginalFilename(), response);
    }
}
