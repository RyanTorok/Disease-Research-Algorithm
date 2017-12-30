import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by 11ryt on 12/12/2016.
 */
public class Actor extends Applet {
    public static final int matrixVerticalPadding = 75;
    public static final int matrixHorizontalPadding = 50;
    public static double CHANCE_CATCH_PREMATURE;
    public static double CHANCE_CATCH;
    public static double meanState[];
    public static int rangeState[];
    protected static int num_Rooms;
    protected static int num_Students;
    protected static int num_Periods;
    protected static int rows;
    protected static int cols;
    static double porportionAbsent;
    static Scanner s;
    static School inst;
    private boolean initial = true;
    protected static int dayNo = 1;
    protected static int pdNo = 1;

    public void initialSetup() {
        try {
            s = new Scanner(new File("C:/Users/11ryt/Documents/inputs.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find file. Please try again.");
            System.exit(0);
        }
        s.nextLine();
        s.nextLine();
        meanState = new double[2];
        rangeState = new int[2];
        CHANCE_CATCH_PREMATURE = Double.parseDouble(s.nextLine().split(":")[1].trim()); //usurps the number following the prompt colon.
        CHANCE_CATCH = Double.parseDouble(s.nextLine().split(":")[1].trim());
        meanState[0] = Double.parseDouble(s.nextLine().split(":")[1].trim());
        rangeState[0] = Integer.parseInt(s.nextLine().split(":")[1].trim());
        meanState[1] = Double.parseDouble(s.nextLine().split(":")[1].trim());
        rangeState[1] = Integer.parseInt(s.nextLine().split(":")[1].trim());
        num_Rooms = Integer.parseInt(s.nextLine().split(":")[1].trim());
        num_Periods = Integer.parseInt(s.nextLine().split(":")[1].trim());
        rows = Integer.parseInt(s.nextLine().split(":")[1].trim());
        cols = Integer.parseInt(s.nextLine().split(":")[1].trim());
        porportionAbsent = Double.parseDouble(s.nextLine().split(":")[1].trim());
        if (rows * cols > 40) {
            rows = 5;
            cols = 8;
        }
        num_Students = rows * cols * num_Rooms;
        Student.chanceAdvanceState12 = new double[rangeState[0] + 1];
        Student.chanceAdvanceState23 = new double[rangeState[1] + 1];
        double sd12 = getStDev(rangeState[0]);
        for (int i = 0; i < Student.chanceAdvanceState12.length; i++) {
            Student.chanceAdvanceState12[i] = normalArea(meanState[0], sd12, i, i + 1);
            Student.chanceAdvanceState12[0] = .05;
            Student.chanceAdvanceState12[Student.chanceAdvanceState12.length - 1] = .05;
        }
        /* At this point, each value stored in the probability array is the initial chance of the day the student gets sick on a particular day.
        The next loop changes the values to be the chance of getting sick on a particular day given he/she did not get sick on a previous day.   */
        for (int i = 0; i < Student.chanceAdvanceState12.length; i++) {
            double sum = 0;
            for (int j = i; j < Student.chanceAdvanceState12.length; j++) {
                sum += Student.chanceAdvanceState12[j];
            }
            Student.chanceAdvanceState12[i] = Student.chanceAdvanceState12[i] / sum;
        }
        double sd23 = getStDev(rangeState[1]);
        for (int i = 0; i < Student.chanceAdvanceState23.length; i++) {
            Student.chanceAdvanceState23[i] = normalArea(meanState[1], sd23, i, i + 1);
            Student.chanceAdvanceState23[0] = .025;
        }
        //same status as above comment, except for the 2 to 3 state change probabilities.
        for (int i = 0; i < Student.chanceAdvanceState23.length; i++) {
            double sum = 0;
            for (int j = i; j < Student.chanceAdvanceState23.length; j++) {
                sum += Student.chanceAdvanceState23[j];
            }
            Student.chanceAdvanceState23[i] = Student.chanceAdvanceState23[i] / sum;
        }
        inst = new School();
        inst.masterStudentList.get(0).catchDisease();
        initial = false;
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                    advancePeriod();
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                advancePeriod();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    public void paint(Graphics g) {
        if (initial) {
            initialSetup();
        }
        this.setSize(1900, 900);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Day " + dayNo + ", Period " + pdNo, this.getBounds().width / 2 - (this.getBounds().width / 18), this.getBounds().height / 20);
        paintbordersAndStudents(g);
    }

    private void paintbordersAndStudents(Graphics g) {
        int numRoomsPerRow = 2 * (int) Math.sqrt(num_Rooms / 2); //sets matrix length to at most twice its height
        int roomLength = (this.getBounds().width - 2 * matrixHorizontalPadding) / numRoomsPerRow;
        int numRoomsPerCol = (num_Rooms % numRoomsPerRow == 0) ? num_Rooms / numRoomsPerRow : num_Rooms / numRoomsPerRow + 1; //accounts for possible incomplete row at bottom of matrix
        int roomHeight = (this.getBounds().height - 2 * matrixVerticalPadding) / numRoomsPerCol;
        for (int i = 0; i < numRoomsPerCol; i++) {
            for (int j = 0; j < numRoomsPerRow; j++) {
                if (i * numRoomsPerRow + j < num_Rooms)
                    g.drawRect(matrixHorizontalPadding + j * roomLength, matrixVerticalPadding + i * roomHeight, roomLength, roomHeight);
            }
        }
        int studentSquareSide = (int) Math.min(.7 * roomLength / cols, .7 * roomHeight / rows);
        int rowPadding = (roomHeight - rows * studentSquareSide) / (rows + 1);
        int columnPadding = (roomLength - cols * studentSquareSide) / (cols + 1);
        for (Student s : inst.masterStudentList) {
            int roomIndex = inst.assignmentOrder.get(pdNo - 1).indexOf(s) / (Actor.rows * Actor.cols); //index in list of rooms of class student has at this time.
            int seatIndex = inst.assignmentOrder.get(pdNo - 1).indexOf(s) % (Actor.rows * Actor.cols); //seat index of student in class.
            int classOrigin[] = {roomLength * (roomIndex % numRoomsPerRow) + matrixHorizontalPadding, roomHeight * (roomIndex / numRoomsPerRow) + matrixVerticalPadding}; //coordinate of top-left corner of specified class
            int horizontalOffset = (seatIndex % cols) * studentSquareSide + (seatIndex % cols + 1) * columnPadding;
            int verticalOffset = (seatIndex / cols) * studentSquareSide + (seatIndex / cols + 1) * rowPadding;
            if (s.isAbsent())
                g.setColor(Color.LIGHT_GRAY);
            else {
                switch (s.getState()) {
                    case 0:
                        g.setColor(Color.GRAY);
                        break;
                    case 1:
                        g.setColor(Color.YELLOW);
                        break;
                    case 2:
                        g.setColor(Color.RED);
                        break;
                    case 3:
                        g.setColor(Color.GREEN);

                }
            }
            g.fill3DRect(classOrigin[0] + horizontalOffset, classOrigin[1] + verticalOffset, studentSquareSide, studentSquareSide, true);
        }
        int sum = 0;
        for(Student s: inst.masterStudentList){
            if (s.getState() == 0){
                sum++;
            }
        }
    }

    void advancePeriod() {
        if (pdNo == num_Periods) {
            pdNo = 1;
            for (Class c : inst.roomList) {
                c.runClass(pdNo);
            }
            dayNo++;
            for (Student s : inst.masterStudentList) {
                s.advanceDay();
            }
        } else {
            pdNo++;
            for (Class c : inst.roomList) {
                c.runClass(pdNo);
            }
        }
        repaint();
    }

    double normalArea(double mean, double stDev, double lowerBound, double upperBound) { //performs a midpoint Riemann Sum with 10000 subdivisions
        int subDiv = 10000;
        double width = (upperBound - lowerBound) / subDiv;
        double totalArea = 0.0;
        for (double i = lowerBound; i < upperBound; i += width) {
            totalArea += (1 / (stDev * Math.sqrt(2 * Math.PI))) * Math.pow(Math.E, -0.5 * (Math.pow((i + (width * .5) - mean) / stDev, 2))) * width;
        }
        return totalArea;
    }
    double getStDev(int range) { //95% of normal distribution area is within two standard deviations of the mean, so the standard deviation is the range of 95% of the data divided by four.
        return range / 4.0;
    }
}
