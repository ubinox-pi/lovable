package com.lovable.service.impls;

import com.lovable.dto.deploy.DeployResponse;
import com.lovable.exception.custom.ResourceNotFoundException;
import com.lovable.exception.custom.UnauthorizedException;
import com.lovable.repository.ProjectRepository;
import com.lovable.service.DeploymentService;
import com.lovable.util.AppUtils;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

/*
 * Copyright (c) 2026 Ramjee Prasad
 * Licensed under a custom Non-Commercial, Attribution, Share-Alike License.
 * See the LICENSE file in the project root for full license information.
 *
 * Project: lovable
 * Package: com.lovable.service.impls
 * Created by: Ashish Kushwaha on 05-09-2026 19:22
 * File: KubernetesDeploymentServiceImpl
 *
 * This source code is intended for educational and non-commercial purposes only.
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *   - Attribution must be given to the original author.
 *   - The code must be shared under the same license.
 *   - Commercial use is strictly prohibited.
 *
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class KubernetesDeploymentServiceImpl implements DeploymentService {

    private static final String NAMESPACE = "lovable-apps";
    private static final String POOL_LABEL = "status";
    private static final String PROJECT_LABEL = "projectId";
    private static final String IDLE = "idle";
    private static final String BUSY = "busy";
    private static final String SYNCER_CONTAINER = "syncer";
    private static final String RUNNER_CONTAINER = "runner";
    private static final String REVERSE_PROXY_CONTAINER = "8090";

    private final KubernetesClient kubernetesClient;
    private final ProjectRepository projectRepository;
    private String url;

    @Override
    public DeployResponse deploy(Long projectId) {
        AtomicReference<DeployResponse> deployResponse = new AtomicReference<>();

        projectRepository.findById(projectId)
                .ifPresentOrElse(project -> {
                            if (project.getOwner().equals(AppUtils.getCurrentUser())) {
                                Optional<Pod> existingPod = Optional.ofNullable(findActivePod(projectId));

                                existingPod.ifPresentOrElse(
                                        _ -> deployResponse.set(new DeployResponse(url)),
                                        () -> deployResponse.set(claimAndStartNewPod(projectId))
                                );
                            } else {
                                throw new UnauthorizedException("You are not authorized to deploy this project.");
                            }
                        }, () -> {
                            throw new ResourceNotFoundException("Project with ID " + projectId + " not found.");
                        }
                );

        return deployResponse.get();
    }

    private Pod findActivePod(Long projectId) {
        return kubernetesClient
                .pods()
                .inNamespace(NAMESPACE)
                .withLabel(PROJECT_LABEL, projectId.toString())
                .withLabel(POOL_LABEL, BUSY)
                .list()
                .getItems()
                .stream()
                .filter(pod -> pod.getStatus().getPhase().equals("Running"))
                .findFirst()
                .orElse(null);
    }

    private DeployResponse claimAndStartNewPod(Long projectId) {
        Pod pod = kubernetesClient
                .pods()
                .inNamespace(NAMESPACE)
                .withLabel(POOL_LABEL, IDLE)
                .list()
                .getItems()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No idle pods available. Scale up the cluster."));
        String podName = pod.getMetadata().getName();
        log.info("Claiming pod: {} for project {}", podName, projectId);

        kubernetesClient
                .pods()
                .inNamespace(NAMESPACE)
                .withName(podName)
                .edit(pod1 -> {
                    pod1.getMetadata().getLabels().put(POOL_LABEL, BUSY);
                    pod1.getMetadata().getLabels().put(PROJECT_LABEL, projectId.toString());
                    return pod1;
                });

        try {

            // syncer commands
            String initialSyncCommand = """
                    rm -rf /app/* && mc mirror --overwrite myminio/lovable-apps/%d/ /app/
                    """.formatted(projectId);

            log.info("Starting initial sync for project {} in pod {}", projectId, podName);
            execCommand(podName, SYNCER_CONTAINER, "sh", "-c", initialSyncCommand);

            String watchCmd = String.format(
                    "nohup mc mirror --overwrite --watch myminio/projects/%d/ /app/ > /app/sync.log 2>&1 &",
                    projectId
            );
            execCommand(podName, SYNCER_CONTAINER, "sh", "-c", watchCmd);

            // runner commands
            String startCmd = "npm install && nohup npm run dev -- --host 0.0.0.0 --port 3000 > /app/dev.log 2>&1 &";

            log.info("Starting application for project {} in pod {}", projectId, podName);
            execCommand(podName, RUNNER_CONTAINER, "sh", "-c", startCmd);

            log.info("Deployment completed for project {} in pod {}", projectId, podName);
            return new DeployResponse(url);
        } catch (Exception e) {
            log.error("Deployment failed for project {} in pod {}: {}", projectId, podName, e.getMessage());
            kubernetesClient
                    .pods()
                    .inNamespace(NAMESPACE)
                    .withName(podName)
                    .delete();
            throw new RuntimeException("Deployment failed: " + e.getMessage());
        }
    }

    private void execCommand(String podName, String container, String... command) {
        log.debug("Exec in {}:{} -> {}", podName, container, String.join(" ", command));

        CompletableFuture<String> data = new CompletableFuture<>();
        try (ExecWatch ignore = kubernetesClient
                .pods()
                .inNamespace(NAMESPACE)
                .withName(podName)
                .inContainer(container)
                .writingOutput(new ByteArrayOutputStream())
                .writingError(new ByteArrayOutputStream())
                .usingListener((code, reason) -> data.complete("Closed with code: " + code + ", reason: " + reason))
                .exec(command)) {

            if (command[command.length - 1].trim().endsWith("$"))
                Thread.sleep(500);
            else
                data.get(30, TimeUnit.SECONDS);

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Pod Execution Failed " + e);
        }
    }
}
