package com.company.jmixpmflowbase.app;

import com.company.jmixpmflowbase.entity.Project;
import com.company.jmixpmflowbase.entity.Task;
import com.company.jmixpmflowbase.entity.User;
import io.jmix.core.DataManager;
import io.jmix.core.EntitySet;
import io.jmix.core.SaveContext;
import io.jmix.core.security.CurrentAuthentication;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component("TaskImportService")
public class TaskImportService {

    private static final Logger log = LoggerFactory.getLogger(TaskImportService.class);

    private final DataManager dataManager;
    private final CurrentAuthentication currentAuthentication;

    public TaskImportService(DataManager dataManager, CurrentAuthentication currentAuthentication) {
        this.dataManager = dataManager;
        this.currentAuthentication = currentAuthentication;
    }

    public int importTask() {
        List<String> externalTaskNames = obtainExternalTaskNames();
        Project defaultProject = loadDefaultProject();

        List<Task> tasks = externalTaskNames.stream()
                .map(taskName -> {
                    Task task = dataManager.create(Task.class);
                    task.setName(taskName);
                    task.setAssignee((User) currentAuthentication.getUser());
                    task.setProject(defaultProject);

                    return task;
                })
                .collect(Collectors.toList());

        EntitySet entitySet = dataManager.save(new SaveContext().saving(tasks));
        log.info("{} tasks imported", entitySet.size());
        return entitySet.size();
    }

    private List<String> obtainExternalTaskNames() {
        return Stream.iterate(0, i -> i).limit(5)
                .map(i -> "Task " + RandomStringUtils.randomAlphabetic(5))
                .collect(Collectors.toList());
    }

    @Nullable
    private Project loadDefaultProject() {
        return dataManager.load(Project.class)
                .query("select p from Project p where p.defaultProject = :isDefault")
                .parameter("isDefault", true)
                .optional()
                .orElse(null);
    }
}
