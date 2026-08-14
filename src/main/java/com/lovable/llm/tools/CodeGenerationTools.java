package com.lovable.llm.tools;

import com.lovable.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.LinkedList;
import java.util.List;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.llm.tools
 * Created by: Ashish Kushwaha on 11-08-2026 22:22
 * File: CodeGenerationTools
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

@Slf4j
@RequiredArgsConstructor
public class CodeGenerationTools {

    private final FileService fileService;
    private final String email;
    private final Long projectId;

    @Tool(
            name = "read_files",
//            description =
//                    """
//                            Read the contents of the files
//                            .only input the files name present inside the FILE_TREE.
//                            Do not input any path which is not present under the file tree
//                            """

            description = """
                    Read the contents of one or more existing project files.
                    
                    Use this tool only when the file contents are required to implement
                    the user's request.
                    
                    Request multiple required files in ONE call whenever possible.
                    
                    Do not request a file that has already been returned by this tool
                    in the current conversation.
                    
                    After receiving the requested file contents, continue with the
                    implementation. Do not repeatedly request additional files unless
                    a directly relevant dependency was discovered in the returned content.
                    """
    )
    public List<String> readFiles(

            @ToolParam(description = "List of relative paths (e.g., ['src/App.jsx'])")
            List<String> filePaths
    ) {
        log.debug("Read files called with filePaths: {}", filePaths);
        List<String> results = new LinkedList<>();

        for (String path : filePaths) {
            String cleanPath = path.startsWith("/") ? path.substring(1) : path;

            log.debug("Requested file: {} for user {}", cleanPath, email);

            String content = fileService.getFileContent(email, projectId, cleanPath).getContent();

            results.add(
                    String.format(
                            "----Start OF FILE---- \n %s \n ----End OF FILE----\n%s", cleanPath, content
                    )
            );

            log.debug("File content: {}", content);
        }

        log.debug("Read files results: {}", results);
        return results;
    }
}
