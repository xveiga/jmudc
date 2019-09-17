package mudc.core;

import java.util.ArrayList;
import java.util.List;

public class UpdateComparator {
	
	public static final int STATUS_NORMAL = 0;
	public static final int STATUS_DELETED = 1;
	public static final int STATUS_UPDATED = 2;
	public static final int STATUS_NEW = 3;
	public static final int STATUS_NEW_CONTENT = 4;

	public UpdateComparatorOutput<MoodleCourse> compareMoodleCourses(List<MoodleCourse> localCourses,
			List<MoodleCourse> updatedCourses) {
		UpdateComparatorOutput<MoodleCourse> output = new UpdateComparatorOutput<MoodleCourse>();
		
		List<MoodleCourse> returnCourses = new ArrayList<MoodleCourse>();
		List<UpdatedObject> updateList = new ArrayList<UpdatedObject>();
		
		// Element cross-check
		for (MoodleCourse localCourse : localCourses) { // Check if the server courses are local
			boolean isLocal = false;
			for (MoodleCourse updatedCourse : updatedCourses) {
				if (localCourse.id == updatedCourse.id) { // Same ID, course is local.
					isLocal = true;
					
					List<UpdatedValue> changedValues = compareCourseUpdatedValues(localCourse, updatedCourse);
					
					if (changedValues.size() > 0) {
						UpdatedObject update = new UpdatedObject();
						update.changedValues = changedValues;
						update.moodleObject = updatedCourse;
						update.updateTime = System.currentTimeMillis() / 1000L;
						updateList.add(update);
						updatedCourse.status = STATUS_UPDATED;
						updatedCourse.categoryList = localCourse.categoryList;
						returnCourses.add(updatedCourse);
					}
					else {
						localCourse.status = STATUS_NORMAL;
						returnCourses.add(localCourse);
					}
					
					break;
				}
			}
			if (!isLocal) {
				// Local course is not on server anymore: deleted.
				localCourse.status = STATUS_DELETED;
				returnCourses.add(localCourse);
			}
		}
		
		//returnCourses.addAll(localCourses);
		
		for (MoodleCourse updatedCourse : updatedCourses) { // Check if remote courses are local
			boolean exists = false;
			for (MoodleCourse localCourse : localCourses) {
				if (updatedCourse.id == localCourse.id) { // Same ID, course is local
					exists = true;
					break;
				}
			}
			if (!exists) {
				// Remote course is not on local: new.
				updatedCourse.status = STATUS_NEW;
				returnCourses.add(updatedCourse);
			}
		}
		
		output.mergedList = returnCourses;
		output.updates = updateList;

		return output;
	}
	
	public UpdateComparatorOutput<MoodleCategory> compareMoodleCourseContents(List<MoodleCategory> localCourses,
			List<MoodleCategory> updatedCourses) {
		UpdateComparatorOutput<MoodleCategory> output = new UpdateComparatorOutput<MoodleCategory>();
		
		List<MoodleCategory> returnCourses = new ArrayList<MoodleCategory>();
		List<UpdatedObject> updateList = new ArrayList<UpdatedObject>();
		
		// Element cross-check
		for (MoodleCategory localCourse : localCourses) { // Check if the server courses are local
			boolean isLocal = false;
			for (MoodleCategory updatedCourse : updatedCourses) {
				if (localCourse.id == updatedCourse.id) { // Same ID, course is local.
					isLocal = true;
					
					List<UpdatedValue> changedValues = compareCategoryUpdatedValues(localCourse, updatedCourse);
					
					if (changedValues.size() > 0) {
						UpdatedObject update = new UpdatedObject();
						update.changedValues = changedValues;
						update.moodleObject = updatedCourse;
						update.updateTime = System.currentTimeMillis() / 1000L;
						updateList.add(update);
						localCourse.status = STATUS_UPDATED;
					}
					else {
						localCourse.status = STATUS_NORMAL;
					}
					
					break;
				}
			}
			if (!isLocal) {
				// Local element is not on server anymore: deleted.
				localCourse.status = STATUS_DELETED;
			}
		}
		
		returnCourses.addAll(localCourses);
		
		for (MoodleCategory updatedCourse : updatedCourses) { // Check if remote courses are local
			boolean exists = false;
			for (MoodleCategory localCourse : localCourses) {
				if (updatedCourse.id == localCourse.id) { // Same ID, course is local
					exists = true;
					break;
				}
			}
			if (!exists) {
				// Remote course is not on local: new.
				updatedCourse.status = STATUS_NEW;
				returnCourses.add(updatedCourse);
			}
		}
		
		output.mergedList = returnCourses;
		output.updates = updateList;

		return output;
	}
	
	private List<UpdatedValue> compareCategoryUpdatedValues(MoodleCategory localCategory, MoodleCategory updatedCategory) {
		
		List<UpdatedValue> changedValues = new ArrayList<UpdatedValue>();
		
		// Check for modified values
		if (!localCategory.name.equals(updatedCategory.name)) {
			UpdatedValue update = new UpdatedValue();
			update.name = "shortname";
			update.oldValue = localCategory.name;
			update.newValue = updatedCategory.name;
			changedValues.add(update);
		}
		if (!localCategory.summary.equals(updatedCategory.summary)) {
			UpdatedValue update = new UpdatedValue();
			update.name = "summary";
			update.oldValue = localCategory.summary;
			update.newValue = updatedCategory.summary;
			changedValues.add(update);
		}
		if (localCategory.summaryformat != updatedCategory.summaryformat) {
			UpdatedValue update = new UpdatedValue();
			update.name = "summaryformat";
			update.oldValue = localCategory.summaryformat;
			update.newValue = updatedCategory.summaryformat;
			changedValues.add(update);
		}
		if (localCategory.visible != updatedCategory.visible) {
			UpdatedValue update = new UpdatedValue();
			update.name = "visible";
			update.oldValue = localCategory.visible;
			update.newValue = updatedCategory.visible;
			changedValues.add(update);
		}
		return changedValues;
		
	}
	
	/*private List<UpdatedValue> compareModuleUpdatedValues(MoodleModule localModule, MoodleModule updatedModule) {
		
		List<UpdatedValue> changedValues = new ArrayList<UpdatedValue>();
		
		// Check for modified values
		if (!localModule.url.equals(updatedModule.url)) {
			UpdatedValue update = new UpdatedValue();
			update.name = "url";
			update.oldValue = localModule.url;
			update.newValue = updatedModule.url;
			changedValues.add(update);
		}
		if (!localModule.name.equals(updatedModule.name)) {
			UpdatedValue update = new UpdatedValue();
			update.name = "name";
			update.oldValue = localModule.name;
			update.newValue = updatedModule.name;
			changedValues.add(update);
		}
		if (localModule.instance != updatedModule.instance) {
			UpdatedValue update = new UpdatedValue();
			update.name = "instance";
			update.oldValue = localModule.instance;
			update.newValue = updatedModule.instance;
			changedValues.add(update);
		}
		if (!localModule.description.equals(updatedModule.description)) {
			UpdatedValue update = new UpdatedValue();
			update.name = "description";
			update.oldValue = localModule.description;
			update.newValue = updatedModule.description;
			changedValues.add(update);
		}
		if (localModule.visible != updatedModule.visible) {
			UpdatedValue update = new UpdatedValue();
			update.name = "visible";
			update.oldValue = localModule.visible;
			update.newValue = updatedModule.visible;
			changedValues.add(update);
		}
		if (!localModule.description.equals(updatedModule.description)) {
			UpdatedValue update = new UpdatedValue();
			update.name = "description";
			update.oldValue = localModule.description;
			update.newValue = updatedModule.description;
			changedValues.add(update);
		}
		if (!localModule.modname.equals(updatedModule.modname)) {
			UpdatedValue update = new UpdatedValue();
			update.name = "modname";
			update.oldValue = localModule.modname;
			update.newValue = updatedModule.modname;
			changedValues.add(update);
		}
		if (!localModule.modplural.equals(updatedModule.modplural)) {
			UpdatedValue update = new UpdatedValue();
			update.name = "modplural";
			update.oldValue = localModule.modplural;
			update.newValue = updatedModule.modplural;
			changedValues.add(update);
		}
		if (localModule.indent != updatedModule.indent) {
			UpdatedValue update = new UpdatedValue();
			update.name = "indent";
			update.oldValue = localModule.indent;
			update.newValue = updatedModule.indent;
			changedValues.add(update);
		}
		return changedValues;
		
	}*/
	
    /*public <T> UpdateComparatorOutput<T> compareMoodleCourseContents(List<T> localCourses,
			List<MoodleCategory> updatedCourses) {
		UpdateComparatorOutput<T> output = new UpdateComparatorOutput<T>();
		
		List<T> returnCourses = new ArrayList<T>();
		List<MoodleUpdatedObject> updateList = new ArrayList<MoodleUpdatedObject>();
		
		// Element cross-check
		for (T localCourse : localCourses) { // Check if the server courses are local
			boolean isLocal = false;
			for (T updatedCourse : updatedCourses) {
				if (localCourse.id == updatedCourse.id) { // Same ID, course is local.
					isLocal = true;
					
					List<MoodleUpdatedValue> changedValues = compareCategoryUpdatedValues(localCourse, updatedCourse);
					
					if (changedValues.size() > 0) {
						MoodleUpdatedObject update = new MoodleUpdatedObject();
						update.changedValues = changedValues;
						update.moodleObject = updatedCourse;
						update.updateTime = System.currentTimeMillis() / 1000L;
						updateList.add(update);
						localCourse.status = STATUS_UPDATED;
					}
					else {
						localCourse.status = STATUS_NORMAL;
					}
					
					break;
				}
			}
			if (!isLocal) {
				// Local element is not on server anymore: deleted.
				localCourse.status = STATUS_DELETED;
			}
		}
		
		returnCourses.addAll(localCourses);
		
		for (MoodleCategory updatedCourse : updatedCourses) { // Check if remote courses are local
			boolean exists = false;
			for (MoodleCategory localCourse : localCourses) {
				if (updatedCourse.id == localCourse.id) { // Same ID, course is local
					exists = true;
					break;
				}
			}
			if (!exists) {
				// Remote course is not on local: new.
				updatedCourse.status = STATUS_NEW;
				returnCourses.add(updatedCourse);
			}
		}
		
		output.mergedList = returnCourses;
		output.updates = updateList;

		return output;
	}*/

	private List<UpdatedValue> compareCourseUpdatedValues(MoodleCourse localCourse, MoodleCourse updatedCourse) {
		
		List<UpdatedValue> changedValues = new ArrayList<UpdatedValue>();
		
		// Check for modified values
		if (!localCourse.shortname.equals(updatedCourse.shortname)) {
			UpdatedValue update = new UpdatedValue();
			update.name = "shortname";
			update.oldValue = localCourse.shortname;
			update.newValue = updatedCourse.shortname;
			changedValues.add(update);
		}
		
		if (!localCourse.fullname.equals(updatedCourse.fullname)) {
			UpdatedValue update = new UpdatedValue();
			update.name = "fullname";
			update.oldValue = localCourse.fullname;
			update.newValue = updatedCourse.fullname;
			changedValues.add(update);
		}
		
		if (localCourse.enrolledusercount != updatedCourse.enrolledusercount) {
			UpdatedValue update = new UpdatedValue();
			update.name = "enrolledusercount";
			update.oldValue = localCourse.enrolledusercount;
			update.newValue = updatedCourse.enrolledusercount;
			changedValues.add(update);
		}
		
		if (!localCourse.idnumber.equals(updatedCourse.idnumber)) {
			UpdatedValue update = new UpdatedValue();
			update.name = "idnumber";
			update.oldValue = localCourse.idnumber;
			update.newValue = updatedCourse.idnumber;
			changedValues.add(update);
		}
		
		if (localCourse.visible != updatedCourse.visible) {
			UpdatedValue update = new UpdatedValue();
			update.name = "visible";
			update.oldValue = localCourse.visible;
			update.newValue = updatedCourse.visible;
			changedValues.add(update);
		}
	
		if (!localCourse.summary.equals(updatedCourse.summary)) {
			UpdatedValue update = new UpdatedValue();
			update.name = "summary";
			update.oldValue = localCourse.summary;
			update.newValue = updatedCourse.summary;
			changedValues.add(update);
		}
		
		if (localCourse.summaryformat != updatedCourse.summaryformat) {
			UpdatedValue update = new UpdatedValue();
			update.name = "summaryformat";
			update.oldValue = localCourse.summaryformat;
			update.newValue = updatedCourse.summaryformat;
			changedValues.add(update);
		}
		
		if (!localCourse.format.equals(updatedCourse.format)) {
			UpdatedValue update = new UpdatedValue();
			update.name = "format";
			update.oldValue = localCourse.format;
			update.newValue = updatedCourse.format;
			changedValues.add(update);
		}
		
		if (localCourse.showgrades != updatedCourse.showgrades) {
			UpdatedValue update = new UpdatedValue();
			update.name = "showgrades";
			update.oldValue = localCourse.showgrades;
			update.newValue = updatedCourse.showgrades;
			changedValues.add(update);
		}
		
		if (!localCourse.lang.equals(updatedCourse.lang)) {
			UpdatedValue update = new UpdatedValue();
			update.name = "lang";
			update.oldValue = localCourse.lang;
			update.newValue = updatedCourse.lang;
			changedValues.add(update);
		}
		
		if (localCourse.enablecompletion != updatedCourse.enablecompletion) {
			UpdatedValue update = new UpdatedValue();
			update.name = "enablecompletion";
			update.oldValue = localCourse.enablecompletion;
			update.newValue = updatedCourse.enablecompletion;
			changedValues.add(update);
		}
		return changedValues;
		
	}

}

class UpdateComparatorOutput<T> {
	List<UpdatedObject> updates = null;
	List<T> mergedList = null;
}
