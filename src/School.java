import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by 11ryt on 12/12/2016.
 */
public class School {
    ArrayList<Student> masterStudentList;
    ArrayList<Class> roomList;
    ArrayList<ArrayList<Student>> assignmentOrder;

    School() {
        masterStudentList = new ArrayList<>();
        for (int i = 0; i < Actor.num_Students; i++) {
            masterStudentList.add(new Student(i + 1));
        }
        roomList = new ArrayList();
        for (int i = 0; i < Actor.num_Rooms; i++) {
            roomList.add(new Class());
        }
        assignmentOrder = new ArrayList();
        for (int per = 0; per < Actor.num_Periods; per++) {
            assignmentOrder.add((ArrayList) masterStudentList.clone());
            Collections.shuffle(assignmentOrder.get(per));
        }
        for (int roomNo = 0; roomNo < Actor.num_Rooms; roomNo++) {
            Class c = roomList.get(roomNo);
            int studentOffset = roomNo * Actor.rows * Actor.cols;
            for (int period = 0; period < Actor.num_Periods; period++) {
                for (int seat = 0; seat < Actor.rows * Actor.cols; seat++) {
                    c.studentList.get(period).add(assignmentOrder.get(period).get(studentOffset + seat));
                }
            }
        }
    }
}
