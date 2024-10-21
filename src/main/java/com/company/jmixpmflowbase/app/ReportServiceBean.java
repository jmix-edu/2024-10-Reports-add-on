package com.company.jmixpmflowbase.app;

import com.company.jmixpmflowbase.entity.Project;
import com.company.jmixpmflowbase.entity.ProjectStatus;
import com.company.jmixpmflowbase.entity.User;
import io.jmix.core.DataManager;
import io.jmix.core.FileRef;
import io.jmix.core.FileStorage;
import io.jmix.reports.entity.ReportOutputType;
import io.jmix.reports.runner.FluentReportRunner;
import io.jmix.reports.runner.ReportRunner;
import io.jmix.reports.yarg.reporting.ReportOutputDocument;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ReportServiceBean {
    private final DataManager dataManager;
    private final FileStorage fileStorage;
    private final ReportRunner reportRunner;

    public ReportServiceBean(DataManager dataManager, FileStorage fileStorage, ReportRunner reportRunner) {
        this.dataManager = dataManager;
        this.fileStorage = fileStorage;
        this.reportRunner = reportRunner;
    }

    public void generateSingleUserReport(User user) {
        final ReportOutputDocument document = reportRunner.byReportCode("user-report")
                .withParams(Map.of("entity", user))
                .withOutputType(ReportOutputType.DOCX)
                .withOutputNamePattern(user.getDisplayName() + "-report.docx")
                .run();

        final byte[] reportContent = document.getContent();
        final String reportName = document.getDocumentName();

        FileRef userDocument = fileStorage.saveStream(reportName,
                new ByteArrayInputStream(reportContent));

        user.setDocument(userDocument);
        dataManager.save(user);

    }

    public List<Map<String, Object>> getProjectsList(ProjectStatus status) {
        List<Project> projectsList = dataManager.load(Project.class)
                .query("select p from Project p where p.status = :status")
                .parameter("status", status.getId())
                .list();

        return projectsList
                .stream()
                .map(project -> Map.<String, Object>of(
                        "name", project.getName() != null ? project.getName() : "",
                        "status", project.getStatus() != null ? project.getStatus() : "",
                        "startDate", project.getStartDate() != null ? project.getStartDate() : "",
                        "manager", project.getManager() != null ? project.getManager().getDisplayName() : ""
                ))
                .collect(Collectors.toList());
    }

}