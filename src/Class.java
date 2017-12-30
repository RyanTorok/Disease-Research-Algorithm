import java.util.ArrayList;

/**
 * Created by 11ryt on 12/12/2016.
 */
public class Class {

    protected ArrayList<ArrayList<Student>> studentList;

    Class() {
        studentList = new ArrayList();
        for (int i = 0; i < Actor.num_Periods; i++) {
            studentList.add(new ArrayList<Student>());
        }
    }

    void runClass(int pdNo) {
        int countStudents = 0;
        Student[][] studentMatrix = new Student[Actor.rows][Actor.cols];
        for (int i = 0; i < Actor.rows; i++) {
            for (int j = 0; j < Actor.cols; j++) {
                studentMatrix[i][j] = studentList.get(pdNo - 1).get(countStudents);
                countStudents++;
            }
        }
        for (int i = 0; i < studentMatrix.length; i++) {
            for (int j = 0; j < studentMatrix[i].length; j++) {
                if (!studentMatrix[i][j].isAbsent()) {
                    switch (studentMatrix[i][j].getState()) {
                        case 0:
                            break;
                        case 1:
                        case 2: {
                            for (int k = 0; k < studentMatrix.length; k++) {
                                for (int l = 0; l < studentMatrix[k].length; l++) {
                                    if (i == k && j == l) { //skips a student possibly infecting him/herself
                                        continue;
                                    }
                                    double distance = Math.sqrt(Math.pow(Math.abs((double) k - (double) i), 2) + Math.pow(Math.abs((double) l - (double) j), 2));
                                    double probabilityExposure = (studentMatrix[i][j].getState() == 1) ? Actor.CHANCE_CATCH_PREMATURE : Actor.CHANCE_CATCH; //direct chance ignoring distance
                                    probabilityExposure = probabilityExposure * Math.pow(0.4, distance - 1);
                                    if (Math.random() < probabilityExposure) {
                                        studentMatrix[k][l].catchDisease();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
