package de.uvwxy.habitrpg;

import org.json.JSONArray;

public class ExpandableTask {
	private String taskTitle = "Habit";
	// either habit, daily, todo, reward
	private JSONArray listTasks = null;
	private TaskType type = TaskType.HABIT;

	private String id;

	public ExpandableTask(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setList(JSONArray list, TaskType type) {
		this.listTasks = list;
		this.type = type;
	}

	public JSONArray getList() {
		return listTasks;
	}

	public TaskType getType() {
		return type;
	}

	public void setType(TaskType type) {
		this.type = type;
	}

	public String getTitle() {
		return taskTitle;
	}

	public void setTitle(String title) {
		this.taskTitle = title;
	}

	public enum TaskType {
		HABIT, DAILY, TODO, REWARD
	}

}
