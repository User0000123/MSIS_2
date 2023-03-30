import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

public class PythonJilbMetrics {

    private static final String pathToScript = "C:\\Users\\Aleksej\\Desktop\\parser.py";
    private static final String pathToPythonExe = "C:\\Program Files\\Python311";
    public static final String defaultDirToFile = "C:\\Users\\Aleksej\\Desktop\\Лабораторные\\МСИС\\2";
    public static final String pathToInputFile = defaultDirToFile+"\\result.py";

    public static LinkedList<TStatement> operators_flow;

    private static void printList(LinkedList<TStatement> list){
        list.forEach(item -> System.out.println(item.statement()+" "+item.depth()));
    }

    public static void parse() {
        String[] commands = {"cmd.exe", "/c","python.exe "+ pathToScript+" "+pathToInputFile};
        String[] lineGroup;
        Scanner virtualCMD;
        try {
            Process proc = (new ProcessBuilder(commands)).directory(new File(pathToPythonExe)).start();
            virtualCMD = new Scanner(proc.getInputStream()).useDelimiter("\n");
        } catch (IOException e) {
            Controller.showError("Runtime exception! Check syntax!");
            throw new RuntimeException(e);
        }

        operators_flow = new LinkedList<>();
        while (virtualCMD.hasNextLine()) {
            lineGroup = virtualCMD.nextLine().split("\s");
            operators_flow.add(new TStatement(lineGroup[0], Integer.parseInt(lineGroup[1])));
        }
    }

    public static int countAbsoluteEnclosure(){
        int result=0;
        for (TStatement item:operators_flow){if (item.statement().equals("If")) result++;}
        return result;
    }

    public static float countRelativeEnclosure(int CL){return (float)CL/operators_flow.size();}

    public static int findMaximumEnclosure(){
        int maximum_if_depth = -1;
        for (TStatement item:operators_flow){if (item.statement().equals("If") && item.depth()>maximum_if_depth) maximum_if_depth = item.depth();}
        return maximum_if_depth;
    }
}
record TStatement(String statement, int depth){
    public String getStatement(){return this.statement;}
    public int getDepth(){return this.depth;}
}