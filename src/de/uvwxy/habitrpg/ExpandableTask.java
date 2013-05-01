package de.uvwxy.habitrpg;

import org.json.JSONArray;

import android.util.Log;

public class ExpandableTask {
	private String taskTitle = "Habit";
	// either habit, daily, todo, reward
	private JSONArray listTasks = null;
	private TaskType type = TaskType.HABIT;

	public void setList(JSONArray list, TaskType type) {
		Log.i("HABIT", "List len: " + list.length());
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
