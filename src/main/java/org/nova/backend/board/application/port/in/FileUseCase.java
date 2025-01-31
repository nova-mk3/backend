package org.nova.backend.board.application.port.in;

import java.util.List;
import org.nova.backend.board.domain.model.entity.File;
import org.nova.backend.board.domain.model.entity.Post;
import org.springframework.web.multipart.MultipartFile;

public interface FileUseCase {
    List<File> saveFiles(List<MultipartFile> files, Post post);
}

