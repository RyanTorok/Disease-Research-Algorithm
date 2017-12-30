

/**
 * Created by 11ryt on 12/12/2016.
 */
public class Student {
    private int id;
    private Class[] classList;
    private byte state; //0 for unaffected, 1 for caught but not contracted, 2 for sick, 3 for healed
    private byte delayShift; //counts the number of days in particular state
    private boolean absent;
    public static double[] chanceAdvanceState12;
    public static double[] chanceAdvanceState23;
    public boolean[] used;

    public Student(int id) {
        classList = new Class[Actor.num_Periods];
        used = new boolean[Actor.num_Periods];
        this.setId(id);
        state = 0;
        absent = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Class[] getClassList() {
        return classList;
    }

    public void setClassList(Class[] classList) {
        this.classList = classList;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public boolean isAbsent() {
        return absent;
    }

    void catchDisease() {
        if (state == 0) {
            state = 1;
            delayShift = 0;
        }
    }

    void advanceDay() {
        switch (state) {
            case 0:
                break;
            case 1:
                if (Math.random() < chanceAdvanceState12[delayShift]) {
                    delayShift = 0;
                    state++;
                    if (Math.random() < Actor.porportionAbsent) {
                        absent = true;
                    }
                } else {
                    delayShift++;
                }
                break;
            case 2: {
                if (Math.random() < chanceAdvanceState23[delayShift]) {
                    delayShift = 0;
                    state++;
                    absent = false;
                } else {
                    delayShift++;
                }
                break;
            }
        }
    }

}