package com.company.jmixpmflowbase.view.task;

import com.company.jmixpmflowbase.app.TaskImportService;
import com.company.jmixpmflowbase.entity.Task;

import com.company.jmixpmflowbase.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "tasks", layout = MainView.class)
@ViewController("Task_.list")
@ViewDescriptor("task-list-view.xml")
@LookupComponent("tasksDataGrid")
@DialogMode(width = "64em")
public class TaskListView extends StandardListView<Task> {

    @Autowired
    private TaskImportService taskImportService;
    @Autowired
    private Notifications notifications;
    @ViewComponent
    private CollectionLoader<Task> tasksDl;

    @Subscribe("tasksDataGrid.importTasks")
    public void onTasksDataGridImportTasks(final ActionPerformedEvent event) {
        notifications.show("Tasks imported: " + taskImportService.importTask());
        tasksDl.load();
    }
}